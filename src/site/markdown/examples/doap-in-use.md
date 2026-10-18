---
title: Generated DOAP In Use
author: 
  - Vincent Siveton
date: 2008-07-17
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

# Generated DOAP In Use

DOAP is used to share metadata about a software project. After generating a DOAP file from the POM, publish it at a stable URL. Consumers can then retrieve the generated RDF document from that URL.

## How To Publish A DOAP File

Publish the file somewhere that is accessible over HTTP or HTTPS. A project repository, a generated project site, or another web server are all suitable locations. The important property is a stable URL that returns the generated RDF document; no central DOAP directory is required.

Projects that publish an HTML site can also advertise the RDF document with a link element in the HTML header such as:

```html
<link rel="meta" type="application/rdf+xml" title="DOAP" href="doap_example.rdf" />
```

By default, the DOAP Plugin generates the file in the reporting output directory.
That directory is `${project.reporting.outputDirectory}`, so the file is included as part of the site.

For more information, read the [Integrated DOAP Plugin With The Site Plugin](./with-site-plugin.html) page.

## How To Share A DOAP File

Share the stable URL with the tools or catalogs that consume DOAP. Check their current submission instructions before registering a project, since third-party services and their availability can change independently of this plugin.

For example, a project publishing its Maven site at `https://example.org/` can make its generated file available at `https://example.org/doap_example.rdf`. Consumers can then fetch that URL directly, and the project can update the file whenever its POM metadata changes.
