---
title: Introduction
author: 
  - Jason van Zyl
date: 2013-07-22
---

<!--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

# Apache Maven DOAP Plugin
The DOAP Plugin generates a compliant [Description of a Project (DOAP)](https://github.com/ewilderj/doap) file from a POM. The main goal is to provide DOAP files for Semantic Web systems. These systems use DOAP files as their primary input. This also removes the need to maintain two sets of metadata.

Version 1.0 of this plugin is designed for projects at [Apache](https://projects.apache.org/doap.html). This will change in later versions of the plugin, as more people become interested in DOAP.

If you do not know about RDF or Semantic Web concepts, read the [Links](./links.html) page.

## Goals Overview

The DOAP Plugin has one goal:

- [doap:generate](./generate-mojo.html) Generates a DOAP file from the POM.
## Usage

The [usage page](./usage.html) has general instructions for the DOAP Plugin. The examples below describe specific ways to use the DOAP Plugin. 

- Examples
- Tips
- Errata

If you have questions about the usage of this plugin, read the [FAQ](./faq.html). Contact the [user mailing list](./mailing-lists.html) for more help.

The [mail archive](./mailing-lists.html) can contain answers to your questions in older threads of the mailing list.

If the plugin is missing a feature, enter a feature request in the [issue tracker](./issue-management.html). If the plugin has a defect, enter a bug report in the [issue tracker](./issue-management.html). When you create a new issue, give a full description of your concern. Attach this information to the issue:

- Complete debug logs
- POMs
- Small demo projects

The developers must be able to reproduce your problem. A small demo project is the most useful attachment. Patches are welcome. Contributors can get the project from the [source repository](./scm.html). The [guide to helping with Maven](https://maven.apache.org/guides/development/guide-helping.html) has more information for contributors.

## Examples

See these examples of the usage of the DOAP Plugin:

- [Generated DOAP in use](./examples/doap-in-use.html)
- [Integrate DOAP Plugin in the Site Plugin](./examples/with-site-plugin.html)
