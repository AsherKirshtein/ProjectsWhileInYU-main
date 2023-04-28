package edu.yu.da;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class StockBookshelfTest 
{
    List<Integer> tanach;
    List<Integer> mussar;
    List<Integer> gamara;
    List<Integer> neviim;
    List<Integer> hebrewMussarMachshava;
    List<Integer> englishMussarMachshava;
    List<Integer> goish;

    @Before
    public void setUp()
    {
        tanach = new ArrayList<>();
        mussar = new ArrayList<>();
        gamara = new ArrayList<>();
        neviim = new ArrayList<>();
        hebrewMussarMachshava = new ArrayList<>();
        englishMussarMachshava = new ArrayList<>();
        goish = new ArrayList<>();
    }

    @Test
    public void simpleTest()
    {
        for(int i = 1; i < 4; i++)
        {
            tanach.add(getRandomInteger(5));
            mussar.add(getRandomInteger(5));
            gamara.add(getRandomInteger(5));
            neviim.add(getRandomInteger(5));
            hebrewMussarMachshava.add(getRandomInteger(5));
            englishMussarMachshava.add(getRandomInteger(5));
            goish.add(getRandomInteger(5));
        }
        StockYourBookshelf stockBookShelf = new StockYourBookshelf();
        Map<String, List<Integer>> seforimClassToTypePrices = new HashMap<>();
        seforimClassToTypePrices.put("tanach", tanach);
        seforimClassToTypePrices.put("mussar", mussar);
        seforimClassToTypePrices.put("gamara", gamara);
        seforimClassToTypePrices.put("neviim", neviim);
        seforimClassToTypePrices.put("englishMussarMachshava", englishMussarMachshava);
        seforimClassToTypePrices.put("hebrewMussarMachshava", hebrewMussarMachshava);
        seforimClassToTypePrices.put("goish", goish);

        int budget = getRandomInteger(60);
        System.out.println(seforimClassToTypePrices + "\n\n");
        int max = stockBookShelf.maxAmountThatCanBeSpent(budget, seforimClassToTypePrices);
        

        stockBookShelf.solution();
    }

    @Test
    public void canBeDoneTest()
    {
        for(int i = 1; i < 10; i++)
        {
            tanach.add(i);
            mussar.add(i);
            gamara.add(i);
            neviim.add(i);
            hebrewMussarMachshava.add(i);
            englishMussarMachshava.add(i);
            goish.add(i);
        }
        StockYourBookshelf stockBookShelf = new StockYourBookshelf();
        Map<String, List<Integer>> seforimClassToTypePrices = new HashMap<>();
        seforimClassToTypePrices.put("tanach", tanach);
        seforimClassToTypePrices.put("mussar", mussar);
        seforimClassToTypePrices.put("gamara", gamara);
        seforimClassToTypePrices.put("neviim", neviim);
        seforimClassToTypePrices.put("englishMussarMachshava", englishMussarMachshava);
        seforimClassToTypePrices.put("hebrewMussarMachshava", hebrewMussarMachshava);
        seforimClassToTypePrices.put("goish", goish);

        int budget = getRandomInteger(60);

        int max = stockBookShelf.maxAmountThatCanBeSpent(budget, seforimClassToTypePrices);
        //assertEquals(budget, max);
    }


    @Test
    public void randomNUmbersTest()
    {
        System.out.println("Random numbers test\n");
        for(int i = 1; i < 10; i++)
        {
            tanach.add(getRandomInteger(25));
            mussar.add(getRandomInteger(25));
            gamara.add(getRandomInteger(25));
            neviim.add(getRandomInteger(25));
            hebrewMussarMachshava.add(getRandomInteger(25));
            englishMussarMachshava.add(getRandomInteger(25));
            goish.add(getRandomInteger(25));
        }
        StockYourBookshelf stockBookShelf = new StockYourBookshelf();
        Map<String, List<Integer>> seforimClassToTypePrices = new HashMap<>();
        seforimClassToTypePrices.put("tanach", tanach);
        seforimClassToTypePrices.put("mussar", mussar);
        seforimClassToTypePrices.put("gamara", gamara);
        seforimClassToTypePrices.put("neviim", neviim);
        seforimClassToTypePrices.put("englishMussarMachshava", englishMussarMachshava);
        seforimClassToTypePrices.put("hebrewMussarMachshava", hebrewMussarMachshava);
        seforimClassToTypePrices.put("goish", goish);

        int budget = getRandomInteger(100);

        int max = stockBookShelf.maxAmountThatCanBeSpent(budget, seforimClassToTypePrices);
        //assertEquals(budget, max);
    }
    @Test
    public void solutionsTest()
    {
        System.out.println("Solutions test\n");
        for(int i = 1; i < 10; i++)
        {
            tanach.add(getRandomInteger(25));
            mussar.add(getRandomInteger(25));
            gamara.add(getRandomInteger(25));
            neviim.add(getRandomInteger(25));
            hebrewMussarMachshava.add(getRandomInteger(25));
            englishMussarMachshava.add(getRandomInteger(25));
            goish.add(getRandomInteger(25));
        }
        StockYourBookshelf stockBookShelf = new StockYourBookshelf();
        Map<String, List<Integer>> seforimClassToTypePrices = new HashMap<>();
        seforimClassToTypePrices.put("tanach", tanach);
        seforimClassToTypePrices.put("mussar", mussar);
        seforimClassToTypePrices.put("gamara", gamara);
        seforimClassToTypePrices.put("neviim", neviim);
        seforimClassToTypePrices.put("englishMussarMachshava", englishMussarMachshava);
        seforimClassToTypePrices.put("hebrewMussarMachshava", hebrewMussarMachshava);
        seforimClassToTypePrices.put("goish", goish);

        int budget = getRandomInteger(100);

        int max = stockBookShelf.maxAmountThatCanBeSpent(budget, seforimClassToTypePrices);
        List<Integer> soluList = stockBookShelf.solution();
        if(soluList.isEmpty())
        {
            return;
        }
        int index = 0;
        Boolean works = true;
        for(String key: seforimClassToTypePrices.keySet())
        {
            if(!seforimClassToTypePrices.get(key).contains(soluList.get(index)))
            {
                works = false;
                System.out.println("Failed on " + key + "which contains " + seforimClassToTypePrices.get(key) + "Expected " + soluList.get(index));
            }
            index++;
        }
        int sum = 0;
        for(int i : soluList)
        {
            sum += i;
        }
        assertEquals(max, sum);
        assertTrue(works);
    }
    @Test
    public void giantTest()
    {
        for(int i = 1; i < 100; i++)
        {
            tanach.add(getRandomInteger(100));
            mussar.add(getRandomInteger(100));
            gamara.add(getRandomInteger(100));
            neviim.add(getRandomInteger(100));
            hebrewMussarMachshava.add(getRandomInteger(100));
            englishMussarMachshava.add(getRandomInteger(100));
            goish.add(getRandomInteger(100));
        }
        StockYourBookshelf stockBookShelf = new StockYourBookshelf();
        Map<String, List<Integer>> seforimClassToTypePrices = new HashMap<>();
        seforimClassToTypePrices.put("tanach", tanach);
        seforimClassToTypePrices.put("mussar", mussar);
        seforimClassToTypePrices.put("gamara", gamara);
        seforimClassToTypePrices.put("neviim", neviim);
        seforimClassToTypePrices.put("englishMussarMachshava", englishMussarMachshava);
        seforimClassToTypePrices.put("hebrewMussarMachshava", hebrewMussarMachshava);
        seforimClassToTypePrices.put("goish", goish);

        int budget = getRandomInteger(740);

        int max = stockBookShelf.maxAmountThatCanBeSpent(budget, seforimClassToTypePrices);
        //assertEquals(budget, max);
        stockBookShelf.solution();
    }
    @Test
    public void runForErrors()
    {
        for(int i = 0; i < 10; i++)
        {
            giantTest();
            simpleTest();
            randomNUmbersTest();
        }
    }
    private static int getRandomInteger(int max)
    {
        double random = Math.floor(Math.random() * max);
        return (int)random;
    }
}