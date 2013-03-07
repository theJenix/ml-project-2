import java.io.File;

import opt.OptimizationAlgorithm;
import opt.SimulatedAnnealing;
import opt.example.NeuralNetworkOptimizationProblem;
import shared.runner.MultiRunner;

public class SimulatedAnnealingRunner extends ABaseNeuralNetworkRunner {

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

        MultiRunner mrunner = new MultiRunner(new SimulatedAnnealingRunner(args[0]), iterArray, pctTrainArray);
        if (args.length >= 4) {
            mrunner.setOutputFolder(new File(args[3]));
        }
        mrunner.runAll();
    }

    public SimulatedAnnealingRunner(String dataFilePath) {
        super(dataFilePath);
    }

    @Override
    protected OptimizationAlgorithm newAlgorithmInstance(
            NeuralNetworkOptimizationProblem nno) {
        // Simulated Annealing is used with the default Weka values.
        return new SimulatedAnnealing(10, 0.999, nno);
    }
}
