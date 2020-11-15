package fr.picotin.liquibase.utils;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.TreeSet;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.StringUtils;

import fr.picotin.liquibase.comparator.LiquibaseComparator;
import fr.picotin.liquibase.constants.LiquibaseConstants;
import fr.picotin.liquibase.domain.PluginOptions;


public class LiquibaseUtils {

    /**
     * Create liquibase master changelog.
     *
     * @param pluginOptions The DTO with Maven plugin Options
     * @param changelogFiles The list of changelog files
     * @throws MojoExecutionException An Exception
     */
    public static void createLiquibaseMasterChangelog(final PluginOptions pluginOptions, final TreeSet<File> changelogFiles) throws MojoExecutionException {
        final String changelogMaster = "db.changelog-master-" + pluginOptions.sqlChangelogFormat + ".xml";
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(pluginOptions.filesLocation + File.separator + changelogMaster, "UTF-8");
            writer.println(LiquibaseConstants.XML_START.replace("${sqlType}", pluginOptions.sqlChangelogFormat).replace("${liquibaseVersion}",
                    pluginOptions.liquibaseVersion));

            for (final File file : changelogFiles) {
                writer.println("  <include file=\"" + file.getName() + "\" relativeToChangelogFile=\"true\"/>");
            }

            writer.println(LiquibaseConstants.XML_END);
        } catch (final FileNotFoundException e) {
            throw new MojoExecutionException(e.getMessage());
        } catch (final UnsupportedEncodingException e) {
            throw new MojoExecutionException(e.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * Get liquibase changelog files.
     *
     * @param pluginOptions The DTO with Maven plugin Options
     * @return The list of files
     * @throws MojoExecutionException An exception
     */
    public static TreeSet<File> getLiquibaseFiles(final PluginOptions pluginOptions) throws MojoExecutionException {

        final File dir = new File(pluginOptions.filesLocation);
        System.out.println("file location: " + pluginOptions.filesLocation);
        System.out.println("file filePattern: " + pluginOptions.filePattern);
        if (dir == null || !dir.isDirectory()) {
            throw new MojoExecutionException("Directory " + pluginOptions.filesLocation + " doesn't exists");
        }

        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (StringUtils.isNotEmpty(pluginOptions.filePattern)) {
                    return name.matches(pluginOptions.filePattern);
                }
                return true;
            }
        };

        final TreeSet<File> files = new TreeSet<File>(new LiquibaseComparator(pluginOptions.filePatternCustomSort));
        files.addAll(Arrays.asList(dir.listFiles(filter)));

        LiquibaseUtils.processCustomFilesToIgnore(pluginOptions, files);

        LiquibaseUtils.processCustomFilesToInsert(pluginOptions, files);

        return files;
    }

    /**
     * Process custom files to ignore in master changelog.
     *
     * @param pluginOptions The DTO with Maven plugin Options
     * @param files The files to add in master changelog
     */
    private static void processCustomFilesToIgnore(final PluginOptions pluginOptions, final TreeSet<File> files) {

        if (StringUtils.isNotEmpty(pluginOptions.customFilesToIgnore)) {
            final String[] customFiles = pluginOptions.customFilesToIgnore.split(";");
            for (final String customFile : customFiles) {

                final File file = new File(pluginOptions.filesLocation + File.separator + customFile);
                if (file.exists()) {
                    files.remove(file);
                }
            }
        }
    }

    /**
     * Process custom files to insert in master changelog.
     *
     * @param pluginOptions The DTO with Maven plugin Options
     * @param files The files to add in master changelog
     */
    private static void processCustomFilesToInsert(final PluginOptions pluginOptions, final TreeSet<File> files) {

        if (StringUtils.isNotEmpty(pluginOptions.customFilesToInsert)) {
            final String[] customFiles = pluginOptions.customFilesToInsert.split(";");
            for (final String customFile : customFiles) {

                final File file = new File(pluginOptions.filesLocation + File.separator + customFile);
                if (file.exists()) {
                    files.add(file);
                }
            }
        }
    }
}