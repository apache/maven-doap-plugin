package org.apache.maven.plugin.doap;

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

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.model.Contributor;
import org.apache.maven.model.Developer;
import org.apache.maven.model.License;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.i18n.I18N;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.xml.XMLWriter;

/**
 * Test {@link DoapUtil} class.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 */
public class DoapUtilTest
    extends PlexusTestCase
{
    /** {@inheritDoc} */
    protected void setUp()
        throws Exception
    {
        super.setUp();
    }

    /** {@inheritDoc} */
    protected void tearDown()
        throws Exception
    {
        super.tearDown();
    }

    /**
     * Test method for {@link DoapUtil#writeElement(XMLWriter, String, String, String)}.
     *
     * @throws Exception if any
     */
    public void testWriteElement()
        throws Exception
    {
        StringWriter w = new StringWriter();
        XMLWriter writer = new PrettyPrintXMLWriter( w );
        DoapUtil.writeElement( writer, null, "name", "value" );
        w.close();
        assertEquals( w.toString(), "<name>value</name>" );

        w = new StringWriter();
        writer = new PrettyPrintXMLWriter( w );
        try
        {
            DoapUtil.writeElement( writer, null, null, null );
            assertTrue( "Null not catched", false );
        }
        catch ( IllegalArgumentException e )
        {
            assertTrue( "IllegalArgumentException catched", true );
        }
        finally
        {
            w.close();
        }
    }

    /**
     * Test method for {@link DoapUtil#writeRdfResourceElement(XMLWriter, String, String, String)}.
     *
     * @throws Exception if any
     */
    public void testWriteRdfResourceElement()
        throws Exception
    {
        StringWriter w = new StringWriter();
        XMLWriter writer = new PrettyPrintXMLWriter( w );
        DoapUtil.writeRdfResourceElement( writer, null, "name", "value" );
        w.close();
        assertEquals( w.toString(), "<name " + DoapUtil.RDF_RESOURCE + "=\"value\"/>" );

        w = new StringWriter();
        writer = new PrettyPrintXMLWriter( w );
        try
        {
            DoapUtil.writeRdfResourceElement( writer, null, null, null );
            assertTrue( "Null not catched", false );
        }
        catch ( IllegalArgumentException e )
        {
            assertTrue( "IllegalArgumentException catched", true );
        }
        finally
        {
            w.close();
        }
    }

    /**
     * Test method for:
     * {@link DoapUtil#getContributorsWithDeveloperRole(I18N, List)}
     * {@link DoapUtil#getContributorsWithDocumenterRole(I18N, List)}
     * {@link DoapUtil#getContributorsWithHelperRole(I18N, List)}
     * {@link DoapUtil#getContributorsWithMaintainerRole(I18N, List)}
     * {@link DoapUtil#getContributorsWithTesterRole(I18N, List)}
     * {@link DoapUtil#getContributorsWithTranslatorRole(I18N, List)}
     * {@link DoapUtil#getContributorsWithUnknownRole(I18N, List)}
     *
     * @throws Exception if any
     */
    public void testDevelopersOrContributorsByDoapRoles()
        throws Exception
    {
        I18N i18n = (I18N) getContainer().lookup( I18N.ROLE );
        assertNotNull( i18n );
        assertNotNull( i18n.getBundle() );

        List<Contributor> developersOrContributors = new ArrayList<>();

        // One role
        Developer dev = new Developer();
        dev.setId( "dev1" );
        dev.addRole( "maintainer" );

        developersOrContributors.add( dev );

        assertTrue( DoapUtil.getContributorsWithDeveloperRole( i18n, developersOrContributors ).isEmpty() );
        assertTrue( DoapUtil.getContributorsWithDocumenterRole( i18n, developersOrContributors ).isEmpty() );
        assertTrue( DoapUtil.getContributorsWithHelperRole( i18n, developersOrContributors ).isEmpty() );
        assertFalse( DoapUtil.getContributorsWithMaintainerRole( i18n, developersOrContributors ).isEmpty() );
        assertTrue( DoapUtil.getContributorsWithTesterRole( i18n, developersOrContributors ).isEmpty() );
        assertTrue( DoapUtil.getContributorsWithTranslatorRole( i18n, developersOrContributors ).isEmpty() );
        assertTrue( DoapUtil.getContributorsWithUnknownRole( i18n, developersOrContributors ).isEmpty() );

        // Several roles
        developersOrContributors.clear();

        dev = new Developer();
        dev.setId( "dev1" );
        dev.addRole( " MAINTAINER" );
        dev.addRole( "tesTER " );
        dev.addRole( "blabla" );
        dev.addRole( "translato r" );

        developersOrContributors.add( dev );

        assertTrue( DoapUtil.getContributorsWithDeveloperRole( i18n, developersOrContributors ).isEmpty() );
        assertTrue( DoapUtil.getContributorsWithDocumenterRole( i18n, developersOrContributors ).isEmpty() );
        assertTrue( DoapUtil.getContributorsWithHelperRole( i18n, developersOrContributors ).isEmpty() );
        assertFalse( DoapUtil.getContributorsWithMaintainerRole( i18n, developersOrContributors ).isEmpty() );
        assertFalse( DoapUtil.getContributorsWithTesterRole( i18n, developersOrContributors ).isEmpty() );
        assertTrue( DoapUtil.getContributorsWithTranslatorRole( i18n, developersOrContributors ).isEmpty() );
        assertFalse( DoapUtil.getContributorsWithUnknownRole( i18n, developersOrContributors ).isEmpty() );

        // Skip emeritus role
        developersOrContributors.clear();

        dev = new Developer();
        dev.setId( "dev1" );
        dev.addRole( "maintainer" );
        dev.addRole( "unknown" );

        developersOrContributors.add( dev );

        int sizeBeforeEmeritus = DoapUtil.getContributorsWithUnknownRole( i18n, developersOrContributors).size();
        dev.addRole( " Emeritus" );

        assertTrue( DoapUtil.getContributorsWithUnknownRole( i18n, developersOrContributors).size() == sizeBeforeEmeritus );

    }

    /**
     * Test method for:
     * {@link DoapUtil#validate(java.io.File)}
     *
     * @throws Exception if any
     */
    public void testValidate()
        throws Exception
    {
        File doapFile = new File( getBasedir(), "src/test/resources/generated-doap-1.0.rdf" );
        assertFalse( DoapUtil.validate( doapFile ).isEmpty() );
    }

    /**
     * Test method for:
     * {@link DoapUtil#interpolate(String, MavenProject, org.apache.maven.settings.Settings)}
     *
     * @throws Exception if any
     */
    public void testInterpolate()
        throws Exception
    {
        License license = new License();
        license.setName( "licenseName" );
        license.setUrl( "licenseUrl" );

        List<Developer> developers = new ArrayList<>();
        Developer developer1 = new Developer();
        developer1.setId( "id1" );
        developer1.setName( "developerName1" );
        developers.add( developer1 );
        Developer developer2 = new Developer();
        developer2.setId( "id1" );
        developer2.setName( "developerName2" );
        developers.add( developer2 );

        MavenProject project = new MavenProject();
        project.setName( "projectName" );
        project.setDescription( "projectDescription" );
        project.setLicenses( Collections.singletonList( license ) );
        project.setDevelopers( developers );
        project.getProperties().put( "myKey", "myValue" );

        assertEquals( DoapUtil.interpolate( "${project.name}", project, null ), "projectName" );
        assertEquals( DoapUtil.interpolate( "my name is ${project.name}", project, null ), "my name is projectName" );
        assertEquals( DoapUtil.interpolate( "my name is ${project.invalid}", project, null ), "my name is ${project.invalid}" );
        assertEquals( DoapUtil.interpolate( "${pom.description}", project, null ), "projectDescription" );
        assertNull( DoapUtil.interpolate( "${project.licenses.name}", project, null ) );
        assertEquals( DoapUtil.interpolate( "${project.licenses[0].name}", project, null ), "licenseName" );
        assertNull( DoapUtil.interpolate( "${project.licenses[1].name}", project, null ) );
        assertNotNull( DoapUtil.interpolate( "${project.developers}", project, null ) );
        assertEquals( DoapUtil.interpolate( "${project.developers[0].name}", project, null ), "developerName1" );
        assertEquals( DoapUtil.interpolate( "${project.developers[1].name}", project, null ), "developerName2" );
        assertEquals( DoapUtil.interpolate( "${myKey}", project, null ), "myValue" );
    }
}
