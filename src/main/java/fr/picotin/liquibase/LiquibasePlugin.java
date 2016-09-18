package fr.picotin.liquibase;

import java.io.File;
import java.util.TreeSet;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import fr.picotin.liquibase.domain.PluginOptions;
import fr.picotin.liquibase.utils.LiquibaseUtils;

public class LiquibasePlugin extends AbstractPlugin {

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();

		final PluginOptions pluginOptions = getPluginOptions();

		getLog().info("Get Liquibase files in project... ");
		final TreeSet<File> files = LiquibaseUtils.getLiquibaseFiles(pluginOptions);
		getLog().info("Files found : " + files.size());

		getLog().info("Generate Changelog master...");
		LiquibaseUtils.createLiquibaseMasterChangelog(pluginOptions, files);
		getLog().info("Changelog master generated!");
	}

}
