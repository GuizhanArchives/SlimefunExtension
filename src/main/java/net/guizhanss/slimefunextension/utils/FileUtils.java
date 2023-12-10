package net.guizhanss.slimefunextension.utils;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;

import lombok.experimental.UtilityClass;

@SuppressWarnings("ConstantConditions")
@UtilityClass
public final class FileUtils {
    // X. abcd
    public static final Pattern ORDERED_PATTERN = Pattern.compile("^\\d+\\.");
    public static final Comparator<String> COMPARATOR = (a, b) -> {
        if (a.equals(b)) return 0;
        // when both name start with a number, compare the numbers
        if (ORDERED_PATTERN.matcher(a).matches() && ORDERED_PATTERN.matcher(b).matches()) {
            int aNumber = Integer.parseInt(ORDERED_PATTERN.matcher(a).replaceAll("$0"));
            int bNumber = Integer.parseInt(ORDERED_PATTERN.matcher(b).replaceAll("$0"));
            return Integer.compare(aNumber, bNumber);
        }
        // if only one name is ordered, then the ordered folder comes first
        else if (ORDERED_PATTERN.matcher(a).matches()) return -1;
        else if (ORDERED_PATTERN.matcher(b).matches()) return 1;
            // otherwise, compare the folder names
        else return a.compareTo(b);
    };

    public static boolean isFolder(@Nonnull File folder) {
        Preconditions.checkArgument(folder != null, "The given File cannot be null");
        return folder.isDirectory();
    }

    @Nonnull
    public static List<String> getFolders(@Nonnull File folder) {
        Preconditions.checkArgument(folder != null, "The given File cannot be null");
        Preconditions.checkArgument(folder.isDirectory(), "The given File is not a folder");

        String[] folders = folder.list((dir, name) -> isFolder(dir));
        return folders == null ? List.of() : List.of(folders);
    }

    public static List<String> getYamlFiles(@Nonnull File folder) {
        Preconditions.checkArgument(folder != null, "The given File cannot be null");
        Preconditions.checkArgument(folder.isDirectory(), "The given File is not a folder");

        String[] files = folder.list((file, name) -> file.isFile()
                && (name.endsWith(".yml") || name.endsWith(".yaml"))
                && !name.startsWith("_") && !name.startsWith(".")
        );
        return files == null ? List.of() : List.of(files);
    }
}
