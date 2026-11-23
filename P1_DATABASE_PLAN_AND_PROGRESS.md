# P1 Database & Persistence: Plan and Progress Tracker

**Developer Role:** P1 (Leaderboard, Results, and Social Persistence)  
**Lead Use Case:** US-4 (Compare with other players / Leaderboards & Friends) & Result Storage for US-2/US-3  
**Architecture:** Clean Architecture & SOLID Principles  
**Backend:** Supabase PostgreSQL + REST Data API (PostgREST)  

---

## 1. Role & Objective Overview

As developer **P1**, the core responsibilities outlined in `geoguessr_daily_challenge_spec.md` are:
1. **Database Schema & Security (Supabase):**
   - Tables and constraints for storing daily challenge results (`daily_results`, `round_results`) and friend connections (`friendships`).
   - Row-Level Security (RLS) policies allowing authenticated desktop users to safely record their own scores and read global/friends leaderboards without service-role keys or plain SQL injection risks.
   - Consistent naming with existing tables (e.g. `geochallenge_profiles` created in P3's auth spike).
2. **Domain Contracts & Repository Interfaces:**
   - Define clean, inward-pointing interfaces: `ResultRepository`, `LeaderboardRepository`, `FriendshipRepository`.
   - Define data models: `DailyResult`, `RoundResult`, `LeaderboardEntry`, `Friendship`.
3. **In-Memory Fakes:**
   - Provide in-memory implementations of the repositories so the rest of the team (P2, P3, P4) can test and develop game play, authentication, and result presentation independently without network dependencies.
4. **Concrete Supabase Gateways:**
   - Implement production-ready repositories using Java's standard `HttpClient` and JSON handling that talk to Supabase's PostgREST API with proper authentication tokens.
5. **Testing & Validation:**
   - Unit tests verifying contract behavior, sorting, tie-breaking, error handling, and RLS validation spikes.

---

## 2. Collaboration Protocol & Instructions for Agents

1. **Explain Before Doing:** Before writing code for any subtask, explain what is being built, the architectural rationale, and which files will be created/modified.
2. **One Subtask at a Time:** Do not rush through multiple subtasks at once. Give the user an opportunity to review, ask questions, or implement parts themselves.
3. **Keep Plan Updated:** When a subtask is started or finished, update the checkbox and status in this file (`P1_DATABASE_PLAN_AND_PROGRESS.md`).
4. **Preserve Clean Architecture:** Inner layers (`entities`, `use_cases`) must never import Supabase, HTTP, or Swing classes.

---

## 3. Subtask Breakdown & Progress

### Phase 1: Database Schema & Migration Design (SQL)

- [x] **Subtask 1.1: Design Schema for Challenge & Round Results**
  - **Goal:** Create SQL table definitions for `geochallenge_daily_results` and `geochallenge_round_results`.
  - **Key Constraints:**
    - Unique `(user_id, challenge_date)` to enforce 1 completed game per player per day.
    - Foreign key linking `round_results` to `daily_results` (cascade delete).
    - Checks on scores (`total_score` 0–25000, round score 0–5000, round numbers 1–5).
  - **Status:** Completed (`docs/supabase/geochallenge_results_and_social.sql`)

- [x] **Subtask 1.2: Design Schema for Friendships**
  - **Goal:** Create SQL table definition for `geochallenge_friendships`.
  - **Key Constraints:**
    - `requester_id` and `addressee_id` referencing `auth.users(id)`.
    - Prevent self-friending (`requester_id <> addressee_id`).
    - Unique pair constraint so duplicates cannot be created.
    - Status column (`pending`, `accepted`, `rejected`).
  - **Status:** Completed (`docs/supabase/geochallenge_results_and_social.sql`)

- [x] **Subtask 1.3: Define Row Level Security (RLS) Policies**
  - **Goal:** Secure all tables so desktop clients using the anon key cannot tamper with other players' data.
  - **Policies:**
    - `daily_results` & `round_results`: Users can INSERT only rows where `user_id = auth.uid()`. All authenticated users can SELECT daily results for leaderboards.
    - `friendships`: Users can only SELECT friendships where `requester_id = auth.uid() OR addressee_id = auth.uid()`. Users can INSERT / UPDATE only where they are a party to the friendship.
  - **Status:** Completed (`docs/supabase/geochallenge_results_and_social.sql`)

- [x] **Subtask 1.4: Design Database View / Query for Leaderboards**
  - **Goal:** Create an SQL view `geochallenge_leaderboard_view` that joins `geochallenge_daily_results` with `geochallenge_profiles`.
  - **Ordering / Deterministic Tie-Breaking:** `total_score DESC, completed_at ASC, username ASC`.
  - **Status:** Completed (`docs/supabase/geochallenge_results_and_social.sql`)

- [x] **Subtask 1.5: Assemble SQL Migration File**
  - **Goal:** Output clean, reproducible migration script `docs/supabase/geochallenge_results_and_social.sql` following the conventions of `docs/supabase/geochallenge_auth_spike.sql`.
  - **Status:** Completed (`docs/supabase/geochallenge_results_and_social.sql`)

---

### Phase 2: Database Verification & HTTP Spike Testing

- [x] **Subtask 2.1: Write Setup & Verification Guide**
  - **Goal:** Document step-by-step instructions in `docs/supabase/results-and-social-spike.md` on how to apply the SQL migration in Supabase and test endpoints.
  - **Status:** Completed (`docs/supabase/results-and-social-spike.md`)

- [ ] **Subtask 2.2: Test HTTP Spike with REST API**
  - **Goal:** Verify PostgREST endpoints using `curl` or HTTP requests:
    - Insert a test daily result with 5 rounds using an authenticated session.
    - Verify duplicate submission on the same date fails.
    - Query global leaderboard for today's date.
    - Test friendship insertion and querying.
  - **Status:** Ready for execution against Supabase instance.

---

### Phase 3: Clean Architecture Contracts & Domain Entities

- [x] **Subtask 3.1: Define Entities and Data Objects**
  - **Goal:** Implement core entities/DTOs:
    - `RoundResult`: round number, locationId, guess coordinates, distanceKm, score.
    - `DailyResult`: result ID, userId, challengeDate, 5 round results, totalScore, completedAt.
    - `LeaderboardEntry`: rank, userId, username, displayName, totalScore, completedAt.
    - `Friendship`: requesterId, addresseeId, status, createdAt.
  - **Status:** Completed (`src/main/java/ca/utoronto/csc207/geochallenge/entities/`)

- [x] **Subtask 3.2: Define Repository Interfaces**
  - **Goal:** Declare interfaces in the `use_cases` layer:
    - `ResultRepository`: `Optional<DailyResult> resultFor(UUID userId, LocalDate date)`, `void saveCompletedResult(DailyResult result)`.
    - `LeaderboardRepository`: `List<LeaderboardEntry> globalLeaderboard(LocalDate date)`, `List<LeaderboardEntry> friendsLeaderboard(UUID userId, LocalDate date)`.
    - `FriendshipRepository`: `List<Friendship> friendsOf(UUID userId)`, `void addFriend(UUID userId, String friendUsername)`, `void removeFriend(UUID userId, UUID friendId)`.
  - **Status:** Completed (`src/main/java/ca/utoronto/csc207/geochallenge/use_cases/`)

---

### Phase 4: In-Memory / Test Repositories (For Team Integration & Tests)

- [x] **Subtask 4.1: Implement In-Memory Repositories**
  - **Goal:** Provide `InMemoryResultRepository`, `InMemoryLeaderboardRepository`, and `InMemoryFriendshipRepository`.
  - **Purpose:** Allows P2, P3, and P4 to develop against working repositories without requiring internet or database access.
  - **Status:** Completed (`src/main/java/ca/utoronto/csc207/geochallenge/frameworks_and_drivers/data_access/`)

- [x] **Subtask 4.2: Unit Tests for Repository Contracts**
  - **Goal:** Write JUnit 5 tests asserting contract invariants:
    - Deterministic ordering of leaderboard entries (score desc, time asc, username asc).
    - Preventing duplicate daily result saves.
    - Friends-only leaderboard isolation.
  - **Status:** Completed (`src/test/java/ca/utoronto/csc207/geochallenge/`)

---

### Phase 5: Concrete Supabase Gateways

- [ ] **Subtask 5.1: PostgREST HTTP Client & JSON Helper**
  - **Goal:** Create reusable HTTP helper methods in `frameworks_and_drivers/data_access` for sending authenticated GET/POST requests and handling errors cleanly.
  - **Status:** Pending

- [ ] **Subtask 5.2: Implement `SupabaseResultRepository`**
  - **Goal:** Complete implementation of `ResultRepository` that persists and retrieves results from Supabase tables.
  - **Status:** Skeleton created (`SupabaseResultRepository.java`)

- [ ] **Subtask 5.3: Implement `SupabaseLeaderboardRepository` & `SupabaseFriendshipRepository`**
  - **Goal:** Complete implementations querying leaderboard views and managing friend relationships.
  - **Status:** Skeletons created (`SupabaseLeaderboardRepository.java`, `SupabaseFriendshipRepository.java`)

- [ ] **Subtask 5.4: Error Handling & Resilience**
  - **Goal:** Ensure failed network operations return meaningful domain exceptions rather than leaking SQL errors or crashing Swing.
  - **Status:** Pending

---

## 4. Current Work Log

| Date | Subtask | Summary of Changes | Next Steps |
|------|---------|--------------------|------------|
| 2026-11-21 | Initialization | Created planning & progress document based on spec. | Set up files across the architectural layers. |
| 2026-11-22 | Initial File Creation | Created SQL migration, spike docs, domain entities, use-case repository interfaces, in-memory test repositories, and gateway skeletons. Verified build with `mvn test-compile`. | Run HTTP Spike (2.2) or implement concrete Supabase PostgREST clients (5.1-5.3). |
