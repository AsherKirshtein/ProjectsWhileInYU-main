package edu.yu.da;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class MultiMergeTest
{
    @Test
    public void quickTest()
    {
        int[][] array = new int[5][5];
        for(int i = 0; i < array.length; i++)
        {
            for(int k = 0; k < array[i].length; k++)
            {
                array[i][k] = getRandomInteger(50);
            }
        }
        MultiMerge merge = new MultiMerge();
        merge.merge(array);
    }
    @Test
    public void anotherTest()
    {
        int[][] array =  {{1,2,3,9,64}, {4,5,7,8,34}, {12,15,16,17,18}, {3,6,7,8,9}, {1,2,3,4,5}};
        MultiMerge merge = new MultiMerge() {
            
        };
        int[] merged = merge.merge(array);
        System.out.print("{");
        for(int i: merged)
        {
            System.out.print(i + ",");
        }
        System.out.print("}\n");
        int[] iMerge = merge.iterative(array);
        
        Boolean b = Arrays.equals(iMerge, merged);
        System.out.println("Merges: " + merge.getNCombinedMerges());
        assertTrue(b);
    }
    @Test
    public void evenAmountTest()
    {
        int[][] array =  {{1,2,3,9,64}, {4,5,7,8,34}, {12,15,16,17,18}, {3,6,7,8,9}};
        MultiMerge merge = new MultiMerge();
        int[] merged = merge.merge(array);
        System.out.print("{");
        for(int i: merged)
        {
            System.out.print(i + ",");
        }
        System.out.print("}\n");
        int[] iMerge = merge.iterative(array);
        Boolean b = Arrays.equals(iMerge, merged);
        System.out.println("Merges: " + merge.getNCombinedMerges());
        assertTrue(b);
    }
    @Test
    public void iterativeTest()
    {
        int[][] array = new int[5][5];
        for(int i = 0; i < array.length; i++)
        {
            for(int k = 0; k < array[i].length; k++)
            {
                array[i][k] = getRandomInteger(50);
                
            }
            Arrays.sort(array[i]);
            System.out.print("{");
            for(int n : array[i])
            {
                System.out.print(n + ", ");
            }
            System.out.println("}");
        }
        MultiMerge merge = new MultiMerge();
        int[] combined = merge.iterative(array);
        System.out.print("\n{");
        for(int l: combined)
        {
            System.out.print(l+", ");
        }
        System.out.print("}");

    }
    @Test
    public void mergeTest()
    {
        System.out.println("MergeTest: \n");
        System.out.println("Simple merges: \n");
        int[] ar1 = {1,3,4,7,8,9,10,11,13,27};
        int[] ar2 = {2,5,6,12,14,15,16,17,19,35};
        System.out.print("Array1: {");
        for(int i : ar1)
        {
            System.out.print(i + ",");
        }
        System.out.print("}\n");
        System.out.print("Array2: {");
        for(int i: ar2)
        {
            System.out.print(i + ",");
        }
        System.out.print("}\n");
        MultiMerge m = new MultiMerge() {};
        int[] merged = m.mergeArrays(ar1, ar2);
        
        System.out.print("Result: {");
        for(int i: merged)
        {
            System.out.print(i + ",");
        }
        System.out.print("}\n");

        System.out.println("Now Testing many merges...\n");
        for(int i = 0; i < 100; i++)
        {
            int[] array1 = new int[100];
            int[] array2 = new int[100];
            for(int k = 0; k < array1.length; k++)
            {
                array1[k] = getRandomInteger(1000);
                array2[k] = getRandomInteger(1000);
            }
            Arrays.sort(array1);
            Arrays.sort(array2);
            int[] shouldEqualMe = new int[array1.length*2];
            for(int l = 0; l < array1.length; l++)
            {
                shouldEqualMe[l] = array1[l];
                shouldEqualMe[l+array1.length] = array2[l];
            }
            Arrays.sort(shouldEqualMe);
            int[] combined = m.mergeArrays(array1, array2);

            //Comments below are for debugging purposes
            // System.out.println("Should be: ");
            // System.out.print("{");
            // for(int w: shouldEqualMe)
            // {
            //     System.out.print(w+",");
            // }
            // System.out.print("}\n");
            // System.out.println("Actual: ");
            // System.out.print("{");
            // for(int w: combined)
            // {
            //     System.out.print(w+",");
            // }
            // System.out.print("}\n");

            Boolean b = Arrays.equals(shouldEqualMe, combined);
            assertTrue(b);
            System.out.println("Passed " + (i+1) + " random merges");
        }
    }
     @Test
    public void speedTest()
    {
        for(int i = 10; i < 5000; i*=5)
        {
            //System.out.println("Creating array of size " + i + "*" + i);
            int[][] array1 = new int[i][i];

            for(int k = 0; k < array1.length; k++)
            {
                for(int j = 0; j < array1[k].length; j++)
                {
                    array1[k][j] = getRandomInteger(1000);
                }
                Arrays.sort(array1[k]);
            }
            MultiMerge m = new MultiMerge() {
                
            };

            long startTime = System.nanoTime();
            m.merge(array1);
            long endTime = System.nanoTime();
            long totalTime = endTime-startTime;
            double recursiveseconds = (double) totalTime/1000000000;   

            long startTime9 = System.nanoTime();
            m.iterative(array1);
            long endTime9 = System.nanoTime();
            long totalTime9 = endTime9-startTime9;
            double iterativeSeconds = (double) totalTime9/1000000000;
            System.out.println("+-----------------------------------------+");
            System.out.println("Iterative: " + iterativeSeconds + " seconds");
            System.out.println("Recursive: " + recursiveseconds + " seconds");
            System.out.println("+-----------------------------------------+");
        }
        
    }
    private static int getRandomInteger(int max)
    {
        double random = Math.floor(Math.random() * max);
        return (int)random;
    }    
}
