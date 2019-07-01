/*
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package es.uam.eps.ir.knnbandit.recommendation.knn.users;

import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableItemIndex;
import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableUserIndex;
import es.uam.eps.ir.knnbandit.recommendation.knn.similarities.UpdateableSimilarity;
import es.uam.eps.ir.ranksys.fast.preference.SimpleFastPreferenceData;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;


/**
 * Probabilistic version of a user-based kNN algorithm.
 *
 * Cañamares, R., Castells, P.  A Probabilistic Reformulation of Memory-Based Collaborative Filtering – Implications on Popularity Biases.
 * 40th Annual International ACM SIGIR Conference on Research and Development in Information Retrieval (SIGIR 2017).
 * Tokyo, Japan, August 2017, pp. 215-224.
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 *
 * @param <U> Type of the users.
 * @param <I> Type of the items.
 */
public class IncrementalProbabilisticUserBasedkNN<U,I> extends AbstractIncrementalUserBasedKNN<U,I>
{
    /**
     * Sum of the ratings of each user
     */
    private final Int2DoubleMap userpops;

    /**
     * Constructor
     * @param uIndex user index.
     * @param iIndex item index.
     * @param prefData training data.
     * @param ignoreUnknown true if missing ratings should be ignored, true otherwise
     * @param ignoreZeroes true if we ignore zero ratings when updating.
     * @param similarities similarities.
     * @param k number of neighbors.
     */
    public IncrementalProbabilisticUserBasedkNN(FastUpdateableUserIndex<U> uIndex, FastUpdateableItemIndex<I> iIndex, SimpleFastPreferenceData<U, I> prefData, boolean ignoreUnknown, boolean ignoreZeroes, UpdateableSimilarity similarities, int k)
    {
        super(uIndex, iIndex, prefData, ignoreUnknown, ignoreZeroes, k, similarities);
        this.userpops = new Int2DoubleOpenHashMap();
        this.trainData.getAllUidx().forEach(uidx -> this.userpops.put(uidx, 0.0));    
    }
    
    /**
     * Constructor
     * @param uIndex user index.
     * @param iIndex item index.
     * @param prefData training data.
     * @param ignoreUnknown true if missing ratings should be ignored, true otherwise
     * @param notReciprocal true if we do not recommend reciprocal users, false otherwise
     * @param ignoreZeroes true if we ignore zero ratings when updating.
     * @param similarities similarities.
     * @param k number of neighbors to use.
     */
    public IncrementalProbabilisticUserBasedkNN(FastUpdateableUserIndex<U> uIndex, FastUpdateableItemIndex<I> iIndex, SimpleFastPreferenceData<U, I> prefData, boolean ignoreUnknown, boolean notReciprocal, boolean ignoreZeroes, UpdateableSimilarity similarities, int k)
    {
        super(uIndex, iIndex, prefData, ignoreUnknown, ignoreZeroes, notReciprocal, k, similarities);
        this.userpops = new Int2DoubleOpenHashMap();
        this.trainData.getAllUidx().forEach(uidx -> this.userpops.put(uidx, 0.0));
    }
    
   
    @Override
    public void updateMethod(int uidx, int iidx, double value)
    {
        this.userpops.put(uidx, this.userpops.get(uidx) + value);
        this.trainData.getIidxPreferences(iidx).forEach(vidx -> 
        {
            this.sim.update(uidx, vidx.v1, iidx, value, vidx.v2); 
        });
    }

    @Override
    protected double score(int vidx, double rating)
    {
        return rating/this.userpops.get(vidx);
    }

}

