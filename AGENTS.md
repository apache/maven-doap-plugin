<!---
 Licensed to the Apache Software Foundation (ASF) under one or more
 contributor license agreements.  See the NOTICE file distributed with
 this work for additional information regarding copyright ownership.
 The ASF licenses this file to You under the Apache License, Version 2.0
 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->
# Working on the Maven DOAP Plugin

These notes cover what the code and POM do not make obvious, for contributors and coding agents.

## Layout

- `src/main/java/org/apache/maven/plugin/doap/DoapMojo.java` is the single `doap:generate` goal;
  `DoapUtil` holds the RDF writing helpers and `options/ASFExtOptionsUtil` the ASF rules (the
  accepted categories and programming languages, and the category resource URI).
- `src/main/mdo/*.mdo` are Modello models. `DoapOptions`, `ASFExtOptions`, `ExtOptions`,
  `DoapArtifact` and `Standard` are generated from them into
  `target/generated-sources/modello`: edit the model, never the generated class. Option defaults
  live in the models.
- `src/main/resources/doap-person*.properties` are translated messages (de, en, es, fr). The
  `reporting` profile adds the l10n plugin's translation report for them.

## Build

- Floors: Java 8 (`maven.compiler.release`), Maven 3.9.0 (`<prerequisites>`). The build compiles
  against Maven 3.9.x and Resolver 1.9.x; Dependabot ignores Maven 3.10+, Resolver 2.x and
  SLF4J 2.x on purpose (see `.github/dependabot.yml`).
- Spotless (palantir-java-format for Java, sortpom for the POM) runs `apply` at `process-sources`
  locally, so a normal build reformats your changes, and `check` when the `CI` environment variable
  is set (the `format` profile in maven-parent). It does not format Markdown.
- Commands:
  - `mvn verify`: unit tests.
  - `mvn -Prun-its verify`: unit tests and the invoker integration tests.
  - `mvn -Prun-its verify -Dinvoker.test=basic`: one integration test.
  - `mvn site`: the site, from `src/site`.

## Tests

- Unit tests use JUnit 5 and maven-plugin-testing-harness 3.5.x:
  `@MojoTest(realRepositorySession = true)`, `@InjectMojo` and `@Basedir` with fixtures under
  `src/test/resources/unit/`. The harness hands the mojo a Mockito mock `MavenSession`; stub
  anything the mojo reads from it. `DoapMojoTest#setUp` stubs `getProjectBuildingRequest()`, which
  `DoapMojo` needs to resolve the POM of a configured `<artifact>`.
- Integration tests are in `src/it/` (`basic`, `doapfile`, `minimal`, `MDOAP-38`). Each has a
  `verify.groovy`; `MDOAP-38` expects the build to fail.
- Namespace URIs such as `http://usefulinc.com/ns/doap#` and
  `http://projects.apache.org/ns/asfext#` are identifiers, not links. Keep them on `http`; the
  tests assert the literal values.

## Site

- `.md.vm` pages are Velocity templates: `##` starts a comment, so they use Setext headings
  (`----` underlines), and a literal `$` is written as `${esc.d}`.
- Keep page names and `<a id="...">` anchors stable: they are published, and `DoapMojo` prints
  a link to `usage.html#DOAP_ASF_Configuration` when ASF validation fails.

## Pull requests

- Pull requests are squash- or rebase-merged. Release Drafter builds the release notes from pull
  request titles and labels, so the title must read as a release note and every pull request
  needs a label.
