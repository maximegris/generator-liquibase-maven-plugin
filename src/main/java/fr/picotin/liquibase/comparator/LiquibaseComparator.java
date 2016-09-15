package fr.picotin.liquibase.comparator;


import java.io.File;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.plexus.util.StringUtils;


public class LiquibaseComparator implements Comparator<File> {

    private final String filePatternCustomSort;

    public LiquibaseComparator(final String filePatternCustomSort) {
        this.filePatternCustomSort = filePatternCustomSort;
    }

    public int compare(File f1, File f2) {

        String fileName1 = f1.getName();
        String fileName2 = f2.getName();

        if (StringUtils.isNotEmpty(this.filePatternCustomSort)) {

            Pattern pattern = Pattern.compile(this.filePatternCustomSort);

            Matcher fileMatcher1 = pattern.matcher(fileName1);
            Matcher fileMatcher2 = pattern.matcher(fileName2);

            Integer fileInt1 = null;
            Integer fileInt2 = null;

            if (fileMatcher1.find()) {
                fileInt1 = Integer.parseInt(fileMatcher1.group(0).replace(".", ""));
            }

            if (fileMatcher2.find()) {
                fileInt2 = Integer.parseInt(fileMatcher2.group(0).replace(".", ""));
            }

            if (fileInt1 != null && fileInt2 != null) {
                if (fileInt1 - fileInt2 < 0) {
                    return -1;
                } else if (fileInt1 - fileInt2 > 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }

        return fileName1.compareTo(fileName2);
    }

}
