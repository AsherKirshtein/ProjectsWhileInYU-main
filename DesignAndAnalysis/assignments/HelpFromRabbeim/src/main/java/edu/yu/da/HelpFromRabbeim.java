package edu.yu.da;

/** Implements the HelpFromRabbeimI interface.
 *
 * Students MAY NOT change the provided constructor signature!
 * 
 * @author Avraham Leff
 */

import java.util.*;

import static edu.yu.da.HelpFromRabbeimI.HelpTopics.*;
import static edu.yu.da.HelpFromRabbeimI.Rebbe;

public class HelpFromRabbeim implements HelpFromRabbeimI {

  /** No-op constructor
   */
  public HelpFromRabbeim() {
    // no-op, students may change the implementation
  }

  @Override
  public Map<Integer, HelpTopics> scheduleIt(List<Rebbe> rabbeim, Map<HelpTopics, Integer> requestedHelp)
  {  
    List<FlowEdge> edges = new LinkedList<>();
    for(int i = 0; i < rabbeim.size(); i++)
    {
        FlowEdge e = new FlowEdge(0, i+1,1,0);
        edges.add(e);
        //System.out.println(e);
    }
    int count = 0;
    for(int i = 0; i < rabbeim.size(); i++)
    {
        Rebbe r = rabbeim.get(i);
        for(HelpTopics h: r._helpTopics)
        {
            FlowEdge e = new FlowEdge(i+1,rabbeim.size() + h.ordinal()+1 ,1,0);
            edges.add(e);
            //System.out.println(e);
        }
    }
    for(HelpTopics h: requestedHelp.keySet())
    {
        FlowEdge e = new FlowEdge(h.ordinal() + 1 +rabbeim.size(), requestedHelp.size() + rabbeim.size()+1 ,requestedHelp.get(h),0);
        edges.add(e);
        count += requestedHelp.get(h);
        //System.out.println(e);
    }

    FlowNetwork network = new FlowNetwork(requestedHelp.size() + rabbeim.size()+2);
    for(FlowEdge e: edges)
    {
        network.addEdge(e);
    }
    FordFulkerson ff = new FordFulkerson(network, 0, requestedHelp.size() + rabbeim.size()+1);
    Map<Integer, HelpTopics> schedule = new HashMap<>();
    for (int v = 0; v < network.V(); v++) {
        for (FlowEdge e : network.adj(v)) 
        {
            if ((v == e.from()) && e.flow() > 0)
            {
                if(e.from() != 0 && e.to() != requestedHelp.size() + rabbeim.size()+1)
                {
                    System.out.println("   " + e);
                    HelpTopics value = HelpTopics.values()[e.to() - rabbeim.size()-1];
                    schedule.put(rabbeim.get(e.from()-1)._id, value);       
                }
            }
        }
    }
    if(count != ff.value())
    {
        System.out.println("Max flow: " + ff.value() + " required: " + count);
        schedule = Collections.emptyMap();
    }
    System.out.println(schedule);
    return schedule;
  }

  public class FlowEdge 
  {  
    private static final double FLOATING_POINT_EPSILON = 1E-10;

    private final int v;             
    private final int w;             
    private final double capacity;   
    private double flow;             

    public FlowEdge(int v, int w, double capacity) 
    {
        if (v < 0) throw new IllegalArgumentException("vertex index must be a non-negative integer");
        if (w < 0) throw new IllegalArgumentException("vertex index must be a non-negative integer");
        if (!(capacity >= 0.0)) throw new IllegalArgumentException("Edge capacity must be non-negative");
        this.v         = v;
        this.w         = w;  
        this.capacity  = capacity;
        this.flow      = 0.0;
    }

    
    public FlowEdge(int v, int w, double capacity, double flow) 
    {
        if (v < 0) throw new IllegalArgumentException("vertex index must be a non-negative integer");
        if (w < 0) throw new IllegalArgumentException("vertex index must be a non-negative integer");
        if (!(capacity >= 0.0))  throw new IllegalArgumentException("edge capacity must be non-negative");
        if (!(flow <= capacity)) throw new IllegalArgumentException("flow exceeds capacity");
        if (!(flow >= 0.0))      throw new IllegalArgumentException("flow must be non-negative");
        this.v         = v;
        this.w         = w;  
        this.capacity  = capacity;
        this.flow      = flow;
    }

