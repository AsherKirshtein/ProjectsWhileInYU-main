package edu.yu.da;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class MaximizePayoutTest
{
    @Test
    public void simpleTest()
    {
        List<Long> A = new ArrayList<>();
        List<Long> B = new ArrayList<>();
        A.add((long) 100);
        A.add((long) 200);
        B.add((long) 1);
        B.add((long) 6);

        long expected = 0;
        
        expected += Math.pow(200, 6);
        expected += Math.pow(100, 1);

        Collections.unmodifiableList(A);
        Collections.unmodifiableList(B);
        
        MaximizePayout payout = new MaximizePayout();
        long actualPayout = payout.max(A, B);

        System.out.println("Expected: " + expected + "\nActual: " + actualPayout);
        assertEquals(expected, actualPayout);
    }
    @Test
    public void anotherSimpleTest()
    {
        List<Long> A = new ArrayList<>();
        List<Long> B = new ArrayList<>();
        A.add((long) 69);
        A.add((long) 43);
        A.add((long) 45);
        A.add((long) 1000);
        A.add((long) 2);

        B.add((long) 1);
        B.add((long) 9);
        B.add((long) 5);
        B.add((long) 2);
        B.add((long) 6);

        long expected = 0;

        expected += Math.pow(1000, 9);
        expected += Math.pow(69, 6);
        expected += Math.pow(45, 5);
        expected += Math.pow(43, 2);
        expected += Math.pow(2, 1);

        Collections.unmodifiableList(A);
        Collections.unmodifiableList(B);
        
        MaximizePayout payout = new MaximizePayout();
        long actualPayout = payout.max(A, B);

        System.out.println("Expected: " + expected + "\nActual: " + actualPayout);
        assertEquals(expected, actualPayout);
    }
        
}
