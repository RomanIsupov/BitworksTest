package ru.romanisupov.utils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class FileWorker {

    private FileWorker() {
        throw new IllegalStateException("Utility class");
    }

    public static void download(final String url, final String filePath) {
        System.out.println("Downloading numbers from " + url + " to " + filePath);
        try (BufferedInputStream inputStream = new BufferedInputStream(new URL(url).openStream())) {
            Files.copy(inputStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int[] readArrayFromFile(final String filePath){
        System.out.println("Reading array from " + filePath);
        List<Integer> fileContent = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(filePath))) {
            scanner.useDelimiter(", ");
            String temp;
            while (scanner.hasNext()) {
                temp = scanner.next();
                try {
                    fileContent.add(Integer.parseInt(temp));
                }
                catch (NumberFormatException e) {
                }
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fileContent.stream().mapToInt(i -> i).toArray();
    }

    public static void writeArrayToFile(int[] array, final String filePath) {
        System.out.println("Writing array to " + filePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(Arrays.toString(array));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
