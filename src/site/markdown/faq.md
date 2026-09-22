---
title: Frequently Asked Questions
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

<a id="top"></a>

# Frequently Asked Questions

1. [What is DOAP?](#What_is_DOAP)
2. [What are the benefits of DOAP?](#What_are_the_benefits_of_DOAP)
3. [Why do I use DOAP when I have a POM?](#Why_would_I_use_DOAP_when_I_have_a_POM)
4. [What to do with the generated DOAP file?](#What_to_do_with_the_generated_DOAP_file)
5. [Why aren't all developers and contributors listed as maintainers in the DOAP file?](#Why_are_not_all_developers.2Fcontributors_listed_in_the_DOAP_file_as_maintainer)

<a id="What_is_DOAP"></a>

### What is DOAP?

DOAP stands for "Description of a Project". The [spec repository](https://github.com/ewilderj/doap) has all the information about DOAP.

<a id="What_are_the_benefits_of_DOAP"></a>

### What are the benefits of DOAP?

The [Semantic Web](http://en.wikipedia.org/wiki/Semantic_web) provides methods to process data in a machine-readable form. A DOAP file is a machine-readable document that helps project research. The data in a DOAP file has a predictable format. This format makes it easy to find project information in the large amount of data on the Web.

<a id="Why_would_I_use_DOAP_when_I_have_a_POM"></a>

### Why do I use DOAP when I have a POM?

A DOAP file takes little effort to generate with the Maven DOAP Plugin. The DOAP file spreads project information to other systems. Cataloging tools like [SWiK](http://swik.net/) and [DoapStore](http://doapstore.org/) use DOAP files. A project must publish its information in many places.

<a id="What_to_do_with_the_generated_DOAP_file"></a>

### What to do with the generated DOAP file?

After the Maven DOAP Plugin generates a DOAP file, read the [DOAP in Use](./examples/doap-in-use.html) page.

<a id="Why_are_not_all_developers.2Fcontributors_listed_in_the_DOAP_file_as_maintainer"></a>

### Why are not all developers and contributors listed as maintainers in the DOAP file?

Developers and contributors with a role that contains &quot;*emeritus*&quot; are no longer active in the project. They cannot be maintainers.
