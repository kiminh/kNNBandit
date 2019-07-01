/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad AutÃ³noma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.knnbandit.data.preference.index;

import es.uam.eps.ir.ranksys.core.index.FeatureIndex;
import java.util.stream.Stream;

/**
 * Updateable index for a set of features.
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <F> type of the features
 */
public interface UpdateableFeatureIndex<F> extends FeatureIndex<F>
{
    /**
     * Adds a new feature
     * @param f the new feature
     * @return the index of the new feature.
     */
    public int addFeature(F f);
    
    /**
     * Removes a feature from the index.
     * @param i the feature to delete. 
     * @return the old identifier of the feature.
     */
    //public int removeFeature(F i);
    /**
     * Adds a set of features to the index
     * @param features a stream containing the features.
     */
    public default void addFeatures(Stream<F> features)
    {
        features.forEach(f -> this.addFeature(f));
    }
}
