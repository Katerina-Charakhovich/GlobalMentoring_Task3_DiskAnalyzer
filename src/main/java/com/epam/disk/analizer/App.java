package com.epam.disk.analizer;

import com.epam.disk.analizer.scanner.Animation;
import com.epam.disk.analizer.scanner.FileTask;
import com.epam.disk.analizer.scanner.Statistic;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

@Command(
        name = "disk-analyser",
        description = "Disk analyser for command line ",
        version = "disk-analyser 1.0",
        mixinStandardHelpOptions = true
)
public class App implements Callable<Integer> {

    public App() {
    }

    public static void main(String[] args) {
        System.exit(
                new CommandLine(new App())
                        .setCaseInsensitiveEnumValuesAllowed(true)
                        .execute(args)
        );
    }

    @Option(names = {"-s", "--search"},
            description = "Search for the file name with the maximum number of letters from option in the name",
            defaultValue = "\u0000")
    private char searchChar;

    @Option(names = {"-plf", "--print-largest-files"},
            description = "Print Top-5 largest files by size in bytes",
            defaultValue = "0")
    private int countTopFiles;

    @Option(names = {"-as", "--average-size"},
            description = "The average file size in the specified directory or any its subdirectory.")
    private boolean isCalculateAverageFileSize;

    @Option(names = {"-cfsc", "--count-file-start-by-char"},
            description = "The divided files by first letter .")
    private boolean isCalculateCountFileStartByChar;

    @Option (names = {"-fs", "--file-size"},
    description = "File Scanner that scans a specified folder and provides detailed statistics such as file count, " +
            "folder count, Size (sum of all files size) ")
    private boolean isFileScanner;

    @Parameters(index = "0",
            description = "Path to directory")
    private Path path;

    @Override
    public Integer call() throws Exception {
        List<Path> files = null;
        if (searchChar != '\u0000') {
            files = getFiles(path);
            List <Para> filesByMostCharacterRepeat = filesByMostCharacterRepeat(files);
            filesByMostCharacterRepeat
                    .forEach(c -> System.out.println("Char: " + searchChar + ". Count the most repeat in files:"
                    + c.getCount() + ". The file name: " + c.getFileName()));
        }
        if (countTopFiles != 0) {
            if (files == null) {
                files = getFiles(path);
            }
            filesSortedBySize(countTopFiles, files)
                    .forEach(file -> System.out.println("File: " + file.getName() + "; size:" + file.length()));
        }
        if (isCalculateAverageFileSize) {
            if (files == null) {
                files = getFiles(path);
            }
            OptionalDouble avgFilesSize = averageFileSize(files);
            System.out.println("The average files size for " + path.toString() + ": " + avgFilesSize.getAsDouble());
        }
        if (isCalculateCountFileStartByChar) {
            if (files == null) {
                files = getFiles(path);
            }
            Map<Character, Long> dividedFilesGroupByFirstLetter = dividedFilesGroupByFirstLetter(files);
            dividedFilesGroupByFirstLetter
                    .forEach((key, value) -> System.out.println(value + "count files, started from char " + key));
        }

        if (isFileScanner){
            Animation.getInstance().start();

            FileTask fileTask = new FileTask(path);
            Statistic statistic =  new ForkJoinPool().invoke(fileTask);

            Animation.getInstance().stop();
            System.out.println("Total files count: " + statistic.getFileCount().longValue());
            System.out.println("Total folders count: " + statistic.getFolderCount().longValue());
            System.out.println("Total files size: " + statistic.getFilesSize().longValue());
        }
        return 1;
    }



    public List<Para> filesByMostCharacterRepeat(List<Path> files) {
        if (files.isEmpty()) {
            return null;
        }
        List<Para> calculateCharRepeat = files.stream()
                .filter(file -> !Files.isDirectory(file))
                .map(p -> new Para(p.getFileName(), p.getFileName().toString().chars()
                        .filter(c -> searchChar == (char) c).count()))
                .collect(Collectors.toList());
        Long countMostCharRepeatOptional = calculateCharRepeat
                .stream()
                .map(Para::getCount).distinct().max(Comparator.naturalOrder()).get();
        return calculateCharRepeat
                .stream()
                .filter(c -> Objects.equals(c.getCount(), countMostCharRepeatOptional))
                .collect(Collectors.toList());
    }

    public Map<Character, Long> dividedFilesGroupByFirstLetter(List<Path> files) {
        return files.isEmpty()
                ? null
                :files
                .stream()
                .filter(f->!Files.isDirectory(f))
                .map(Path::getFileName)
                .map(p -> p.toString().substring(0, 1))
                .map(String::toCharArray)
                .map(c -> c[0])
                .collect(groupingBy(c -> c, counting()));
    }


    private List<File> filesSortedBySize(int count, List<Path> files ) {
        return  files.isEmpty()
                ? null
                :files
                .stream()
                .filter(file -> !Files.isDirectory(file))
                .map(Path::toFile)
                .sorted(Comparator.comparingLong(File::length).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private OptionalDouble averageFileSize(List<Path> files) {
        return files.isEmpty()
                ? null
                : files.stream().map(Path::toFile)
                .mapToDouble(File::length)
                .average();
    }

    private List<Path> getFiles(Path directoryPath) {
        List<Path> files = new ArrayList<>();
        try (Stream<Path> stream = Files.list(directoryPath)) {
            List<Path> paths = stream.collect(Collectors.toList());
            files = paths.stream().filter(s -> !Files.isDirectory(s)).collect(Collectors.toList());
            List<Path> directories = paths.stream().filter(Files::isDirectory).collect(Collectors.toList());
            for (Path directory : directories
            ) {
                files.addAll(getFiles(directory));
            }
        } catch (IOException ex) {
            System.out.println(ex.getCause());
        }
        return files;
    }

    private List<Path> getDirectories(Path directoryPath) {
        List<Path> directories = new ArrayList<>();
        try (Stream<Path> stream = Files.list(directoryPath)) {
            List<Path> paths = stream.collect(Collectors.toList());
            directories = paths.stream().filter(Files::isDirectory).collect(Collectors.toList());
            for (Path directory : directories
            ) {
                directories.addAll(getDirectories(directory));
            }
        } catch (IOException ex) {
            System.out.println(ex.getCause());
        }
        return directories;
    }
}




