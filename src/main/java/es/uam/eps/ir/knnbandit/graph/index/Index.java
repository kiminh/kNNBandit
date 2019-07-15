/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.graph.index;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Generic index
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <I> The indexed objects
 */
public interface Index<I> extends ReducedIndex<I>
{
     /**
     * Checks if the index contains a given object
     * @param i The object to check
     * @return True if the index contains the object, false if not.
     */
    public boolean containsObject(I i);
    
    /**
     * Index size.
     * @return The number of objects in the index.
     */
    public int numObjects();
    /**
     * A stream of all the objects in the index.
     * @return the stream.
     */
    public Stream<I> getAllObjects();
    
    /**
     * A stream of all ids
     * @return all ids.
     */
    public IntStream getAllObjectsIds();

    /**
     * Adds a object to the index
     * @param i Object to add
     * @return The index of the added object.
     */
    public int addObject(I i);
    
    /**
     * Removes an object of the index.
     * @param i the object to remove.
     * @return The index of the removed object, -1 if it did not exist.
     */
    public int removeObject(I i);
}
