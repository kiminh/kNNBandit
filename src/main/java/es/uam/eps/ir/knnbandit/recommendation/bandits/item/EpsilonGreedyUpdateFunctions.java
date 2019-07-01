/*
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package es.uam.eps.ir.knnbandit.recommendation.bandits.item;

/**
 * Different epsilon greedy update functions.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class EpsilonGreedyUpdateFunctions 
{
    /**
     * Updates the value of the corresponding arm as if the reward was stationary.
     * @return a Epsilon-Greedy update function that updates the value of the corresponding
     * arm as if the reward was stationary.
     */
    public static EpsilonGreedyUpdateFunction stationary()
    {
        return (double oldvalue, double reward, double oldsum, double increment, double numtimes) -> 
        {
            if(numtimes == 0) return reward;
            else return oldvalue + (reward - oldvalue)/(numtimes + 0.0);
        };
    }
    
    /**
     * Updates the value of the corresponding arm, giving more weight to new rewards.
     * @param alpha the weight of the new value compared with the old (1-alpha)
     * @return the Epsilon-Greedy update function for non-stationary rewards.
     */
    public static EpsilonGreedyUpdateFunction nonstationary(double alpha)
    {
        return (double oldvalue, double reward, double oldsum, double increment, double numtimes) -> oldvalue + alpha*(reward-oldvalue);
    }
    
    /**
     * Updates the value of the arm considering the rewards for the rest of the arms.
     * @return the corresponding Epsilon-Greedy update function. 
     */
    public static EpsilonGreedyUpdateFunction useall()
    {
        return (double oldvalue, double reward, double oldsum, double increment, double numtimes) -> (oldvalue*oldsum + reward)/(oldsum + increment);
    }
    
    /**
     * Updates the value of the arm by adding the reward to the old value.
     * @return 
     */
    public static EpsilonGreedyUpdateFunction count()
    {
        return (double oldvalue, double reward, double oldsum, double increment, double numtimes) -> (oldvalue + reward);
    }
    
}
