/*
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package es.uam.eps.ir.knnbandit.stats;

import java.util.Random;

/**
 * Dirichlet distribution
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class DirichletDistribution implements MultivariateStatisticalDistribution
{
    /**
     * Parameters for each variate
     */
    private double[] alphas;

    /**
     * Sum of all alphas
     */
    private double sum;

    private Random rng = new Random(0);

    /**
     * Constructor.
     * @param alphas Dirichlet initial parameters
     */
    public DirichletDistribution(double[] alphas)
    {
        this.alphas = alphas;
        this.sum = 0;
        for (int i = 0; i < alphas.length; ++i)
        {
            sum += alphas[i];
        }
    }

    /**
     * Constructor. Initializes all parameters to 1.
     * @param num the number of parameters.
     */
    public DirichletDistribution(int num)
    {
        this.alphas = new double[num];

        for (int i = 0; i < alphas.length; ++i)
        {
            alphas[i] = 1;
        }
        sum = num;
    }

    @Override
    public double[] sample()
    {
        double[] aux = new double[alphas.length];
        double total = 0.0;
        for (int i = 0; i < alphas.length; ++i)
        {
            GammaDistribution gamma = new GammaDistribution(alphas[i], 1);
            aux[i] = gamma.sample();
            total += aux[i];
        }

        double[] sample = new double[alphas.length];
        for (int i = 0; i < alphas.length; ++i) {
            sample[i] = aux[i] / total;
        }

        return sample;
    }

    @Override
    public void update(Double... values)
    {
        if (values.length == alphas.length)
        {
            sum = 0;
            for (int i = 0; i < alphas.length; ++i)
            {
                alphas[i] = values[i];
                sum += values[i];
            }
        }
    }

    @Override
    public void update(double value, int i)
    {
        if (i >= 0 && i < alphas.length)
        {
            sum = sum + value - alphas[i];
            alphas[i] = value;
        }
    }

    /**
     * Updates the distribution by adding some value to the i-th alpha.
     * @param value the increment of the i-th alpha.
     * @param i the index of the alpha.
     */
    public void updateAdd(double value, int i)
    {
        if (i >= 0 && i < alphas.length)
        {
            sum = sum + value;
            alphas[i] += value;
        }
    }

    @Override
    public double getParameter(int i) {
        if (i >= 0 && i < alphas.length)
        {
            return alphas[i];
        }
        return Double.NaN;
    }

    @Override
    public double mean(int i) {
        if (i >= 0 && i < alphas.length)
        {
            return alphas[i] / sum;
        }
        return Double.NaN;
    }

    /**
     * Obtains the mean vector.
     */
    @Override
    public double[] means()
    {
        double[] means = new double[alphas.length];
        for (int i = 0; i < alphas.length; ++i)
        {
            means[i] = alphas[i] / sum;
        }
        return means;
    }
}