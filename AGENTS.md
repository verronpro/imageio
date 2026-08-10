# Imageio — AGENTS.md

Agent-agnostic guidance for working in the **imageio** project. This file is the standalone
guidance for this project; it does not assume any relationship to other projects.

## Project layout

Three independent Maven modules, each registering a `javax.imageio.ImageReaderSpi` in the
`IIORegistry`:

- `imageio-svg`
- `imageio-emf`
- `imageio-wmf`

They provide vector image support (SVG / EMF / WMF) to Java applications via the standard
ImageIO API.

## General development standards

This project uses **Java 25** and **Maven**.

### Build
- `mvn clean install` — build the project.
- `mvn clean install -DskipTests` — build without running tests.
- `mvn clean install -pl <module>` — build a single Maven module within this project.
- `mvn clean install -pl <module> -am` — build a module and its dependencies.
- `mvn site` — generate the Maven documentation site from `src/site/asciidoc/`.

### Testing
- JUnit 5 (Jupiter); use `@DisplayName` for descriptive names; follow Arrange-Act-Assert.
- Tests live in `src/test/java/`.
- Coverage via JaCoCo, mutation testing via Pitest, architecture constraints via ArchUnit.
- `java.awt.headless=true` is set automatically by the Surefire plugin.

### Code style
- Java 25 features in use: records, JPMS modules (`module-info.java`), modern APIs.
- Soft line-length limit: 120 characters. Indentation: 4 spaces.
- Opening braces on the same line; `else` on a new line.
- Naming: PascalCase for classes/interfaces, camelCase for methods/variables,
  UPPER_SNAKE_CASE for constants.
- Prefer composition to inheritance; static factory methods or builders for complex objects;
  constructor injection for dependencies.
- Javadoc required for all public elements; use Markdown syntax inside Javadoc.
- Custom exceptions; do not swallow exceptions; use try-with-resources.

## Rules

- Do **not** introduce dependencies between the three plugins; they are intentionally
  independent and register themselves via `META-INF/services`.
- When adding tests, ensure the `ImageReaderSpi` is actually registered in `IIORegistry` — the
  `META-INF/services` auto-load may not fire in every test classpath, so register explicitly if
  a test needs it.
