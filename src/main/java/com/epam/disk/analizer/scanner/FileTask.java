package com.epam.disk.analizer.scanner;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileTask extends RecursiveTask<Statistic> {
    private final Path dir;

    public FileTask(Path dir) {
        this.dir = dir;
    }

    @Override
    protected Statistic compute() {
        Statistic statistic = new Statistic();
        List<Path> files = getFiles(dir);
        List<Path>  folders = getFolders (dir);
        statistic.setFileCount(BigInteger.valueOf(files.stream().count()));
        statistic.setFolderCount(BigInteger.valueOf(folders.stream().count()));
        statistic.setFilesSize(BigInteger.valueOf(files.stream().map(Path::toFile)
                .mapToLong(File::length).sum()));
        for (Path folder:folders){
            FileTask fileTask = new FileTask(folder);
            Statistic fileTaskStatistic = fileTask.invoke();
            statistic.add(fileTaskStatistic);
        }
        return statistic;
    }

    private List<Path> getFiles(Path directoryPath) {
        List<Path> files = new ArrayList<>();
        try (Stream<Path> stream = Files.list(directoryPath)) {
            List<Path> paths = stream.collect(Collectors.toList());
            files = paths.stream().filter(s -> !Files.isDirectory(s)).collect(Collectors.toList());
        } catch (IOException ex) {
            System.out.println(ex.getCause());
        }
        return files;
    }

    private List<Path> getFolders(Path directoryPath) {
        List<Path> folders= new ArrayList<>();
        try (Stream<Path> stream = Files.list(directoryPath)) {
            List<Path> paths = stream.collect(Collectors.toList());
            folders = paths.stream().filter(Files::isDirectory).collect(Collectors.toList());
        } catch (IOException ex) {
            System.out.println(ex.getCause());
        }
        return folders;
    }
}
