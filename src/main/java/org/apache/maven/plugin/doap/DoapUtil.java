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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFReader;
import org.apache.jena.rdf.model.impl.RDFDefaultErrorHandler;
import org.apache.maven.model.Contributor;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.apache.maven.wagon.proxy.ProxyUtils;
import org.codehaus.plexus.i18n.I18N;
import org.codehaus.plexus.interpolation.EnvarBasedValueSource;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.ObjectBasedValueSource;
import org.codehaus.plexus.interpolation.PrefixedObjectValueSource;
import org.codehaus.plexus.interpolation.PropertiesBasedValueSource;
import org.codehaus.plexus.interpolation.RegexBasedInterpolator;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.introspection.ClassMap;
import org.codehaus.plexus.util.xml.XMLWriter;
import org.codehaus.plexus.util.xml.XmlWriterUtil;

/**
 * Utility class for {@link DoapMojo} class.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @since 1.0
 */
public class DoapUtil {
    /** Email regex */
    private static final String EMAIL_REGEX =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /** Email pattern */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    /** Magic number to repeat '=' */
    private static final int REPEAT_EQUALS = 21;

    /** The default timeout used when fetching url, i.e. 2000. */
    public static final int DEFAULT_TIMEOUT = 2000;

    /** RDF resource attribute */
    protected static final String RDF_RESOURCE = "rdf:resource";

    /** RDF nodeID attribute */
    protected static final String RDF_NODE_ID = "rdf:nodeID";

    /** DoaP Organizations stored by name */
    private static Map<String, DoapUtil.Organization> organizations = new HashMap<>();

    /**
     * Write comments in the DOAP file header
     *
     * @param writer not null
     */
    public static void writeHeader(XMLWriter writer) {
        XmlWriterUtil.writeLineBreak(writer);

        XmlWriterUtil.writeCommentLineBreak(writer);
        XmlWriterUtil.writeComment(
                writer,
                StringUtils.repeat("=", REPEAT_EQUALS) + " - DO NOT EDIT THIS FILE! - "
                        + StringUtils.repeat("=", REPEAT_EQUALS));
        XmlWriterUtil.writeCommentLineBreak(writer);
        XmlWriterUtil.writeComment(writer, " ");
        XmlWriterUtil.writeComment(writer, "Any modifications will be overwritten.");
        XmlWriterUtil.writeComment(writer, " ");
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US);
        XmlWriterUtil.writeComment(
                writer,
                "Generated by Maven Doap Plugin " + getPluginVersion() + " on "
                        + dateFormat.format(new Date(System.currentTimeMillis())));
        XmlWriterUtil.writeComment(writer, "See: http://maven.apache.org/plugins/maven-doap-plugin/");
        XmlWriterUtil.writeComment(writer, " ");
        XmlWriterUtil.writeCommentLineBreak(writer);

