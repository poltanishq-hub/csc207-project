-- GeoGuessr Daily Challenge: results and social persistence migration (P1)
--
-- Scope: Defines tables, RLS policies, views, and indexes for:
-- 1. Daily results (geochallenge_daily_results)
-- 2. Round results (geochallenge_round_results)
-- 3. Friend relationships (geochallenge_friendships)
-- 4. Public leaderboard view (geochallenge_leaderboard_view)
--
-- Depends on: geochallenge_profiles (from geochallenge_auth_spike.sql)

-- ============================================================================
-- 1. DAILY RESULTS TABLE
-- ============================================================================
create table if not exists public.geochallenge_daily_results (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null references auth.users(id) on delete cascade,
    challenge_date date not null,
    total_score integer not null,
    completed_at timestamptz not null default now(),
    constraint geochallenge_daily_results_unique_user_date
        unique (user_id, challenge_date),
    constraint geochallenge_daily_results_total_score_range
        check (total_score between 0 and 25000)
);

-- Index for daily leaderboard lookups
create index if not exists idx_geochallenge_daily_results_date_score
    on public.geochallenge_daily_results (challenge_date, total_score desc, completed_at asc);

-- ============================================================================
-- 2. ROUND RESULTS TABLE
-- ============================================================================
create table if not exists public.geochallenge_round_results (
    id uuid primary key default gen_random_uuid(),
    daily_result_id uuid not null references public.geochallenge_daily_results(id) on delete cascade,
    round_number integer not null,
    location_id text not null,
    guess_lat double precision not null,
    guess_lon double precision not null,
    distance_km double precision not null,
    score integer not null,
    constraint geochallenge_round_results_unique_round
        unique (daily_result_id, round_number),
    constraint geochallenge_round_results_round_number_range
        check (round_number between 1 and 5),
    constraint geochallenge_round_results_score_range
        check (score between 0 and 5000),
    constraint geochallenge_round_results_distance_non_negative
        check (distance_km >= 0),
    constraint geochallenge_round_results_lat_range
        check (guess_lat between -90.0 and 90.0),
    constraint geochallenge_round_results_lon_range
        check (guess_lon between -180.0 and 180.0)
);

-- ============================================================================
-- 3. FRIENDSHIPS TABLE
-- ============================================================================
create table if not exists public.geochallenge_friendships (
    id uuid primary key default gen_random_uuid(),
    requester_id uuid not null references auth.users(id) on delete cascade,
    addressee_id uuid not null references auth.users(id) on delete cascade,
    status text not null default 'accepted',
    created_at timestamptz not null default now(),
    constraint geochallenge_friendships_no_self_friend
        check (requester_id <> addressee_id),
    constraint geochallenge_friendships_unique_pair
        unique (requester_id, addressee_id),
    constraint geochallenge_friendships_status_valid
        check (status in ('pending', 'accepted', 'rejected'))
);

create index if not exists idx_geochallenge_friendships_users
    on public.geochallenge_friendships (requester_id, addressee_id);

-- ============================================================================
-- 4. ROW LEVEL SECURITY (RLS) POLICIES
-- ============================================================================
alter table public.geochallenge_daily_results enable row level security;
alter table public.geochallenge_round_results enable row level security;
alter table public.geochallenge_friendships enable row level security;

-- 4.1 Daily results RLS:
-- Any authenticated user can read daily results for leaderboard calculation.
drop policy if exists geochallenge_daily_results_read on public.geochallenge_daily_results;
create policy geochallenge_daily_results_read
    on public.geochallenge_daily_results
    for select
    to authenticated
    using (true);

-- A player can only insert their own daily result.
drop policy if exists geochallenge_daily_results_insert on public.geochallenge_daily_results;
create policy geochallenge_daily_results_insert
    on public.geochallenge_daily_results
    for insert
    to authenticated
    with check (auth.uid() = user_id);

-- 4.2 Round results RLS:
-- Round breakdowns are visible to authenticated players (for review).
drop policy if exists geochallenge_round_results_read on public.geochallenge_round_results;
create policy geochallenge_round_results_read
    on public.geochallenge_round_results
    for select
    to authenticated
    using (true);

-- Round results can only be inserted if the parent daily_result belongs to auth.uid().
drop policy if exists geochallenge_round_results_insert on public.geochallenge_round_results;
create policy geochallenge_round_results_insert
    on public.geochallenge_round_results
    for insert
    to authenticated
    with check (
        exists (
            select 1 from public.geochallenge_daily_results dr
            where dr.id = daily_result_id and dr.user_id = auth.uid()
        )
    );

-- 4.3 Friendships RLS:
-- Players can view friendships in which they are either requester or addressee.
drop policy if exists geochallenge_friendships_read on public.geochallenge_friendships;
create policy geochallenge_friendships_read
    on public.geochallenge_friendships
    for select
    to authenticated
    using (auth.uid() = requester_id or auth.uid() = addressee_id);

-- Players can initiate a friend request/link where they are the requester.
drop policy if exists geochallenge_friendships_insert on public.geochallenge_friendships;
create policy geochallenge_friendships_insert
    on public.geochallenge_friendships
    for insert
    to authenticated
    with check (auth.uid() = requester_id);

-- Players can remove or update friend links involving them.
drop policy if exists geochallenge_friendships_delete on public.geochallenge_friendships;
create policy geochallenge_friendships_delete
    on public.geochallenge_friendships
    for delete
    to authenticated
    using (auth.uid() = requester_id or auth.uid() = addressee_id);

-- ============================================================================
-- 5. LEADERBOARD VIEW
-- ============================================================================
-- Joins daily results with public profiles, ranked deterministically:
-- 1) total_score DESC
-- 2) completed_at ASC
-- 3) username ASC
create or replace view public.geochallenge_leaderboard_view as
select
    dr.id as daily_result_id,
    dr.user_id,
    p.username,
    p.display_name,
    dr.challenge_date,
    dr.total_score,
    dr.completed_at
from public.geochallenge_daily_results dr
join public.geochallenge_profiles p on dr.user_id = p.user_id;

-- ============================================================================
-- 6. PERMISSIONS
-- ============================================================================
grant select, insert on public.geochallenge_daily_results to authenticated;
grant select, insert on public.geochallenge_round_results to authenticated;
grant select, insert, delete, update on public.geochallenge_friendships to authenticated;
grant select on public.geochallenge_leaderboard_view to authenticated;
