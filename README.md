# kNN Bandit
This repository contains the code needed to reproduce the experiments of the paper

> J. Sanz-Cruzado, P. Castells, E. López. A Simple Multi-Armed Nearest-Neighbor Bandit for Interactive Recommendation. 13th ACM Conference on Recommender Systems (RecSys 2019). Copenhagen, Denmark, September 2019.

## Authors
Information Retrieval Group at Universidad Autónoma de Madrid
- Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
- Pablo Castells (pablo.castells@uam.es)
## Software description
This repository contains all the necessary classes to execute the experiments explained in the paper. The software contains the following packages:
- `es.uam.eps.ir.knnbandit.data`: classes for managing the ratings of the users to the items. Extension of the RankSys preference data classes to allow the addition of new users, items and ratings.
- `es.uam.eps.ir.knnbandit.graph`: classes for managing graphs for contact recommendation.
- `es.uam.eps.ir.knnbandit.grid`: classes for reading the list of algorithms to execute
- `es.uam.eps.ir.knnbandit.metrics`: the set of cumulative metrics used in the algorithm.
- `es.uam.eps.ir.knnbandit.recommendation`: implementation of the different algorithms and similarities which are included in this software.
- `es.uam.eps.ir.knnbandit.stats`: probability distributions.
- `es.uam.eps.ir.knnbandit.utils`: additional classes, useful for the rest of the program

### Algorithms
The software allows the use of several recommendation algorithms:
#### Myopic approaches:
These approaches are just an updateable version of classical recommendation algorithms, baselines. The algorithms included in this comparison are:
- **Baselines:** Random, popularity-based recommendation, relevant popularity-base recommendation, average rating.
- **Matrix factorization:** Implicit matrix factorization (iMF) [2], fast iMF [3], pLSA [4].
- **User-based kNN:** Not normalized versions of classic user-based kNN and probablistic user-based kNN.
We include two different myopic similarities: cosine similarity and the probabilistic similarity described in [1].

### Metrics
In order to evaluate the different proposals, we include two different metrics:
- **Incremental Recall:** Measures the proportion of the dataset which has been discovered at a certain point of time.
- **Incremental Gini:** Measures how imbalanced is the distribution of the number of times each item has been recommended over time.

#### Reinforcement learning approaches:
These approaches introduce the exploration-exploitation concept from Reinforcement learning:
- **Not personalized multi-armed bandits:**: Epsilon-greedy, epsilon t-greedy, UCB1, UCB1-tuned, Thompson sampling
- **User-based kNN:** The main contribution of this paper: we include an stochastic similarity that uses Thompson sampling to estimate the similarities between users. 

## System Requirements
**Java JDK:** 1.8 or above (the software was tested using the version 1.8.0_181).

**Maven:** tested with version 3.6.0.

