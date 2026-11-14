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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.maven.model.Contributor;
import org.apache.maven.model.Developer;
import org.apache.maven.plugin.doap.options.DoapOptions;
import org.codehaus.plexus.i18n.I18N;
import org.codehaus.plexus.util.xml.XMLWriter;

class ContributorWriter {

    private final DoapOptions doapOptions;

    private final I18N i18n;

    private final UserMessages messages;

    ContributorWriter(DoapOptions doapOptions, I18N i18n, UserMessages messages) {
        this.doapOptions = doapOptions;
        this.i18n = i18n;
        this.messages = messages;
    }

    /**
     * Write all DOAP persons.
     *
     * @param writer       not null
     * @param contributors list of developers or contributors
     */
    void writeContributors(XMLWriter writer, List<Contributor> contributors) {
        if (contributors == null || contributors.isEmpty()) {
            return;
        }

        boolean isDeveloper =
                Developer.class.isAssignableFrom(contributors.get(0).getClass());
        if (isDeveloper) {
            DoapUtil.writeComment(writer, "Main committers.");
        } else {
            DoapUtil.writeComment(writer, "Contributed persons.");
        }

        List<Contributor> maintainers = DoapUtil.getContributorsWithMaintainerRole(i18n, contributors);
        List<Contributor> developers = DoapUtil.getContributorsWithDeveloperRole(i18n, contributors);
        List<Contributor> documenters = DoapUtil.getContributorsWithDocumenterRole(i18n, contributors);
        List<Contributor> translators = DoapUtil.getContributorsWithTranslatorRole(i18n, contributors);
        List<Contributor> testers = DoapUtil.getContributorsWithTesterRole(i18n, contributors);
        List<Contributor> helpers = DoapUtil.getContributorsWithHelperRole(i18n, contributors);
        List<Contributor> unknowns = DoapUtil.getContributorsWithUnknownRole(i18n, contributors);

        // By default, all developers are maintainers and contributors are helpers
        if (isDeveloper) {
            maintainers.addAll(unknowns);
        } else {
            helpers.addAll(unknowns);
        }

        // all alphabetical
        if (developers.size() != 0) {
            writeContributor(writer, developers, "developer");
        }
        if (documenters.size() != 0) {
            writeContributor(writer, documenters, "documenter");
        }
        if (helpers.size() != 0) {
            writeContributor(writer, helpers, "helper");
        }
        if (maintainers.size() != 0) {
            writeContributor(writer, maintainers, "maintainer");
        }
        if (testers.size() != 0) {
            writeContributor(writer, testers, "tester");
        }
        if (translators.size() != 0) {
            writeContributor(writer, translators, "translator");
        }
    }

    /**
     * Write a DOAP maintainer or developer or documenter or translator or tester or helper, for instance:
     * <p/>
     * <pre>
     *   &lt;maintainer&gt;
     *     &lt;foaf:Person&gt;
     *       &lt;foaf:name&gt;Emmanuel Venisse&lt;/foaf:name&gt;
     *       &lt;foaf:mbox rdf:resource="mailto:evenisse@apache.org"/&gt;
     *     &lt;/foaf:Person&gt;
     *   &lt;/maintainer&gt;
     * </pre>
     *
     * @param writer                   not null
     * @param developersOrContributors list of <code>{@link Developer}/{@link Contributor}</code>
     * @param doapType                 not null
     * @see #writeContributor(XMLWriter, Contributor, String, String)
     */
    void writeContributor(XMLWriter writer, List<Contributor> developersOrContributors, String doapType) {
        if (developersOrContributors == null || developersOrContributors.isEmpty()) {
            return;
        }

        sortContributors(developersOrContributors);

        for (Contributor developersOrContributor : developersOrContributors) {
            writeContributor(writer, developersOrContributor, doapOptions.getXmlnsPrefix(), doapType);
        }
    }

