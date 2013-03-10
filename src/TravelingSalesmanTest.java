

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import dist.DiscreteDependencyTree;
import dist.DiscretePermutationDistribution;
import dist.DiscreteUniformDistribution;
import dist.Distribution;

import opt.OptimizationAlgorithm;
import opt.SwapNeighbor;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.*;
import opt.ga.CrossoverFunction;
import opt.ga.SwapMutation;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.StandardGeneticAlgorithm;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;

/**
 * 
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class TravelingSalesmanTest extends BaseTest{
    /** The n value */
    private static final int N = 50;
    /**
     * The test main
     * @param args ignored
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        new TravelingSalesmanTest().go();
    }
    
    private void go() throws IOException {
        Random random = new Random();
        // create the random points
        double[][] points = new double[N][2];
        for (int i = 0; i < points.length; i++) {
            points[i][0] = random.nextDouble();
            points[i][1] = random.nextDouble();   
        }
        // for rhc, sa, and ga we use a permutation based encoding
        TravelingSalesmanEvaluationFunction ef = new TravelingSalesmanRouteEvaluationFunction(points);
        Distribution odd = new DiscretePermutationDistribution(N);
        NeighborFunction nf = new SwapNeighbor();
        MutationFunction mf = new SwapMutation();
        CrossoverFunction cf = new TravelingSalesmanCrossOver(ef);
        final HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        final GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
        
        int iterations = 10;
        System.out.println("Randomized Hill Climbing");
        runOne(new OptimizationAlgorithmFactory() {
            @Override
            public OptimizationAlgorithm newOptimizationAlgorithm() {
                return new RandomizedHillClimbing(hcp);
            }
        }, ef, 200000, iterations);

        System.out.println("Simulated Annealing");
        runOne(new OptimizationAlgorithmFactory() {
            
            @Override
            public OptimizationAlgorithm newOptimizationAlgorithm() {
                return new SimulatedAnnealing(1E12, .95, hcp);
            }
        }, ef, 200000, iterations);
        
        System.out.println("Genetic Algorithms");
        runOne(new OptimizationAlgorithmFactory() {
            
            @Override
            public OptimizationAlgorithm newOptimizationAlgorithm() {
                return new StandardGeneticAlgorithm(200, 150, 20, gap);
            }
        }, ef, 1000, iterations);
        System.out.println("MIMIC");
        
        
        // for mimic we use a sort encoding
        ef = new TravelingSalesmanSortEvaluationFunction(points);
        int[] ranges = new int[N];
        Arrays.fill(ranges, N);
        odd = new  DiscreteUniformDistribution(ranges);
        Distribution df = new DiscreteDependencyTree(.1, ranges); 
        final ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);
        
        runOne(new OptimizationAlgorithmFactory() {
            
            @Override
            public OptimizationAlgorithm newOptimizationAlgorithm() {
                return new MIMIC(200, 100, pop);
            }
        }, ef, 1000, iterations);        
    }
}
