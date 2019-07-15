/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.recommendation.basic;

import java.util.List;

import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableItemIndex;
import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableUserIndex;
import es.uam.eps.ir.ranksys.fast.preference.SimpleFastPreferenceData;
import org.jooq.lambda.tuple.Tuple3;

/**
 * Reinforcement learning version of a relevant popularity algorithm algorithm.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <U> Type of the users.
 * @param <I> Type of the items.
 */
public class PopRecommender<U,I> extends AbstractBasicInteractiveRecommender<U,I>
{
    /**
     * Relevance threshold. 
     */
    public final double threshold;
    
    /**
     * Constructor.
     * @param uIndex user index.
     * @param iIndex item index.
     * @param prefData preference data.
     * @param ignoreUnknown true if we must ignore unknown items when updating.
     * @param threshold relevance threshold
     */
    public PopRecommender(FastUpdateableUserIndex<U> uIndex, FastUpdateableItemIndex<I> iIndex, SimpleFastPreferenceData<U,I> prefData, boolean ignoreUnknown, double threshold)
    {
        super(uIndex, iIndex, prefData,ignoreUnknown);
        this.threshold = threshold;
    }
    
    /**
     * Constructor.
     * @param uIndex user index.
     * @param iIndex item index.
     * @param prefData preference data.
     * @param ignoreUnknown true if we must ignore unknown items when updating.
     * @param threshold relevance threshold
     * @param notReciprocal true if we do not recommend reciprocal social links, false otherwise
     */
    public PopRecommender(FastUpdateableUserIndex<U> uIndex, FastUpdateableItemIndex<I> iIndex, SimpleFastPreferenceData<U,I> prefData, boolean ignoreUnknown, double threshold, boolean notReciprocal)
    {
        super(uIndex, iIndex, prefData,ignoreUnknown, notReciprocal);
        this.threshold = threshold;
    }
    
    @Override
    public void updateMethod(int uidx, int iidx, double value)
    {
        this.values[iidx] += (value >= threshold ? 1.0 : 0.0);
    }
    
    @Override
    public void updateMethod(List<Tuple3<Integer,Integer,Double>> train)
    {
        for(int iidx = 0; iidx < this.prefData.numItems(); ++iidx)
        this.values[iidx] = this.trainData.getIidxPreferences(iidx).filter(vidx -> vidx.v2 > 0).count();
    }

}
