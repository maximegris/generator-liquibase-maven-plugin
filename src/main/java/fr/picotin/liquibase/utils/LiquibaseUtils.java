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

import com.google.common.collect.Sets;

import fr.picotin.liquibase.comparator.LiquibaseComparator;
import fr.picotin.liquibase.constants.LiquibaseConstants;


public class LiquibaseUtils {

    /**
     * Create liquibase master changelog.
     *
     * @param destFolder The destination folder of master changelog file
     * @param sqlType The sql type of master changelog file
     * @param liquibaseVersion The version of liquibase to target
     * @param changelogFiles The list of changelog files
     * @throws MojoExecutionException An Exception
     */
    public void createLiquibaseMasterChangelog(final String destFolder, final String sqlType, final String liquibaseVersion, final TreeSet<File> changelogFiles)
            throws MojoExecutionException {
        String changelogMaster = "db.changelog-master-" + sqlType + ".xml";
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(destFolder + File.separator + changelogMaster, "UTF-8");
            writer.println(LiquibaseConstants.XML_START.replace("${sqlType}", sqlType).replace("${liquibaseVersion}", liquibaseVersion));

            for (File file : changelogFiles) {
                writer.println("  <include file=\"" + file.getName() + "\" relativeToChangelogFile=\"true\"/>");
            }

            writer.println(LiquibaseConstants.XML_END);
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException(e.getMessage());
        } catch (UnsupportedEncodingException e) {
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
     * @param folder The folder to inspect
     * @param filePattern The sql type of liquibase file to search
     * @param filePatternCustomSort The custom pattern to sort files
     * @param customFilesToIgnore List of files to ignore in master changelog
     * @param customFilesToInsert List of additional files to insert
     * @return The list of files
     * @throws MojoExecutionException An exception
     */
    public TreeSet<File> getLiquibaseFiles(final String srcFolder, final String filePattern, final String filePatternCustomSort,
            final String customFilesToIgnore, final String customFilesToInsert) throws MojoExecutionException {

        File dir = new File(srcFolder);

        if (dir == null || !dir.isDirectory()) {
            throw new MojoExecutionException("Directory " + srcFolder + " doesn't exists");
        }

        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (StringUtils.isNotEmpty(filePattern)) {
                    return name.matches(filePattern);
                }
                return true;
            }
        };

        TreeSet<File> files = Sets.newTreeSet(new LiquibaseComparator(filePatternCustomSort));
        files.addAll(Arrays.asList(dir.listFiles(filter)));

        this.processCustomFiles(srcFolder, files, customFilesToIgnore, Boolean.FALSE);

        this.processCustomFiles(srcFolder, files, customFilesToInsert, Boolean.TRUE);

        return files;
    }

    /**
     * Process custom files (ignore or insert in master changelog)
     *
     * @param srcFolder The folder to inspect
     * @param files The files to add in master changelog
     * @param customFilesStr The custom files to process (string)
     * @param insert TRUE is insert file in TreeSet files, false otherwise
     */
    public void processCustomFiles(final String srcFolder, final TreeSet<File> files, final String customFilesStr, final boolean insert) {
        if (StringUtils.isNotEmpty(customFilesStr)) {
            String[] customFiles = customFilesStr.split(";");
            for (String customFile : customFiles) {

                File file = new File(srcFolder + File.separator + customFile);
                if (file.exists()) {
                    if (insert) {
                        files.add(file);
                    } else {
                        files.remove(file);
                    }
                }
            }
        }
    }

}
