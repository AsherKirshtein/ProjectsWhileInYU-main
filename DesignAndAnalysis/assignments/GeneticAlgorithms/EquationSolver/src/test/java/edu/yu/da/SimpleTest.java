package edu.yu.da;

import org.junit.Test;

import edu.yu.da.GeneticAlgorithmConfig.SelectionType;
import edu.yu.da.SimpleEquation.Solution;
import edu.yu.da.SimpleEquationI.SolutionI;

public class SimpleTest
{
    @Test
    public void simpleTest()
    {

    }

    @Test
    public void tournamentTest()
    {
        SimpleEquation s = new SimpleEquation();
        GeneticAlgorithmConfig gac = new GeneticAlgorithmConfig(100, 10, 13, SelectionType.TOURNAMENT, 0.25, 0.25);
        SolutionI sol = s.solveIt(gac);
        System.out.println("Answer: "+ sol);
    }
    @Test
    public void rouletteTest()
    {
        SimpleEquation s = new SimpleEquation();
        GeneticAlgorithmConfig gac = new GeneticAlgorithmConfig(100, 10, 13, SelectionType.ROULETTE, 0.25, 0.25);
        SolutionI sol = s.solveIt(gac);
        System.out.println("Answer: " + sol);
    }
    @Test
    public void printALotOfAnswers()
    {
        System.out.println("Roulette Style: ");
        for(int i = 0; i < 100; i++)
        {
            SimpleEquation s = new SimpleEquation();
            GeneticAlgorithmConfig gac = new GeneticAlgorithmConfig(10000, 100, 13, SelectionType.ROULETTE, 0.25, 0.25);
            SolutionI sol = s.solveIt(gac);
            System.out.println("Answer: " + sol);
        }
        System.out.println("\n\nTournament Style: \n\n");
        for(int i = 0; i < 100; i++)
        {
            SimpleEquation s = new SimpleEquation();
            GeneticAlgorithmConfig gac = new GeneticAlgorithmConfig(10000, 100, 13, SelectionType.TOURNAMENT, 0.25, 0.25);
            SolutionI sol = s.solveIt(gac);
            System.out.println("Answer: " + sol);
        }
    }
}