    /**
     * Writer a single developer or contributor.
     *
     * @param writer                 not null
     * @param xmlsPrefix             could be null
     * @param developerOrContributor not null, instance of <code>{@link Developer}/{@link Contributor}</code>
     * @param doapType               not null
     * @see <a href="http://usefulinc.com/ns/doap#maintainer">http://usefulinc.com/ns/doap#maintainer</a>
     * @see <a href="http://usefulinc.com/ns/doap#developer">http://usefulinc.com/ns/doap#developer</a>
     * @see <a href="http://usefulinc.com/ns/doap#documenter">http://usefulinc.com/ns/doap#documenter</a>
     * @see <a href="http://usefulinc.com/ns/doap#translator">http://usefulinc.com/ns/doap#translator</a>
     * @see <a href="http://usefulinc.com/ns/doap#tester">http://usefulinc.com/ns/doap#tester</a>
     * @see <a href="http://usefulinc.com/ns/doap#helper">http://usefulinc.com/ns/doap#helper</a>
     * @see <a href="http://xmlns.com/foaf/0.1/Person">http://xmlns.com/foaf/0.1/Person</a>
     * @see <a href="http://xmlns.com/foaf/0.1/name">http://xmlns.com/foaf/0.1/name</a>
     * @see <a href="http://xmlns.com/foaf/0.1/mbox">http://xmlns.com/foaf/0.1/mbox</a>
     * @see <a href="http://xmlns.com/foaf/0.1/Organization">http://xmlns.com/foaf/0.1/Organization</a>
     * @see <a href="http://xmlns.com/foaf/0.1/homepage">http://xmlns.com/foaf/0.1/homepage</a>
     */
    void writeContributor(XMLWriter writer, Contributor developerOrContributor, String xmlsPrefix, String doapType) {
        if (developerOrContributor == null) {
            return;
        }

        if (doapType == null || doapType.isEmpty()) {
            throw new IllegalArgumentException("doapType is required.");
        }

        String name = developerOrContributor.getName();
        String email = developerOrContributor.getEmail();
        String organization = developerOrContributor.getOrganization();
        String organizationUrl = developerOrContributor.getOrganizationUrl();
        String homepage = developerOrContributor.getUrl();
        String nodeId = null;

        // Name is required to write doap
        if (name == null || name.isEmpty()) {
            messages.addMessage(
                    new String[] {"project", "developers|contributors", "developer|contributor", "name"},
                    null,
                    UserMessages.REQUIRED);
            return;
        }

        if (!(organization == null || organization.isEmpty())
                || !(organizationUrl == null || organizationUrl.isEmpty())) {
            DoapUtil.Organization doapOrganization = DoapUtil.addOrganization(organization, organizationUrl);
            nodeId = DoapUtil.getNodeId();
            doapOrganization.addMember(nodeId);
        }

        DoapUtil.writeStartElement(writer, xmlsPrefix, doapType);
        DoapUtil.writeStartElement(writer, "foaf", "Person");
        if (nodeId != null && !nodeId.isEmpty()) {
            writer.addAttribute("rdf:nodeID", nodeId);
        }
        DoapUtil.writeStartElement(writer, "foaf", "name");
        writer.writeText(name);
        writer.endElement(); // foaf:name
        if (email != null && !email.isEmpty()) {
            if (DoapUtil.isValidEmail(email)) {
                DoapUtil.writeRdfResourceElement(writer, "foaf", "mbox", "mailto:" + email);
            } else {
                messages.addMessage(
                        new String[] {"project", "developers|contributors", "developer|contributor", "email"},
                        null,
                        UserMessages.INVALID_EMAIL);
            }
        }
        if ((organization != null && !organization.isEmpty())
                && (organizationUrl != null && !organizationUrl.isEmpty())) {
            try {
                new URL(organizationUrl);

                DoapUtil.addOrganization(organization, organizationUrl);
            } catch (MalformedURLException e) {
                messages.addMessage(
                        new String[] {"project", "developers|contributors", "developer|contributor", "organizationUrl"},
                        organizationUrl,
                        UserMessages.INVALID_URL);
            }
        }
        if (homepage != null && !homepage.isEmpty()) {
            try {
                new URL(homepage);

                DoapUtil.writeRdfResourceElement(writer, "foaf", "homepage", homepage);
            } catch (MalformedURLException e) {
                messages.addMessage(
                        new String[] {"project", "developers|contributors", "developer|contributor", "homepage"},
                        homepage,
                        UserMessages.INVALID_URL);
            }
        }
        writer.endElement(); // foaf:Person
        writer.endElement(); // doapType
    }

    /**
     * Sort Contributor by name or Developer by id.
     *
     * @param contributors not null
     * @since 1.1
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    static void sortContributors(List contributors) {
        Collections.sort(contributors, new Comparator<Contributor>() {
            public int compare(Contributor contributor1, Contributor contributor2) {
                if (contributor1 == contributor2) {
                    return 0;
                }

                if (contributor1 == null && contributor2 != null) {
                    return -1;
                }
                if (contributor1 != null && contributor2 == null) {
                    return +1;
                }

                if (Developer.class.isAssignableFrom(contributor1.getClass())
                        && Developer.class.isAssignableFrom(contributor2.getClass())) {
                    Developer developer1 = (Developer) contributor1;
                    Developer developer2 = (Developer) contributor2;

                    if (developer1.getId() == null && developer2.getId() != null) {
                        return -1;
                    }
                    if (developer1.getId() != null && developer2.getId() == null) {
                        return +1;
                    }

                    return developer1.getId().compareTo(developer2.getId());
                }

                if (contributor1.getName() == null && contributor2.getName() != null) {
                    return -1;
                }
                if (contributor1.getName() != null && contributor2.getName() == null) {
                    return +1;
                }
                return contributor1.getName().compareTo(contributor2.getName());
            }
        });
    }
}
