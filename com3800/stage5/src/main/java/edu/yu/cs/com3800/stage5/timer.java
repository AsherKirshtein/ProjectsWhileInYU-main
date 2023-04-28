package edu.yu.cs.com3800.stage5;

public class timer extends Thread
{
    private long waitTime;
    private boolean timesUp;
    timer(long waitTime)
    {
        this.waitTime = waitTime;
        this.timesUp = false;
    }

    @Override
    public void run()
    {
        long start = System.currentTimeMillis();
        while(!isInterrupted())
        {
            long currentTime = System.currentTimeMillis() - start;
            if(currentTime >= this.waitTime)
            {
                System.out.println("FIRE THE LEADER!!! ITS TAKING TOO LONG!!!");
                this.timesUp = true;
                return;
            }
        }
    }
    public boolean timesUp()
    {
        return this.timesUp;
    }
}
