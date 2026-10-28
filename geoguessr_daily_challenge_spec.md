# GeoGuessr Daily Challenge — Starter Specification

**Version:** 0.1  
**Platform:** Java desktop application using Swing  
**Team:** 4 developers  
**Purpose:** Define a small, testable MVP and an architecture the team can implement in parallel.

## 1. Product summary

GeoGuessr Daily Challenge is a desktop game in which a signed-in player completes the same five location guesses as other players on a given day. For each round, the player views a location image, places a guess on a small map, submits it, and sees the actual location, distance, and score. After round five, the game presents a total score and a breakdown. Players can compare their results with the global leaderboard and their friends.

The initial version should prioritize a complete, reliable end-to-end flow over realistic street-view navigation or a large social feature set.

## 2. Required scope and explicit non-goals

### Must have

1. Register, log in, and log out.
2. Home screen with **Play today's challenge**, completion status, and leaderboard navigation.
3. Five rounds per daily challenge, with a location image and a clickable guess map.
4. Per-round reveal: actual location, distance from the guess, score, and **Next round**.
5. Final summary: total score out of 25,000 and five-row round breakdown.
6. Global leaderboard and a friends-only leaderboard.
7. Basic friend management: find a player by username, add/remove a friend (or use friend requests if the team has time).
8. Persistent accounts, friend relationships, and daily results through an external API/database.
9. Unit tests for game rules and repository-independent use cases.

### Four core user stories (one lead per developer)

1. **US-1 — Account access (P3):** As a player, I want to register and log in so that my identity, scores, and friends persist between sessions.
2. **US-2 — Play today's challenge (P2):** As a player, I want to make one location guess in each of five rounds so that I can earn a daily score.
3. **US-3 — Review my challenge performance (P4):** As a player, I want to see the score, distance, guess, and target for every round after I finish so that I can understand how I performed.
4. **US-4 — Compare with other players (P1):** As a player, I want to compare today's score on the global and friends leaderboards so that I can see how I rank; I also need a simple way to add or remove friends.

These are user goals, not a prescription to create one interactor for every screen or button. US-2 owns the game rules and guess submission; US-3 owns presenting and retrieving the completed breakdown; US-4 owns ranking and friend comparisons. The final screen can link to US-4 without duplicating leaderboard logic.

### Defer until the MVP works

- 360-degree/panorama navigation, streaks, hints, badges, chat, tournaments, and live multiplayer.
- Complex profile customization, password-reset UI, and anti-cheat protections.
- Endless dynamically generated rounds. Use a fixed, attributed location catalogue first.

## 3. Functional specification

### FR-1 — Account access

- A visitor can create an account with a unique username and the fields required by the selected authentication provider.
- A registered user can log in and log out.
- Failed registration/login displays a useful error without crashing or exposing technical details.
- Passwords are handled by the authentication provider; the application must not store plaintext passwords in its own tables or files.
- After successful login, the user sees the home screen.

### FR-2 — Home and daily challenge

- The home screen displays the player's username, the challenge date, whether today's challenge is complete, **Play today's challenge**, and leaderboard/friends navigation.
- All players see the same five targets for a given canonical challenge date. Pick one timezone for the reset and use it everywhere (recommended for this project: `America/Toronto`).
- If today's challenge is complete, the main action becomes **View today's results** rather than allowing a second scored attempt.

### FR-3 — Play a round

- Show progress such as `Round 2 of 5`, the location image, and a map on which the player can select a guessed point.
- The player must select a point before **Guess** is enabled.
- Submitting a guess records one latitude/longitude pair and locks that round against resubmission.
- The result view shows the guess marker, the target marker, estimated straight-line distance, score, and **Next round**.
- After rounds 1–4, **Next round** loads the next target. After round 5, it opens the final summary.
- A player cannot skip a round or submit a sixth guess.

### FR-4 — Scoring and final summary

Use a simple, documented scoring rule for the MVP. Calculate great-circle distance with the Haversine formula, in kilometres, then:

