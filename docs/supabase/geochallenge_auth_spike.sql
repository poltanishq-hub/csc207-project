-- GeoGuessr Daily Challenge: authentication/profile spike
--
-- Scope: this migration owns only the geochallenge_profiles table and the
-- geochallenge_* function, trigger, and RLS policy below. It deliberately does
-- not create or alter any game-result, friendship, leaderboard, or generic
-- profile tables that another teammate may own.
--
-- Run this once in the Supabase SQL Editor as a project owner. It is safe to
-- re-run; every object name in this file is unique to this feature.

create table if not exists public.geochallenge_profiles (
    user_id uuid primary key references auth.users(id) on delete cascade,
    username text not null unique,
    display_name text not null,
    created_at timestamptz not null default now(),
    constraint geochallenge_profiles_username_format
        check (username ~ '^[a-z0-9_]{3,24}$'),
    constraint geochallenge_profiles_display_name_not_blank
        check (length(btrim(display_name)) between 1 and 64)
);

-- A SECURITY DEFINER trigger creates the profile at the same time as the Auth
-- user. The desktop client therefore never receives database write access for
-- this table and never needs a service-role key.
create or replace function public.geochallenge_create_profile()
returns trigger
language plpgsql
security definer
set search_path = ''
as $$
declare
    requested_username text := lower(btrim(coalesce(new.raw_user_meta_data ->> 'username', '')));
    requested_display_name text := btrim(coalesce(new.raw_user_meta_data ->> 'display_name', ''));
begin
    if requested_username !~ '^[a-z0-9_]{3,24}$' then
        raise exception using
            errcode = '22023',
            message = 'Username must contain 3-24 lowercase letters, numbers, or underscores.';
    end if;

    if length(requested_display_name) not between 1 and 64 then
        raise exception using
            errcode = '22023',
            message = 'Display name must contain 1-64 characters.';
    end if;

    insert into public.geochallenge_profiles (user_id, username, display_name)
    values (new.id, requested_username, requested_display_name);

    return new;
end;
$$;

drop trigger if exists geochallenge_create_profile_after_auth_signup on auth.users;
create trigger geochallenge_create_profile_after_auth_signup
    after insert on auth.users
    for each row execute procedure public.geochallenge_create_profile();

alter table public.geochallenge_profiles enable row level security;

-- A signed-in player may read public identity fields required by future
-- leaderboard and friend features. No client insert/update/delete policy is
-- granted; writes are limited to the trigger above.
drop policy if exists geochallenge_profiles_authenticated_read on public.geochallenge_profiles;
create policy geochallenge_profiles_authenticated_read
    on public.geochallenge_profiles
    for select
    to authenticated
    using (true);

revoke all on public.geochallenge_profiles from anon, authenticated;
grant select on public.geochallenge_profiles to authenticated;
