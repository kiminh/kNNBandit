/*
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package es.uam.eps.ir.knnbandit.recommendation.bandits.item;

import es.uam.eps.ir.knnbandit.recommendation.bandits.functions.ValueFunction;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Random;

/**
 * Variable Epsilon-Greedy item bandit.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <U> Type of the users.
 * @param <I> Type of the items.
 */
public class EpsilonTGreedyItemBandit<U,I> extends ItemBandit<U,I>
{
    /**
     * Slope parameter.
     */
    private final double alpha;
    /**
     * Values of each arm.
     */
    double[] values;
    /**
     * Number of times a bandit has been selected.
     */
    double[] numtimes;
    /**
     * The sum of the values.
     */
    double sumvalues;
    /**
     * The number of items.
     */
    private final int numItems;
    /**
     * Number of iterations.
     */
    private int numIter;

    /**
     * Random number generator.
     */
    private final Random rng = new Random();
    /**
     * Epsilon greedy update function.
     */
    private final EpsilonGreedyUpdateFunction updatefunct;
    
    /**
     * Constructor.
     * @param alpha parameter for indicating how many ratings we must have for obtaining the maximum value for  
     * @param numItems 
     * @param updatefunct an update function.
     */
    public EpsilonTGreedyItemBandit(double alpha, int numItems, EpsilonGreedyUpdateFunction updatefunct)
    {
        super();
        this.alpha = alpha;
        this.numItems = numItems;
        this.sumvalues = 0.0;
        this.values = new double[numItems];
        this.numtimes = new double[numItems];
        this.updatefunct = updatefunct;
        this.numIter = 1;
    }
    
    @Override
    public int next(int uidx, int[] available, ValueFunction valf)
    {
        if(available == null || available.length == 0)
            return -1;
        if(available.length == 1)
        {
            return available[0];
        }
        else
        {
            double epsilon = Math.min(1.0, this.alpha*numItems/(numIter+0.0));
            if(rng.nextDouble() < epsilon)
            {
                int item = untierng.nextInt(available.length);
                return available[item];
            }
            else
            {
                double max = Double.NEGATIVE_INFINITY;
                IntList top = new IntArrayList();
                
                for(int i : available)
                {
                    double val = valf.apply(uidx, i, values[i], numtimes[i]);
                    if(val > max)
                    {
                        max = val;
                        top = new IntArrayList();
                        top.add(i);
                    }
                    else if(val == max)
                    {
                        top.add(i);
                    }
                }
                
                int size = top.size();
                int iidx;
                if(size == 1)
                {
                    iidx = top.get(0);
                }
                else
                {
                    iidx = top.get(untierng.nextInt(size));
                }
                
                return iidx;
            }
        }
    }
    
    @Override
    public int next(int uidx, IntList available, ValueFunction valf)
    {
        if(available == null || available.isEmpty())
            return -1;
        if(available.size() == 1)
        {
            return available.get(0);
        }
        else
        {
            double epsilon = Math.min(1.0, this.alpha*numItems/(numIter+0.0));
            if(rng.nextDouble() < epsilon)
            {
                int item = untierng.nextInt(available.size());
                return available.get(item);
            }
            else
            {
                double max = Double.NEGATIVE_INFINITY;
                IntList top = new IntArrayList();
                
                for(int i : available)
                {
                    double val = valf.apply(uidx, i, values[i], numtimes[i]);
                    if(val > max)
                    {
                        max = val;
                        top = new IntArrayList();
                        top.add(i);
                    }
                    else if(val == max)
                    {
                        top.add(i);
                    }
                }
                
                int size = top.size();
                int iidx;
                if(size == 1)
                {
                    iidx = top.get(0);
                }
                else
                {
                    iidx = top.get(untierng.nextInt(size));
                }
                
                return iidx;
            }
        }
    }

    @Override
    public void update(int i, double value)
    {
        double oldsum = this.sumvalues;
        double increment = value;
        double nTimes = this.numtimes[i]+1;
        double oldval = this.values[i];
     
        numtimes[i]++;
        numIter++;
        double newval = this.updatefunct.apply(oldval, value, oldsum, increment, nTimes);
        this.values[i] = newval;
        this.sumvalues += (newval - oldval);
    }
}
