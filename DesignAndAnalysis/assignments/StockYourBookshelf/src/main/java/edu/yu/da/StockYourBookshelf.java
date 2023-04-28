package edu.yu.da;

/** Implements the StockYourBookshelfI API.
 *
 * Students MAY NOT change the provided constructor signature!
 * 
 * @author Avraham Leff
 */

import java.util.*;
import java.util.concurrent.CountDownLatch;

import javax.swing.text.DefaultEditorKit.CutAction;

public class StockYourBookshelf implements StockYourBookshelfI {

    List<Integer> solution;
    List<List<Integer>> possibleSolutions;
    int maxSpent;
    Map<String , List<Integer>> seforimClassToTypePrices;

    public StockYourBookshelf() 
    {
        solution = new ArrayList<Integer>();
    }

    @Override
    public List<Integer> solution()
    {
        if(this.maxSpent == Integer.MIN_VALUE)
        {
            return null;
        }
    
        Set<String> keySet = seforimClassToTypePrices.keySet();
        List<String> keyList = new ArrayList<String>(keySet);
        Collections.reverse(keyList);
        int index = 0;
        int posIndex = possibleSolutions.size() - 2;
        List<List<Integer>> paths = new ArrayList<List<Integer>>();
        List<Integer> a = new ArrayList<>();
        a.add(this.maxSpent);
        paths.add(a);
        for(String key: keyList)
        {
            List<Integer> temp = new ArrayList<Integer>();    
            for(int i : this.seforimClassToTypePrices.get(key))
                {
                    for(int x : paths.get(index))
                    {
                        if(posIndex == -1)
                        {
                            if(!temp.contains(0))
                            {
                                temp.add(0);
                            }
                            continue;
                        }
                        if(!temp.contains(x-i) && this.possibleSolutions.get(posIndex).contains(x-i))
                        {
                            temp.add(x-i);
                        }
                    }
                }
                posIndex--;
                paths.add(temp);

            index++;
            //System.out.println(paths);
        }
        Boolean finished = true;
        for(List<Integer> path : paths)
        {
            if(path.size() > 1)
            {
                finished = false;
                break;
            }
        }
        if(finished)
        {
            Collections.reverse(paths);
            for(int i = 0; i < paths.size()-1; i++)
            {
                int price = paths.get(i+1).get(0) - paths.get(i).get(0);
                this.solution.add(price);
            }
            //System.out.println("Solution = " +this.solution);
            //System.out.println(this.seforimClassToTypePrices);
            return this.solution;
        }
        Collections.reverse(paths);
        List<String> keys = new ArrayList<String>(keySet);
        for(int i = 0; i < paths.size()-1; i++)
        {
            for(int j = 0; j < keys.size(); j++)
            {
                for(int k = 0; k < paths.get(i).size(); k++)
                {
                    int current = paths.get(i).get(k);
                    int price = this.seforimClassToTypePrices.get(keys.get(i)).get(j);
                    if(paths.get(i+1).contains(current+ price))
                    {
                        paths.get(i+1).clear();
                        paths.get(i+1).add(current+price);
                        continue;
                    }
                }
            }
        }
            for(int i = 0; i < paths.size()-1; i++)
            {
                if(paths.get(i+1).size() != 1 || paths.get(i).size() != 1)
                {
                    this.solution.add(Integer.MIN_VALUE);
                }
                else
                {
                    int price = paths.get(i+1).get(0) - paths.get(i).get(0);
                    this.solution.add(price);
                }
            }
            for(int i = 0; i < this.solution.size(); i++)
            {
                if(this.solution.get(i) == Integer.MIN_VALUE)
                {
                    int k = this.seforimClassToTypePrices.get(keys.get(i)).get(0);
                    this.solution.set(i, k);

                }
            }
        //System.out.println("Solution = " +this.solution);
        //System.out.println("Paths = " +paths);
        //System.out.println(this.seforimClassToTypePrices);
        return this.solution;
    }
    @Override
    public int maxAmountThatCanBeSpent (final int budget, final Map<String, List<Integer>> seforimClassToTypePrices)
    {   
        this.seforimClassToTypePrices = seforimClassToTypePrices;
        for(String s: seforimClassToTypePrices.keySet())
        {
            Collections.sort(seforimClassToTypePrices.get(s));
        }
        //first we sort
        List<List<Integer>> possibleNums = new ArrayList<List<Integer>>();
        int i = 0;
        for(List<Integer> category: seforimClassToTypePrices.values())
        {
            List<Integer> possible = new ArrayList<Integer>();
            HashSet<Integer> set = new HashSet<Integer>();
            //System.out.println("Going through the " + (i+1) + "th category");
            if(i == 0)
            {
                for(int price: category)
                {
                    if(!set.contains(price))
                    {
                        if(price <= budget)
                        {
                            possible.add(price);
                            set.add(price);
                        }
                    }
                }
            }
            else
            {
                List<Integer> pastPrices = possibleNums.get(i-1);
                for(int lastPrice: pastPrices)
                {
                    for(int price: category)
                    {
                        int addPrice = lastPrice + price;
                        if(!set.contains(addPrice))
                        {
                            if(lastPrice+price <= budget)
                            {
                                possible.add(addPrice);
                                set.add(addPrice);
                            }
                        }
                    }
                }
            }
            i++;
            possibleNums.add(possible);
        }
        //System.out.println(possibleNums);
        int lastList = possibleNums.size()-1;
        int highest = Integer.MIN_VALUE;
        for(int max: possibleNums.get(lastList))
        {
            if(max > highest)
            {
                highest = max;
            }
        }
        //System.out.println("Highest Possible: " + highest + "\n\n");
        this.possibleSolutions = possibleNums;
        this.maxSpent = highest;
        return highest;
    }
     
} // StockYourBookshelf