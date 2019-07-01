/*
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package es.uam.eps.ir.knnbandit.recommendation.basic;

import java.util.List;

import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableItemIndex;
import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableUserIndex;
import es.uam.eps.ir.ranksys.fast.preference.SimpleFastPreferenceData;
import org.jooq.lambda.tuple.Tuple3;

/**
 * Reinforcement learning version of a Popularity algorithm.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <U> Type of the users.
 * @param <I> Type of the items.
 */
public class PopRecommender<U,I> extends AbstractBasicReinforcementLearningRecommender<U,I> 
{
    /**
     * Constructor.
     * @param uIndex user index.
     * @param iIndex item index.
     * @param prefData preference data.
     * @param ignoreUnknown true if we must ignore unknown items when updating.
     */
    public PopRecommender(FastUpdateableUserIndex<U> uIndex, FastUpdateableItemIndex<I> iIndex, SimpleFastPreferenceData<U,I> prefData, boolean ignoreUnknown)
    {
        super(uIndex, iIndex, prefData, ignoreUnknown);
    }
    
    /**
     * Constructor.
     * @param uIndex user index.
     * @param iIndex item index.
     * @param prefData preference data.
     * @param ignoreUnknown true if we must ignore unknown items when updating.
     * @param notReciprocal true if we do not recommend reciprocal users, false otherwise
     */
    public PopRecommender(FastUpdateableUserIndex<U> uIndex, FastUpdateableItemIndex<I> iIndex, SimpleFastPreferenceData<U,I> prefData, boolean ignoreUnknown, boolean notReciprocal)
    {
        super(uIndex, iIndex, prefData, ignoreUnknown, notReciprocal);
    }
    
    @Override
    public void updateMethod(int uidx, int iidx, double value)
    {
        this.values[iidx]++;
    }

    @Override
    public void updateMethod(List<Tuple3<Integer,Integer,Double>> train)
    {
        for(int i = 0; i < prefData.numItems(); ++i) this.values[i] = trainData.numUsers(i);
    }
}
