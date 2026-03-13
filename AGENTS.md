# Repository Guidelines

## Project Structure & Module Organization
This repository is split into two main modules:
- `backend/`: Spring Boot 3 + Java 21 service (Telegram bot + REST API), with Java code in `backend/src/main/java/com/tony/log4m`, SQL/mapper/config in `backend/src/main/resources`, and tests in `backend/src/test/java`.
- `frontend-admin/`: Vue 3 + TypeScript admin UI, with feature code in `frontend-admin/src` (`api`, `stores`, `views`, `components`, `router`).

Other important paths:
- `data/`: local H2 data files.
- `docker-compose.yml`: full-stack local deployment.
- `.vscode/tasks.json`: optional local run/build tasks.

## Build, Test, and Development Commands
- `mvn -pl backend clean package`: build backend JAR (`backend/target/log4M.jar`).
- `mvn -pl backend test`: run backend unit tests (JUnit 5).
- `cd backend && mvn spring-boot:run -Dspring-boot.run.profiles=local`: run backend on `:9001`.
- `cd frontend-admin && npm install`: install frontend dependencies.
- `cd frontend-admin && npm run dev`: start Vite dev server on `:3000`.
- `cd frontend-admin && npm run build`: type-check and create production build.
- `cd frontend-admin && npm run lint`: lint and auto-fix `.vue/.ts/.js` files.
- `docker-compose up --build`: run full stack with containers.

## Coding Style & Naming Conventions
- Java: 4-space indentation, `UpperCamelCase` classes, `lowerCamelCase` methods/fields, package root `com.tony.log4m`.
- Vue/TS: prefer `<script setup lang="ts">` patterns already used in `src`, 2-space indentation, `PascalCase` for component files (e.g., `DataTable.vue`), `camelCase` for stores/api helpers.
- Run `npm run lint` before opening a PR; keep imports and unused code clean.

## Testing Guidelines
- Backend uses Spring Boot Test + JUnit 5 + Mockito (`backend/src/test/java/.../*Test.java`).
- Name tests as `*Test` and prefer descriptive method names like `testXxx_WhenYyy`.
- Frontend currently has no automated test runner configured; at minimum run `npm run lint` and `npm run type-check` before submitting changes.

## Commit & Pull Request Guidelines
- Follow Conventional Commits style seen in history: `feat(scope): ...`, `fix(scope): ...`, `refactor(scope): ...`, `chore: ...`.
- Keep commits focused by module (`backend` vs `frontend-admin`) when possible.
- PRs should include:
  1. clear summary of behavior changes,
  2. linked issue/task,
  3. validation steps (commands run),
  4. screenshots/GIFs for UI changes in `frontend-admin`.

## Security & Configuration Tips
- Never commit real tokens/secrets; use environment variables (`BOT_TOKEN`) and local `.env` files.
- Confirm frontend API proxy target (`frontend-admin/vite.config.ts`) matches your backend host before testing.

## Category Keywords Configuration
- Config file: `backend/src/main/resources/application.yml` under `category-keywords.categories`
- Structure uses English keys (e.g., `drink`, `fruit`) with nested `name` (Chinese display name) and `keywords` list
- **Important**: Do NOT use Chinese characters as YAML Map keys - Spring Boot's YAML parser has issues with them and will convert them to numeric indices (0, 1, 2...)
- Correct structure:
  ```yaml
  category-keywords:
    categories:
      drink:
        name: 饮
        keywords: [luckin, 瑞幸, ...]
      fruit:
        name: 水果
        keywords: [香蕉, 苹果, ...]
  ```
- Java class: `CategoryKeywordProperties.java` with `CategoryConfig` inner class
- Used by `CommandHandler.parseFreeText()` for automatic categorization based on text keywords
