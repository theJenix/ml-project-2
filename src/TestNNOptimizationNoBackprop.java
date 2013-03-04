import opt.OptimizationAlgorithm;
import opt.RandomizedHillClimbing;
import opt.example.NeuralNetworkOptimizationProblem;
import shared.DataSet;
import shared.ErrorMeasure;
import shared.FixedIterationTrainer;
import shared.Instance;
import shared.SumOfSquaresError;
import func.nn.FeedForwardNetwork;


public class TestNNOptimizationNoBackprop {

    /**
     * Tests out the perceptron with the classic xor test
     * @param args ignored
     */
    public static void main(String[] args) {
        double[][][] data = {
               { { 1, 1, 1, 1 }, { 0 } },
               { { 1, 0, 1, 0 }, { 1 } },
               { { 0, 1, 0, 1 }, { 1 } },
               { { 0, 0, 0, 0 }, { 0 } }
        };
        Instance[] patterns = new Instance[data.length];
        for (int i = 0; i < patterns.length; i++) {
            patterns[i] = new Instance(data[i][0]);
            patterns[i].setLabel(new Instance(data[i][1]));
        }
        FeedForwardNeuralNetworkFactory factory = new FeedForwardNeuralNetworkFactory();
        FeedForwardNetwork network = factory.createClassificationNetwork(new int[] { 4, 3, 1 });
        ErrorMeasure measure = new SumOfSquaresError();
        DataSet set = new DataSet(patterns);
        NeuralNetworkOptimizationProblem nno = new NeuralNetworkOptimizationProblem(
            set, network, measure);
        OptimizationAlgorithm o = new RandomizedHillClimbing(nno);
        FixedIterationTrainer fit = new FixedIterationTrainer(o, 5000);
        fit.train();
        Instance opt = o.getOptimal();
        network.setWeights(opt.getData());
        for (int i = 0; i < patterns.length; i++) {
            network.setInputValues(patterns[i].getData());
            network.run();
            System.out.println("~~");
            System.out.println(patterns[i].getLabel());
            System.out.println(network.getOutputValues());
        }
    } 
}
