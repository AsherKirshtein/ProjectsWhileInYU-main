package edu.yu.da;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Implements the WaitNoMoreI API.
 *
 * Students MAY NOT change the provided constructor signature!
 * 
 */

public class WaitNoMore implements WaitNoMoreI
{ 
  public WaitNoMore() 
  {
    // no-op, students may change the implementation
  }
  @Override
  public int minTotalWaitingTime(final int[] durations, final int[] weights) 
  {
      if(durations.length != weights.length)
      {
        throw new IllegalArgumentException();
      }
      int length = durations.length;
      int[] ds = Arrays.copyOf(durations, durations.length);
      int[] ws = Arrays.copyOf(weights, weights.length);
      List<PrintJob> printJobs = new ArrayList<>();
      int minWaitTime = 0;
      for(int i =0 ; i < length; i++)
      {
        PrintJob job = new PrintJob(ds[i], ws[i]);
        printJobs.add(job); 
      }
      Collections.sort(printJobs);
      // for(PrintJob p: printJobs)
      // {
      //   System.out.println(p);
      // }
      int waiting = 0;
      for(int i = 0; i < length; i++)
      {
        minWaitTime += waiting * printJobs.get(i).weight;
        waiting += printJobs.get(i).duration;
      }
      return minWaitTime;
  }
  private class PrintJob implements Comparable<PrintJob>
  {
    private int duration;
    private int weight;

    private PrintJob(int duration, int weight)
    {
      this.weight = weight;
      this.duration = duration;
    }
    private double getRatio()
    {
      return (double)weight/duration;
    }
    @Override
    public String toString()
    {
      return "Weight: " + this.weight + " Duration: " + this.duration;
    }
    @Override
    public int compareTo(PrintJob o)
    {
      if(o.getRatio() < this.getRatio())
      {
        return -1;
      }
      else if(o.getRatio() > this.getRatio())
      {
        return 1;
      }
      else
      {
      return 0;
      }
    }
  }
} 