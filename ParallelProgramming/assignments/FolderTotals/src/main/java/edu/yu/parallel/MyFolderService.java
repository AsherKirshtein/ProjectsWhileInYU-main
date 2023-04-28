package edu.yu.parallel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.nio.file.*;

public class MyFolderService implements FolderService {
    
    private final String rootFolder;

    public MyFolderService(String rootFolder)
    {
        if(rootFolder == null)
        {
            throw new InvalidPathException("rootFolder cannot be null, must be a valid path", "rootFolder");
        }
        this.rootFolder = rootFolder;
    }

    @Override
    public PropertyValues getPropertyValuesSequential()
    {
        if(this.rootFolder == null || !new File(this.rootFolder).isDirectory())
        {
            throw new InvalidPathException(rootFolder, rootFolder + " is not a valid path");
        }
        int files = 0;
        int bytes = 0;
        int folders = 0;
        File file = new File(this.rootFolder);
        File[] childFiles = file.listFiles();

        if(childFiles == null)
        {
            return new PropertyValuesImpl(files, bytes, folders);
        }

        for(File childFile : childFiles)
        {
            if(childFile.isFile())
            {
                files++;
                bytes += childFile.length();
            }
            else if(childFile.isDirectory())
            {
                folders++;
                MyFolderService childService = new MyFolderService(childFile.getAbsolutePath());
                PropertyValues childValues = childService.getPropertyValuesSequential();
                files += childValues.getFileCount();
                bytes += childValues.getByteCount();
                folders += childValues.getFolderCount();
            }
        }   
        return new PropertyValuesImpl(files, bytes, folders);
    }

    @Override
    public Future<PropertyValues> getPropertyValuesParallel()
    {
        if(!new File(this.rootFolder).isDirectory())
        {
            throw new InvalidPathException(rootFolder, rootFolder + " is not a valid path");
        }
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());
        List<Future<PropertyValues>> futures = new ArrayList<>(); 
        return executor.submit(() -> {
            int files = 0;
            int bytes = 0;
            int folders = 0;
            File file = new File(this.rootFolder);
            File[] childFiles = file.listFiles();

            if(childFiles == null)
            {
                return new PropertyValuesImpl(files, bytes, folders);
            }

            for(File childFile : childFiles)
            {
                if(childFile.isFile())
                {
                    files++;
                    bytes += childFile.length();
                }
                else if(childFile.isDirectory())
                {
                    folders++;
                    MyFolderService childService = new MyFolderService(childFile.getAbsolutePath());
                    Future<PropertyValues> childValues = childService.getPropertyValuesParallel();
                    futures.add(childValues);
                }
            }
            for(Future<PropertyValues> future : futures)
            {
                PropertyValues childValues = future.get();
                files += childValues.getFileCount();
                bytes += childValues.getByteCount();
                folders += childValues.getFolderCount();
            }
            executor.shutdown();
            return new PropertyValuesImpl(files, bytes, folders);
        });
    }
}
