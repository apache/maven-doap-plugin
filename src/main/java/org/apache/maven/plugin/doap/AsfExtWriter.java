/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.plugin.doap;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.maven.model.Developer;
import org.apache.maven.plugin.doap.options.ASFExtOptions;
import org.apache.maven.plugin.doap.options.ASFExtOptionsUtil;
import org.apache.maven.plugin.doap.options.DoapOptions;
import org.apache.maven.plugin.doap.options.Standard;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.util.xml.XMLWriter;

class AsfExtWriter {

    private final DoapOptions doapOptions;

    private final ASFExtOptions asfExtOptions;

    private final Settings settings;

    private final UserMessages messages;

    private final ContributorWriter contributorWriter;

    AsfExtWriter(
            DoapOptions doapOptions,
            ASFExtOptions asfExtOptions,
            Settings settings,
            UserMessages messages,
            ContributorWriter contributorWriter) {
        this.doapOptions = doapOptions;
        this.asfExtOptions = asfExtOptions;
        this.settings = settings;
        this.messages = messages;
        this.contributorWriter = contributorWriter;
    }

    /**
     * Write the ASF extensions.
     *
     * @param writer  not null
     * @param project the Maven project, not null
     * @see <a href="https://projects.apache.org/ns/asfext#">
     *      https://projects.apache.org/ns/asfext#</a>
     * @see <a href="http://projects.apache.org/docs/pmc.html">http://projects.apache.org/docs/pmc.html</a>
     */
    void writeASFext(XMLWriter writer, MavenProject project) {
        if (!asfExtOptions.isIncluded()) {
            return;
        }

        DoapUtil.writeComment(writer, "ASF extension.");

        // asfext:pmc
        String pmc = DoapUtil.interpolate(asfExtOptions.getPmc(), project, settings);
        if (pmc != null && !pmc.isEmpty()) {
            DoapUtil.writeRdfResourceElement(writer, asfExtOptions.getXmlnsPrefix(), "pmc", pmc);
        } else {
            messages.addMessage(new String[] {"asfExtOptions", "pmc"}, null, UserMessages.REQUIRED_BY_ASF);
        }

        // asfext:name
        String name = DoapUtil.interpolate(asfExtOptions.getName(), project, settings);
        if (name != null && !name.isEmpty()) {
            if (!name.toLowerCase(Locale.ENGLISH).trim().startsWith("apache")) {
                name = "Apache " + name;
            }
            DoapUtil.writeElement(writer, asfExtOptions.getXmlnsPrefix(), "name", name);
        } else {
            messages.addMessage(new String[] {"asfExtOptions", "name"}, null, UserMessages.REQUIRED_BY_ASF);
        }

        String homepage = DoapUtil.interpolate(doapOptions.getHomepage(), project, settings);
        if (homepage != null && !homepage.isEmpty()) {
            try {
                new URL(homepage);

                DoapUtil.writeRdfResourceElement(writer, "foaf", "homepage", homepage);
            } catch (MalformedURLException e) {
                messages.addMessage(new String[] {"doapOptions", "homepage"}, homepage, UserMessages.INVALID_URL);
            }
        }

        // asfext:charter
        if (asfExtOptions.getCharter() == null || asfExtOptions.getCharter().isEmpty()) {
            messages.addMessage(new String[] {"asfExtOptions", "charter"}, null, UserMessages.REQUIRED_BY_ASF);
        } else {
            DoapUtil.writeElement(writer, asfExtOptions.getXmlnsPrefix(), "charter", asfExtOptions.getCharter());
        }

        // asfext:chair
        @SuppressWarnings("unchecked")
        List<Developer> developers = new ArrayList<>(project.getDevelopers());
        ContributorWriter.sortContributors(developers);

        if (asfExtOptions.getChair() != null && !asfExtOptions.getChair().isEmpty()) {
            DoapUtil.writeStartElement(writer, asfExtOptions.getXmlnsPrefix(), "chair");
            DoapUtil.writeStartElement(writer, "foaf", "Person");
            DoapUtil.writeStartElement(writer, "foaf", "name");
            writer.writeText(asfExtOptions.getChair());
            writer.endElement(); // foaf:name
            writer.endElement(); // foaf:Person
            writer.endElement(); // asfext:chair
        } else {
            Developer chair = ASFExtOptionsUtil.findChair(developers);
            if (chair != null) {
                contributorWriter.writeContributor(writer, chair, asfExtOptions.getXmlnsPrefix(), "chair");
                developers.remove(chair);
            } else {
                messages.addMessage(new String[] {"asfExtOptions", "chair"}, null, UserMessages.REQUIRED_BY_ASF);
            }
        }

        // asfext:member
        if (developers != null && !developers.isEmpty()) {
            List<Developer> pmcMembers = ASFExtOptionsUtil.findPMCMembers(developers);
            for (Developer pmcMember : pmcMembers) {
                contributorWriter.writeContributor(writer, pmcMember, asfExtOptions.getXmlnsPrefix(), "member");
            }
        }

        writeASFImplements(writer);

        Map<Object, String> map = asfExtOptions.getExtra();
        DoapUtil.writeExtra(writer, project, settings, "Extra ASFExt vocabulary.", map, asfExtOptions.getXmlnsPrefix());
    }

