/*
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package es.uam.eps.ir.knnbandit.recommendation.bandits.functions;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;

/**
 * Functions that determine the value of arms.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class ValueFunctions 
{
    /**
     * The value keeps the same.
     * @return a function that keeps the value.
     */
    public static ValueFunction identity()
    {
        return (int uidx, int iidx, double currentValue, double numtimes) -> 
        {
            return currentValue;
        };
    }

    /**
     * Function that takes the neighbors into account.
     * @param values similarities between the user
     * @return
     */
    public static ValueFunction neighbor(Int2DoubleMap values)
    {
        return (int uidx, int iidx, double currentValue, double numtimes) -> 
        {
            if(values.containsKey(iidx))
            {
                return values.get(iidx)*currentValue;
            }
            else
            {
                return currentValue*currentValue;
            }
        };
    }

    /**
     * A value function which takes the reward as val*(1-val)
     * @return
     */
    public static ValueFunction unseenfunction()
    {
        return (int uidx, int iidx, double currentValue, double numtimes) ->
        {
            return currentValue*(1.0-currentValue);
        };
    }
}
