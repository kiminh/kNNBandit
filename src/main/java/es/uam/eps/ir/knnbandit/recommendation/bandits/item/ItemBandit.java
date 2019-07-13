/*
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.knnbandit.recommendation.bandits.item;

import es.uam.eps.ir.knnbandit.UntieRandomNumber;
import es.uam.eps.ir.knnbandit.recommendation.bandits.functions.ValueFunction;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Random;

/**
 * Bandit in which arms are represented by items.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <U> Type of the users.
 * @param <I> Type of the items.
 */
public abstract class ItemBandit<U,I>
{       
    /**
     * Untie random
     */
    protected final Random untierng;
    
    /**
     * Constructor.
     */
    public ItemBandit()
    {
        this.untierng = new Random(UntieRandomNumber.RNG);
    }
    
    /**
     * Selects the next item, given that a selection of them is available.
     * @param uidx identifier of the user that selects the item.
     * @param available the selection of available items.
     * @param ValF a function that determines the effective value of the arm, given a context.
     * @return the next selected item.
     */
    public abstract int next(int uidx, int[] available, ValueFunction ValF);
    
    /**
     * Selects the next item, given that a selection of them is available.
     * @param uidx identifier of the user that selects the item.
     * @param available the selection of available items.
     * @param ValF a function that determines the effective value of the arm, given a context.
     * @return the next selected item.
     */
    public abstract int next(int uidx, IntList available, ValueFunction ValF);
    
    /**
     * Updates the corresponding item, given the reward.
     * @param iidx the item to update.
     * @param value the reward.
     */
    public abstract void update(int iidx, double value);
}
