package edu.yu.introtoalgs;

import edu.yu.introtoalgs.BigOMeasurable;

public class EstimateSecretAlgorithmsClient2 
{
    public static void main(String[] args)
    {
        testAlgorithm1();
        testAlgorithm2();
        testAlgorithm3();
        testAlgorithm4();
    }
    private static void testAlgorithm1()
    {
        BigOMeasurable alg1 = new SecretAlgorithm1();
        System.out.println("\n Secret Algorithm 1: \n\n");
        int n = 100;
        while(n < 50000)
        {
            alg1.setup(n);
            long startTime = System.nanoTime();
            alg1.execute();
            long endTime = System.nanoTime();
            long totalTime = endTime-startTime;
            double seconds = (double) totalTime/1000000000; 
            System.out.println("Time for " + n + " is: " + seconds + " Seconds.");
            n = n*2;
        }

    }
    private static void testAlgorithm2()
    {
        BigOMeasurable alg2 = new SecretAlgorithm2();
        System.out.println("\n\n Secret Algorithm 2: \n\n");
        int n = 100;
        while(n < 100000000)
        {
            alg2.setup(n);
            long startTime = System.nanoTime();
            alg2.execute();
            long endTime = System.nanoTime();
            long totalTime = endTime-startTime;
            double seconds = (double) totalTime/1000000000; 
            System.out.println("Time for " + n + " is: " + seconds + " Seconds.");
            n = n*2;
        }
    }
   private static void testAlgorithm3()
    {
        BigOMeasurable alg3 = new SecretAlgorithm3();
        System.out.println("\n\n Secret Algorithm 3: \n\n");
        int n = 100;
        while(n < 5000000)
        {
            alg3.setup(n);
            long startTime = System.nanoTime();
            alg3.execute();
            long endTime = System.nanoTime();
            long totalTime = endTime-startTime;
            double seconds = (double) totalTime/1000000000; 
            System.out.println("Time for " + n + " is: " + seconds + " Seconds.");
            n = n*2;
        }
    }
    private static void testAlgorithm4()
    {
        BigOMeasurable alg4 = new SecretAlgorithm4();
        System.out.println("\n\n Secret Algorithm 4: \n\n");
        int n = 1000000;
        while(n < 1000000000)
        {
            alg4.setup(n);
            long startTime = System.nanoTime();
            alg4.execute();
            long endTime = System.nanoTime();
            long totalTime = endTime-startTime;
            double seconds = (double) totalTime/1000000000; 
            System.out.println("Time for " + n + " is: " + seconds + " Seconds.");
            n = n*2;
        }
    }
}