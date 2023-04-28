package edu.yu.introtoalgs;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.yu.introtoalgs.CountStringsFJ.ForkJoinSum;

public class CountStringsFJTest
{
    @Test
    public void correctStringTest()
    {
        String[] arr = {"SpongeBob", "Squidward", "Mr Krabs", "SpongeBob" ,"Plankton", "Patrick","SpongeBob", "Sandy", "Gary", "Larry", "Mrs. Puff", "SpongeBob", "Pearl"};
        String spongeBob = "SpongeBob";
        int threshold = 5;

        CountStringsFJ countStrings = new CountStringsFJ(arr, spongeBob, threshold);

        int amount = countStrings.doIt();
        System.out.println("\n\n+--------------------+\n");
        System.out.println("correct String Test:\n");
        for(String s: arr)
        {
            System.out.println(s);
        }
        System.out.println("\nString Searching for: " + spongeBob);
        System.out.println("expecting 4 counted\n");
        System.out.println("actual counted = " + 4);
        assertEquals(4, amount);
        System.out.println("Test Passed");
        System.out.println("+--------------------+\n");

        String[] arr2 = {"Grizzlies", "Kings", "Knicks", "Spurs" ,"Heat", "Magic","Suns", "Bucs", "Sonics", "Grizzlies", "Bulls", "Lakers", "Celtics"};
        String grizzlies = "Grizzlies";
        int threshold2 = arr2.length+1;

        CountStringsFJ countStrings2 = new CountStringsFJ(arr2, grizzlies, threshold2);
        for(String s: arr)
        {
            System.out.println(s);
        }
        int amount2 = countStrings2.doIt();
        System.out.println("\n\n+--------------------+\n");
        System.out.println("correct String Test(higher threshold):\n");
        for(String s: arr2)
        {
            System.out.println(s);
        }
        System.out.println("\nString Searching for: " + grizzlies);
        System.out.println("expecting 2 counted\n");
        System.out.println("actual counted = " + amount2);
        assertEquals(2, amount2);
        System.out.println("Test Passed");
        System.out.println("+--------------------+\n");
    }
    @Test
    public void randomStringsTest()
    {
        System.out.println("+--------------------+\n");
        System.out.println("\nRandom Array and String and Threshold Test: \n");
        String[] arr = new String[100];
        for(int i = 0; i < 100; i++)
        {
            arr[i] = getRandomString();
        }
        String str = getRandomString();
        int threshold = getRandomInteger(100)*2;

        CountStringsFJ countStringsFJ = new CountStringsFJ(arr, str, threshold+1);
        int count = 0;
        for(String s: arr)
        {
            if(s.equals(str))
            {
                count++;
            }
        }
        int amount = countStringsFJ.doIt();
        System.out.println("We are finding '" + str + "' in our random array. Threshold is " + threshold);
        System.out.println("We expect to find " + count + " our actual amount is " + amount);
        assertEquals(count, amount);
    }

   /*@Test
   public void speedTest()
   {   
    System.out.println("\n\nSpeed Test:\n+---------------+\n");
    double[] speedIncrease = new double[4];
    int timeIndex = 0;
    for(int i = 100000; i < 1000000000; i*=10)
    {
        String[] arr = new String[i];
        for(int j = 0; j < i; j++)
        {
            arr[j] = getRandomString();
        }
        String str = getRandomString();
        int threshold = 1;

        CountStringsFJ countStringsFJ = new CountStringsFJ(arr, str, threshold);
        long startTime = System.nanoTime();
        countStringsFJ.doIt();
        long endTime = System.nanoTime();
        long totalTime = endTime-startTime;
        double seconds = (double) totalTime/1000000000;

        System.out.println("With " + i + " values it took " + seconds + " seconds with the divide and conquer strategy");

        int threshold2 = arr.length +1;
        
        CountStringsFJ countStringsFJ2 = new CountStringsFJ(arr, str, threshold2);
        long startTime2 = System.nanoTime();
        countStringsFJ2.doIt();
        long endTime2 = System.nanoTime();
        long totalTime2 = endTime2-startTime2;
        double seconds2 = (double) totalTime2/1000000000;

        System.out.println("With " + i + " values it took " + seconds2 + " seconds with the sequential strategy");
        System.out.println(seconds2 + " / " + seconds + " = " + (seconds2/seconds) + "\n");
        speedIncrease[timeIndex] = seconds2/seconds;
        timeIndex++;
    }
    double average = 0;
    System.out.print("\nValues in speedArray: ");
    for(double d: speedIncrease)
    {
        average+=d;
        System.out.print(average + ", ");
    }
    System.out.println();
    average /= speedIncrease.length;
    System.out.println("Speed Increase: " + average);
    System.out.println("The Divide and Conquer strategy is " + average*100 + "% the speed of Sequential\n");
    System.out.println("+-------------------+");
}*/
    private static int getRandomInteger(int max)
    {
        double random = Math.floor(Math.random() * max);
        return (int)random;
    }
    
