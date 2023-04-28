package edu.yu.introtoalgs;

import java.util.*;

/** Implements the "Add an Interval To a Set of Intervals" semantics defined in
 * the requirements document.
 */

public class MergeAnInterval {

  /** An immutable class, holds a left and right integer-valued pair that
   * defines a closed interval
   *
   * IMPORTANT: students may not modify the semantics of the "left", "right"
   * instance variables, nor may they use any other constructor signature.
   * Students may (are encouraged to) add any other methods that they choose,
   * bearing in mind that my tests will ONLY DIRECTLY INVOKE the constructor
   * and the "merge" method.
   */
      public static class Interval implements Comparable<Interval>{
        public final int left;
        public final int right;

        /** Constructor
         * 
         * @param left the left endpoint of the interval, may be negative
         * @param right the right endpoint of the interval, may be negative
         * @throws IllegalArgumentException if left is >= right
         */
        public Interval(int l, int r) 
        {
          if(l >= r)
          {
              throw new IllegalArgumentException();
          }  
          this.left = l;
          this.right = r;
        }


      @Override
        public boolean equals(Object a) 
        {
          if(a == this)
          {
              return true;
          }
          if(!(a instanceof Interval))
          {
              return false;
          }
          Interval other = (Interval) a;
          if(other.right == this.right && other.left == this.left)
          {
            return true;
          }
          return false;
        }
        @Override
        public int compareTo(Interval o) //Sort based on which has the higher right value
        {
          if(o == null)
          {
            throw new NullPointerException();
          }
          if(this.right > o.right)
          {
            return 1;
          }
          if(o.right > this.right)
          {
            return -1;
          }
          return 0;
        }
      } // Interval class


  /** Merges the new interval into an existing set of disjoint intervals.
   *
   * @param intervals an set of disjoint intervals (may be empty)
   * @param newInterval the interval to be added
   * @return a new set of disjoint intervals containing the original intervals
   * and the new interval, merging the new interval if necessary into existing
   * interval(s), to preseve the "disjointedness" property.
   * @throws IllegalArgumentException if either parameter is null
   */
  public static Set<Interval> merge(final Set<Interval> intervals, Interval newInterval)
  {
    if(intervals == null || newInterval == null)
    {
      throw new IllegalArgumentException();
    }
    if(intervals.size() == 0)
    {
      intervals.add(newInterval);
      return intervals;
    }
    intervals.add(newInterval);
    Set<Interval> finishedSet = merge(intervals);
    return finishedSet;
  }
  public static Set<Interval> merge(Set<Interval> intervals)
  {
    if(intervals == null)
    {
      throw new IllegalArgumentException();
    }
    int merges = 0; // we count to see if the merges are > 0 so that we know whether or not to stop the recursive call
    Interval[] values = new Interval[intervals.size()];
    int index = 0;
    for(Interval in : intervals)
    {
      values[index++] = in;
    }
    Arrays.sort(values);
    Set<Interval> finishedSet = new HashSet<Interval>();
    for(int i = 0; i < intervals.size()-1; i++)
    { 
      if(canMerge(values[i], values[i+1]))
       {
          Interval a = getInterval(values[i], values[i+1]);
          values[i] = null;
          values[i+1] = a;
          merges++;
       }
    }
    for(Interval in: values)
    {
      if(in != null)
      {
        finishedSet.add(in);
      }
    }
    if(merges == 0)
    {
      return finishedSet;
    }
    return merge(finishedSet);
  }
  public static boolean canMerge(Interval a, Interval b) //Are you able to merge these two intervals together?
  {
      if(a.left > b.right || b.left > a.right)
      {
        return false;
      }
      return true;
  }
  
  public static Interval getInterval(Interval a, Interval b)//returns the interval result of combining the 2 intervals
  {
     if(!canMerge(a, b))
     {
       throw new IllegalArgumentException();
     }
     int left;
     int right;
     if(a.left <= b.left) //get the lesser of the left values
     {
        left = a.left;
     }
     else
     {
        left = b.left;
     }

     if(a.right >= b.right) // get the greater of the right values
     {
        right = a.right;
     }
     else 
     {
       right = b.right;
     }

     Interval interval = new Interval(left, right);
     return interval;
  }
}
