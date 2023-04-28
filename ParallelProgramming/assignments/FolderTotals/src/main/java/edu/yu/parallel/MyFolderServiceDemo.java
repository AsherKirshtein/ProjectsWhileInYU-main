package edu.yu.parallel;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MyFolderServiceDemo
{
    public static void main(String[] args)
    {
        long sequentialStart = System.currentTimeMillis();
        smallTest();
        largeTest();
        verylargeTest();
        long sequentialEnd = System.currentTimeMillis();

        
        try
        {
            long parallelStart = System.currentTimeMillis();
            smallParallelTest();
            largeParallelTest();
            veryLargeParallelTest();
            long parallelEnd = System.currentTimeMillis();
            System.out.println("\n+------------------------------------------+\n");
            System.out.println("Total Sequential time: " + (sequentialEnd - sequentialStart) + " milliseconds");
            System.out.println("Total Parallel time: " + (parallelEnd - parallelStart) + " milliseconds");
            double speedup = (sequentialEnd - sequentialStart) / (double)(parallelEnd - parallelStart);
            System.out.println("Parallel is " + speedup + "X faster than sequential");
        } 
        catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
        }
    }
    
    private static void smallTest()
    { 
        System.out.println("Small Sequential test\n");
        long startTime = System.nanoTime();
        MyFolderService service = new MyFolderService("C:\\Users\\asher\\Code\\YUGitHub\\Kirshtein_Asher_800610242\\ParallelProgramming\\assignments\\folderTotals");
        PropertyValues values = service.getPropertyValuesSequential();
        long endTime = System.nanoTime();
        System.out.println("files = " + values.getFileCount());
        System.out.println("bytes = " + values.getByteCount());
        System.out.println("folders = " + values.getFolderCount());
        long timeInSeconds = (endTime - startTime) / 1000000000;
        System.out.println("\nElapsed time = " + timeInSeconds + " seconds");
    }
    private static void largeTest()
    {
        System.out.println("Large Sequential test\n");
        long startTime = System.nanoTime();

        MyFolderService service = new MyFolderService("C:\\Users\\asher\\Code\\YUGitHub");
        PropertyValues values = service.getPropertyValuesSequential();
        long endTime = System.nanoTime();
        System.out.println("files = " + values.getFileCount());
        System.out.println("bytes = " + values.getByteCount());
        System.out.println("folders = " + values.getFolderCount());
        long timeInSeconds = (endTime - startTime) / 1000000000;
        System.out.println("\nElapsed time = " + timeInSeconds + " seconds");
    }

    private static void verylargeTest()
    {
        System.out.println("very Large Sequential test\n");
        long startTime = System.nanoTime();
        MyFolderService service = new MyFolderService("C:\\Users\\asher");
        PropertyValues values = service.getPropertyValuesSequential();
        System.out.println("files = " + values.getFileCount());
        System.out.println("bytes = " + values.getByteCount());
        System.out.println("folders = " + values.getFolderCount());
        long endTime = System.nanoTime();
        long timeInSeconds = (endTime - startTime) / 1000000000;
        System.out.println("\nElapsed time = " + timeInSeconds + " seconds");
    }

    private static void smallParallelTest() throws InterruptedException, ExecutionException
    {
        System.out.println("Small parallel test\n");
        long startTime = System.nanoTime();
        MyFolderService service = new MyFolderService("C:\\Users\\asher\\Code\\YUGitHub\\Kirshtein_Asher_800610242\\ParallelProgramming\\assignments\\folderTotals");
        Future<PropertyValues> values = service.getPropertyValuesParallel();
        long endTime = System.nanoTime();
        System.out.println("files = " + values.get().getFileCount());
        System.out.println("bytes = " + values.get().getByteCount());
        System.out.println("folders = " + values.get().getFolderCount());
        long timeInSeconds = (endTime - startTime) / 1000000000;
        System.out.println("\nElapsed time = " + timeInSeconds + " seconds");
    }

    private static void largeParallelTest() throws InterruptedException, ExecutionException
    {
        System.out.println("large parallel test\n");
        long startTime = System.nanoTime();
        MyFolderService service = new MyFolderService("C:\\Users\\asher\\Code\\YUGitHub");
        Future<PropertyValues> values = service.getPropertyValuesParallel();
        long endTime = System.nanoTime();
        System.out.println("files = " + values.get().getFileCount());
        System.out.println("bytes = " + values.get().getByteCount());
        System.out.println("folders = " + values.get().getFolderCount());
        long timeInSeconds = (endTime - startTime) / 1000000000;
        System.out.println("\nElapsed time = " + timeInSeconds + " seconds");
    }

    private static void veryLargeParallelTest() throws InterruptedException, ExecutionException
    {
        System.out.println("Very Large parallel test\n");
        long startTime = System.nanoTime();
        MyFolderService service = new MyFolderService("C:\\Users\\asher");
        Future<PropertyValues> values = service.getPropertyValuesParallel();
        long endTime = System.nanoTime();
        System.out.println("files = " + values.get().getFileCount());
        System.out.println("bytes = " + values.get().getByteCount());
        System.out.println("folders = " + values.get().getFolderCount());
        long timeInSeconds = (endTime - startTime) / 1000000000;
        System.out.println("\nElapsed time = " + timeInSeconds + " seconds");
    }
}
