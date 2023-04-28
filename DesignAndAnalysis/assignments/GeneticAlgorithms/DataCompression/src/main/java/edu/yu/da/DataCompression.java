package edu.yu.da;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.crypto.Data;

import edu.yu.da.DataCompressionI.SolutionI;
import edu.yu.da.GeneticAlgorithmConfig.SelectionType;

public class DataCompression implements DataCompressionI
{
    SelectionType selectionType;
    public class Solution implements SolutionI
    {
        private List<String> original;
        private List<String> myList;
        private int generations;

        public Solution(List<String> original)
        {
            this.original = original;
            this.myList = original;
        }
        public void changeList(final List<String> newList)
        {
            this.myList = List.copyOf(newList);
        }
        @Override
        public List<String> getList()
        {
            return this.myList;
        }

        @Override
        public List<String> getOriginalList()
        {
            return this.original;
        }

        @Override
        public double relativeImprovement()
        {
            double originalCompressed = DataCompressionI.bytesCompressed(this.original);
            //System.out.println("original compressed: " + originalCompressed);
            double geneticCompressed = DataCompressionI.bytesCompressed(this.myList);
            //System.out.println("Current compressed: " + geneticCompressed);
            double ratio = originalCompressed/geneticCompressed;
            return ratio;
        }
        public void addGeneration()
        {
            this.generations++;
        }
        @Override
        public int nGenerations()
        {
            return this.generations;
        }
    
    }
    private List<String> og;
    private List<String> currentList;

    public DataCompression(final List<String> original) 
    {
        this.og = List.copyOf(original);
        this.currentList = new ArrayList<String>();
        for(String s : og)
        {
            currentList.add(s);
        }
    }
    
    @Override
    public SolutionI solveIt(GeneticAlgorithmConfig gac)
    {
        int maxGenerations = gac.getMaxGenerations();
        double threshold = gac.getThreshold();
        this.selectionType = gac.getSelectionType();
        double mutationProbability = gac.getMutationProbability();
        double crossoverProbability = gac.getCrossoverProbability();
        Solution bestSolution = new Solution(this.og);
        for(int i = 0; i < maxGenerations; i++)
        {
            // System.out.println("Generation: " + i);
            bestSolution.addGeneration();
            crossover(crossoverProbability);
            mutate(mutationProbability);
            // System.out.println("this list bytes compressed: "+ DataCompressionI.bytesCompressed(this.currentList));
            // System.out.println("current best solution compressed: " + DataCompressionI.bytesCompressed(bestSolution.getList()));
            if(DataCompressionI.bytesCompressed(this.currentList) < DataCompressionI.bytesCompressed(bestSolution.getList()))
            {
                bestSolution.changeList(this.currentList);
                // if(DataCompressionI.bytesCompressed(this.currentList)-1 >= threshold)
                // {
                //     return bestSolution;
                // }
            }
        }
        // System.out.println("Original: " + DataCompressionI.bytesCompressed(this.og));
        // System.out.println("Best: " + DataCompressionI.bytesCompressed(this.currentList));
        // System.out.println("Improved by: " + bestSolution.relativeImprovement());
        return bestSolution;
    }
    private void crossover(double crossoverProbability)
    {
        double percentCrossed = crossoverProbability * 100;
        int random = getRandomInteger(100);
        if(random >= percentCrossed)
        {
            return;
        }
        Collections.reverse(this.currentList);
    }

    private void mutate(double mutationProbability)
    {
        double percentChanceOfMutation = mutationProbability * 100;
        int random = getRandomInteger(100);
        if(random >= percentChanceOfMutation)
        {
            return;
        }   
        Collections.shuffle(this.currentList);
    }
    
    private static int getRandomInteger(int max)
    {
        double random = Math.floor(Math.random() * max);
        return (int)random;
    }

    @Override
    public int nCompressedBytesInOriginalList()
    {
        return DataCompressionI.bytesCompressed(this.og);
    }  
}
    
