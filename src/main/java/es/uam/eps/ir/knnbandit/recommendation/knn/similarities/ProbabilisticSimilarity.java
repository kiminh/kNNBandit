/*
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package es.uam.eps.ir.knnbandit.recommendation.knn.similarities;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import java.util.function.IntToDoubleFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.ranksys.core.util.tuples.Tuple2id;

/**
 * kNN similarity, based on the similarity on the paper:
 *
 * Cañamares, R., Castells, P.  A Probabilistic Reformulation of Memory-Based Collaborative Filtering – Implications on Popularity Biases.
 * 40th Annual International ACM SIGIR Conference on Research and Development in Information Retrieval (SIGIR 2017).
 * Tokyo, Japan, August 2017, pp. 215-224.
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class ProbabilisticSimilarity implements UpdateableSimilarity
{
    /**
     * Similarities.
     */
    private final double sims[][];
    
    /**
     * The sum of the ratings.
     */
    private double sum;
    /**
     * The last updated user.
     */
    private int lastUser;
    /**
     * The last updated item.
     */
    private int lastItem;
    /**
     * The number of users.
     */
    private final int numUsers;
    
    /**
     * Constructor.
     * @param numUsers the number of users. 
     */
    public ProbabilisticSimilarity(int numUsers)
    {
        this.sims = new double[numUsers][numUsers];
        this.lastUser = -1;
        this.lastItem = -1;
        this.sum = 0.0;
        this.numUsers = numUsers;
    }
    
    @Override
    public void update(int uidx, int vidx, int iidx, double uval, double vval)
    {
        if(!Double.isNaN(vval))
        {
            sims[uidx][vidx] += uval*vval;
            sims[vidx][uidx] += uval*vval;
        }
        
        if(uidx != lastUser || iidx != lastItem)
        {
            sum += uval;
            this.lastUser = uidx;
            this.lastItem = iidx;
        }
    }

    @Override
    public IntToDoubleFunction similarity(int idx)
    {
        return (int idx2) -> 
        {
            if(sum > 0) return this.sims[idx][idx2]/(sum*sum);
            else return 0.0;
        };
    }

    @Override
    public Stream<Tuple2id> similarElems(int idx)
    {
        return IntStream.range(0, this.numUsers).filter(i -> i != idx).mapToObj(i -> new Tuple2id(i, similarity(idx, i))).filter(x -> x.v2 > 0.0);
    }

    @Override
    public void update(FastPreferenceData<?,?> prefData)
    {
        prefData.getAllUidx().forEach(uidx ->
        {
            prefData.getAllUidx().forEach(vidx ->
            {
                this.sims[uidx][vidx] = 0.0;
            });
        });

        this.sum = prefData.getAllUidx().mapToDouble(uidx ->
        {
            return prefData.getUidxPreferences(uidx).mapToDouble(iidx ->
            {
               prefData.getIidxPreferences(iidx.v1).forEach(vidx ->
               {
                  this.sims[uidx][vidx.v1] +=  iidx.v2*vidx.v2;
               });

               return iidx.v2;
            }).sum();
        }).sum();
    }

}
