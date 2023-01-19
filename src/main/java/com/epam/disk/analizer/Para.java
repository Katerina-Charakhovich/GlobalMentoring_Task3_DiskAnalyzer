package com.epam.disk.analizer;

import java.nio.file.Path;

public class Para {
    private Path fileName;
    private Long count;

    public Para(Path fileName, Long count) {
        this.fileName = fileName;
        this.count = count;
    }

    public Path getFileName() {
        return fileName;
    }

    public void setFileName(Path fileName) {
        this.fileName = fileName;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