        XmlWriterUtil.writeLineBreak(writer);
    }

    /**
     * Write comment.
     *
     * @param writer not null
     * @param comment not null
     * @throws IllegalArgumentException if comment is null or empty
     * @since 1.1
     */
    public static void writeComment(XMLWriter writer, String comment) throws IllegalArgumentException {
        if (comment == null || comment.isEmpty()) {
            throw new IllegalArgumentException("comment should be defined");
        }

        XmlWriterUtil.writeLineBreak(writer);
        XmlWriterUtil.writeCommentText(writer, comment, 2);
    }

    /**
     * @param writer not null
     * @param xmlnsPrefix could be null
     * @param name not null
     * @param value could be null. In this case, the element is not written.
     * @throws IllegalArgumentException if name is null or empty
     */
    public static void writeElement(XMLWriter writer, String xmlnsPrefix, String name, String value)
            throws IllegalArgumentException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name should be defined");
        }

        if (value != null) {
            writeStartElement(writer, xmlnsPrefix, name);
            writer.writeText(value);
            writer.endElement();
        }
    }

    /**
     * @param writer not null
     * @param xmlnsPrefix could be null
     * @param name not null
     * @param lang not null
     * @param value could be null. In this case, the element is not written.
     * @throws IllegalArgumentException if name is null or empty
     */
    public static void writeElement(XMLWriter writer, String xmlnsPrefix, String name, String value, String lang)
            throws IllegalArgumentException {
        if (lang == null || lang.isEmpty()) {
            writeElement(writer, xmlnsPrefix, name, value);
            return;
        }

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name should be defined");
        }

        if (value != null) {
            writeStartElement(writer, xmlnsPrefix, name);
            writer.addAttribute("xml:lang", lang);
            writer.writeText(value);
            writer.endElement();
        }
    }

    /**
     * @param writer not null
     * @param xmlnsPrefix could be null
     * @param name not null
     * @throws IllegalArgumentException if name is null or empty
     * @since 1.1
     */
    public static void writeStartElement(XMLWriter writer, String xmlnsPrefix, String name)
            throws IllegalArgumentException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name should be defined");
        }

        if (xmlnsPrefix != null && !xmlnsPrefix.isEmpty()) {
            writer.startElement(xmlnsPrefix + ":" + name);
        } else {
            writer.startElement(name);
        }
    }

    /**
     * @param writer not null
     * @param xmlnsPrefix could be null
     * @param name not null
     * @param value could be null. In this case, the element is not written.
     * @throws IllegalArgumentException if name is null or empty
     */
    public static void writeRdfResourceElement(XMLWriter writer, String xmlnsPrefix, String name, String value)
            throws IllegalArgumentException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name should be defined");
        }

        if (value != null) {
            writeStartElement(writer, xmlnsPrefix, name);
            writer.addAttribute(RDF_RESOURCE, value);
            writer.endElement();
        }
    }

    /**
     * @param writer not null
     * @param name not null
     * @param value could be null. In this case, the element is not written.
     * @throws IllegalArgumentException if name is null or empty
     */
    public static void writeRdfNodeIdElement(XMLWriter writer, String xmlnsPrefix, String name, String value)
            throws IllegalArgumentException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name should be defined");
        }

        if (value != null) {
            writeStartElement(writer, xmlnsPrefix, name);
            writer.addAttribute(RDF_NODE_ID, value);
            writer.endElement();
        }
    }

    /**
     * @param i18n the internationalization component
     * @param developersOrContributors list of <code>{@link Contributor}</code>
     * @return a none null list of <code>{@link Contributor}</code> which have a <code>developer</code> DOAP role.
     */
    public static List<Contributor> getContributorsWithDeveloperRole(
            I18N i18n, List<Contributor> developersOrContributors) {
        return filterContributorsByDoapRoles(i18n, developersOrContributors).get("developers");
    }

    /**
     * @param i18n the internationalization component
     * @param developersOrContributors list of <code>{@link Contributor}</code>
     * @return a none null list of <code>{@link Contributor}</code> which have a <code>documenter</code> DOAP role.
     */
    public static List<Contributor> getContributorsWithDocumenterRole(
            I18N i18n, List<Contributor> developersOrContributors) {
        return filterContributorsByDoapRoles(i18n, developersOrContributors).get("documenters");
    }

    /**
     * @param i18n the internationalization component
     * @param developersOrContributors list of <code>{@link Contributor}</code>
     * @return a none null list of <code>{@link Contributor}</code> which have an <code>helper</code> DOAP role.
     */
    public static List<Contributor> getContributorsWithHelperRole(
            I18N i18n, List<Contributor> developersOrContributors) {
        return filterContributorsByDoapRoles(i18n, developersOrContributors).get("helpers");
    }

    /**
     * @param i18n the internationalization component
     * @param developersOrContributors list of <code>{@link Contributor}</code>
     * @return a none null list of <code>{@link Contributor}</code> which have a <code>maintainer</code> DOAP role.
     */
    public static List<Contributor> getContributorsWithMaintainerRole(
            I18N i18n, List<Contributor> developersOrContributors) {
        return filterContributorsByDoapRoles(i18n, developersOrContributors).get("maintainers");
    }

    /**
     * @param i18n the internationalization component
     * @param developersOrContributors list of <code>{@link Contributor}</code>
     * @return a none null list of <code>{@link Contributor}</code> which have a <code>tester</code> DOAP role.
     */
    public static List<Contributor> getContributorsWithTesterRole(
            I18N i18n, List<Contributor> developersOrContributors) {
        return filterContributorsByDoapRoles(i18n, developersOrContributors).get("testers");
    }

    /**
     * @param i18n the internationalization component
     * @param developersOrContributors list of <code>{@link Contributor}</code>
     * @return a none null list of <code>{@link Contributor}</code> which have a <code>translator</code> DOAP role.
     */
    public static List<Contributor> getContributorsWithTranslatorRole(
            I18N i18n, List<Contributor> developersOrContributors) {
        return filterContributorsByDoapRoles(i18n, developersOrContributors).get("translators");
    }

    /**
     * @param i18n the internationalization component
     * @param developersOrContributors list of <code>{@link Contributor}</code>
     * @return a none null list of <code>{@link Contributor}</code> which have an <code>unknown</code> DOAP role.
     */
    public static List<Contributor> getContributorsWithUnknownRole(
            I18N i18n, List<Contributor> developersOrContributors) {
        return filterContributorsByDoapRoles(i18n, developersOrContributors).get("unknowns");
    }

    /**
     * Utility class for keeping track of DOAP organizations in the DoaP mojo.
     *
     * @author <a href="mailto:t.fliss@gmail.com">Tim Fliss</a>
     * @since 1.1
     */
    public static class Organization {
        private String name;

        private String url;

        private List<String> members = new LinkedList<>();

        public Organization(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public void addMember(String nodeId) {
            members.add(nodeId);
        }

        public List<String> getMembers() {
            return members;
        }
    }

    /**
     * put an organization from the pom file in the organization list.
     *
     * @param name from the pom file (e.g. Yoyodyne)
     * @param url from the pom file (e.g. http://yoyodyne.example.org/about)
     * @return the existing organization if a duplicate, or a new one.
     */
    public static DoapUtil.Organization addOrganization(String name, String url) {
        Organization organization = organizations.get(name);

        if (organization == null) {
            organization = new DoapUtil.Organization(name, url);
        }

        organizations.put(name, organization);

        return organization;
    }

    // unique RDF blank node index scoped internal to the DOAP file
    private static int nodeNumber = 1;

    /**
     * get a unique (within the DoaP file) RDF blank node ID
     *
     * @return the nodeID
     * @see <a href="http://www.w3.org/TR/rdf-syntax-grammar/#section-Syntax-blank-nodes">
     *      http://www.w3.org/TR/rdf-syntax-grammar/#section-Syntax-blank-nodes</a>
     */
    public static String getNodeId() {
        return "b" + nodeNumber++;
    }

    /**
     * get the set of Organizations that people are members of
     *
     * @return Map.EntrySet of DoapUtil.Organization
     */
    public static Set<Entry<String, DoapUtil.Organization>> getOrganizations() {
        return organizations.entrySet();
    }

    /**
     * Validate the given DOAP file.
     *
     * @param doapFile not null and should exist
     * @return an empty list if the DOAP file is valid, otherwise a list of errors.
     * @since 1.1
     */
    public static List<String> validate(File doapFile) {
        if (doapFile == null || !doapFile.isFile()) {
            throw new IllegalArgumentException("The DOAP file should exist");
        }

        Model model = ModelFactory.createDefaultModel();
        RDFReader r = model.getReader("RDF/XML");
        r.setProperty("error-mode", "strict-error");
        final List<String> errors = new ArrayList<>();
        r.setErrorHandler(new RDFDefaultErrorHandler() {
            @Override
            public void error(Exception e) {
                errors.add(e.getMessage());
            }
        });

        try {
            r.read(model, doapFile.toURI().toURL().toString());
        } catch (MalformedURLException e) {
            // ignored
        }

        return errors;
    }

    /**
     * @param str not null
     * @return <code>true</code> if the str parameter is a valid email, <code>false</code> otherwise.
     * @since 1.1
     */
    public static boolean isValidEmail(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        Matcher matcher = EMAIL_PATTERN.matcher(str);
        return matcher.matches();
    }

    /**
     * Fetch a URL.
     *
     * @param settings the user settings used to fetch the URL with an active proxy, if defined
     * @param url the URL to fetch
     * @throws IOException if any
     * @see #DEFAULT_TIMEOUT
     * @since 1.1
     */
    @SuppressWarnings("checkstyle:emptyblock")
    public static void fetchURL(Settings settings, URL url) throws IOException {
        if (url == null) {
            throw new IllegalArgumentException("The url is null");
        }

        if ("file".equals(url.getProtocol())) {
            // [ERROR] src/main/java/org/apache/maven/plugin/doap/DoapUtil.java:[474,53] (blocks) EmptyBlock: Empty try
            // block.
            // Test if file exists
            try (InputStream in = url.openStream()) {}
            return;
        }

        // http, https...
        HttpClient httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(DEFAULT_TIMEOUT);
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(DEFAULT_TIMEOUT);
        httpClient.getParams().setBooleanParameter(HttpClientParams.ALLOW_CIRCULAR_REDIRECTS, true);

        // Some web servers don't allow the default user-agent sent by httpClient
        httpClient
                .getParams()
                .setParameter(HttpMethodParams.USER_AGENT, "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");

        if (settings != null && settings.getActiveProxy() != null) {
            Proxy activeProxy = settings.getActiveProxy();

            ProxyInfo proxyInfo = new ProxyInfo();
            proxyInfo.setNonProxyHosts(activeProxy.getNonProxyHosts());

            if (StringUtils.isNotEmpty(activeProxy.getHost())
                    && !ProxyUtils.validateNonProxyHosts(proxyInfo, url.getHost())) {
                httpClient.getHostConfiguration().setProxy(activeProxy.getHost(), activeProxy.getPort());

                if (StringUtils.isNotEmpty(activeProxy.getUsername()) && activeProxy.getPassword() != null) {
                    Credentials credentials =
                            new UsernamePasswordCredentials(activeProxy.getUsername(), activeProxy.getPassword());

                    httpClient.getState().setProxyCredentials(AuthScope.ANY, credentials);
                }
            }
        }

        GetMethod getMethod = new GetMethod(url.toString());
        try {
            int status;
            try {
                status = httpClient.executeMethod(getMethod);
            } catch (SocketTimeoutException e) {
                // could be a sporadic failure, one more retry before we give up
                status = httpClient.executeMethod(getMethod);
            }

            if (status != HttpStatus.SC_OK) {
                throw new FileNotFoundException(url.toString());
            }
        } finally {
            getMethod.releaseConnection();
        }
    }

    /**
     * Interpolate a string with project and settings.
     *
     * @param value could be null
     * @param project not null
     * @param settings could be null
     * @return the value trimmed and interpolated or null if the interpolation doesn't work.
     * @since 1.1
     */
    public static String interpolate(String value, final MavenProject project, Settings settings) {
        if (project == null) {
            throw new IllegalArgumentException("project is required");
        }

        if (value == null) {
            return value;
        }

        if (!value.contains("${")) {
            return value.trim();
        }

        RegexBasedInterpolator interpolator = new RegexBasedInterpolator();
        try {
            interpolator.addValueSource(new EnvarBasedValueSource());
        } catch (IOException e) {
            // ignore
        }
        interpolator.addValueSource(new PropertiesBasedValueSource(System.getProperties()));
        interpolator.addValueSource(new PropertiesBasedValueSource(project.getProperties()));
        interpolator.addValueSource(new PrefixedObjectValueSource("project", project));
        interpolator.addValueSource(new PrefixedObjectValueSource("pom", project));
        interpolator.addValueSource(new ObjectBasedValueSource(project) {
            @Override
            public Object getValue(String expression) {
                try {
                    return ReflectionValueExtractor.evaluate(expression, project, true);
                } catch (Exception e) {
                    addFeedback("Failed to extract \'" + expression + "\' from: " + project, e);
                }

                return null;
            }
        });

        if (settings != null) {
            interpolator.addValueSource(new PrefixedObjectValueSource("settings", settings));
        }

        String interpolatedValue = value;
        try {
            interpolatedValue = interpolator.interpolate(value).trim();
        } catch (InterpolationException e) {
            // ignore
        }

        if (interpolatedValue.startsWith("${")) {
            return null;
        }

        return interpolatedValue;
    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    /**
     * Filter the developers/contributors roles by the keys from {@link I18N#getBundle()}. <br/>
     * I18N roles supported in DOAP, i.e. <code>maintainer</code>, <code>developer</code>, <code>documenter</code>,
     * <code>translator</code>, <code>tester</code>, <code>helper</code>. <br/>
     * <b>Note:</b> Actually, only English keys are used.
     *
     * @param i18n i18n component
     * @param developersOrContributors list of <code>{@link Contributor}</code>
     * @return a none null map with <code>maintainers</code>, <code>developers</code>, <code>documenters</code>,
     *         <code>translators</code>, <code>testers</code>, <code>helpers</code>, <code>unknowns</code> as keys and
     *         list of <code>{@link Contributor}</code> as value.
     */
    private static Map<String, List<Contributor>> filterContributorsByDoapRoles(
            I18N i18n, List<Contributor> developersOrContributors) {
        Map<String, List<Contributor>> returnMap = new HashMap<>(7);
        returnMap.put("maintainers", new ArrayList<>());
        returnMap.put("developers", new ArrayList<>());
        returnMap.put("documenters", new ArrayList<>());
        returnMap.put("translators", new ArrayList<>());
        returnMap.put("testers", new ArrayList<>());
        returnMap.put("helpers", new ArrayList<>());
        returnMap.put("unknowns", new ArrayList<>());

        if (developersOrContributors == null || developersOrContributors.isEmpty()) {
            return returnMap;
        }

        for (Contributor contributor : developersOrContributors) {
            List<String> roles = contributor.getRoles();

            if (roles != null && roles.size() != 0) {
                for (String role : roles) {
                    role = role.toLowerCase(Locale.ENGLISH);
                    if (role.contains(getLowerCaseString(i18n, "doap.maintainer"))) {
                        if (!returnMap.get("maintainers").contains(contributor)) {
                            returnMap.get("maintainers").add(contributor);
                        }
                    } else if (role.contains(getLowerCaseString(i18n, "doap.developer"))) {
                        if (!returnMap.get("developers").contains(contributor)) {
                            returnMap.get("developers").add(contributor);
                        }
                    } else if (role.contains(getLowerCaseString(i18n, "doap.documenter"))) {
                        if (!returnMap.get("documenters").contains(contributor)) {
                            returnMap.get("documenters").add(contributor);
                        }
                    } else if (role.contains(getLowerCaseString(i18n, "doap.translator"))) {
                        if (!returnMap.get("translators").contains(contributor)) {
                            returnMap.get("translators").add(contributor);
                        }
                    } else if (role.contains(getLowerCaseString(i18n, "doap.tester"))) {
                        if (!returnMap.get("testers").contains(contributor)) {
                            returnMap.get("testers").add(contributor);
                        }
                    } else if (role.contains(getLowerCaseString(i18n, "doap.helper"))) {
                        if (!returnMap.get("helpers").contains(contributor)) {
                            returnMap.get("helpers").add(contributor);
                        }
                    } else if (role.contains(getLowerCaseString(i18n, "doap.emeritus"))) {
                        // Don't add as developer nor as contributor as the person is no longer involved
                    } else {
                        if (!returnMap.get("unknowns").contains(contributor)) {
                            returnMap.get("unknowns").add(contributor);
                        }
                    }
                }
            } else {
                if (!returnMap.get("unknowns").contains(contributor)) {
                    returnMap.get("unknowns").add(contributor);
                }
            }
        }

        return returnMap;
    }

    /**
     * @param i18n not null
     * @param key not null
     * @return lower case value for the key in the i18n bundle.
     */
    private static String getLowerCaseString(I18N i18n, String key) {
        return i18n.getString("doap-person", Locale.ENGLISH, key).toLowerCase(Locale.ENGLISH);
    }

    /**
     * @return the Maven artefact version.
     */
    private static String getPluginVersion() {
        Properties pomProperties = new Properties();

        try (InputStream is = DoapUtil.class.getResourceAsStream(
                "/META-INF/maven/org.apache.maven.plugins/" + "maven-doap-plugin/pom.properties")) {
            if (is == null) {
                return "<unknown>";
            }

            pomProperties.load(is);

            return pomProperties.getProperty("version", "<unknown>");
        } catch (IOException e) {
            return "<unknown>";
        }
    }

    /**
     * Fork of {@link org.codehaus.plexus.interpolation.reflection.ReflectionValueExtractor} to care of list or arrays.
     */
    static class ReflectionValueExtractor {
        @SuppressWarnings("rawtypes")
        private static final Class[] CLASS_ARGS = new Class[0];

        private static final Object[] OBJECT_ARGS = new Object[0];

        /**
         * Use a WeakHashMap here, so the keys (Class objects) can be garbage collected. This approach prevents permgen
         * space overflows due to retention of discarded classloaders.
         */
        @SuppressWarnings("rawtypes")
        private static final Map<Class, ClassMap> CLASS_MAPS = new WeakHashMap<>();

        private ReflectionValueExtractor() {}

        public static Object evaluate(String expression, Object root) throws Exception {
            return evaluate(expression, root, true);
        }

        // TODO: don't throw Exception
        public static Object evaluate(String expression, Object root, boolean trimRootToken) throws Exception {
            // if the root token refers to the supplied root object parameter, remove it.
            if (trimRootToken) {
                expression = expression.substring(expression.indexOf('.') + 1);
            }

            Object value = root;

            // ----------------------------------------------------------------------
            // Walk the dots and retrieve the ultimate value desired from the
            // MavenProject instance.
            // ----------------------------------------------------------------------

            StringTokenizer parser = new StringTokenizer(expression, ".");

            while (parser.hasMoreTokens()) {
                String token = parser.nextToken();
                if (value == null) {
                    return null;
                }

                StringTokenizer parser2 = new StringTokenizer(token, "[]");
                int index = -1;
                if (parser2.countTokens() > 1) {
                    token = parser2.nextToken();
                    try {
                        index = Integer.parseInt(parser2.nextToken());
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                }

                final ClassMap classMap = getClassMap(value.getClass());

                final String methodBase = StringUtils.capitalizeFirstLetter(token);

                String methodName = "get" + methodBase;

                Method method = classMap.findMethod(methodName, CLASS_ARGS);

                if (method == null) {
                    // perhaps this is a boolean property??
                    methodName = "is" + methodBase;

                    method = classMap.findMethod(methodName, CLASS_ARGS);
                }

                if (method == null) {
                    return null;
                }

                value = method.invoke(value, OBJECT_ARGS);
                if (value == null) {
                    return null;
                }
                if (Collection.class.isAssignableFrom(value.getClass())) {
                    ClassMap classMap2 = getClassMap(value.getClass());

                    Method method2 = classMap2.findMethod("toArray", CLASS_ARGS);

                    value = method2.invoke(value, OBJECT_ARGS);
                }
                if (value.getClass().isArray()) {
                    value = ((Object[]) value)[index];
                }
            }

            return value;
        }

        private static ClassMap getClassMap(Class<? extends Object> clazz) {
            ClassMap classMap = CLASS_MAPS.get(clazz);

            if (classMap == null) {
                classMap = new ClassMap(clazz);

                CLASS_MAPS.put(clazz, classMap);
            }

            return classMap;
        }
    }
}
