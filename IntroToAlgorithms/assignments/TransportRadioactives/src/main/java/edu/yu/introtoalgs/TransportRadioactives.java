package edu.yu.introtoalgs;

/** Specifies the interface for generating a sequence of transportation states
 * that moves the radioactives from src to dest per the requirements doc.
 *
 * @author Asher Kirshtein
 */

import java.util.*;

import edu.yu.introtoalgs.TransportationState.Location;

public class TransportRadioactives
{

    /**
     * Computes a sequence of "transport radioactives" movements between the src
     * and the dest such that all of the initial methium and initial cathium are
     * transported safely from the src to the dest. Each movement must respect
     * the constraints specified in the requirements doc.
     *
     * @param initialMithium initial amount of mithium (in kg) at the src
     * @param initialCathium initial amount of cathium (in kg) at the src
     * @return List of "transport radioactives" movements between the src and the
     *         dest (if such a sequence can be computed), or an empty List if no
     *         such
     *         sequence can be computed under the specified constraints.
     */
    public static List<TransportationState> transportIt(final int initialMithium, final int initialCathium)
    {
        if(initialCathium >= initialMithium)
        {
            return new ArrayList<>();
        }
        int currentMithiumAtSrc = initialMithium;
        int currentCathiumAtSrc = initialCathium;
        int cathiumAtDest = 0;
        int mithiumAtDest = 0;
        Location truckLocation = Location.SRC;

        List<TransportationState> moves = new ArrayList<>();
        while(currentCathiumAtSrc > 0 || currentMithiumAtSrc > 0)
        {
            TransportationState state = new TransportationStateImpl(currentMithiumAtSrc, currentCathiumAtSrc, truckLocation, initialMithium, initialCathium);
            //System.out.println(state);
            moves.add(state);
            if(currentCathiumAtSrc > currentMithiumAtSrc && currentMithiumAtSrc > 0)
            {
                System.out.println("\n\nEXPLOSION!!!!!\n\n");
                System.out.println("Something went wrong at the SRC");
                return null;
            }
            if(cathiumAtDest > mithiumAtDest && mithiumAtDest > 0)
            {
                System.out.println("\n\nEXPLOSION!!!!!\n\n");
                System.out.println("Something went wrong at the DEST");
                return null;
            }
            if(truckLocation == Location.SRC)
            {
                truckLocation = Location.DEST;
                if(currentCathiumAtSrc == currentMithiumAtSrc || currentCathiumAtSrc + 1 == currentMithiumAtSrc)
                {
                    currentCathiumAtSrc--;
                    currentMithiumAtSrc--;
                    cathiumAtDest++;
                    mithiumAtDest++;
                }
                else if(currentCathiumAtSrc + 2 <= currentMithiumAtSrc)
                {
                    mithiumAtDest += 2;
                    currentMithiumAtSrc-=2;
                }
            }
            else if(truckLocation == Location.DEST)
            {
                truckLocation = Location.SRC;
                if(mithiumAtDest > cathiumAtDest)
                {
                    mithiumAtDest--;
                    currentMithiumAtSrc++;
                }
                else if(mithiumAtDest == cathiumAtDest)
                {
                    cathiumAtDest--;
                    currentCathiumAtSrc++;
                }
            }
            
        }
        TransportationState finalState = new TransportationStateImpl(currentMithiumAtSrc, currentCathiumAtSrc, truckLocation, initialMithium, initialCathium);
        //System.out.println(finalState);
        moves.add(finalState);
        return moves;
    }
} // TransportRadioactives
