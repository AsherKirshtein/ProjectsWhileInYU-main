package edu.yu.introtoalgs;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class TransportRadioactivesTest 
{

    @Test
    public void testTransportItNormalValues() 
    {
        System.out.println("\nTransport it with normal values Test: \n\n");
        List<TransportationState> moves = TransportRadioactives.transportIt(11, 10);
        System.out.println("We have 11 mithium and 10 cathium");
        System.out.println("\nThis is the log of all of the trips: \n\n");
        for(TransportationState state: moves)
        {
            System.out.println(state);
        }
        System.out.println("\n+-------------------------------+");
    }
    @Test
    public void testABunchOfRandomNumbers()
    {
        System.out.println("\nA bunch of Random numbersTest: \n\n");
        Boolean correct = true;
        for(int i = 10; i < 1000; i*=10)
        {
            int cathium = getRandomInteger(i);
            int mithium = getRandomInteger(i);

            if(mithium > cathium)
            {
                List<TransportationState> moves = TransportRadioactives.transportIt(mithium, cathium);
                if(moves == null)
                {
                    correct = false;
                }
                for(TransportationState state: moves)
                {
                    if(state.getCathiumSrc() > state.getMithiumSrc() || state.getCathiumDest() > state.getMithiumDest())
                    {
                        System.out.println("INVALID STATE: " + state);
                        assertEquals(true, false);
                    }
                }
            }
        }
        assertEquals(true, correct);
        System.out.println("TestPassed");
    }

    @Test
    public void largeNumbersSpeedTest()
    {
        System.out.println("\nLarge Numbers Test: \n");
        Long startTime = System.nanoTime();
        System.out.println("Mithium: 10000000\nCathium: 9999999");
        TransportRadioactives.transportIt(10000000, 9999999);
        Long endTime = System.nanoTime();
        Long totalTime = endTime - startTime;
        double seconds = (double) totalTime/1000000000;
        System.out.println(seconds + " seconds");
    }

    private static int getRandomInteger(int max)
    {
        double random = Math.floor(Math.random() * max);
        return (int)random;
    }

}
