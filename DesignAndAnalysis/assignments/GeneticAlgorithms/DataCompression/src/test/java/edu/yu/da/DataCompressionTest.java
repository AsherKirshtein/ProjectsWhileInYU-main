package edu.yu.da;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.yu.da.DataCompressionI.SolutionI;
import edu.yu.da.GeneticAlgorithmConfig.SelectionType;

public class DataCompressionTest
{
    @Test
    public void simpleTest()
    {
        GeneticAlgorithmConfig GA = new GeneticAlgorithmConfig(10, 10, 1.0, SelectionType.ROULETTE, 0.5, 0.5);
        List<String> stringList = new ArrayList<String>();
        String string1 = "Asher Kirshtein";
        String string2 = "Oze Botach";
        String string3 = "Max Freidman";
        String string4 = "Azriel Bachrach";
        String string5 = "Shmuel Newmark";
        String string6 = "Proffesor Leff";
        String string7 = "Yaakov Baker";
        String string8 = "Doron Schwartz";
        String string9 = "Yehuda Snow";
        String string10 = "Yaakov Bienstock";
        stringList.add(string1);
        stringList.add(string2);
        stringList.add(string3);
        stringList.add(string4);
        stringList.add(string5);
        stringList.add(string6);
        stringList.add(string7);
        stringList.add(string8);
        stringList.add(string9);
        stringList.add(string10);
        DataCompression DC = new DataCompression(stringList);
        SolutionI solution = DC.solveIt(GA);
    }

    @Test
    public void randomTest()
    {
        for(int u = 0; u < 1; u++)
        {
        System.out.println("\nRun: "+ u +"\n");
        GeneticAlgorithmConfig GA = new GeneticAlgorithmConfig(10, 100, 1.0, SelectionType.TOURNAMENT, 1, 1);
        List<String> stringList = new ArrayList<String>();
        for(int i = 0; i < 500; i++)
        {
            String s = "";
            for(int k = 0; k < 80; k++)
            {
                char letter = (char) (65 + getRandomInteger(57));
                String l = Character.toString(letter);
                s += l;
            }
            stringList.add(s);
        }
        DataCompression DC = new DataCompression(stringList);
        SolutionI solution = DC.solveIt(GA);
        }
    }

    private static int getRandomInteger(int max)
    {
        double random = Math.floor(Math.random() * max);
        return (int)random;
    }
}
