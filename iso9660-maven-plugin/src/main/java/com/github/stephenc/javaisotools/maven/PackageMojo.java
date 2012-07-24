/*
 * Copyright (c) 2010. Stephen Connolly.
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.github.stephenc.javaisotools.maven;

import java.io.File;
import java.io.FileNotFoundException;

import com.github.stephenc.javaisotools.iso9660.ISO9660RootDirectory;
import com.github.stephenc.javaisotools.iso9660.StandardConfig;
import com.github.stephenc.javaisotools.iso9660.impl.ISOImageFileHandler;
import com.github.stephenc.javaisotools.rockridge.impl.RockRidgeConfig;
import com.github.stephenc.javaisotools.sabre.StreamHandler;
import com.github.stephenc.javaisotools.iso9660.ConfigException;
import com.github.stephenc.javaisotools.iso9660.impl.CreateISO;
import com.github.stephenc.javaisotools.iso9660.impl.ISO9660Config;
import com.github.stephenc.javaisotools.joliet.impl.JolietConfig;
import com.github.stephenc.javaisotools.sabre.HandlerException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;

/**
 * Creates an iso9660 image.
 *
 * @goal iso
 * @phase package
 */
public class PackageMojo extends AbstractMojo {

    /**
     * The directory to place the iso9660 image.
     *
     * @parameter default-value="${project.build.directory}"
     */
    private File outputDirectory;

    /**
     * The directory to capture the content from.
     *
     * @parameter default-value="${project.build.outputDirectory}"
     */
    private File inputDirectory;

    /**
     * The name of the file to create.
     *
     * @parameter default-value="${project.build.finalName}.${project.packaging}"
     */
    private String finalName;

    /**
     * The system id.
     *
     * @parameter
     */
    private String systemId;

    /**
     * The volume id.
     *
     * @parameter default-value="${project.artifactId}"
     */
    private String volumeId;

    /**
     * The volume set id.
     *
     * @parameter
     */
    private String volumeSetId;

    /**
     * The publisher.
     *
     * @parameter default-value="${project.organization.name}"
     */
    private String publisher;

    /**
     * The preparer.
     *
     * @parameter default-value="${project.organization.name}"
     */
    private String preparer;

    /**
     * The application.
     *
     * @parameter default-value="iso9660-maven-plugin"
     */
    private String application;

    /**
     * The volume sequence number.
     *
     * @parameter
     */
    private Integer volumeSequenceNumber;

    /**
     * The volume set size.
     */
    private Integer volumeSetSize;
    
    /**
	* The maven project.  This is injected by Maven.
	* 
	* @parameter expression="${project}"
	* @required
	* @readonly
	*/
	private MavenProject project;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (outputDirectory.isFile()) {
            throw new MojoExecutionException("Output directory: " + outputDirectory + " is a file");
        }
        outputDirectory.mkdirs();
        if (!outputDirectory.isDirectory()) {
            throw new MojoExecutionException("Could not create output directory: " + outputDirectory);
        }
        
        // Directory hierarchy, starting from the root
        ISO9660RootDirectory.MOVED_DIRECTORIES_STORE_NAME = "rr_moved";
        ISO9660RootDirectory root = new ISO9660RootDirectory();

        File isoFile = new File(outputDirectory, finalName);
        
        try {
            if (inputDirectory.isDirectory()) {
                root.addContentsRecursively(inputDirectory);
            }

            StreamHandler streamHandler = new ISOImageFileHandler(isoFile);
            CreateISO iso = new CreateISO(streamHandler, root);
            ISO9660Config iso9660Config = new ISO9660Config();
            iso9660Config.allowASCII(false);
            iso9660Config.setInterchangeLevel(1);
            iso9660Config.restrictDirDepthTo8(true);
            iso9660Config.forceDotDelimiter(true);
            applyConfig(iso9660Config);
            RockRidgeConfig rrConfig = new RockRidgeConfig();
            rrConfig.setMkisofsCompatibility(false);
            rrConfig.hideMovedDirectoriesStore(true);
            rrConfig.forcePortableFilenameCharacterSet(true);

            JolietConfig jolietConfig = new JolietConfig();
            jolietConfig.forceDotDelimiter(true);
            applyConfig(jolietConfig);

            iso.process(iso9660Config, rrConfig, jolietConfig, null);
        } catch (HandlerException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (ConfigException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
        
        project.getArtifact().setFile(isoFile);
    }

    private void applyConfig(StandardConfig config) throws ConfigException {
        if (StringUtils.isNotEmpty(systemId)) {
            config.setSystemID(systemId);
        }
        if (StringUtils.isNotEmpty(volumeId)) {
            config.setVolumeID(volumeId);
        }
        if (StringUtils.isNotEmpty(volumeSetId)) {
            config.setVolumeSetID(volumeSetId);
        }
        if (StringUtils.isNotEmpty(publisher)) {
            config.setPublisher(publisher);
        }
        if (StringUtils.isNotEmpty(preparer)) {
            config.setDataPreparer(preparer);
        }
        if (StringUtils.isNotEmpty(application)) {
            config.setApp(application);
        }
    }
}
