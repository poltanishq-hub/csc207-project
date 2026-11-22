# Supabase authentication spike

This is the exact Supabase setup required before implementing the application's concrete authentication gateway. It is intentionally limited to account access and its public identity record.

## Ownership boundary

This feature owns only these named objects:

- `public.geochallenge_profiles`
- `public.geochallenge_create_profile()`
- `geochallenge_create_profile_after_auth_signup`
- `geochallenge_profiles_authenticated_read`

Do not rename, alter, or reuse these objects for unrelated storage. The SQL does **not** touch `daily_results`, `round_results`, `friendships`, location data, or any existing generic `profiles` table. Other team members who need player identity should use the stable `user_id` from `PlayerProfile` and, when they need public names, read `geochallenge_profiles` rather than creating a second player-profile table.

## One-time dashboard setup

Only a teammate who is an owner of the shared Supabase project should perform these steps.

1. Open the intended shared project in Supabase. Confirm the project reference with the teammate who is already using the project; do not create the objects in a different project by accident.
2. Open **SQL Editor**, create a new query named `GeoChallenge auth spike`, paste the contents of [`geochallenge_auth_spike.sql`](geochallenge_auth_spike.sql), and run it.
3. Open **Authentication → Providers** (or **Authentication → Sign In / Providers**) and ensure the Email provider is enabled.
4. For this classroom MVP, decide as a team whether confirmation email is required:
   - **Recommended for the demo:** turn **Confirm email** off. Registration immediately returns a session, so the spec's register → home flow works without a desktop deep-link implementation.
   - **If kept on:** registration cannot enter Home until the email has been confirmed. A future desktop confirmation/deep-link flow is then required, so do not claim registration is complete yet.
5. Do not enable anonymous sign-ins. Do not use, copy, or commit a `service_role`/secret key.
6. Open **Project Settings → API** and record the project URL and the current **publishable/anon** client key. The publishable key may be supplied to a desktop client only because the RLS policy limits database access. The secret/service-role key must never leave the dashboard/server environment.
7. In your ignored local `.env` file, add values in this shape (replace the placeholders locally):

   ```dotenv
   SUPABASE_URL=https://YOUR_PROJECT_REF.supabase.co
   SUPABASE_PUBLISHABLE_KEY=sb_publishable_YOUR_KEY
   ```

   Do not put real values in `.env.example`, source files, commits, screenshots, or chat messages. The currently untracked `.env.example` has deliberately not been changed by this branch.

## What the SQL protects

- Supabase Auth owns passwords. The application database holds no password column.
- Every profile row has the same UUID as its `auth.users` row and is deleted automatically when that Auth user is deleted.
- A profile is created by an Auth trigger from the signup metadata. The client has no insert/update/delete permission on the profile table.
- Usernames are normalized to lowercase and must match `[a-z0-9_]{3,24}`. Display names must be 1–64 non-blank characters.
- Only authenticated users can read public profile fields. This supports future leaderboard/friends screens without exposing credentials or email addresses.

## Required HTTP spike checks

Run these checks after setup. Use two disposable email addresses and passwords; never include their credentials in a commit or issue.

### 1. Register account A

Send `POST $SUPABASE_URL/auth/v1/signup` with headers:

```text
apikey: $SUPABASE_PUBLISHABLE_KEY
Content-Type: application/json
```

and a body shaped like:

```json
{
  "email": "gc-spike-a@example.test",
  "password": "use-a-long-disposable-password",
  "data": {
    "username": "gc_spike_a",
    "display_name": "GC Spike A"
  }
}
```

With Confirm email disabled, the response should include a user ID and session. In the Table Editor, verify one matching row in `public.geochallenge_profiles` with the returned UUID. Do not query or edit `auth.users` manually.

### 2. Log in and load the public profile

Send `POST $SUPABASE_URL/auth/v1/token?grant_type=password` using the same `apikey` and JSON body containing account A's email/password. Use its returned `access_token` for:

```text
GET $SUPABASE_URL/rest/v1/geochallenge_profiles?select=user_id,username,display_name&user_id=eq.<ACCOUNT_A_UUID>
apikey: $SUPABASE_PUBLISHABLE_KEY
Authorization: Bearer <ACCOUNT_A_ACCESS_TOKEN>
```

Expect exactly account A's public profile. The later Java gateway will make these same three calls, but must map failures to `AuthenticationFailure` rather than display raw responses.

### 3. Verify account isolation and username uniqueness

Register account B with a different email and username (`gc_spike_b`), then sign in as B.

- Account B can read the public profile fields needed by a future leaderboard, but cannot insert, update, or delete rows in `geochallenge_profiles` through the REST API.
- A third sign-up using an already claimed username must fail; the application will map the provider/database response to `USERNAME_UNAVAILABLE`.
- A sign-up using an already registered email must not be presented as a technical error. Treat it as `EMAIL_UNAVAILABLE` or a generic registration failure, depending on the provider response.

Record only pass/fail and the project environment used. Delete the disposable test accounts when the test is finished if the team does not want them in the demo leaderboard.

## Hand-off to the Java application

Step 4 will implement `SupabaseAuthGateway` behind the existing `AuthGateway` interface. It will:

1. send registration metadata (`username`, `display_name`) to Supabase Auth;
2. log in with email/password;
3. retrieve `geochallenge_profiles` by the authenticated UUID;
4. keep any access token in memory only; and
5. map expected HTTP/provider errors to the existing `AuthenticationFailure` enum.

## Sources

- [Supabase password authentication](https://supabase.com/docs/guides/auth/passwords)
- [Supabase user-management guidance for public profile tables](https://supabase.com/docs/guides/auth/managing-user-data)
- [Supabase Auth and RLS overview](https://supabase.com/docs/guides/auth)
- [Supabase API-key and Authorization header guidance](https://supabase.com/docs/guides/functions/auth-headers)
