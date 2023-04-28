package edu.yu.da;

import static edu.yu.da.SimpleEquationI.SolutionI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import edu.yu.da.GeneticAlgorithmConfig.SelectionType;

public class SimpleEquation implements SimpleEquationI {

  /** Constructor.
   *
   * Students MAY NOT define any other constructor signature.  They
   * MAY change the stubbed implemention in any way they choose.
   */
  List<Solution> solutions;

  
  public SimpleEquation()
  {
    this.solutions = new ArrayList<>();
  }
  /** Returns the value of x for the equation's solution.
     */
  public class Solution implements SolutionI
  {
    public int x;
    public int y;
    public int nGenerations;

    public Solution() 
    {
      this.x = getRandomInteger(100);
      this.y = getRandomInteger(100);
      this.nGenerations = 0;
    }

    public int getX()
    {
      return this.x;
    }
    public int getY()
    {
      return this.y;
    }
    public double fitness()
    {
      int x = this.getX();
      int y = this.getY();
      return 6*x - x*x + 4*y - y*y;
    }

    public int nGenerations()
    {
      return this.nGenerations;
    }
    @Override
    public String toString()
    {
      return "Solution with fitness: " + fitness() + "!";
    }
}

  @Override
  public SolutionI solveIt(final GeneticAlgorithmConfig gac)
  {
    int populationSize = gac.getInitialPopulationSize();
    Solution mostFit = null;
    for(int i = 0; i < populationSize; i++)
    {
      Solution s = new Solution();
      this.solutions.add(s);
      if(mostFit == null || s.fitness() > mostFit.fitness())
      {
        mostFit = s;
      }
      if(s.fitness() > gac.getThreshold())
      {
        return (SolutionI) s;
      }
    }
    int currentGen = 0;
    while(currentGen < gac.getMaxGenerations())
    {
      currentGen++;
      List<Solution> parents; 
      if(mostFit.fitness() > gac.getThreshold())
      {
        return (SolutionI) mostFit;
      }
      if(gac.getSelectionType() == SelectionType.TOURNAMENT)
      {
        parents = tournament();
      }
      else
      {
        parents = roulette();
      }
      boolean crossover;
      if(gac.getCrossoverProbability()*100 >= getRandomInteger(100))
      {
        crossover = true;
      }
      else
      {
        crossover = false;
      }
      List<Solution> kids = crossover(parents.get(0), parents.get(1), currentGen, crossover);
      for(int i = 0; i < kids.size(); i++)
      {
        if(gac.getMutationProbability() * 100 >= getRandomInteger(100))
        {
          Solution a = kids.remove(i);
          kids.add(i, mutation(a));
        }
      }
      for(Solution k : kids)
      {
        if(mostFit.fitness() < k.fitness())
        {
          mostFit = k;
        }
      }
    }
    return (SolutionI) mostFit;
  }

  private Solution mutation(Solution child)
  {
    child.x = (getRandomInteger(100));
    child.y = (getRandomInteger(100));
    return child;
  }
  private List<Solution> crossover(Solution dad, Solution mom, int gen, boolean crossover)
  {
    if(!crossover)
    {
      List<Solution> kids = new ArrayList<>();
      kids.add(dad);
      kids.add(mom);
    }
    int crossPoint = getRandomInteger(2);
    List<Solution> kids = new ArrayList<>();
    for(int i = 0; i < 2; i++)
    { 
      if(crossPoint == 0)
      {
        kids.add(dad);
      }
      if(crossPoint == 2)
      {
        kids.add(mom);
      }
      if(crossPoint == 1)
      {  
        Solution s = new Solution();
        s.x = dad.getX();
        s.y = mom.getY();
        Solution d = new Solution();
        d.x = mom.getX();
        d.y = dad.getY();
        if(d.fitness() > s.fitness())
        {
          kids.add(d);
        }
        else
        {
          kids.add(s);
        }
      }
    }
    return kids;
  }
  private List<Solution> roulette()
  {
    List<Solution> parents = new ArrayList<>();
    int totalFitness = 0;
    List<Double> probabilities = new ArrayList<>();
    for(Solution s: this.solutions)
    {
       totalFitness += s.fitness();
    }
    double probability; 
    int probSum = 0; 
    for(Solution s: this.solutions)
    {
      probability = probSum + (s.fitness() / probSum);
      probSum += probability;
      probabilities.add(probability);
    }
    
    while(parents.size() < 2)
    {
      Double random;
      for(int i = 0; i < 2; i++)
      {
        random = Math.random();
      
        for(int k = 0; k < this.solutions.size()-1; k++)
        {
          if(random > probabilities.get(k) && random < probabilities.get(k+1))
          {
            Solution parent = this.solutions.get(k);
            parents.add(parent);
          }
        }
      }
      parents.add(this.solutions.get(getRandomInteger(this.solutions.size())));
    }
    return parents;
  }

  private List<Solution> tournament()
  {
    List<Solution> parents = new ArrayList<>();
    int size = this.solutions.size();
    int random1 = getRandomInteger(size);
    int random2 = getRandomInteger(size);
    int random3 = getRandomInteger(size);
    int random4 = getRandomInteger(size);
    Solution s1 = this.solutions.get(random1);
    Solution s2 = this.solutions.get(random2);
    Solution s3 = this.solutions.get(random3);
    Solution s4 = this.solutions.get(random4);

    if(s1.fitness() >= s2.fitness())
    {
      parents.add(s1);
    }
    else
    {
       parents.add(s2);
    }
    if(s3.fitness() >= s4.fitness())
    {
      parents.add(s3);
    }
    else
    {
      parents.add(s4);
    }
    return parents;
  }

  private static int getRandomInteger(int max)
  {
      double random = Math.floor(Math.random() * max);
      return (int)random;
  }
} // public class SimpleEquation
