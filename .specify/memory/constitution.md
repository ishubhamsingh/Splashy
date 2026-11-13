# Splashy Constitution

## Core Principles

### I. Shared-First Multiplatform

Splashy is a Kotlin Multiplatform + Compose Multiplatform app for Android and iOS.

- Business logic, state, networking, persistence, and UI MUST live in `commonMain` by default.
- Platform source sets (`androidMain`, `iosMain`) MAY contain code only when it requires a
  platform API (e.g., file I/O, wallpaper setting, database drivers), exposed to common code via
  `expect`/`actual` declarations.
- Every user-facing feature MUST work on both Android and iOS, or the spec MUST state explicitly
  which platform is out of scope and why.

Rationale: one codebase for two platforms is the project's reason to exist; logic duplicated in
platform source sets drifts and doubles maintenance.

### II. Feature-Sliced Architecture

- Each feature lives in `features/<name>/` with a Voyager `ScreenModel` plus `ui/` holding its
  screen, immutable `State`, and `Event` types. UI emits events; the ScreenModel owns state.
- Data access MUST go through repository interfaces in `core/domain`, implemented in
  `core/network` (Ktor) or `db/` (SQLDelight). Screens and ScreenModels MUST NOT call Ktor or
  SQLDelight directly.
- Remote results MUST be surfaced through `NetworkResult` (or an equivalent sealed type) so
  loading, success, and error states are handled explicitly in UI.
- Dependencies MUST be provided through Koin modules in `core/di`; no manual singletons.
- Reusable composables go in `ui/components`; theming goes through `ui/theme`.

Rationale: a consistent shape per feature keeps the codebase navigable and makes logic testable
independent of UI.

### III. Verify Before Done (NON-NEGOTIABLE)

- A successful build, `spotlessCheck`, or lint pass is NOT proof that a feature works or a bug is
  fixed.
- New or changed non-UI logic (ScreenModels, repositories, mappers, utils) SHOULD be covered by
  tests in `commonTest` where practical; any exception MUST be noted in the plan.
- For behavior that can only be verified manually (UI, device-specific, runtime state), the
  change MUST ship with explicit verification steps for each affected platform, and is not done
  until the user confirms them.
- Bug fixes MUST state the root cause before code is changed. If the cause is unclear, say so
  rather than guess-fix.

Rationale: the project currently has no automated test suite, so explicit verification is the
only reliable guard against regressions.

### IV. Unsplash API Compliance & Secret Hygiene

- The Unsplash access key, keystore, and signing credentials MUST come from `secrets.properties`
  (or CI secrets) and MUST NEVER be committed, logged, or hard-coded.
- Features using Unsplash content MUST follow the Unsplash API guidelines: credit the
  photographer and Unsplash, hotlink returned image URLs, and trigger the photo's
  `download_location` endpoint when a user downloads or applies a photo.
- Network code MUST respect API rate limits: avoid redundant requests, paginate, and cache where
  reasonable.

Rationale: violating the API terms or leaking keys can get the app's API access revoked.

### V. Minimal, Scoped Changes

- Each change MUST address only its stated goal. Unrelated refactors, renames, or reformatting
  MUST NOT be bundled; they are raised separately.
- Prefer the simplest solution that meets the spec (YAGNI). New dependencies or abstractions MUST
  be justified in the plan.
- Work MUST be broken into small phases, each leaving the app buildable and functional.

Rationale: small, focused diffs are easier to review, verify, and revert, and fit within limited
working sessions.

## Technology & Platform Constraints

- Stack: Kotlin Multiplatform, Compose Multiplatform, Ktor Client, SQLDelight, Voyager, Koin,
  Coil, Napier, kotlinx-serialization, Moko Permissions. Replacing a core library is a MAJOR
  decision that requires a written justification in the plan.
- All dependency versions MUST be declared in `gradle/libs.versions.toml`; Renovate manages
  upgrades.
- Code formatting is enforced by Spotless with ktfmt; code MUST pass `./gradlew spotlessCheck`.
- Logging MUST use Napier, and MUST NOT emit secrets or personal data.
- Target JDK for builds is 17 (matching CI).

## Development Workflow & Quality Gates

- CI (`.github/workflows/Check.yaml`) runs `spotlessCheck lintRelease :composeApp:assembleRelease`
  on every push and PR to `main`; these MUST pass before merge.
- iOS-affecting changes MUST be built and run on iOS (simulator or device) before being declared
  done, since CI builds Android only.
- Commits follow Conventional Commits (`feat:`, `fix(scope):`, `chore(deps):`, `style:`, ...).
- No commit, push, merge, or release happens without explicit approval from the maintainer.
- Spec Kit flow: `/speckit-specify` → `/speckit-plan` → `/speckit-tasks` → `/speckit-implement`;
  each plan MUST include a Constitution Check against these principles.

## Governance

- This constitution supersedes conflicting practices in specs, plans, and tasks. Plans that
  deviate from a principle MUST document the deviation and justification in their Complexity
  Tracking section.
- Amendments are made via `/speckit-constitution`, require maintainer approval, and MUST include
  a Sync Impact Report during review.
- Versioning follows semantic versioning: MAJOR for removed or redefined principles, MINOR for
  new principles or materially expanded guidance, PATCH for clarifications and wording.
- Compliance is reviewed at plan time (Constitution Check) and at review time for every PR.

**Version**: 1.0.0 | **Ratified**: 2026-11-14 | **Last Amended**: 2026-11-14
