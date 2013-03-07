import java.io.File;

import opt.OptimizationAlgorithm;
import opt.RandomizedHillClimbing;
import opt.example.NeuralNetworkOptimizationProblem;
import opt.ga.StandardGeneticAlgorithm;
import shared.DataSet;
import shared.DataSetDescription;
import shared.ErrorMeasure;
import shared.FixedIterationTrainer;
import shared.Instance;
import shared.SumOfSquaresError;
import shared.filt.LabelSplitFilter;
import shared.reader.ArffDataSetReader;
import shared.reader.DataSetLabelBinarySeperator;
import shared.runner.MultiRunner;
import shared.tester.AccuracyTestMetric;
import shared.tester.ConfusionMatrixTestMetric;
import shared.tester.NeuralNetworkTester;
import shared.tester.RawOutputTestMetric;
import shared.tester.TestMetric;
import shared.tester.Tester;
import func.nn.feedfwd.FeedForwardNetwork;
import func.nn.feedfwd.FeedForwardNeuralNetworkFactory;

public class GeneticRunner extends ABaseNeuralNetworkRunner {

    public static void main(String[] args) throws Exception {
        String iterationsList = args[1];
        String[] parts = iterationsList.split(",");
        int[] iterArray = new int[parts.length];
        for (int ii = 0; ii < parts.length; ii++) {
            iterArray[ii] = Integer.valueOf(parts[ii]);
        }
        
        String splitList = args[2];
        parts = splitList.split(",");
        int[] pctTrainArray = new int[parts.length];
        for (int ii = 0; ii < parts.length; ii++) {
            pctTrainArray[ii] = Integer.valueOf(parts[ii]);
        }

        MultiRunner mrunner = new MultiRunner(new GeneticRunner(args[0]), iterArray, pctTrainArray);
        if (args.length >= 4) {
            mrunner.setOutputFolder(new File(args[3]));
        }
        mrunner.runAll();
    }

    public GeneticRunner(String dataFilePath) {
        super(dataFilePath);
    }

    @Override
    protected OptimizationAlgorithm newAlgorithmInstance(
            NeuralNetworkOptimizationProblem nno) {
        // Genetic Algorithm is used with a population of 60 and mating/mutating 2 per generation.
        return new StandardGeneticAlgorithm(60, 2, 2, nno);
    }
}