    public FlowEdge(FlowEdge e) 
    {
        this.v         = e.v;
        this.w         = e.w;
        this.capacity  = e.capacity;
        this.flow      = e.flow;
    }
    public int from() 
    {
        return v;
    }  
    public int to() 
    {
        return w;
    }  
    public double capacity() 
    {
        return capacity;
    }
    public double flow() 
    {
        return flow;
    }
    public int other(int vertex) {
        if      (vertex == v) return w;
        else if (vertex == w) return v;
        else throw new IllegalArgumentException("invalid endpoint");
    }
    public double residualCapacityTo(int vertex) 
    {
        if      (vertex == v) return flow;              
        else if (vertex == w) return capacity - flow;   
        else throw new IllegalArgumentException("invalid endpoint");
    }
    public void addResidualFlowTo(int vertex, double delta) 
    {
        if (!(delta >= 0.0)) throw new IllegalArgumentException("Delta must be non-negative");

        if      (vertex == v) flow -= delta;           
        else if (vertex == w) flow += delta;           
        else throw new IllegalArgumentException("invalid endpoint");


        if (Math.abs(flow) <= FLOATING_POINT_EPSILON)
            flow = 0;
        if (Math.abs(flow - capacity) <= FLOATING_POINT_EPSILON)
            flow = capacity;

        if (!(flow >= 0.0))      throw new IllegalArgumentException("Flow is negative");
        if (!(flow <= capacity)) throw new IllegalArgumentException("Flow exceeds capacity");
    }
    public String toString() 
    {
        return v + "->" + w + " " + flow + "/" + capacity;
    }
  }

  public class FlowNetwork {
    private final String NEWLINE = System.getProperty("line.separator");

    private final int V;
    private int E;
    private List<FlowEdge>[] adj;
    
    public FlowNetwork(int V) 
    {
        if (V < 0) throw new IllegalArgumentException("Number of vertices in a Graph must be non-negative");
        this.V = V;
        this.E = 0;
        adj = (List<FlowEdge>[]) new LinkedList[V];
        for (int v = 0; v < V; v++)
            adj[v] = new LinkedList<FlowEdge>();
    }
    public int V() {
        return V;
    }
    public int E() {
        return E;
    }
    private void validateVertex(int v) 
    {
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }
    public void addEdge(FlowEdge e) 
    {
        int v = e.from();
        int w = e.to();
        validateVertex(v);
        validateVertex(w);
        adj[v].add(e);
        adj[w].add(e);
        E++;
    }
    public Iterable<FlowEdge> adj(int v) 
    {
        validateVertex(v);
        return adj[v];
    }

    public Iterable<FlowEdge> edges() 
    {
        List<FlowEdge> list = new LinkedList<FlowEdge>();
        for (int v = 0; v < V; v++)
            for (FlowEdge e : adj(v)) 
            {
                if (e.to() != v)
                    list.add(e);
            }
        return list;
    }
    public String toString() 
    {
        StringBuilder s = new StringBuilder();
        s.append(V + " " + E + NEWLINE);
        for (int v = 0; v < V; v++) 
        {
            s.append(v + ":  ");
            for (FlowEdge e : adj[v]) 
            {
                if (e.to() != v) s.append(e + "  ");
            }
            s.append(NEWLINE);
        }
        return s.toString();
    }
  }
  public class FordFulkerson 
  {
    private static final double FLOATING_POINT_EPSILON = 1E-11;

    private final int V;          
    private boolean[] marked;     
    private FlowEdge[] edgeTo;    
    private double value;         

