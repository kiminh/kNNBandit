/*
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package es.uam.eps.ir.knnbandit.grid;

/**
 * Identifiers of the algorithms which can be used.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class AlgorithmIdentifiers 
{
    // Simple algorithms
    public static final String RANDOM = "random";
    public static final String POP = "pop";
    public static final String AVG = "avg";
    public static final String RELPOP = "relpop";
    public static final String ORACLE = "oracle";
    // Not-personalized bandits
    public static final String SIMPLEBANDIT = "bandit";
    // User based
    public static final String USERBASEDKNN = "ub";
    // Matrix factorization.
    public static final String MF = "mf";
}
