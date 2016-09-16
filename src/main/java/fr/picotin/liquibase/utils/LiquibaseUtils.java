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
import fr.picotin.liquibase.dto.PluginOptionsDTO;

public class LiquibaseUtils {

	/**
	 * Create liquibase master changelog.
	 *
	 * @param pluginOptionsDTO
	 *            The DTO with Maven plugin Options
	 * @param changelogFiles
	 *            The list of changelog files
	 * @throws MojoExecutionException
	 *             An Exception
	 */
	public void createLiquibaseMasterChangelog(final PluginOptionsDTO pluginOptionsDTO,
			final TreeSet<File> changelogFiles) throws MojoExecutionException {
		final String changelogMaster = "db.changelog-master-" + pluginOptionsDTO.sqlChangelogFormat + ".xml";
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(pluginOptionsDTO.filesLocation + File.separator + changelogMaster, "UTF-8");
			writer.println(LiquibaseConstants.XML_START.replace("${sqlType}", pluginOptionsDTO.sqlChangelogFormat)
					.replace("${liquibaseVersion}", pluginOptionsDTO.liquibaseVersion));

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
	 * @param pluginOptionsDTO
	 *            The DTO with Maven plugin Options
	 * @return The list of files
	 * @throws MojoExecutionException
	 *             An exception
	 */
	public TreeSet<File> getLiquibaseFiles(final PluginOptionsDTO pluginOptionsDTO) throws MojoExecutionException {

		final File dir = new File(pluginOptionsDTO.filesLocation);

		if (dir == null || !dir.isDirectory()) {
			throw new MojoExecutionException("Directory " + pluginOptionsDTO.filesLocation + " doesn't exists");
		}

		final FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (StringUtils.isNotEmpty(pluginOptionsDTO.filePattern)) {
					return name.matches(pluginOptionsDTO.filePattern);
				}
				return true;
			}
		};

		final TreeSet<File> files = Sets.newTreeSet(new LiquibaseComparator(pluginOptionsDTO.filePatternCustomSort));
		files.addAll(Arrays.asList(dir.listFiles(filter)));

		processCustomFiles(pluginOptionsDTO, files, Boolean.FALSE);

		processCustomFiles(pluginOptionsDTO, files, Boolean.TRUE);

		return files;
	}

	/**
	 * Process custom files (ignore or insert in master changelog)
	 *
	 * @param pluginOptionsDTO
	 *            The DTO with Maven plugin Options
	 * @param files
	 *            The files to add in master changelog
	 * @param insert
	 *            TRUE is insert file in TreeSet files, false otherwise
	 */
	private void processCustomFiles(final PluginOptionsDTO pluginOptionsDTO, final TreeSet<File> files,
			final Boolean insert) {
		final String customFilesStr = getCustomFilesToProcess(pluginOptionsDTO, insert);

		if (StringUtils.isNotEmpty(customFilesStr)) {
			final String[] customFiles = customFilesStr.split(";");
			for (final String customFile : customFiles) {

				final File file = new File(pluginOptionsDTO.filesLocation + File.separator + customFile);
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

	/**
	 * Get the custom files to process (ignore or insert in master changelog)
	 *
	 * @param pluginOptionsDTO
	 *            The DTO with Maven plugin Options
	 * @param insert
	 *            TRUE is insert file in TreeSet files, false otherwise
	 * @return The String of custom files
	 */
	private String getCustomFilesToProcess(final PluginOptionsDTO pluginOptionsDTO, final Boolean insert) {
		if (insert) {
			return pluginOptionsDTO.customFilesToInsert;
		} else {
			return pluginOptionsDTO.customFilesToIgnore;
		}
	}

}
