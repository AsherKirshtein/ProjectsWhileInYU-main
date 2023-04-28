package edu.yu.introtoalgs;

/** Defines and implements the AnthroChassidus API per the requirements
 * documentation.
 */

public class AnthroChassidus {

  /** Constructor.  When the constructor completes, ALL necessary processing
   * for subsequent API calls have been made such that any subsequent call will
   * incur an O(1) cost.
   *
   * @param n the size of the underlying population that we're investigating:
   * need not correspond in any way to the number of people actually
   * interviewed (i.e., the number of elements in the "a" and "b" parameters).
   * Must be greater than 2.
   * @param a interviewed people, element value corresponds to a unique "person
   * id" in the range 0..n-1
   * @param b interviewed people, element value corresponds to a unique "person
   * id" in the range 0..n-1.  Pairs of a_i and b_i entries represent the fact
   * that the corresponding people follow the same Chassidus (without
   * specifying what that Chassidus is).
   */


    public int[] a;
    public int[] b;
    public int population;
    public WeightedQuickUnionUF unionFind;

    public AnthroChassidus(final int n, final int[] a, final int[] b)
    {
        if(a.length != b.length)
        {
          throw new IllegalArgumentException("The arrays need to be the same length.");
        }
        this.a = a;
        this.b = b;
        this.population = n;
        this.unionFind = new WeightedQuickUnionUF(n);
        unionArrays();
    }

  /** Return the tightest value less than or equal to "n" specifying how many
   * types of Chassidus exist in the population: this answer is inferred from
   * the interviewers data supplied to the constructor
   *
   * @return tightest possible lower bound on the number of Chassidus in the
   * underlying population.
   */

   
    public int getLowerBoundOnChassidusTypes()
    {
      return this.unionFind.count();
    }

  /** Return the number of interviewed people who follow the same Chassidus as
   * this person.
   *
   * @param id uniquely identifies the interviewed person
   * @return the number of interviewed people who follow the same Chassidus as
   * this person.
   */
  public int nShareSameChassidus(final int id)
  {
       return this.unionFind.findShares(id);
  }

  private void unionArrays()
  {
     for(int index = 0; index < this.a.length; index++)
     {
        this.unionFind.union(this.a[index], this.b[index]);
     }
  }

  private class WeightedQuickUnionUF 
      {
        private int[] parents; // parent link (site indexed)
        private int[] size; // size of component for roots (site indexed)
        private int count; // number of components

      private WeightedQuickUnionUF(int N)
      {
        this.count = N;
        this.parents = new int[N];
        this.size = new int[N];

        for (int i = 0; i < N; i++) //I moved the sz array into this for loop because for some reason the book used a second for loop of the same size to do it
        {
          this.parents[i] = i;
          this.size[i] = 1;
        }
      }
      private int count()
      { 
          return this.count; 
      }
      private boolean connected(int p, int q)
      { 
          return find(p) == find(q);
      }

      private int find(int p)
      { // Follow links to find a root.

        while (p != this.parents[p])//while we aren't at our parent root we go up through the "tree" 
        { 
          parents[p] = parents[parents[p]];
          p = parents[p];
        }
        return p;
      }
      private int findShares(int n)
      {
        return this.size[this.parents[n]];
      }

      private void union(int p, int q)
      {
        int i = find(p);
        int j = find(q);

        if (i == j) 
        {
          return;
        }
        if(size[i] < size[j]) 
        { 
          parents[i] = j; 
          size[j] += size[i]; 
        }
        else 
        { 
          parents[j] = i; 
          size[i] += size[j]; 
        }
        count--;
      }
  }
} 


