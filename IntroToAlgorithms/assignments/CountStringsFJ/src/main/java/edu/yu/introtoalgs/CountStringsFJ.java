package edu.yu.introtoalgs;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/** Implements the CountStringsFJ semantics specified in the requirements
 * document.
 */

public class CountStringsFJ {

  /** Constructor.
   *
   * @param arr the array to process, can't be null or empty
   * @param str the string to match, can't be null, may be empty
   * @param threshold when the length of arr is less than threshold, processing
   * must be sequential; otherwise, processing must use a fork/join, recursive
   * divide-and-conquer strategy.  The parameter must be greater than 0.
   *
   * IMPORTANT: Students must use this constructor, they MAY NOT add another
   * constructor.
   */

  private String[] arr;
  private String str;
  private int threshold;
  
  public CountStringsFJ(final String[] arr, final String str, final int threshold)
  {
        if(arr == null || arr.length == 0)
        {
            throw new IllegalArgumentException("arr was either null or was empty");
        }
        if(str == null || str == "")
        {
            throw new IllegalArgumentException("str was either null or was empty");
        }
        if(!(threshold > 0))
        {
            throw new IllegalArgumentException("Threshold was less than 0");
        }
        this.arr = arr;
        this.str = str;
        this.threshold = threshold;
  }
  
  /** Returns the number of elements in arr that ".equal" the "str" parameter
   *
   * @return Using a strategy dictated by the relative values of threshold and
   * the size of arr, returns the number of times that str appears in arr
   */
  public int doIt()
  {
    ForkJoinSum fjs = new ForkJoinSum(this.arr, this.str, this.threshold, 0, arr.length);
    final ForkJoinPool fjPool = new ForkJoinPool();
    int parallelSum = fjPool.invoke(fjs);
    fjPool.shutdown();
    return parallelSum;
  }

  public int sequentialIteration() //Run if arr is less than the threshold
  {
    int counter = 0;
    for(int index = 0; index < arr.length; index++)
    {
        if(arr[index].equals(this.str))
        {
            counter++;
        }
    }
    return counter;
  }

    class ForkJoinSum extends RecursiveTask<Integer> 
    {
       String str;
       String[] arr;
       int threshold;
       int low;
       int high;
      
       public ForkJoinSum(String[] arr, String str, int threshold, int low, int high)
       {
          this.str = str;
          this.arr = arr;
          this.threshold = threshold;
          this.high = high;
          this.low = low;
       }
      
       @Override
      protected Integer compute()
      {
        if(high - low <= threshold) 
        {
          return computeSequentialSum(this.arr , this.low , this.high);
        }
        else
        {
            ForkJoinSum left = new ForkJoinSum(this.arr, this.str, this.threshold, low, (high + low)/2);
            ForkJoinSum right = new ForkJoinSum(this.arr, this.str, this.threshold, (high + low )/2, high);
            left.fork();
            final Integer rightAnswer = right.compute();
            final Integer leftAnswer = left.join();
            return leftAnswer + rightAnswer;
        }
      }

      private Integer computeSequentialSum(String[] arr2, int low2, int high2)
      {
        int counter = 0;
        for(int index = low2; index < high2; index++)
        {
            if(arr[index].equals(this.str))
            {
                counter++;
            }
        }
        return counter;
      }
    }
}
