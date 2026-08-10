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
3. [Why would I use DOAP when I have a POM?](#Why_would_I_use_DOAP_when_I_have_a_POM)
4. [What to do with the generated DOAP file?](#What_to_do_with_the_generated_DOAP_file)
5. [Why are not all developers/contributors listed in the DOAP file as maintainer?](#Why_are_not_all_developers.2Fcontributors_listed_in_the_DOAP_file_as_maintainer)

<a id="What_is_DOAP"></a>

### What is DOAP?

DOAP stands for &quot;Description of a Project&quot; and you can find out everything there is to
know about DOAP in its [homepage](http://usefulinc.com/doap) and in this
[article](http://www.ibm.com/developerworks/xml/library/x-osproj3/).

<a id="What_are_the_benefits_of_DOAP"></a>

### What are the benefits of DOAP?

The [Semantic Web](http://en.wikipedia.org/wiki/Semantic_web) provides
mechanisms to process data provided in a form that is easily processed by machines. Thus,
a DOAP is a machine readable document which facilitates projects research: it becomes much
easier to seek information in the mass of data of the Web, since the data have a given
foreseeable format.

<a id="Why_would_I_use_DOAP_when_I_have_a_POM"></a>

### Why would I use DOAP when I have a POM?

That's a very good question! The answer is that generating a DOAP file should take no
effort if you are using Maven DOAP Plugin and it helps disseminate project information
which can only be a good thing. Cataloging tools like [SWiK](http://swik.net/)
or like [DoapStore](http://doapstore.org/) can benefit from you generating DOAP
files and that can also only be a good thing. Even so, it is still important to spread as
much information about projects around as possible so there is no downside to creating
DOAP files.

<a id="What_to_do_with_the_generated_DOAP_file"></a>

### What to do with the generated DOAP file?

Maven DOAP plugin has generated a DOAP file, what's next? See
[DOAP in Use](./examples/doap-in-use.html) part.

<a id="Why_are_not_all_developers.2Fcontributors_listed_in_the_DOAP_file_as_maintainer"></a>

### Why are not all developers/contributors listed in the DOAP file as maintainer?

Developers/contributors having a role containing &quot;*emeritus*&quot; are no longer active in the
project and can't be maintainers therefore.
