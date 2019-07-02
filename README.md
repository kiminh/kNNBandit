# kNN Bandit
This repository contains the code needed to reproduce the experiments of the paper

> J. Sanz-Cruzado, P. Castells, E. López. A Simple Multi-Armed Nearest-Neighbor Bandit for Interactive Recommendation. 13th ACM Conference on Recommender Systems (RecSys 2019). Copenhagen, Denmark, September 2019.

## Authors
Information Retrieval Group at Universidad Autónoma de Madrid
- Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
- Pablo Castells (pablo.castells@uam.es)

## Software description

### Algorithms

The software contains the following algorithms:
- **Not personalized multi-armed bandits**:
  - Epsilon greedy
  - UCB1
  - Thompson Sampling
  - Epsilon t-greedy
  - UCB1-tuned
- **User-based kNN**:
  - Classic user-based kNN without normalization
  - Probabilistic user-based kNN without normalization [1]
- **Matrix factorization**:
  - Implicit Matrix Factorization: [2]
  - Fast Implicit Matrix Factorization: [3] 
  - pLSA
- **Baselines**:
  - Popularity-based recommendation
  - Relevant popularity-based recommendation
  - Average rating recommendation
  - Random recommendation
  
For the user-based kNN, we include three different similarities:
- Cosine similarity
- Probablistic similarity: the one used in [1].
- **Beta stochastic similarity:** Thompson-sampling based similarity. When applied to the probabilistic kNN version, the algorithm corresponds to the kNN bandit introduced in the paper.

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

- For general recommendation
```
java -jar knnbandit-jar-with-dependencies.jar generalrec algorithmsFile dataFile outputFolder numIter threshold recover useRatings
```
- For contact recommendation
```
java -jar knnbandit-jar-with-dependencies.jar contactrec algorithmsFile dataFile outputFolder numIter directed recover notReciprocal
```
## References
1. Cañamares, R. & Castells P. (2017). A Probabilistic Reformulation of Memory-Based Collaborative Filtering – Implications on Popularity Biases. 40th Annual International ACM SIGIR Conference on Research and Development in Information Retrieval (SIGIR 2017). Tokyo, Japan, August 2017, pp. 215-224.
2. Hu, Y., Koren, Y., & Volinsky, C. (2008). Collaborative Filtering for Implicit Feedback Datasets. In 2008 Eighth IEEE International Conference on Data Mining (pp. 263–272).
3. Pilászy, I., Zibriczky, D., & Tikk, D. (2010). Fast als-based matrix factorization for explicit and implicit feedback datasets. In Proceedings of the fourth ACM conference on Recommender systems - RecSys ’10 (p. 71).