    /**
     * Write the ASF implements.
     *
     * @param writer not null
     * @see <a href="https://projects.apache.org/ns/asfext#">
     *      https://projects.apache.org/ns/asfext#</a>
     * @see <a href="http://projects.apache.org/docs/standards.html">http://projects.apache.org/docs/standards.html</a>
     */
    private void writeASFImplements(XMLWriter writer) {
        if (asfExtOptions.getStandards() == null || asfExtOptions.getStandards().isEmpty()) {
            return;
        }

        for (Standard standard : asfExtOptions.getStandards()) {
            DoapUtil.writeStartElement(writer, asfExtOptions.getXmlnsPrefix(), "implements");
            DoapUtil.writeStartElement(writer, asfExtOptions.getXmlnsPrefix(), "Standard");

            if (standard.getTitle() == null || standard.getTitle().isEmpty()) {
                messages.addMessage(
                        new String[] {"asfExtOptions", "standards", "title"}, null, UserMessages.REQUIRED_BY_ASF);
            } else {
                DoapUtil.writeElement(
                        writer,
                        asfExtOptions.getXmlnsPrefix(),
                        "title",
                        standard.getTitle().trim());
            }

            if (standard.getBody() == null || standard.getBody().isEmpty()) {
                messages.addMessage(
                        new String[] {"asfExtOptions", "standards", "body"}, null, UserMessages.REQUIRED_BY_ASF);
            } else {
                DoapUtil.writeElement(
                        writer,
                        asfExtOptions.getXmlnsPrefix(),
                        "body",
                        standard.getBody().trim());
            }

            if (standard.getId() == null || standard.getId().isEmpty()) {
                messages.addMessage(
                        new String[] {"asfExtOptions", "standards", "id"}, null, UserMessages.REQUIRED_BY_ASF);
            } else {
                DoapUtil.writeElement(
                        writer,
                        asfExtOptions.getXmlnsPrefix(),
                        "id",
                        standard.getId().trim());
            }

            if (standard.getUrl() != null && !standard.getUrl().isEmpty()) {
                String standardUrl = standard.getUrl().trim();
                try {
                    new URL(standardUrl);

                    DoapUtil.writeElement(writer, asfExtOptions.getXmlnsPrefix(), "url", standardUrl);
                } catch (MalformedURLException e) {
                    messages.addMessage(
                            new String[] {"asfExtOptions", "standards", "url"}, standardUrl, UserMessages.INVALID_URL);
                }
            }

            writer.endElement(); // asfext:Standard
            writer.endElement(); // asfext:implements
        }
    }
}