`roundScore = round(5000 * exp(-distanceKm / 2000))`

Clamp the result to the range 0–5000. A daily total is the sum of the five round scores, from 0 to 25,000. This is the project's scoring rule, not a claim to reproduce GeoGuessr's exact scoring formula.

The final summary shows:

- Total score out of 25,000.
- Round number, target name after reveal, guessed location, distance, and points for each round.
- A link/button to compare the score on the leaderboard and a button to return home.
- A completed result is saved once for the user and challenge date.

### FR-5 — Leaderboard and friends

- The global leaderboard lists completed daily totals for a selected date, highest score first.
- The friends leaderboard shows results for accepted friends and the current player for that date.
- Rows display rank, username/display name, total score, and completion time (if available).
- Ties use a documented deterministic rule, e.g. earlier completion time ranks first; if tied again, username alphabetically.
- A player can search by exact username and add/remove a friend. If implementing requests, show pending/accepted states and allow acceptance/rejection.
- Empty states are explicit (e.g. “No friends have completed today's challenge yet”).

### FR-6 — Failure states

- Invalid map selection, unavailable challenge data, failed API calls, and failed result saves must produce understandable UI feedback.
- If saving a completed result fails, do not silently display it as permanently saved. Offer retry and keep the result in the current session until the user retries or exits.
- Never show API keys, access tokens, stack traces, or raw database errors in user-facing messages.

## 4. Data and API decisions

### Location catalogue

Bundle a small, fixed catalogue of locations in the application resources for the first version. Each item should contain:

- Stable `locationId`.
- Image resource path.
- Target latitude and longitude.
- Display name, revealed only after guessing.
- Image creator/source and licence/attribution text or source URL.

Use properly licensed images and retain their attribution. Start with 10–15 targets to prove the round flow, then expand to enough targets for the dates shown in the demo. Define the five targets for a date deterministically, preferably in a `challenges.json` resource keyed by date. Do not randomly choose a new set every time a user opens the game.

For the first version, a still image is an acceptable scope limit; full Street View-style navigation is a stretch goal.

### External API and persistence

**Recommended implementation:** Supabase Auth plus its REST Data API backed by PostgreSQL. This supports real accounts and cross-installation results/friend comparisons. Keep all calls behind Java interfaces so the application core does not depend on Supabase. Java's `java.net.http.HttpClient` can be used for HTTP calls, with a JSON library for encoding/decoding.

- The desktop app may contain a publishable/anonymous project key, protected by correctly configured Row Level Security (RLS) policies.
- **Never put a Supabase secret/service-role key in the desktop application or Git repository.**
- Restrict each user to their own account data and result writes; expose only the public data required for leaderboard/profile views; restrict friend relationships to their participants.
- Before committing to the schema, test a minimal end-to-end spike: sign up, log in, insert one result, and retrieve a leaderboard row. If the course-provided login API is easier, it is a valid alternative only if it supports the required cross-user leaderboard/friend queries as well as persistence.

Suggested tables (adapt to the selected provider):

| Table | Important fields | Purpose |
|---|---|---|
| `profiles` | `user_id`, `username` (unique), `display_name`, `created_at` | Public player information; authentication credentials remain in Auth |
| `friendships` | `requester_id`, `addressee_id`, `status`, `created_at` | Friend links or requests |
| `daily_results` | `id`, `user_id`, `challenge_date`, `total_score`, `completed_at` | One completed score per player/date; unique `(user_id, challenge_date)` |
| `round_results` | `daily_result_id`, `round_number`, `location_id`, `guess_lat`, `guess_lon`, `distance_km`, `score` | Five-round breakdown; unique `(daily_result_id, round_number)` |

The challenge targets can remain in the bundled catalogue; store the stable `location_id` with each round result. Avoid storing duplicate image files or passwords in the database.

## 5. Clean Architecture design

Follow the Clean Architecture dependency rule: **source-code dependencies point inward**. The domain and use cases must not import Swing classes, HTTP classes, Supabase classes, JSON response types, or database row types. Outer layers adapt those details to simple data objects used by inner layers.

### Layer responsibilities

1. **Entities (`entities`)** — Core game concepts and rules that do not depend on screens or services.
2. **Use cases (`use_cases`)** — Application-specific operations: authenticate, play/submit a guess, retrieve a result, get a leaderboard, manage friendships. Use cases depend on abstractions (interfaces/ports), not concrete APIs.
3. **Interface adapters (`interface_adapters`)** — Controllers translate UI input into input data; presenters translate output data into view models; gateway implementations convert between domain objects and external data formats.
4. **Frameworks and drivers (`frameworks_and_drivers`)** — Swing views, HTTP client, Supabase/JSON implementation details, resource loading, and any map library.
5. **Application wiring (`app`)** — The composition root that creates concrete gateways, use cases, controllers, presenters, and views and connects them together.

### Suggested package layout

```text
src/main/java/ca/utoronto/geochallenge/
  app/
    Main.java                 # composition root / dependency wiring
  entities/
    PlayerProfile.java
    Location.java
    Guess.java
    RoundResult.java
    DailyResult.java
    Friendship.java
    ScoreCalculator.java
  use_cases/
    auth/
      RegisterInputData.java
      RegisterInteractor.java
      RegisterOutputBoundary.java
      LoginInputData.java
      LoginInteractor.java
      LoginOutputBoundary.java
    challenge/
      ChallengeRepository.java
      LocationCatalog.java
      SubmitGuessInputData.java
      SubmitGuessInteractor.java
      SubmitGuessOutputBoundary.java
      GetDailyResultInteractor.java
    leaderboard/
      LeaderboardRepository.java
      GetLeaderboardInteractor.java
      GetLeaderboardInputData.java
      GetLeaderboardOutputBoundary.java
    friends/
      FriendshipRepository.java
      ManageFriendsInteractor.java
  interface_adapters/
    controllers/
      AuthController.java
      ChallengeController.java
      LeaderboardController.java
      FriendsController.java
    presenters/
      AuthPresenter.java
      ChallengePresenter.java
      LeaderboardPresenter.java
      FriendsPresenter.java
    viewmodels/
      HomeViewModel.java
      RoundViewModel.java
      ResultViewModel.java
      LeaderboardViewModel.java
  frameworks_and_drivers/
    views/
      MainFrame.java
      LoginView.java
      RegisterView.java
      HomeView.java
      GameView.java
      RoundResultView.java
      FinalResultView.java
      LeaderboardView.java
      FriendsView.java
    data_access/
      SupabaseAuthGateway.java
      SupabaseLeaderboardRepository.java
      SupabaseFriendshipRepository.java
      SupabaseResultRepository.java
      LocalLocationCatalog.java
    api/
      HttpClientFactory.java
      ApiErrorMapper.java
    resources/
      locations.json
      challenges.json
      images/
```

This is a starting layout, not a requirement to create every file immediately. Add a class only when it has a clear responsibility; merge small DTOs or presenters if that makes the code simpler without mixing layers.

### Key interfaces (contracts to agree on before parallel work)

The exact signatures can change, but agree on these concepts first:

```java
public interface LocationCatalog {
    List<Location> locationsFor(LocalDate challengeDate);
}

public interface LeaderboardRepository {
    List<LeaderboardEntry> globalLeaderboard(LocalDate date);
    List<LeaderboardEntry> friendsLeaderboard(UUID userId, LocalDate date);
}

public interface FriendshipRepository {
    List<Friendship> friendsOf(UUID userId);
    void addFriend(UUID userId, String friendUsername);
    void removeFriend(UUID userId, UUID friendId);
}

public interface ResultRepository {
    Optional<DailyResult> resultFor(UUID userId, LocalDate date);
    void saveCompletedResult(DailyResult result);
}
```

`LeaderboardEntry` can be a simple use-case data object containing rank, username, score, and completion time. Keep interfaces small: separate result, friendship, and profile responsibilities rather than creating one giant `DataAccessObject` with unrelated methods.

### Typical call flow

```text
Swing View
  -> Controller
    -> Use-case Interactor
      -> Entity / game rules
      -> Repository interface (declared inward)
         <- concrete repository in frameworks_and_drivers implements it
    -> Output boundary interface
       <- Presenter implements it
          -> ViewModel
             -> Swing View displays state
```

The controller should not calculate scores, call Supabase directly, or build screen components. The presenter should format output, not decide game rules. A use case should not know that a button or `JFrame` exists.

## 6. Main entities and rules

- **`PlayerProfile`**: stable user ID, username, display name.
- **`Location`**: stable ID, target coordinates, image path, reveal-only name, attribution.
- **`Guess`**: guessed latitude and longitude, validated to legal coordinate ranges.
- **`RoundResult`**: round number, location ID, guess, distance, score.
- **`DailyResult`**: player ID, challenge date, exactly five round results, total score, completion time.
- **`Friendship`**: requester, addressee, status and timestamps.
- **`ScoreCalculator`**: pure functions for Haversine distance and round score; no network, files, or UI.

Validate that a completed daily result contains exactly five rounds and that its total equals the sum of round scores. Prevent a second saved result for the same user/date. Make challenge date/time injectable (e.g. a Java `Clock`) so tests do not depend on the real current date.

## 7. Swing UI behavior

Use one `JFrame` with a `CardLayout` for switching between screens. Build each screen as a focused `JPanel`. Views display view-model data and forward user actions to controllers; they should not own game logic or data-access code.

- **Login/Register:** input fields, submit button, validation/error area.
- **Home:** Play today's challenge, completion status, leaderboard and friends navigation.
- **Game:** round counter, image, clickable map, selected guess marker, Guess button.
- **Round result:** actual/guessed markers, distance, points, Next round.
- **Final result:** total, round-by-round table, leaderboard comparison link, return home.
- **Leaderboard/Friends:** global/friends filter, ranked rows, username search and add/remove actions.

Do not make network requests on Swing's Event Dispatch Thread. Use a `SwingWorker` (or another appropriate background task), then update Swing components on the Event Dispatch Thread. If using map tiles, show appropriate attribution and provide a clear message when tiles cannot load.

## 8. Four-developer ownership

These assignments preserve the P1–P4 split from the initial sketch while ensuring each person leads a coherent use case. Everyone should still make reviewed contributions outside their own feature when integration requires it.

| Developer | Lead use case / area | Main deliverables | Contract needed from team |
|---|---|---|---|
| **P1** | View leaderboard and compare results | Repository/API implementation for results, leaderboard query, global/friends leaderboard screen and tests | `LeaderboardRepository`, `ResultRepository`, shared `LeaderboardEntry`/`DailyResult` models |
| **P2** | Play a daily challenge / submit guesses | Round state, map selection input, distance and score calculations, five-round progression, tests | `LocationCatalog`, `SubmitGuess` input/output objects, round/result entity contract |
| **P3** | Register/login and enter the app | Auth use cases, authentication gateway, login/register screens, home screen and navigation | `AuthGateway` methods and a stable authenticated-user/profile model |
| **P4** | Review round and final results | Round-reveal and final-summary views/presenters, round breakdown and completion flow, result-display tests | `RoundResult`, `DailyResult`, challenge output boundary and when a challenge is complete |

**Shared work in the first session:** all four agree on entities, repository method signatures, date handling, result-save semantics, and the screen navigation sketch before implementing features independently. P1 should not invent contracts while implementing the database; publish them for review first. P2 owns scoring logic; P4 displays its output but does not duplicate the formula.

## 9. SOLID examples to aim for

- **SRP — Single Responsibility:** `ScoreCalculator` calculates distance/points; `SupabaseResultRepository` saves/loads results; `LeaderboardPresenter` formats leaderboard output. Avoid a `GameManager` class that owns UI, scoring, HTTP, and persistence.
- **OCP — Open/Closed:** game rules depend on `LocationCatalog`/repository interfaces so the local catalogue can later be replaced without rewriting `SubmitGuessInteractor`. Do not add provider-specific `if` statements throughout the use cases.
- **LSP — Liskov Substitution:** an in-memory fake repository used by tests must honour the same behaviour as the real repository (including empty results, uniqueness, and error semantics).
- **ISP — Interface Segregation:** keep authentication, result, leaderboard, and friendship interfaces focused; clients should not implement unrelated methods.
- **DIP — Dependency Inversion:** use cases depend on repository/output-boundary abstractions; Supabase and Swing implementations depend on those abstractions, not the reverse.

Avoid creating abstractions without a reason. The goal is low coupling and clear boundaries, not the largest possible number of interfaces.

## 10. Testing and acceptance checklist

### Unit tests (no internet, no Swing)

- Same coordinates produce distance 0 and score 5000.
- Increasing distance never increases score; scores stay within 0–5000.
- Invalid latitude/longitude is rejected.
- The same challenge date always returns the same five targets; no target repeats within one challenge.
- A round cannot be submitted twice; a sixth round cannot be opened.
- A total is exactly the sum of five round scores and never exceeds 25,000.
- A user cannot complete the same date twice.
- Global leaderboard ordering and tie-breaking are deterministic.
- Friends leaderboard excludes unrelated users.
- Failed repository operations are reported to the presenter/use case rather than silently swallowed.

### Integration/manual tests

- Register -> log in -> home -> play all five rounds -> result persists -> log out/in -> same day's results remain.
- Two accounts can complete the same challenge and see each other's public leaderboard score.
- A friend appears in the friends leaderboard after the friendship is accepted.
- API unavailable: the app shows an understandable error and does not claim an unsaved result was saved.
- Images have source/licence attribution, and no secrets are committed.

## 11. Git workflow and milestones

The course requires Pull Requests throughout development, Java Swing, an external API, persistence, and an individual contribution to a lead use case. Use feature branches and small PRs; do not work directly on `main`.

1. **Milestone 1 — Contracts and skeleton:** repository setup, package layout, entities, use-case interfaces, one Swing frame with dummy screens, a tiny in-memory repository, and passing unit tests for scoring.
2. **Milestone 2 — Vertical slice:** one location can be guessed, scored, revealed, and represented by a `DailyResult`; test without the API.
3. **Milestone 3 — Accounts and persistence:** API spike, authentication, save/load one completed result, RLS/security checks.
4. **Milestone 4 — Five-round flow:** deterministic daily challenge, no duplicate guesses, final summary.
5. **Milestone 5 — Friends/leaderboard:** global and friends rankings, username search/add/remove, empty/error states.
6. **Milestone 6 — Polish and presentation:** integration tests, accessibility/usability pass, demo data, diagram showing dependency directions, and design rationale for SOLID/Clean Architecture.

Every PR should have a narrow purpose, a description of the user-visible behaviour, tests (where practical), and at least one teammate reviewer. Keep API credentials in local configuration/environment variables; commit an example configuration file without secrets.

## 12. Decisions to confirm early

1. Prove the selected API provider can support account access, cross-user leaderboard reads, friend relationships, and safe per-user writes before building all UI screens.
2. Confirm whether the course staff accepts the chosen service as the external API/persistence solution; the course project brief explicitly allows an API-backed user record with associated JSON as one possible approach.
3. Pick the map implementation (a Swing-compatible map component or a bundled map panel) and test clicking to a latitude/longitude before the active-round screen is fully built.
4. Agree on a location-image licence/attribution convention and the canonical challenge timezone.

## References

- [CSC207 course notes repository](https://github.com/CSC207-UofT/207-course-notes) — especially Program Specification, Design Principles, Introduction to Layered Architectures, and The Clean Architecture.
- [Robert C. Martin, “The Clean Architecture”](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html).
- [Supabase Data REST API documentation](https://supabase.com/docs/guides/api).
- [Supabase API key guidance](https://supabase.com/docs/guides/getting-started/api-keys).
