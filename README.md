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
# [Apache Maven DOAP Plugin](https://maven.apache.org/plugins/maven-doap-plugin/)

[![Apache License, Version 2.0, January 2004](https://img.shields.io/github/license/apache/maven.svg?label=License)][license]
[![Maven Central](https://img.shields.io/maven-central/v/org.apache.maven.plugins/maven-doap-plugin.svg?label=Maven%20Central)][central]
[![Reproducible Builds](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/jvm-repo-rebuild/reproducible-central/master/content/org/apache/maven/plugins/maven-doap-plugin/badge.json)][reproducible]
[![Verify](https://github.com/apache/maven-doap-plugin/actions/workflows/maven-verify.yml/badge.svg)][verify]

The DOAP Plugin generates a [Description of a Project (DOAP)](https://github.com/ewilderj/doap)
RDF file from a project's POM, so the project metadata is kept in one place. For Apache projects
it also writes the ASF extensions (PMC, chair, standards) and fails the build when a field the
[ASF DOAP guidelines](https://projects.apache.org/guidelines.html) require is missing.

The plugin runs on Java 8 or later and requires Maven 3.9.0 or later.

## Usage

Generate the DOAP file into `target/site/doap_<artifactId>.rdf`:

```shell
mvn doap:generate
```

To publish it with the project site, bind the `generate` goal to the `site` phase. The site
documents the [goal parameters](https://maven.apache.org/plugins/maven-doap-plugin/generate-mojo.html),
[pure DOAP and ASF configurations](https://maven.apache.org/plugins/maven-doap-plugin/usage.html),
[site integration](https://maven.apache.org/plugins/maven-doap-plugin/examples/with-site-plugin.html)
and an [FAQ](https://maven.apache.org/plugins/maven-doap-plugin/faq.html).

## Contributing

Report bugs and feature requests in [GitHub Issues][issues] and discuss larger changes on the
[developer mailing list][ml-list]. Build conventions, test layout and the pitfalls of this
repository are in [AGENTS.md](AGENTS.md).

Run the unit tests and integration tests before opening a pull request:

```shell
mvn -Prun-its verify
```

Pull requests are squash- or rebase-merged, and Release Drafter builds the release notes from
pull request titles and labels, so give each pull request a title that reads as a release note.

## Releasing

Follow the [Maven project release procedure](https://maven.apache.org/developers/release/maven-project-release-procedure.html).

[license]: https://www.apache.org/licenses/LICENSE-2.0
[central]: https://central.sonatype.com/artifact/org.apache.maven.plugins/maven-doap-plugin
[reproducible]: https://github.com/jvm-repo-rebuild/reproducible-central/blob/master/content/org/apache/maven/plugins/maven-doap-plugin/README.md
[verify]: https://github.com/apache/maven-doap-plugin/actions/workflows/maven-verify.yml
[issues]: https://github.com/apache/maven-doap-plugin/issues
[ml-list]: https://maven.apache.org/mailing-lists.html
