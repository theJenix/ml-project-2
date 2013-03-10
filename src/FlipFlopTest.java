

import java.io.IOException;
import java.util.Arrays;

import opt.DiscreteChangeOneNeighbor;
import opt.EvaluationFunction;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.OptimizationAlgorithm;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.FourPeaksEvaluationFunction;
import opt.ga.CrossoverFunction;
import opt.ga.DiscreteChangeOneMutation;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.SingleCrossOver;
import opt.ga.StandardGeneticAlgorithm;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import dist.DiscreteDependencyTree;
import dist.DiscreteUniformDistribution;
import dist.Distribution;

/**
 * A test using the flip flop evaluation function
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class FlipFlopTest extends BaseTest {
    /** The n value */
    private static final int N = 80;
    
    private static final int T = N/10;
    
    public static void main(String[] args) throws IOException {
        new FlipFlopTest().go();
    }

    private void go() throws IOException {
        int[] ranges = new int[N];
        Arrays.fill(ranges, 2);
        EvaluationFunction ef = new FourPeaksEvaluationFunction(T);
        Distribution odd = new DiscreteUniformDistribution(ranges);
        NeighborFunction nf = new DiscreteChangeOneNeighbor(ranges);
        MutationFunction mf = new DiscreteChangeOneMutation(ranges);
        CrossoverFunction cf = new SingleCrossOver();
        Distribution df = new DiscreteDependencyTree(.1, ranges); 
        final HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        final GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
        final ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);
        
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
                return new SimulatedAnnealing(100, .95, hcp);
            }
        }, ef, 200000, iterations);
        
        System.out.println("Genetic Algorithms");
        runOne(new OptimizationAlgorithmFactory() {
            
            @Override
            public OptimizationAlgorithm newOptimizationAlgorithm() {
                return new StandardGeneticAlgorithm(200, 100, 20, gap);
            }
        }, ef, 1000, iterations);
        System.out.println("MIMIC");
        runOne(new OptimizationAlgorithmFactory() {
            
            @Override
            public OptimizationAlgorithm newOptimizationAlgorithm() {
                return new MIMIC(200, 5, pop);
            }
        }, ef, 1000, iterations);
    }
}
