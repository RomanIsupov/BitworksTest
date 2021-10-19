package ru.romanisupov;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileWorker {

    private FileWorker() {
        throw new IllegalStateException("Utility class");
    }

    public static void download(final String url, final String filePath) {
        try (BufferedInputStream inputStream = new BufferedInputStream(new URL(url).openStream())) {
            Files.copy(inputStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int[] readArrayFromFile(final String filePath) {
        List<Integer> fileContent = new ArrayList<>();
        try (Scanner scanner = new Scanner(filePath)) {
            while (scanner.hasNext()) {
                try {
                    fileContent.add(scanner.nextInt());
                }
                catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return fileContent.stream().mapToInt(i -> i).toArray();
    }

    public static void writeArrayToFile(int[] array, final String filePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write(array.toString());
        writer.close();
    }
}
