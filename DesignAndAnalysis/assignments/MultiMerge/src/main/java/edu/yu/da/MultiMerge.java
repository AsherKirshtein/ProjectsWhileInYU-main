package edu.yu.da;

import java.util.Arrays;
import java.util.Collection;

/** Specifies the divide-and-conquer MultiMerge API per the requirements
 * document.
 *
 * Students may not modify this class in any way, nor may they add any
 * constructors to this class.
 * 
 * 
 */

public class MultiMerge extends MultiMergeBase
{

  private int nCombinedMerges;

  public MultiMerge()
  {
    nCombinedMerges = 0;
  }

  /** Does a divide-and-conquer mergesort on the z integer-valued arrays (first
   * dimension is the ith array).
   * 
   * Note: the client maintains ownership of the input parameter,
   * i.e., invoking this method is a non-destructive operation.
   *
   * @param arrays z (z >= 1) integer valued arrays, each of which is sorted,
   * and of identical size n where n>0 and need not be a power of two.  Results
   * are undefined if the arrays aren't sorted, or if they aren't the same
   * size.
   * @return result array of size "z * n": when the method completes, holds the
   * sorted contents of all input arrays.
   */
  public int[] merge(final int[][] arrays)
  {
    int[][] copy = arrays.clone();
    int[][] ar1d2d = merge2D(copy);
    int[] finalMerged = ar1d2d[0];
    return finalMerged;
  }

  private int[][] merge2D(final int[][] arrays)
  {
    if(arrays.length == 1)
    {
      return arrays;
    }
    int[][] merged;
    if(arrays.length % 2 == 0) // if the array has an even number of arrays
    {
      merged = new int[arrays.length/2][arrays[0].length*2];
      int mergedIndex = 0;
      for(int i = 0; i < arrays.length; i+=2)
      {
          int[] combined = mergeArrays(arrays[i], arrays[i+1]);
          merged[mergedIndex] = combined;
          mergedIndex++;
      }
      return merge2D(merged);
    }
    else // it has an odd number of arrays
    {
      merged = new int[(arrays.length/2) + 1][arrays[0].length];
      int mergedIndex = 0;
      for(int i = 0; i < arrays.length-1; i+=2)
      {
          int[] combined = mergeArrays(arrays[i], arrays[i+1]);
          merged[mergedIndex] = combined;
          mergedIndex++;
      }
      merged[merged.length-1] = arrays[arrays.length-1];
      return merge2D(merged);
    }
    
  }
  /** This method must be invoked every time that the merge implementation
   * combines the result of a divide-and-conquer merge call.
   */
  public int[] iterative(final int[][] arrays)
  {
    int[] current = arrays[0];
    for(int i = 1; i < arrays.length; i++)
    {
      current = mergeArrays(current, arrays[i]);
    }
    return current;
  }

  public int[] mergeArrays(int[] array1, int[] array2)//Combines two arrays together; if they are sorted it will return a sorted array
  {
      int[] merged = new int[array1.length + array2.length];
      int index1 = 0;
      int index2 = 0;
      int mIndex = 0;
      while(index1 < array1.length && index2 < array2.length)
      {
        if(array1[index1] > array2[index2])
        {
          merged[mIndex] = array2[index2];
          mIndex++;
          index2++;
        }
        else
        {
          merged[mIndex] = array1[index1];
          mIndex++;
          index1++;
        }
      }
      if(index1 == array1.length)
      {
        for(int i = index2; index2 < array2.length; i++)
        {
          merged[mIndex] = array2[index2];
          mIndex++;
          index2++;
        }
      }
      else
      {
        for(int i = index1; index1 < array1.length; i++)
        {
          merged[mIndex] = array1[index1];
          mIndex++;
          index1++;
        }
      }
      combinedAMerge();
      return merged;
  }

  public void combinedAMerge() {
    nCombinedMerges++;
    System.out.println("Another merge! merges: " + nCombinedMerges);
  }

  public int getNCombinedMerges() {
    return nCombinedMerges;
  }
}