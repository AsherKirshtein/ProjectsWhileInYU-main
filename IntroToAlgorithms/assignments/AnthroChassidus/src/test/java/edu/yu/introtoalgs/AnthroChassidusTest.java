package edu.yu.introtoalgs;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AnthroChassidusTest 
{ 
    @Test
    public void lowerBoundTest()
    {
        int[] array1 = new int[]{1,2,3,4,5,6,7,8,9,10};
        int[] array2 = new int[]{11,12,13,14,15,1,2,18,19,1};
        int population = 40;
        
        AnthroChassidus chassidus = new AnthroChassidus(population, array1, array2);

        int leastPossible = chassidus.getLowerBoundOnChassidusTypes();
        
        assertEquals(30, leastPossible);

    }   
    @Test
    public void shareChassidusTest()
    {
        int[] array1 = new int[]{1,2,3,4,5,6,7,8,9,10};
        int[] array2 = new int[]{11,12,13,14,15,1,2,18,19,1};
        int population = 40;
        
        AnthroChassidus chassidus = new AnthroChassidus(population, array1, array2);

        int shareWith1 = chassidus.nShareSameChassidus(1);
        int shareWith2 = chassidus.nShareSameChassidus(2);
        int shareWith3 = chassidus.nShareSameChassidus(3);
        int shareWith10 = chassidus.nShareSameChassidus(10);
        
        assertEquals(4, shareWith1);
        assertEquals(3, shareWith2);
        assertEquals(2, shareWith3);
        assertEquals(4, shareWith10);

    }
    @Test 
    public void constructorSpeedTest()
    {
        System.out.println("Constructor Speed Test:\n");
        for(int i = 10; i < 1000000000; i*=10)
        {
            int[] array1 = new int[i];
            int[] array2 = new int[i];
            for(int j = 0; j < i; j++)
            {
                array1[j] = j;
                array2[j] = i-j;
            }
            long startTime = System.nanoTime();
            AnthroChassidus chassidus = new AnthroChassidus(i+100, array1, array2);
            long endTime = System.nanoTime();
            long totalTime = endTime-startTime;
            double seconds = (double) totalTime/1000000000;
            
            System.out.println("With " + i + " values it took " +seconds+ " seconds.");
            
            int n = i;
            double logN = Math.log(i);
            double fn = seconds;
            double logFn = Math.log(fn);

            System.out.println("n = " + n + "\nLog(n) = " + logN + "\nF(n) = " + fn + "\nLog(F(n)) = " + logFn + "\n");
        }
    } 
    
    @Test 
    public void ShareChassidusSpeedTest()
    {
        System.out.println("Share Chassidus Speed Test:\n");
        for(int i = 10; i < 1000000000; i*=10)
        {
            int[] array1 = new int[i];
            int[] array2 = new int[i];
            for(int j = 0; j < i; j++)
            {
                array1[j] = j;
                array2[j] = i-j;
            }
            
            AnthroChassidus chassidus = new AnthroChassidus(i+100, array1, array2);
            long startTime = System.nanoTime();
            chassidus.nShareSameChassidus(10);
            long endTime = System.nanoTime();
            long totalTime = endTime-startTime;
            double seconds = (double) totalTime/1000000000;
            
            System.out.println("With " + i + " values it took " +seconds+ " seconds.");
            
            int n = i;
            double logN = Math.log(i);
            double fn = seconds;
            double logFn = Math.log(fn);

            System.out.println("n = " + n + "\nLog(n) = " + logN + "\nF(n) = " + fn + "\nLog(F(n)) = " + logFn + "\n");
        }
    }
    
    @Test 
    public void minimumSizeSpeedTest()
    {
        System.out.println("Lower Bound Speed Test:\n");
        for(int i = 10; i < 1000000000; i*=10)
        {
            int[] array1 = new int[i];
            int[] array2 = new int[i];
            for(int j = 0; j < i; j++)
            {
                array1[j] = j;
                array2[j] = i-j;
            }
            
            AnthroChassidus chassidus = new AnthroChassidus(i+100, array1, array2);
            long startTime = System.nanoTime();
            chassidus.getLowerBoundOnChassidusTypes();
            long endTime = System.nanoTime();
            long totalTime = endTime-startTime;
            double seconds = (double) totalTime/1000000000;
            
            System.out.println("With " + i + " values it took " +seconds+ " seconds.");
            
            int n = i;
            double logN = Math.log(i);
            double fn = seconds;
            double logFn = Math.log(fn);

            System.out.println("n = " + n + "\nLog(n) = " + logN + "\nF(n) = " + fn + "\nLog(F(n)) = " + logFn + "\n");
        }
    }    
}
