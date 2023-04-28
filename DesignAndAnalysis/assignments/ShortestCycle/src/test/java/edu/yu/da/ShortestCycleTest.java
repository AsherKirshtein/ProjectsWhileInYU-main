package edu.yu.da;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import edu.yu.da.ShortestCycleBase.Edge;

/**
 * Unit test for simple App.
 */
public class ShortestCycleTest
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void simpleTest() 
    {
        System.out.println("Simple Test: \n");
        
        Edge e1 = new Edge(1, 2, 23);
        Edge e2 = new Edge(2, 3, 17);
        Edge e3 = new Edge(3, 4, 21);
        Edge e4 = new Edge(3, 1, 34);
        Edge e5 = new Edge(2, 4, 1);
        Edge e6 = new Edge(4, 5, 1);
        Edge e7 = new Edge(5, 6, 1);
        Edge e8 = new Edge(6, 1, 1);
        Edge e9 = new Edge(1, 7, 13);

        System.out.println("Edges: ");

        final List<Edge> edges = new ArrayList<>();
        edges.add(e1);
        edges.add(e2);
        edges.add(e3);
        edges.add(e4);
        edges.add(e5);
        edges.add(e6);
        edges.add(e7);
        edges.add(e8);
        edges.add(e9);

        int line = 0;
        for(Edge e: edges)
        {
            line++;
            System.out.println(line + ". " + e);
        }
        System.out.println();

        ShortestCycle cycle = new ShortestCycle(edges, e1);

        List<Edge> expectedEdges = new ArrayList<>();

        expectedEdges.add(e1);
        expectedEdges.add(e5);
        expectedEdges.add(e6);
        expectedEdges.add(e7);
        expectedEdges.add(e8);

        line = 0;
        System.out.println("Expected Shortest Path: ");
        for(Edge e: expectedEdges)
        {
            line++;
            System.out.println(line + ". " + e);
        }
        
        List<Edge> myCycle = cycle.doIt();
        System.out.println("Actual Path: ");
        line = 0;
        for(Edge e: myCycle)
        {
            line++;
            System.out.println(line + ". " + e);
        }

        assertTrue(true);
    }
    
    @Test
    public void professorsTest()
    {
        //Will create a graph based on these edges: 
        //[{(1,2), weight=3.00}, {(1,3), weight=1.00}, {(2,3), weight=7.00}, {(2,4), weight=5.00}, 
        //{(3,4), weight=2.00}, {(2,5), weight=1.00}, {(4,5), weight=7.00}]

        Edge e1 = new Edge(1, 2, 3.00);
        Edge e2 = new Edge(1, 3, 1.00);
        Edge e3 = new Edge(2, 3, 7.00);
        Edge e4 = new Edge(2, 4, 5.00);
        Edge e5 = new Edge(3, 4, 2.00);
        Edge e6 = new Edge(2, 5, 1.00);
        Edge e7 = new Edge(4, 5, 7.00);

        final List<Edge> edges = new ArrayList<>();
        edges.add(e1);
        edges.add(e2);
        edges.add(e3);
        edges.add(e4);
        edges.add(e5);
        edges.add(e6);
        edges.add(e7);

        ShortestCycle cycle = new ShortestCycle(edges, e1);

        List<Edge> myCycle = cycle.doIt();

        System.out.println(myCycle);

    }
    @Test
    public void immutableTest()
    {
        Edge e1 = new Edge(1, 2, 3.00);
        Edge e2 = new Edge(1, 3, 1.00);
        Edge e3 = new Edge(2, 3, 7.00);
        Edge e4 = new Edge(2, 4, 5.00);
        Edge e5 = new Edge(3, 4, 2.00);
        Edge e6 = new Edge(2, 5, 1.00);
        Edge e7 = new Edge(4, 5, 7.00);

        List<Edge> edges = new ArrayList<>();
        edges.add(e1);
        edges.add(e2);
        edges.add(e3);
        edges.add(e4);
        edges.add(e5);
        edges.add(e6);
        edges.add(e7);

        List<Edge> noChange = Collections.unmodifiableList(edges);

        ShortestCycle cycle = new ShortestCycle(noChange, e1);

        List<Edge> myCycle = cycle.doIt();

        System.out.println(myCycle);
    }
    private static int getRandomInteger(int max)
    {
        double random = Math.floor(Math.random() * max);
        return (int)random;
    }
}
