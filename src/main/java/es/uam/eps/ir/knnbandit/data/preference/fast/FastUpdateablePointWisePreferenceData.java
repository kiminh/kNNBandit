/*
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.knnbandit.data.preference.fast;


import org.ranksys.fast.preference.FastPointWisePreferenceData;

/**
 * Fast updateable version of a pointwise preference data.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <U> Type of the users.
 * @param <I> Type of the items.
 */
public interface FastUpdateablePointWisePreferenceData<U,I> extends FastPointWisePreferenceData<U,I>, FastUpdateablePreferenceData<U,I>
{

}
