# kNN Bandit
This repository contains the code needed to reproduce the experiments of the paper

> J. Sanz-Cruzado, P. Castells, E. López. A Simple Multi-Armed Nearest-Neighbor Bandit for Interactive Recommendation. 13th ACM Conference on Recommender Systems (RecSys 2019). Copenhagen, Denmark, September 2019.

## Authors
Information Retrieval Group at Universidad Autónoma de Madrid
- Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
- Pablo Castells (pablo.castells@uam.es)

## Software description

### Algorithms
The software allows the use of several recommendation algorithms. 
#### Myopic approaches:
These approaches are just an updateable version of classical recommendation algorithms, baselines. The algorithms included in this comparison are:
- **Baselines:** Random, popularity-based recommendation, relevant popularity-base recommendation, average rating.
- **Matrix factorization:** Implicit matrix factorization (iMF) [2], fast iMF [3], pLSA.
- **User-based kNN:** Not normalized versions of classic user-based kNN and probablistic user-based kNN.
We include two different myopic similarities: cosine similarity and the probabilistic similarity described in [1].

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
## References
1. Cañamares, R. & Castells P. (2017). A Probabilistic Reformulation of Memory-Based Collaborative Filtering – Implications on Popularity Biases. 40th Annual International ACM SIGIR Conference on Research and Development in Information Retrieval (SIGIR 2017). Tokyo, Japan, August 2017, pp. 215-224.
2. Hu, Y., Koren, Y., & Volinsky, C. (2008). Collaborative Filtering for Implicit Feedback Datasets. In 2008 Eighth IEEE International Conference on Data Mining (pp. 263–272).
3. Pilászy, I., Zibriczky, D., & Tikk, D. (2010). Fast als-based matrix factorization for explicit and implicit feedback datasets. In Proceedings of the fourth ACM conference on Recommender systems - RecSys ’10 (p. 71).