    private static String getRandomString()
    {
        int random = getRandomInteger(10);
        switch(random)
        {
            case(0): return "Hello";
            case(1): return "49ers";
            case(2): return "Blake Shelton";
            case(3): return "Captain Falcon";
            case(4): return "Allen Iverson";
            case(5): return "Cookies";
            case(6): return "November";
            case(7): return "Naruto";
            case(8): return "Ed Edd n' Eddy";
            case(9): return "Lamps";
            case(10): return "Sledding";
            case(11): return "Mario";
            case(12): return "CompSci is cool";
        }
        return "addMoreStrings";
    }
    @Test
    public void smallTest()
    {
        System.out.println("+--------------------+");
        System.out.println("small Test:\n");
        String[] arr = {"Oze","Asher", "Shmuel", "Asher", "Asher", "Azriel", "Max"};
        String asher = "Asher";

        CountStringsFJ countStrings = new CountStringsFJ(arr, asher, 5);
        int counted = countStrings.doIt();
        assertEquals(3, counted);
    }
    @Test
    public void getSequentialSpeed()
    {
        System.out.println("\n\n+--------------------+\n");
        System.out.println("Gather Sequential speed: \n");
        for(int arraySize = 1000000; arraySize < 1000000000; arraySize*=2)
        {
            System.out.println("\nTest with array size: " + arraySize + "\n");
            String[] array = new String[arraySize];
            String match = getRandomString();
            List<Double> times = new ArrayList<>();
            int index = 0;
            for(int j = 0; j < arraySize; j++)
            {
                String random = getRandomString();
                array[j] = random;
            }
            for(int threshold = 250; threshold < arraySize; threshold*=2)
            {
                long startAt = System.nanoTime();
                sequential(array, match);
                long finishAt = System.nanoTime();
                long tookTime = finishAt - startAt;
                double t = (double) tookTime/1000;
                times.add(t);
                System.out.println("Sequential speed: " + t);
            }
            double average = 0;
            for(double d : times)
            {
                average+=d;
            }
            average /= times.size();
            System.out.println("\nAverage speed: " + average);
        }    
    }
    @Test
    public void gatherDataTest()
    {
        System.out.println("\n\n+--------------------+\n");
        System.out.println("Gather Data Test: \n");
        for(int arraySize = 1000000; arraySize < 1000000000; arraySize*=2)
        {
            System.out.println("\nTest with array size: " + arraySize + "\n");
            String[] array = new String[arraySize];
            String match = getRandomString();
            List<Double> ratios = new ArrayList<>();
            List<Double> sequDoubles = new ArrayList<>();
            List<Double> paraleDoubles = new ArrayList<>();
            for(int j = 0; j < arraySize; j++)
            {
                String random = getRandomString();
                array[j] = random;
            }
            for(int threshold = 250; threshold < arraySize; threshold*=2)
            {
                long startAt = System.nanoTime();
                sequential(array, match);
                long finishAt = System.nanoTime();
                long tookTime = finishAt - startAt;
                double t = (double) tookTime/1000;
                
                CountStringsFJ countStringsFJ = new CountStringsFJ(array, match, threshold);

                long startTime = System.nanoTime();
                countStringsFJ.doIt();
                long endTime = System.nanoTime();
                long totalTime = endTime-startTime;
                double time = (double) totalTime/1000;
                //System.out.println("Threshold: "+ threshold);
                //System.out.print("\t Sequential Speed: " + t + " ms");
                //System.out.println("(" + time + "," + t + ")");
                double ratio = time/t;
                //System.out.println("(" + ratio + "," + threshold+ ")");
                System.out.println("(" + threshold + "," + ratio + ")");
                ratios.add(ratio);
                sequDoubles.add(t);
                paraleDoubles.add(time);
            }
            double average = 0;
            for(double d: ratios)
            {
                average+=d;
            }
            double sequAverage = 0;
            for(double a: sequDoubles)
            {
                sequAverage+=a;
            }
            double paralAverage = 0;
            for(double b: paraleDoubles)
            {
                paralAverage+=b;
            }
            paralAverage /= paraleDoubles.size();
            sequAverage /= sequDoubles.size();
            //System.out.println("Average Sequential speed: (" + arraySize + "," + sequAverage + ")");
            //System.out.println("Average Parallel speed: (" + arraySize + "," + paralAverage + ")");
            average /= ratios.size();
            //System.out.println("Threshhold with ratio: (" + average + "," + +")");
            //System.out.println("");
        }
    }
    @Test
    public void getParallelInfo()
    {
        System.out.println("\n\n+--------------------+\n");
        System.out.println("Gather Parallel Data: \n");
        for(int arraySize = 1000000; arraySize < 1000000000; arraySize*=2)
        {
            System.out.println("\nTest with array size: " + arraySize + "\n");
            String[] array = new String[arraySize];
            String match = getRandomString();
            List<Double> times = new ArrayList<>();
            for(int j = 0; j < arraySize; j++)
            {
                String random = getRandomString();
                array[j] = random;
            }
            for(int threshold = 250; threshold < arraySize; threshold*=2)
            { 
                CountStringsFJ countStringsFJ = new CountStringsFJ(array, match, threshold);

                long startTime = System.nanoTime();
                countStringsFJ.doIt();
                long endTime = System.nanoTime();
                long totalTime = endTime-startTime;
                double time = (double) totalTime/1000;
                System.out.println("Parallel speed: " + time);
                times.add(time);
            }
            double average = 0;
            for(double d: times)
            {
                average+=d;
            }
            average /= times.size();
            System.out.println("\nAverage speed: "+average);
        }
    }
    public int sequential(String[] array, String match)
    {
        int count = 0;
        for(int index = 0; index < array.length; index++)
        {
            if(array[index] == match)
            {
                count++;
            }
        }
        return count;
    }
}
