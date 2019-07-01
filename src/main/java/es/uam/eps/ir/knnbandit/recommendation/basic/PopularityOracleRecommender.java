/*
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package es.uam.eps.ir.knnbandit.recommendation.basic;

import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableItemIndex;
import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableUserIndex;
import es.uam.eps.ir.ranksys.fast.preference.SimpleFastPreferenceData;

/**
 * Oracle popularity recommender. Recommends users according to the true values of popularity
 * of the dataset
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <U> Type of the users.
 * @param <I> Type of the items.
 */
public class PopularityOracleRecommender<U,I> extends AbstractBasicReinforcementLearningRecommender<U,I>
{
    /**
     * Constructor.
     * @param uIndex user index.
     * @param iIndex item index.
     * @param prefData preference data.
     * @param ignoreUnknown true if we want to ignore missing ratings at updating, false if we want to treat them as failures.
     */
    public PopularityOracleRecommender(FastUpdateableUserIndex<U> uIndex, FastUpdateableItemIndex<I> iIndex, SimpleFastPreferenceData<U,I> prefData, boolean ignoreUnknown)
    {
        super(uIndex, iIndex, prefData, ignoreUnknown);
        for(int i = 0; i < iIndex.numItems();++i)
        {
            this.values[i] = this.prefData.getIidxPreferences(i).mapToDouble(x -> x.v2).sum();
        }
        
    }
    
    /**
     * Constructor.
     * @param uIndex user index.
     * @param iIndex item index.
     * @param prefData preference data.
     * @param ignoreUnknown true if we want to ignore missing ratings at updating, false if we want to treat them as failures.
     * @param notReciprocal true if we do not recommend reciprocal users, false otherwise
     */
    public PopularityOracleRecommender(FastUpdateableUserIndex<U> uIndex, FastUpdateableItemIndex<I> iIndex, SimpleFastPreferenceData<U,I> prefData, boolean ignoreUnknown, boolean notReciprocal)
    {
        super(uIndex, iIndex, prefData, ignoreUnknown, notReciprocal);
        for(int i = 0; i < iIndex.numItems();++i)
        {
            this.values[i] = this.prefData.getIidxPreferences(i).mapToDouble(x -> x.v2).sum();
        }
    }

    @Override
    public void updateMethod(int uidx, int iidx, double value)
    {
        
    }
}