    public FordFulkerson(FlowNetwork G, int s, int t) {
        V = G.V();
        validate(s);
        validate(t);
        if (s == t)               throw new IllegalArgumentException("Source equals sink");
        if (!isFeasible(G, s, t)) throw new IllegalArgumentException("Initial flow is infeasible");

        value = excess(G, t);
        while (hasAugmentingPath(G, s, t)) 
        {
            double bottle = Double.POSITIVE_INFINITY;
            for (int v = t; v != s; v = edgeTo[v].other(v)) 
            {
                bottle = Math.min(bottle, edgeTo[v].residualCapacityTo(v));
            }
            for (int v = t; v != s; v = edgeTo[v].other(v)) 
            {
                edgeTo[v].addResidualFlowTo(v, bottle); 
            }
            value += bottle;
        }
        assert check(G, s, t);
    }
    
    public double value()  
    {
        return value;
    }

    public boolean inCut(int v) 
    {
        validate(v);
        return marked[v];
    }

    private void validate(int v)  
    {
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }

    private boolean hasAugmentingPath(FlowNetwork G, int s, int t) 
    {
        edgeTo = new FlowEdge[G.V()];
        marked = new boolean[G.V()];

        Queue<Integer> queue = new LinkedList<Integer>();
        queue.add(s);
        marked[s] = true;
        while (!queue.isEmpty() && !marked[t]) 
        {
            int v = queue.remove();
            for (FlowEdge e : G.adj(v)) 
            {
                int w = e.other(v);
                if (e.residualCapacityTo(w) > 0) 
                {
                    if (!marked[w]) 
                    {
                        edgeTo[w] = e;
                        marked[w] = true;
                        queue.add(w);
                    }
                }
            }
        }
        return marked[t];
    }
    private double excess(FlowNetwork G, int v) 
    {
        double excess = 0.0;
        for (FlowEdge e : G.adj(v)) 
        {
            if (v == e.from()) excess -= e.flow();
            else               excess += e.flow();
        }
        return excess;
    }
    private boolean isFeasible(FlowNetwork G, int s, int t) 
    {

        for (int v = 0; v < G.V(); v++) 
        {
            for (FlowEdge e : G.adj(v)) 
            {
                if (e.flow() < -FLOATING_POINT_EPSILON || e.flow() > e.capacity() + FLOATING_POINT_EPSILON) 
                {
                    System.err.println("Edge does not satisfy capacity constraints: " + e);
                    return false;
                }
            }
        }
        if (Math.abs(value + excess(G, s)) > FLOATING_POINT_EPSILON) 
        {
            System.err.println("Excess at source = " + excess(G, s));
            System.err.println("Max flow         = " + value);
            return false;
        }
        if (Math.abs(value - excess(G, t)) > FLOATING_POINT_EPSILON) 
        {
            System.err.println("Excess at sink   = " + excess(G, t));
            System.err.println("Max flow         = " + value);
            return false;
        }
        for (int v = 0; v < G.V(); v++) 
        {
            if (v == s || v == t) continue;
            else if (Math.abs(excess(G, v)) > FLOATING_POINT_EPSILON) {
                System.err.println("Net flow out of " + v + " doesn't equal zero");
                return false;
            }
        }
        return true;
    }

    private boolean check(FlowNetwork G, int s, int t) 
    {
        if (!isFeasible(G, s, t)) 
        {
            System.err.println("Flow is infeasible");
            return false;
        }
        if (!inCut(s)) 
        {
            System.err.println("source " + s + " is not on source side of min cut");
            return false;
        }
        if (inCut(t)) 
        {
            System.err.println("sink " + t + " is on source side of min cut");
            return false;
        }
        double mincutValue = 0.0;
        for (int v = 0; v < G.V(); v++)
        {
            for (FlowEdge e : G.adj(v)) 
            {
                if ((v == e.from()) && inCut(e.from()) && !inCut(e.to()))
                    mincutValue += e.capacity();
            }
        }

        if (Math.abs(mincutValue - value) > FLOATING_POINT_EPSILON)
        {
            System.err.println("Max flow value = " + value + ", min cut value = " + mincutValue);
            return false;
        }
        return true;
    }
  }
}
