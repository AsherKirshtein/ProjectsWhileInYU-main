package edu.yu.introtoalgs;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class QueueTest 
{
    @Test
    public void maxTest()
    {
        System.out.println("\nMaxTest \n");
        
        MaxQueue queue = new MaxQueue();

        for(int i = 0; i < 200; i++)
        {
            queue.enqueue(i);
            System.out.println("I = " + i);
            assertEquals(i, queue.max());
        }
        
    }

    @Test
    public void queueSizeTest()
    {
        MaxQueue queue = new MaxQueue();

        for(int i = 0; i < 50; i++)
        {
            queue.enqueue(i);
        }

        assertEquals(50, queue.size());
    }

    @Test
    public void dequeueTest()
    {
        MaxQueue queue = new MaxQueue();

        for(int i = 0; i < 10; i++)
        {
            queue.enqueue(i);
        }

        int value = queue.dequeue();
        int value2 = queue.dequeue();
        int value3 = queue.dequeue();

        assertEquals(0, value);
        assertEquals(1, value2);
        assertEquals(2, value3);
    }

    @Test
    public void queueSpeedTest()
    {

        System.out.println("\nQueue speed test: \n \n");

        for(int attempt = 1; attempt < 15; attempt++)
        {
        
            MaxQueue queue = new MaxQueue();

            long startTime = System.nanoTime();

            for(int i = 1; i < attempt*1000000; i++)
            {
                queue.enqueue(i);
            }

            long endTime = System.nanoTime();
            long totalTime = endTime-startTime;
            double seconds = (double) totalTime/1000000000;
            
            System.out.println("It took " + seconds + " seconds to execute with " + attempt*1000000 + " values. Attempt number: "+ attempt);
        }    
    }
    @Test
    public void dequeueSpeedTest()
    {
        System.out.println("\nDequeue speed Test : \n \n");

        for(int attempt = 1; attempt < 15; attempt++)
        {
        
            MaxQueue queue = new MaxQueue();

            for(int i = 0; i < attempt*1000000; i++)
            {
                queue.enqueue(i);
            }

            long startTime = System.nanoTime();
            
            for(int h = 1; h < attempt*1000000; h++)
            {
                queue.dequeue();
            }

            long endTime = System.nanoTime();
            long totalTime = endTime-startTime;
            double seconds = (double) totalTime/1000000000;
            
            System.out.println("It took " + seconds + " seconds to execute with " + attempt*1000000 + " values. Attempt number: "+ attempt);
        }

       
    }
    @Test
    public void quickTest()
    {
        MaxQueue queue = new MaxQueue();
        queue.enqueue(13);
        queue.enqueue(12);
        queue.enqueue(25);
        queue.enqueue(11);
        queue.enqueue(4);
        queue.enqueue(17);

        for(int i = 0; i < 2; i++)
        {
            queue.dequeue();
        }
        
        assertEquals(25, queue.max());

        queue.dequeue();

        assertEquals(17, queue.max());

        System.out.println();
    }
}
