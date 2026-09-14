# CLAUDE.md

Guidance for Claude Code (claude.ai/code) working in this repository.

## Read AGENTS.md first

**[AGENTS.md](AGENTS.md) is the source of truth** and is kept current by the maintainers.
It covers the stack, the mandatory environment variables, every Gradle command, the
toolchain quirks (auto-format on compile, git-based versioning, AspectJ weaving, Lombok
and MapStruct wiring), the module map, task ordering, the BDD suite, and the dual-repo
publishing setup.

This file exists only because Claude Code loads `CLAUDE.md` by name. Do not duplicate
AGENTS.md here — if guidance applies to every agent, it belongs there.

## Verifying a change without Mirakl credentials

The one thing AGENTS.md does not cover, because it assumes you have the credentials.

`./gradlew build` cannot resolve `com.mirakl:mmp-sdk-operator` without
`PAYPAL_MIRAKL_SDK_USER` / `PAYPAL_MIRAKL_SDK_PASSWORD`: the Artifactory returns 401 and
the artifact is not on Maven Central. `hmc.lib-conventions` puts that dependency on every
library module, so **no module builds** without them, and there is no offline fallback.

That does not always mean a change cannot be verified. Several modules contain no Mirakl
code of their own — `hmc-jobsystem` has zero references to `com.mirakl`, for example — so
the affected classes and their tests can be compiled and run against a classpath assembled
from Maven Central:

1. Check the module is actually clean: `grep -rn "com.mirakl" <module>/src/`. Note that a
   module may still reach Mirakl *transitively* through `hmc-infrastructure`, which does
   use it heavily; that limits which classes can be isolated, not which module.
2. Match the pinned versions rather than guessing: Spring Boot is in
   `buildSrc/build.gradle`, everything else follows from it (Boot 3.5.14 → Spring
   Framework 6.2.x, Quartz 2.5.0).
3. Compile with an explicit file list and `-implicit:none`. Letting `-sourcepath` pull in
   the closure drags in unrelated classes and produces confusing failures.
4. **Use JDK 21, not the project's Java 25 toolchain.** Lombok trails new JDK releases, and
   on a too-new JDK it silently fails to generate accessors — which surfaces as
   `cannot find symbol: method getStatus()` in code that is perfectly fine. Compiling with
   `--release 21` is not enough; the *running* javac has to be one Lombok supports.
5. Run with `junit-platform-console-standalone`.

State plainly what this does and does not cover. It is a real test run of the classes you
changed; it is not `./gradlew check`, and it excludes the integration suite, Checkstyle,
ArchUnit and the auto-formatter. Say which of those you skipped rather than implying a
green build.

## Before claiming a fix works

The repo's own conventions (Conventional Commits, `./gradlew format` running on compile,
Checkstyle on main sources only) are in AGENTS.md. One habit worth adding: **prove a
regression test fails without its fix.** Revert the change, watch the test go red, restore
it. For #144 that meant putting the batch job instance back into the `JobDataMap` and
confirming the serialization test failed with the same `NotSerializableException` the bug
report showed — which is the difference between a test that documents a fix and a test
that would catch the bug coming back.
