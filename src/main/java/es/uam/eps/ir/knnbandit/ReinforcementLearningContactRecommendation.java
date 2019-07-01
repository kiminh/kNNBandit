/*
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package es.uam.eps.ir.knnbandit;

import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableItemIndex;
import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableUserIndex;
import es.uam.eps.ir.knnbandit.data.preference.index.fast.SimpleFastUpdateableItemIndex;
import es.uam.eps.ir.knnbandit.data.preference.index.fast.SimpleFastUpdateableUserIndex;
import es.uam.eps.ir.knnbandit.graph.io.GraphReader;
import es.uam.eps.ir.knnbandit.graph.io.TextGraphReader;
import es.uam.eps.ir.knnbandit.grid.BanditGrid;
import es.uam.eps.ir.knnbandit.grid.UnconfiguredException;
import es.uam.eps.ir.knnbandit.metrics.IncrementalGini;
import es.uam.eps.ir.knnbandit.metrics.IncrementalRecall;
import es.uam.eps.ir.knnbandit.metrics.IncrementalRecommendationMetric;
import es.uam.eps.ir.knnbandit.recommendation.ReinforcementLearningRecommender;
import es.uam.eps.ir.ranksys.fast.preference.SimpleFastPreferenceData;

import es.uam.eps.ir.knnbandit.graph.Graph;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;

import org.ranksys.formats.parsing.Parsers;

/**
 * Class for executing reinforcement learning algorithms.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class ReinforcementLearningContactRecommendation
{
    /**
     * Executes reinforcement learning algorithms in contact recommendation.
     * @param args Execution arguments:
     * <ol>
     *     <li>Algorithms: configuration file for the algorithms</li>
     *     <li>Input: preference data</li>
     *     <li>Output: folder in which to store the output</li>
     *     <li>Num. Iter: Number of iterations. 0 if we want to apply until full coverage.</li>
     *     <li>Directed: true if the graph is directed, false otherwise</li>
     *     <li>Recover: true if we want to retrieve data from previous executions, false to overwrite</li>
     *     <li>Not reciprocal: true if we don't want to recommend reciprocal edges, false otherwise</li>
     * </ol>
     * @throws IOException if something fails while reading / writing.
     * @throws UnconfiguredException if something fails while retrieving the algorithms.
     */
    public static void main(String[] args) throws IOException, UnconfiguredException
    {
        if(args.length < 7)
        {
            System.err.println("ERROR: Invalid arguments");
            System.err.println("Usage:");
            System.err.println("\tAlgorithms: Reinforcement learning algorithms list");
            System.err.println("\tInput: Preference data input");
            System.err.println("\tOutput: Folder in which to store the output");
            System.err.println("\tNum. Iter.: Number of iterations. 0 if we want to apply it until the end");
            System.err.println("\tDirected: true if the graph is directed, false otherwise");
            System.err.println("\tRecover: true if we want to recover data from previous executions, false if we want to overwrite");
            System.err.println("\tNot Reciprocal: true if we want to recommend reciprocal edges, false otherwise");
            return;
        }

        String algorithms = args[0];
        String input = args[1];
        String output = args[2];
        int auxIter = Parsers.ip.parse(args[3]);
        boolean recover = args[4].equalsIgnoreCase("true");
        int numIter = (auxIter == 0) ? Integer.MAX_VALUE : auxIter;
        
        boolean directed = args[5].equalsIgnoreCase("true");
        boolean notReciprocal = !directed || args[6].equalsIgnoreCase("true");


        // First, we identify and find the random seed which will be used for unties.
        if(recover)
        {
            File f = new File(output + "rngseed");
            if(f.exists())
            {
                try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f))))
                {
                    UntieRandomNumber.RNG = Parsers.ip.parse(br.readLine());
                }
            }
            else
            {
                Random rng = new Random();
                UntieRandomNumber.RNG = rng.nextInt();
            }
        }
        else
        {
            Random rng = new Random();
            UntieRandomNumber.RNG = rng.nextInt();
        }
        
        try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output + "rngseed"))))
        {
            bw.write("" + UntieRandomNumber.RNG);
        }
        
        // Read the ratings.
        Set<Long> users = new HashSet<>();
        List<Tuple3<Long,Long,Double>> triplets = new ArrayList<>();
        
        Graph<Long> graph;
        GraphReader<Long> greader = new TextGraphReader<>(false, directed, false, false, "\t", Parsers.lp);
        graph = greader.read(input);
        
        graph.getAllNodes().forEach(users::add);
        int numEdges = new Long(graph.getEdgeCount()).intValue()*(directed ? 1 : 2);
        int numRecipr = graph.getAllNodes().mapToInt(graph::getMutualNodesCount).sum();

        int numrel = numEdges - numRecipr/2;
        
        graph.getAllNodes().forEach(u -> 
        {
            graph.getAdjacentNodes(u).forEach(v -> 
            {
                triplets.add(new Tuple3<>(u,v,1.0));
            });
        });

        FastUpdateableUserIndex<Long> uIndex = SimpleFastUpdateableUserIndex.load(users.stream());
        FastUpdateableItemIndex<Long> iIndex = SimpleFastUpdateableItemIndex.load(users.stream());
        SimpleFastPreferenceData<Long, Long> prefData = SimpleFastPreferenceData.load(triplets.stream(), uIndex, iIndex);

        System.out.println("Num items:" + users.size());
        System.out.println("Num. users: " + prefData.numUsersWithPreferences());
        // Initialize the metrics to compute.
        Map<String, Supplier<IncrementalRecommendationMetric<Long,Long>>> metrics = new HashMap<>();
        metrics.put("recall", () -> new IncrementalRecall(prefData, numrel, 0.5));
        metrics.put("gini", () -> new IncrementalGini(users.size()));
        
        List<String> metricNames = new ArrayList<>(metrics.keySet());
        
        // Select the algorithms
        long a = System.currentTimeMillis();
        BanditGrid<Long, Long> banditgrid = new BanditGrid<>();
        banditgrid.configure(uIndex, iIndex, prefData, 0.5, notReciprocal);
        banditgrid.addFile(algorithms);
        Map<String, ReinforcementLearningRecommender<Long,Long>> recs = banditgrid.getRecs();
        long b = System.currentTimeMillis();
        
        System.out.println("Recommenders prepared (" + (b-a) + " ms.)");
        recs.entrySet().parallelStream().forEach(re -> 
        {
            Random rng = new Random(0);

            IntList userList = prefData.getUidxWithPreferences().boxed().collect(Collectors.toCollection(IntArrayList::new));
            int numUsers = prefData.numUsersWithPreferences();
            ReinforcementLearningRecommender<Long,Long> rec = re.getValue();
            String filename = output + re.getKey() + ".txt";
            
            List<Tuple3<Integer,Integer,Long>> list = new ArrayList<>();
            if(recover)
            {
                File f = new File(filename);
                if(f.exists()) // if the file exists, then recover:
                {
                    try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename))))
                    {
                        String line = br.readLine();
                        int len;
                        if(line != null)
                        {
                            String[] split = line.split("\t");
                            len = split.length;
                            
                            while((line = br.readLine()) != null)
                            {
                                split = line.split("\t");
                                if(split.length < len) break;

                                int uidx = Parsers.ip.parse(split[1]);
                                int iidx = Parsers.ip.parse(split[2]);
                                long time = Parsers.lp.parse(split[len-1]);
                                list.add(new Tuple3<>(uidx, iidx, time));
                            }
                        }                       
                    }
                    catch (IOException ex)
                    {
                        Logger.getLogger(ReinforcementLearningContactRecommendation.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
            try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output + re.getKey() + ".txt"))))
            {
                double val = 0.0;
                int i = 0;
                
                Map<String, IncrementalRecommendationMetric> localMetrics = new HashMap<>();
                
                
                bw.write("iter\tuser\titem");
                for(String name : metricNames)
                {
                    bw.write("\t" + name);
                    localMetrics.put(name, metrics.get(name).get());
                }
                bw.write("\ttime\n");
                
                if(recover && !list.isEmpty())
                {
                    long timea = System.currentTimeMillis();

                    List<Tuple2<Integer,Integer>> recovered = new ArrayList<>();
                    int j = 0;
                    for(Tuple3<Integer,Integer,Long> tuple : list)
                    {
                        int uidx = tuple.v1;
                        int iidx = tuple.v2;
                        long time = tuple.v3;
                        
                        bw.write(j + "\t" + uidx + "\t" + iidx);
                        for(String name : metricNames)
                        {
                            IncrementalRecommendationMetric metric = localMetrics.get(name);
                            metric.update(uidx, iidx);
                            bw.write("\t" + metric.compute());
                        }
                        
                        recovered.add(new Tuple2<>(uidx,iidx));
                        bw.write("\t" + time + "\n");
                        ++j;
                        if(j % 1000 == 0)
                        {
                            long timeb = System.currentTimeMillis();
                            System.out.println(re.getKey() + ": recovered " + j + " iterations (" + (timeb-timea) + " ms.)");
                            bw.flush();
                        }
                    }
                    i=j;
                    
                    if(i < numIter)
                    {
                        timea = System.currentTimeMillis();
                        rec.update(recovered);
                        long auxtimeb = System.currentTimeMillis();
                        System.out.println(re.getKey() + ": updated (" + (auxtimeb - timea)+ " ms.)");
                    }
                }
                
                
                long longtimea = System.currentTimeMillis();
                while(i < numIter && numUsers > 0)
                {
                    int index = rng.nextInt(numUsers);
                    int uidx = userList.get(index);
                    long timea = System.currentTimeMillis();
                    int nextitem = rec.next(uidx);
                    if(nextitem == -1)
                    {
                        userList.removeInt(index);
                        numUsers--;
                        continue;
                    }
                    
                    rec.update(uidx, nextitem);
                    long timeb = System.currentTimeMillis();
                    
                    bw.write(i + "\t" + uidx + "\t" + nextitem);
                    for(String name : metricNames)
                    {
                        IncrementalRecommendationMetric metric = localMetrics.get(name);
                        metric.update(uidx, nextitem);
                        bw.write("\t" + metric.compute());
                    }
                   
                    bw.write("\t" + (timeb-timea) + "\n");
                    ++i;
                    if(i % 1000 == 0)
                    {
                        long longtimeb = System.currentTimeMillis();
                        bw.flush();
                        System.out.println(re.getKey() + ": iteration " + i + " finished (" + (longtimeb - longtimea) + " ms.)");
                        longtimea = System.currentTimeMillis();
                    }
                }
            }
            catch (IOException ex)
            {
                System.err.println("Something failed while writing file for " + re.getKey());
            }
        });
        
    }
}
