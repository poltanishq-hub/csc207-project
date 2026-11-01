# GeoGuessr Daily Challenge

A Java Swing desktop application for a five-round daily geography guessing game.

## Requirements

- JDK 11 or later (the project compiles against Java 11)
- Maven 3.9 or later, or IntelliJ IDEA's bundled Maven
- Git

Check your setup:

```bash
java -version
javac -version
mvn -version
```

## Build and test

Run these commands from the directory containing `pom.xml`:

```bash
mvn test       # compile and run unit tests
mvn package    # compile, test, and package the project
mvn clean      # remove generated build output
```

## Run the desktop application

In IntelliJ, open the project folder, load the Maven project, and run `Main.java`.
Alternatively, after compiling, run:

```bash
java -cp target/classes ca.utoronto.csc207.geochallenge.Main
```

The initial window is only a working scaffold. Add gameplay through use cases and keep scoring/domain rules out of Swing classes.

## Architecture guidelines

The intended direction of dependencies is inward:

- `entities`: core domain objects and rules; no Swing, HTTP, or database imports.
- `use_cases`: application actions, input/output models, and interfaces (ports) for required external operations.
- `interface_adapters`: controllers, presenters, and view models that translate between Swing and use cases.
- `frameworks_and_drivers`: Swing screens and concrete API/database implementations.
- `Main`: composition root that constructs and connects the objects.

Do not create a class just to fill a folder. Add classes when there is a defined use case or responsibility. Keep the initial structure small and follow the CSC207 course notes.

## Team workflow

1. Keep `main` buildable.
2. Create a branch for each coherent task: `feature/<short-name>` or `fix/<short-name>`.
3. Commit and push the branch, then open a Pull Request into `main`.
4. Have a different teammate review it; address feedback and run `mvn test` before merging.
5. Everyone should author PRs and review teammates' PRs throughout development.

## Secrets

Never commit API keys, database passwords, or service-role credentials. Put local configuration in an ignored `.env` file or environment variables and document safe setup with placeholders in `.env.example` if needed.
