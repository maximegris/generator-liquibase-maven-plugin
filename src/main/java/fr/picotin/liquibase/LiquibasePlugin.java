package fr.picotin.liquibase;


import java.io.File;
import java.util.TreeSet;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import fr.picotin.liquibase.domain.PluginOptions;
import fr.picotin.liquibase.utils.LiquibaseUtils;


@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, threadSafe = true)
public class LiquibasePlugin extends AbstractMojo {

    @Parameter(property = "liquibaseVersion", required = false, defaultValue = "3.1")
    protected String liquibaseVersion;

    @Parameter(property = "filesLocation", required = false, defaultValue = "src/main/resources/db/changelogs")
    protected String filesLocation;

    @Parameter(property = "sqlChangelogFormat", required = false, defaultValue = "postgresql")
    protected String sqlChangelogFormat;

    @Parameter(property = "filePattern", required = false, defaultValue = "*")
    protected String filePattern;

    @Parameter(property = "filePatternCustomSort", required = false, defaultValue = "")
    protected String filePatternCustomSort;

    @Parameter(property = "customFilesToIgnore", required = false, defaultValue = "")
    protected String customFilesToIgnore;

    @Parameter(property = "customFilesToInsert", required = false, defaultValue = "")
    protected String customFilesToInsert;

    public void execute() throws MojoExecutionException, MojoFailureException {
        this.displayConfigurationLogs();

        PluginOptions options = getPluginOptions();

        getLog().info("Get Liquibase files in project... ");
        final TreeSet<File> files = LiquibaseUtils.getLiquibaseFiles(options);
        getLog().info("Files found : " + files.size());

        getLog().info("Generate Changelog master...");
        LiquibaseUtils.createLiquibaseMasterChangelog(options, files);
        getLog().info("Changelog master generated!");
    }

    protected void displayConfigurationLogs() {
        getLog().info("Liquibase Version to match is : " + liquibaseVersion);
        getLog().info("Changelog location is : " + filesLocation);
        getLog().info("Sqlformat is : " + sqlChangelogFormat);
        getLog().info("File Pattern is : " + filePattern);
        getLog().info("File Pattern Custom Sort is : " + filePatternCustomSort);
        getLog().info("Custom Files to ignore are : " + customFilesToIgnore);
        getLog().info("Custom Files to insert are : " + customFilesToInsert);
    }

    protected PluginOptions getPluginOptions() {
        final PluginOptions options = new PluginOptions();

        options.liquibaseVersion = liquibaseVersion;
        options.filesLocation = filesLocation;
        options.sqlChangelogFormat = sqlChangelogFormat;
        options.filePattern = filePattern;
        options.filePatternCustomSort = filePatternCustomSort;
        options.customFilesToIgnore = customFilesToIgnore;
        options.customFilesToInsert = customFilesToInsert;

        return options;
    }

}