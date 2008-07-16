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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.maven.model.Contributor;
import org.apache.maven.model.Developer;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.XMLWriter;
import org.codehaus.plexus.util.xml.XmlWriterUtil;

/**
 * Utility class for DOAP mojo.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class DoapUtil
{
    /**
     * I18N roles supported in DOAP, i.e. <code>maintainer</code>, <code>developer</code>, <code>documenter</code>,
     * <code>translator</code>, <code>tester</code>, <code>helper</code>,
     * <br/>
     * <b>Note:</b> Actually, only English keys are used.
     */
    private static final ResourceBundle DEVELOPER_OR_CONTRIBUTOR_ROLES_I18N = ResourceBundle.getBundle( "doap-person",
                                                                                                        Locale.ENGLISH );

    /** RDF resource attribute */
    private static final String RDF_RESOURCE = "rdf:resource";

    /**
     * Write comments in the DOAP file header
     *
     * @param writer not null
     */
    public static void writeHeader( XMLWriter writer )
    {
        XmlWriterUtil.writeLineBreak( writer );

        XmlWriterUtil.writeCommentLineBreak( writer );
        XmlWriterUtil.writeComment( writer, StringUtils.repeat( "=", 21 ) + " - DO NOT EDIT THIS FILE! - "
            + StringUtils.repeat( "=", 21 ) );
        XmlWriterUtil.writeCommentLineBreak( writer );
        XmlWriterUtil.writeComment( writer, " " );
        XmlWriterUtil.writeComment( writer, "Any modifications will be overwritten." );
        XmlWriterUtil.writeComment( writer, " " );
        DateFormat dateFormat = DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT, Locale.US );
        XmlWriterUtil.writeComment( writer, "Generated by Maven Doap Plugin on "
            + dateFormat.format( new Date( System.currentTimeMillis() ) ) );
        XmlWriterUtil.writeComment( writer, "See: http://maven.apache.org/plugins/maven-doap-plugin/" );
        XmlWriterUtil.writeComment( writer, " " );
        XmlWriterUtil.writeCommentLineBreak( writer );

        XmlWriterUtil.writeLineBreak( writer );
    }

    /**
     * @param writer not null
     * @param name not null
     * @param value could be null. In this case, the element is not written.
     * @throws IllegalArgumentException if name is null or empty
     */
    public static void writeElement( XMLWriter writer, String name, String value )
        throws IllegalArgumentException
    {
        if ( StringUtils.isEmpty( name ) )
        {
            throw new IllegalArgumentException( "name should be defined" );
        }

        if ( value != null )
        {
            writer.startElement( name );
            writer.writeText( value );
            writer.endElement();
        }
    }

    /**
     * @param writer not null
     * @param name not null
     * @param value could be null. In this case, the element is not written.
     * @throws IllegalArgumentException if name is null or empty
     */
    public static void writeRdfResourceElement( XMLWriter writer, String name, String value )
        throws IllegalArgumentException
    {
        if ( StringUtils.isEmpty( name ) )
        {
            throw new IllegalArgumentException( "name should be defined" );
        }

        if ( value != null )
        {
            writer.startElement( name );
            writer.addAttribute( RDF_RESOURCE, value );
            writer.endElement();
        }
    }

    /**
     * Filter the developers/contributors role by the keys in {@link #DEVELOPER_OR_CONTRIBUTOR_ROLES_I18N}
     *
     * @param developersOrContributors list of <code>{@link Developer}/{@link Contributor}</code>
     * @return a none null map with <code>maintainers</code>, <code>developers</code>, <code>documenters</code>,
     * <code>translators</code>, <code>testers</code>, <code>helpers</code>, <code>unknowns</code> as keys and list of
     * <code>{@link Developer}/{@link Contributor}</code> as value.
     */
    public static Map filterDevelopersOrContributorsByDoapRoles( List developersOrContributors )
    {
        Map returnMap = new HashMap( 7 );
        returnMap.put( "maintainers", new ArrayList() );
        returnMap.put( "developers", new ArrayList() );
        returnMap.put( "documenters", new ArrayList() );
        returnMap.put( "translators", new ArrayList() );
        returnMap.put( "testers", new ArrayList() );
        returnMap.put( "helpers", new ArrayList() );
        returnMap.put( "unknowns", new ArrayList() );

        if ( developersOrContributors == null || developersOrContributors.size() == 0 )
        {
            return returnMap;
        }

        for ( Iterator it = developersOrContributors.iterator(); it.hasNext(); )
        {
            Object obj = it.next();

            List roles;
            if ( Developer.class.isAssignableFrom( obj.getClass() ) )
            {
                Developer developer = (Developer) obj;
                roles = developer.getRoles();
            }
            else
            {
                Contributor contributor = (Contributor) obj;
                roles = contributor.getRoles();
            }

            if ( roles != null && roles.size() != 0 )
            {
                for ( Iterator it2 = roles.iterator(); it2.hasNext(); )
                {
                    String role = (String) it2.next();

                    if ( role.toLowerCase().indexOf(
                                                     DEVELOPER_OR_CONTRIBUTOR_ROLES_I18N.getString( "doap.maintainer" )
                                                         .toLowerCase() ) != -1 )
                    {
                        ( (List) returnMap.get( "maintainers" ) ).add( obj );
                    }
                    else if ( role.toLowerCase().indexOf(
                                                           DEVELOPER_OR_CONTRIBUTOR_ROLES_I18N
                                                               .getString( "doap.developer" ).toLowerCase() ) != -1 )
                    {
                        ( (List) returnMap.get( "developers" ) ).add( obj );
                    }
                    else if ( role.toLowerCase().indexOf(
                                                           DEVELOPER_OR_CONTRIBUTOR_ROLES_I18N
                                                               .getString( "doap.documenter" ).toLowerCase() ) != -1 )
                    {
                        ( (List) returnMap.get( "documenters" ) ).add( obj );
                    }
                    else if ( role.toLowerCase().indexOf(
                                                           DEVELOPER_OR_CONTRIBUTOR_ROLES_I18N
                                                               .getString( "doap.translator" ).toLowerCase() ) != -1 )
                    {
                        ( (List) returnMap.get( "translators" ) ).add( obj );
                    }
                    else if ( role.toLowerCase().indexOf(
                                                           DEVELOPER_OR_CONTRIBUTOR_ROLES_I18N
                                                               .getString( "doap.tester" ).toLowerCase() ) != -1 )
                    {
                        ( (List) returnMap.get( "testers" ) ).add( obj );
                    }
                    else if ( role.toLowerCase().indexOf(
                                                           DEVELOPER_OR_CONTRIBUTOR_ROLES_I18N
                                                               .getString( "doap.helper" ).toLowerCase() ) != -1 )
                    {
                        ( (List) returnMap.get( "helpers" ) ).add( obj );
                    }
                    else
                    {
                        ( (List) returnMap.get( "unknowns" ) ).add( obj );
                    }
                }
            }
            else
            {
                ( (List) returnMap.get( "unknowns" ) ).add( obj );
            }
        }

        return returnMap;
    }
}