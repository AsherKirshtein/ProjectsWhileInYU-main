package edu.yu.da;

/** Implements the MaximizePayoutI API.
 *
 * Students MAY NOT change the provided constructor signature!
 * 
 * @author Asher Kirshtein
 */

import java.util.*;

public class MaximizePayout implements MaximizePayoutI
{

  /** No-op constructor
   */
  public MaximizePayout()
  {
    // no-op, students may change the implementation
  }

  //According to Wolfram Alpha: Product[Power[a,b],{i,1,n}] == a^(b n).

  @Override
  public long max(final List<Long> A, final List<Long> B)
  {
    if(A.size() != B.size())
    {
        throw new IllegalArgumentException();
    }
    Collections.sort(A);
    Collections.sort(B);
    long sumTotal = 0;
    for(int i = 0; i < A.size(); i++)
    {
       sumTotal += Math.pow(A.get(i), B.get(i));
    }  
    return sumTotal;
  }

} // MaximizePayout