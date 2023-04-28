package edu.yu.da;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WaitNoMoreTest {
    
    @Test
    public void simpleTest()
    {
        final int [] d = {2,4};
        final int [] w = {1,1};
        final WaitNoMoreI wnm = new WaitNoMore();
        int simpleAnswer = wnm.minTotalWaitingTime(d, w);
        assertEquals(2, simpleAnswer);
    }
    @Test
    public void anotherRandomTest()
    {
        WaitNoMore noMoWait = new WaitNoMore();
        int[] durations = {1,2,6,7,3,1,4,10};
        int[] weights = {5,2,6,2,2,2,3,2};
        noMoWait.minTotalWaitingTime(durations, weights);
    }
}
