package fr.picotin.liquibase;

import java.io.File;
import java.util.TreeSet;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import fr.picotin.liquibase.dto.PluginOptionsDTO;
import fr.picotin.liquibase.utils.LiquibaseUtils;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, threadSafe = true)
public class LiquibasePlugin extends AbstractMojo {

	/**
	*
	*/
	@Parameter(property = "liquibaseVersion", required = false, defaultValue = "3.1")
	private String liquibaseVersion;

	/**
	 *
	 */
	@Parameter(property = "filesLocation", required = true, defaultValue = "src/main/resources/db/changelogs")
	private String filesLocation;

	/**
	 *
	 */
	@Parameter(property = "sqlChangelogFormat", required = true, defaultValue = "postgresql")
	private String sqlChangelogFormat;

	/**
	*
	*/
	@Parameter(property = "filePattern", required = false, defaultValue = "")
	private String filePattern;

	/**
	*
	*/
	@Parameter(property = "filePatternCustomSort", required = false, defaultValue = "")
	private String filePatternCustomSort;

	/**
	*
	*/
	@Parameter(property = "customFilesToIgnore", required = false, defaultValue = "")
	private String customFilesToIgnore;

	/**
	*
	*/
	@Parameter(property = "customFilesToInsert", required = false, defaultValue = "")
	private String customFilesToInsert;

	public void execute() throws MojoExecutionException, MojoFailureException {

		displayConfigurationLogs();
		final PluginOptionsDTO pluginOptionsDTO = setPluginOptionsDTO();

		getLog().info("Get Liquibase files in project... ");
		final TreeSet<File> files = LiquibaseUtils.getLiquibaseFiles(pluginOptionsDTO);
		getLog().info("Files found : " + files.size());

		getLog().info("Generate Changelog master...");
		LiquibaseUtils.createLiquibaseMasterChangelog(pluginOptionsDTO, files);
		getLog().info("Changelog master generated!");
	}

	/**
	 * Display configuration info in console.
	 */
	private void displayConfigurationLogs() {
		getLog().info("Liquibase Version to match is : " + liquibaseVersion);
		getLog().info("Changelog location is : " + filesLocation);
		getLog().info("Sqlformat is : " + sqlChangelogFormat);
		getLog().info("File Pattern is : " + filePattern);
		getLog().info("File Pattern Custom Sort is : " + filePatternCustomSort);
		getLog().info("Custom Files to ignore are : " + customFilesToIgnore);
		getLog().info("Custom Files to insert are : " + customFilesToInsert);
	}

	/**
	 * Set Maven plugin options.
	 *
	 * @return The DTO with Maven plugin Options.
	 */
	private PluginOptionsDTO setPluginOptionsDTO() {
		final PluginOptionsDTO dto = new PluginOptionsDTO();

		dto.liquibaseVersion = liquibaseVersion;
		dto.filesLocation = filesLocation;
		dto.sqlChangelogFormat = sqlChangelogFormat;
		dto.filePattern = filePattern;
		dto.filePatternCustomSort = filePatternCustomSort;
		dto.customFilesToIgnore = customFilesToIgnore;
		dto.customFilesToInsert = customFilesToInsert;

		return dto;

	}
}
