# Architecture decisions (initial)

## Decision 1: One Maven module

The project is one Java desktop application, so begin with a single Maven module and the standard `src/main/java` and `src/test/java` layout. Revisit a multi-module build only if a concrete need emerges.

## Decision 2: Dependency direction

Dependencies point inward. Entities must not import or depend on Swing, HTTP clients, JSON libraries, or persistence implementations. Use cases define interfaces for services they need; outer adapters implement those interfaces.

## Decision 3: Swing is an outer-layer detail

Swing event handlers should call controllers/use cases, not implement game rules. Keep the UI creation and object wiring in the outer layer / composition root.

## To decide as a team before implementing the game

- Which API will satisfy the external-API requirement and provide suitable location imagery/map functionality?
- How will daily challenges be deterministic for all users on the same date?
- Which backend/database will persist accounts, results, and friendships?
- What is the scoring formula and how is distance computed?
- What location-data attribution and API terms must be shown/respected?
