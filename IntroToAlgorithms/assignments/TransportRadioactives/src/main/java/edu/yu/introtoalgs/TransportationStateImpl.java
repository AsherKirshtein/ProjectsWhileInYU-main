
package edu.yu.introtoalgs;

/** Implements the TransportationState interface.  
 *
 *
 * Students may ONLY use the specified constructor, and may (perhaps even
 * encouraged to) add as many other methods as they choose.
 *
 * @author Asher Kirshtein
 */

import static edu.yu.introtoalgs.TransportationState.Location.*;

import java.security.PublicKey;

public class TransportationStateImpl implements TransportationState { 

    public int mithiumAtSrc;
    public int cathiumAtSrc;
    public Location truckLocation;
    private int totalMithium;
    private int totalCathium;
    public int[] truck;

    public TransportationStateImpl(final int mithiumAtSrc, final int cathiumAtSrc, final Location truckLocation, final int totalMithium, final int totalCathium)
    {
        if(!(mithiumAtSrc >= 0))
        {
            throw new IllegalArgumentException("mithium at src must be greater than or equal to 0");
        }
        if(!(cathiumAtSrc >= 0))
        {
            throw new IllegalArgumentException("cathium at src must be greater than or equal to 0");
        }
        if(truckLocation == null)
        {
            throw new IllegalArgumentException("truck location must not be null");
        }
        this.mithiumAtSrc = mithiumAtSrc;
        this.cathiumAtSrc = cathiumAtSrc;
        this.truckLocation = truckLocation;
        this.totalMithium = totalMithium;
        this.totalCathium = totalCathium;
        this.truck = new int[2];
    } 
    
    
    @Override
    public int getMithiumSrc()
    { 
        return this.mithiumAtSrc; 
    }
    public void drive(int cathium, int mithium)
    {
        this.cathiumAtSrc -= cathium;
        this.mithiumAtSrc -= mithium;
    }
    @Override
    public int getCathiumSrc()
    { 
      return this.cathiumAtSrc; 
    }
    
    @Override
    public int getMithiumDest()
    { 
        return (this.totalMithium - this.mithiumAtSrc); 
    }
        
    @Override
    public int getCathiumDest()
    { 
        return (this.totalCathium - this.cathiumAtSrc); 
    }
        
    @Override
    public Location truckLocation()
    { 
        return this.truckLocation; 
    }

    @Override
    public int getTotalMithium() 
    { 
        return this.totalMithium; 
    }

    @Override
    public int getTotalCathium() 
    { 
        return this.totalCathium; 
    }
    @Override 
    public String toString()
    {
        String state = "\nMithium at source: " + this.mithiumAtSrc + " kg\n";
        state += "Cathium at source: " + this.cathiumAtSrc + " kg\n";
        state += "Truck Location: " + this.truckLocation + "\n";
        state += "Mithium at destination: " + getMithiumDest() + " kg\n";
        state += "Cathium at destination: " + getCathiumDest() + " kg\n";
        state += "Cathium on truck: " + truck[1] + " kg\n";
        state += "Mithium on truck: " + truck[0] + " kg\n";
        return state;
    }
}  