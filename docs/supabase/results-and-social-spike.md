# Supabase Results and Social Persistence Spike

This guide provides setup and verification instructions for the database schema owned by **P1** (Leaderboard, Results, and Social Persistence).

---

## 1. Ownership & Scope

This migration owns:
- `public.geochallenge_daily_results`
- `public.geochallenge_round_results`
- `public.geochallenge_friendships`
- `public.geochallenge_leaderboard_view`
- Associated indexes and Row-Level Security (RLS) policies.

It depends upon `public.geochallenge_profiles` (created in `docs/supabase/geochallenge_auth_spike.sql` by P3).

---

## 2. Dashboard Setup Instructions

1. Log in to the shared Supabase project.
2. Ensure `geochallenge_auth_spike.sql` has already been run.
3. Open **SQL Editor**, create a new query named `GeoChallenge results and social`, paste the contents of [`geochallenge_results_and_social.sql`](geochallenge_results_and_social.sql), and run it.
4. Verify in **Table Editor** that `geochallenge_daily_results`, `geochallenge_round_results`, and `geochallenge_friendships` appear with RLS enabled.

---

## 3. RLS Security Model

- **Public Client Safety:** The desktop application communicates using the `publishable/anon` key and user JWT tokens. It has no service-role key.
- **Write Isolation:**
  - A user can only insert daily results where `user_id = auth.uid()`.
  - A user can only insert round results attached to their own daily result.
  - A user can only create or delete friendship links involving themselves.
- **Read Access:**
  - Daily results are readable by all authenticated users to allow global leaderboard computation.
  - Round results are readable by all authenticated users for performance reviews.
  - Friendships are private: only visible to the two users involved in the relationship.

---

## 4. HTTP Spike Verification (curl)

### 4.1 Save a Daily Result
```bash
# 1. Insert daily result
POST $SUPABASE_URL/rest/v1/geochallenge_daily_results
apikey: $SUPABASE_PUBLISHABLE_KEY
Authorization: Bearer <USER_ACCESS_TOKEN>
Content-Type: application/json
Prefer: return=representation

{
  "user_id": "<USER_UUID>",
  "challenge_date": "2026-11-22",
  "total_score": 21500
}
```

### 4.2 Query Leaderboard View
```bash
GET $SUPABASE_URL/rest/v1/geochallenge_leaderboard_view?challenge_date=eq.2026-11-22&order=total_score.desc,completed_at.asc
apikey: $SUPABASE_PUBLISHABLE_KEY
Authorization: Bearer <USER_ACCESS_TOKEN>
```
