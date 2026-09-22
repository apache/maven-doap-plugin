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

People use DOAP to share information and metadata about a software project.
The DOAP Plugin generates a DOAP file from a POM.

## How To Publish A DOAP File

Publish the DOAP file on a web server or in source code control. Make sure that other people can get the file with an HTTP or HTTPS request.

By default, the DOAP Plugin generates the file in the reporting output directory (that is ${project.reporting.outputDirectory})
so that it is included as part of the site.

For more information, read the [Integrated DOAP Plugin With The Site Plugin](./with-site-plugin.html) page.

## How To Distribute A DOAP File

Several Semantic Web directories, like [http://doapspace.org/](http://doapspace.org/) and [http://doapstore.org](http://doapstore.org), list metadata about open source projects in a public catalog.

Enter the URL of the DOAP file into their catalogs. Use [Ping the Semantic Web](http://pingthesemanticweb.com/) to share RDF data with the web.

# Examples for the Maven DOAP

The Maven project shares its DOAP file in this [SVN repository](http://svn.apache.org/repos/asf/maven/maven-3/trunk/doap_Maven.rdf). Some Semantic Web directories show this file:

- [Doapstore](http://doapstore.org/view.php?uri=http%3A%2F%2FMaven.rdf.apache.org%2F)
- [Zigtgist RDF Viewer](http://dataviewer.zitgist.com/?uri=http%3A//svn.apache.org/repos/asf/maven/maven-3/trunk/doap_Maven.rdf)
