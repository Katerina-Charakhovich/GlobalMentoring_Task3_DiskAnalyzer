package com.epam.disk.analizer.scanner;
import java.math.BigInteger;


public class Statistic {
    private BigInteger fileCount = BigInteger.ZERO;
    private BigInteger folderCount = BigInteger.valueOf(-1);
    private BigInteger filesSize = BigInteger.ZERO;

    public Statistic(){

    }

    public void setFileCount(BigInteger fileCount){
        this.fileCount = fileCount;
    }

    public void setFolderCount(BigInteger folderCount){
        this.folderCount = folderCount;
    }

    public void setFilesSize (BigInteger filesSize){
        this.filesSize = filesSize;
    }

    public BigInteger getFileCount(){
        return this.fileCount;
    }

    public BigInteger getFolderCount(){
        return this.folderCount;
    }

    public BigInteger getFilesSize (){
        return this.filesSize;
    }

    public void addFileCount(long fileCount) {
        this.fileCount = this.fileCount.add(BigInteger.valueOf(fileCount));
    }

    public void addFolderCount(long folderCount) {
        this.folderCount = this.folderCount.add(BigInteger.valueOf(folderCount));
    }

    public void addFileSize(long filesSize) {
        this.filesSize = this.filesSize.add(BigInteger.valueOf(filesSize));
    }

    public void add(Statistic statistic) {
        addFileSize(statistic.getFilesSize().longValue());
        addFolderCount(statistic.getFolderCount().longValue());
        addFileCount(statistic.getFileCount().longValue());
    }
}
