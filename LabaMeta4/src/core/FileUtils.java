package core;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;

public class FileUtils {
    public static String[] readLines(String filePath) throws IOException {

        String content = Files.readString(Paths.get(filePath), StandardCharsets.UTF_8);
        return content.trim().split("\\s+");
    }

    public static void writeLines(String filePath, String[] data) throws IOException {
        String resultString = String.join(" ", data);

        Files.writeString(Paths.get(filePath), resultString, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}