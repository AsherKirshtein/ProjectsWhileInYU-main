package edu.yu.introtoalgs;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import edu.yu.introtoalgs.MergeAnInterval.Interval;

public class MergeAnIntervalTest
{
    @Test
    public void mergeTest()
    {
        Set<Interval> intervals = new HashSet<>();

        for(int i = 0; i < 25; i++)
        {
            int a = getRandomInteger(i);
            int b = a + 1 + getRandomInteger(i);

            Interval inter = new Interval(a, b);
            intervals.add(inter);
        }

        for(int i = 100; i < 125; i++)
        {
            int a = getRandomInteger(i);
            int b = a + 1 + getRandomInteger(i);

            Interval inter = new Interval(a, b);
            intervals.add(inter);
        }

        for(int i = 200; i < 214; i++)
        {
            int a = getRandomInteger(i);
            int b = a + 1 + getRandomInteger(i);

            Interval inter = new Interval(a, b);
            intervals.add(inter);
        }
        System.out.println("Unsorted Set: \n");
        int line = 1;
        for(Interval in: intervals)
        {
            System.out.println(line + ". [" + in.left + "," + in.right + "]");
            line++;
        }
        Interval n = new Interval(0, 1);
        Set<Interval> merged = MergeAnInterval.merge(intervals,n);
        System.out.println("________________ \n");
        System.out.println("Sorted Set: \n");
        line = 1;
        for(Interval in: merged)
        {
            System.out.println(line + ". [" + in.left + "," + in.right + "]");
            line++;
        }
    }
    
    @Test
    public void canMergeTest()
    {
        Interval a = new Interval(0, 1);
        Interval b = new Interval(0, 2);
        Interval c = new Interval(6, 9);
        Interval d = new Interval(1, 6);

        assertTrue(MergeAnInterval.canMerge(a,b));
        assertTrue(MergeAnInterval.canMerge(d,a));
        assertTrue(MergeAnInterval.canMerge(d,c));
        assertTrue(MergeAnInterval.canMerge(c,d));
        assertFalse(MergeAnInterval.canMerge(a,c));
    }
    
    @Test
    public void mergeTwo()
    {
        System.out.println("\n \nMerging 2 intervals test: \n \n");
        
        Interval a = new Interval(0, 1);
        Interval b = new Interval(0, 2);
        Interval c = new Interval(6, 9);
        Interval d = new Interval(1, 6);

        Interval ab = new Interval(0,2);
        Interval cd = new Interval(1,9);
        Interval bd = new Interval(0,6);

        Interval merged1 = MergeAnInterval.getInterval(a,b);
        Interval merged2 = MergeAnInterval.getInterval(c,d);
        Interval merged3 = MergeAnInterval.getInterval(d,b);

        System.out.println("Merge [0,1] and [0,2]: [" + merged1.left + "," + merged1.right + "]");
        System.out.println("Merge [6,9] and [1,6]: [" + merged2.left + "," + merged2.right + "]");
        System.out.println("Merge [1,6] and [0,2]: [" + merged3.left + "," + merged3.right + "]");
        System.out.println("\n");
        assertTrue(ab.equals(merged1));
        assertTrue(cd.equals(merged2));
        assertTrue(bd.equals(merged3));
        System.out.println("+--------------------+");
    } 
    
    @Test 
    public void speedTest()
    {
        System.out.println("\n\nSpeed Test:\n+---------------+\n");
        double[] logY = new double[10];
        double[] logX = new double[10];
        int logArrayIndex = 0; 
        for(int i = 10; i < 100000000; i*=10)
        {
            Set<Interval> intervals = new HashSet<Interval>();
            for(int j = 0; j < i; j++)
            {
                int a = getRandomInteger(j);
                int b = a + 1 + getRandomInteger(j);
                Interval inter = new Interval(a, b);
                intervals.add(inter);
            }
            Interval x = new Interval(0, 1);
            long startTime = System.nanoTime();
            MergeAnInterval.merge(intervals,x);
            long endTime = System.nanoTime();
            long totalTime = endTime-startTime;
            double seconds = (double) totalTime/1000000000;
            
            int n = i;
            double logN = Math.log(i);
            double fn = seconds;
            double logFn = Math.log(fn);

            System.out.println("n = " + n + "\nLog(n) = " + logN + "\nF(n) = " + fn + "\nLog(F(n)) = " + logFn + "\n");
            logX[logArrayIndex] = logN;
            logY[logArrayIndex] = logFn;
            logArrayIndex++;
            if(fn > 60)
            {
                break;
            }
        }
        double x = logX[2] - logX[logArrayIndex];
        double y = logY[logArrayIndex] - logY[2];
        double slope = x/y;
        System.out.println("x = "+x);
        System.out.println("y = "+y);
        System.out.println("\nAverage Slope of the Log Graph: " + slope);
        System.out.println("+-------------------+");
    }
    private static int getRandomInteger(int max)
    {
        double random = Math.floor(Math.random() * max);
        return (int)random;
    } 
}
