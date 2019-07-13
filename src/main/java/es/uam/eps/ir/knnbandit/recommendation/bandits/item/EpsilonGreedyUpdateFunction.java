/*
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.knnbandit.recommendation.bandits.item;

/**
 * Functions that update the arm values in an epsilon-greedy bandit.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
@FunctionalInterface
public interface EpsilonGreedyUpdateFunction 
{
    /**
     * Constructor.
     * @param oldValue old arm value.
     * @param reward the obtained reward.
     * @param oldSum the sum of all the arms.
     * @param increment the sum of the increments of all arms.
     * @param numTimes number of times the arm has been selected.
     * @return the new value of the arm.
     */
    public double apply(double oldValue, double reward, double oldSum, double increment, double numTimes);
}