## Installation
In order to install this program, you need to have Maven (https://maven.apache.org/) installed on your system. Then, download the files into a directory, and execute the following command:
```
mvn compile assembly::single
```
If you do not want to use Maven, it is still possible to compile the code using any Java compiler. In that case, you will need the following libraries:
- Ranksys version 0.4.3: http://ranksys.org/
- Colt version 1.2.0: https://dst.lbl.gov/ACSSoftware/colt/
- Google MTJ version 1.0.4: https://github.com/fommil/matrix-toolkits-java

## Execution
Once you have a generated .jar, you can execute the program. There are two different ways to execute this program: one for general recommendation (movies, songs, venues...) and another one for contact recommendation in social networks, since the evaluation protocols show slight differences between both tasks.

### General recommendation
```
java -jar knnbandit-jar-with-dependencies.jar generalrec algorithmsFile dataFile outputFolder numIter threshold recover useRatings
```
where
  - `algorithmsFile`: File indicating which algorithms have to be executed
  - `dataFile`: The ratings data. Format: on each line: `user \t item \t rating`.
  - `outputFolder` is the directory where the output files will be stored.
  - `numIter` is the number of iterations to execute for each algorithm. Use value `0` for executing until no new items can be recommended.
  - `threshold`: relevance threshold. Ratings equal or greater than this value will be considered as relevant.
  - `recover`: true if we want to retrieve the previous computed values (if any) or false to overwrite them and start from the beginning.
  - `useRatings`: true for using the real ratings, false for binary ratings.
  
For reproducing the experiments of the paper, arguments were
- `numIter = 500000` for Foursquare-NY, `numIter = 1000000` for Foursquare-Tokyo and `numIter = 3000000` for MovieLens1M.
- `threshold = 1` for Foursquare and `threshold = 4` for MovieLens1M
- `useRatings = false` for all of them.
### Contact recommendation
```
java -jar knnbandit-jar-with-dependencies.jar contactrec algorithmsFile dataFile outputFolder numIter directed recover notReciprocal
```
where
  - `algorithmsFile`: File indicating which algorithms have to be executed
  - `dataFile`: The graph data. Format: on each line: `originUser \t destUser \t weight`.
  - `outputFolder` is the directory where the output files will be stored.
  - `numIter` is the number of iterations to execute for each algorithm. Use value `0` for executing until no new items can be recommended.
  - `directed`: true if the graph is directed, false otherwise.
  - `recover`: true if we want to retrieve the previous computed values (if any) or false to overwrite them and start from the beginning.
  - `notReciprocal`: true if we do not want to recommend reciprocal links, false otherwise.
  
For reproducing the experiments of the paper, arguments were 
- `numIter = 5000000`
- `directed = true`
- `notReciprocal = true`.
### Algorithm files
In order to execute different configurations, we include in the folder `configs` the optimal configurations for the different datasets we used in the paper. Each row represents the configuration for a single algorithm.

### Output example
The output of both programs is the same: for each algorithm in the comparison, a file will be created. 
The name of the file will be the same as the chosen algorithm configuration. Each of the output files has the following format:

Separated by tabs, the first line contains the header of the file. Then, each row contains the information of a single iteration: the number of the iteration, the selected user, the selected item, the value of the metrics and the time needed to execute the iteration (in ms.)

Next, we show an example file:
```
iter	user	item	recall	gini	time
0	1713	4901	0.0	1.0	27
1	1880	1477	0.0	0.9999838334195551	13
2	1626	56725	0.0	0.9999676668391102	3
3	2002	34539	0.0	0.9999515002586653	3
4	477	5085	0.0	0.9999353336782204	6
5	2012	44312	0.0	0.9999191670977755	5
6	1526	60448	0.0	0.9999030005173306	45
7	528	9392	0.0	0.9998868339368857	46
8	887	2878	0.0	0.9998706673564408	31
9	1313	22947	0.0	0.9998545007759959	56
10	2274	45478	0.0	0.9998383341955509	1
11	1615	7493	0.0	0.999822167615106	2
12	1481	58528	0.0	0.9998060010346611	0
```
## References
1. Cañamares, R. & Castells P. (2017). A Probabilistic Reformulation of Memory-Based Collaborative Filtering – Implications on Popularity Biases. 40th Annual International ACM SIGIR Conference on Research and Development in Information Retrieval (SIGIR 2017). Tokyo, Japan, August 2017, pp. 215-224.
2. Hofmann, T. (2004). Latent semantic models for collaborative filtering. ACM Transactions on Information Systems, 22(1), pp. 89–115
3. Hu, Y., Koren, Y., & Volinsky, C. (2008). Collaborative Filtering for Implicit Feedback Datasets. In 2008 Eighth IEEE International Conference on Data Mining (ICDM 2008). Pisa, Italy, December 2008, pp. 263–272.
4. Pilászy, I., Zibriczky, D., & Tikk, D. (2010). Fast ALS-based matrix factorization for explicit and implicit feedback datasets. In Proceedings of the 4th ACM conference on Recommender systems (Recsys 2010). Barcelona, Spain, September 2010, pp. 71-78.
