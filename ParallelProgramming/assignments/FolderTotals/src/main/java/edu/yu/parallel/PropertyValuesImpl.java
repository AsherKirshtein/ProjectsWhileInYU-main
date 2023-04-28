package edu.yu.parallel;

public class PropertyValuesImpl implements PropertyValues {
    private int fileCount;
    private long byteCount;
    private int folderCount;

    public PropertyValuesImpl(int fileCount, long byteCount, int folderCount)
    {
        this.fileCount = fileCount;
        this.byteCount = byteCount;
        this.folderCount = folderCount;
    }

    @Override
    public int getFileCount() {
        return fileCount;
    }

    @Override
    public long getByteCount() {
        return byteCount;
    }

    @Override
    public int getFolderCount() {
        return folderCount;
    }
}
