import opt.OptimizationAlgorithm;
import opt.RandomizedHillClimbing;
import opt.example.NeuralNetworkOptimizationProblem;
import shared.DataSet;
import shared.DataSetDescription;
import shared.ErrorMeasure;
import shared.FixedIterationTrainer;
import shared.Instance;
import shared.SumOfSquaresError;
import shared.filt.LabelSplitFilter;
import shared.reader.ArffDataSetReader;
import shared.tester.AccuracyTestMetric;
import shared.tester.ConfusionMatrixTestMetric;
import shared.tester.NeuralNetworkTester;
import shared.tester.RawOutputTestMetric;
import shared.tester.TestMetric;
import shared.tester.Tester;
import func.nn.feedfwd.FeedForwardNetwork;
import func.nn.feedfwd.FeedForwardNeuralNetworkFactory;


public class RHCImageSegmentation {

    public static void main(String[] args) throws Exception {
        // 1) Construct data instances for training.  These will also be run
        //    through the network at the bottom to verify the output
//        int[] labels = { 0, 1 };
//        double[][][] data = {
//               { { 1, 1, 1, 1 }, { labels[0] } },
//               { { 1, 0, 1, 0 }, { labels[1] } },
//               { { 0, 1, 0, 1 }, { labels[1] } },
//               { { 0, 0, 0, 0 }, { labels[0] } }
//        };
//        Instance[] patterns = new Instance[data.length];
//        for (int i = 0; i < patterns.length; i++) {
//            patterns[i] = new Instance(data[i][0]);
//            patterns[i].setLabel(new Instance(data[i][1]));
//        }

        ArffDataSetReader reader = new ArffDataSetReader(args[0]);
        DataSet set = reader.read();
        LabelSplitFilter flt = new LabelSplitFilter();
        flt.filter(set);
        DataSetDescription desc = set.getDescription();
        DataSetDescription labelDesc = desc.getLabelDescription();
        
        // 2) Instantiate a network using the FeedForwardNeuralNetworkFactory.  This network
        //    will be our classifier.
        FeedForwardNeuralNetworkFactory factory = new FeedForwardNeuralNetworkFactory();
        // 2a) These numbers correspond to the number of nodes in each layer.
        //     This network has 4 input nodes, 3 hidden nodes in 1 layer, and 1 output node in the output layer.
        FeedForwardNetwork network = factory.createClassificationNetwork(new int[] {
                desc.getAttributeCount(),
                2 * desc.getAttributeCount() / 3 + 1,
                1 });
        
        // 3) Instantiate a measure, which is used to evaluate each possible set of weights.
        ErrorMeasure measure = new SumOfSquaresError();
        
        // 5) Instantiate an optimization problem, which is used to specify the dataset, evaluation
        //    function, mutator and crossover function (for Genetic Algorithms), and any other
        //    parameters used in optimization.
        NeuralNetworkOptimizationProblem nno = new NeuralNetworkOptimizationProblem(
            set, network, measure);
        
        // 6) Instantiate a specific OptimizationAlgorithm, which defines how we pick our next potential
        //    hypothesis.
        OptimizationAlgorithm o = new RandomizedHillClimbing(nno);
        
        // 7) Instantiate a trainer.  The FixtIterationTrainer takes another trainer (in this case,
        //    an OptimizationAlgorithm) and executes it a specified number of times.
        FixedIterationTrainer fit = new FixedIterationTrainer(o, 5000);
        
        // 8) Run the trainer.  This may take a little while to run, depending on the OptimizationAlgorithm,
        //    size of the data, and number of iterations.
        fit.train();
        
        // 9) Once training is done, get the optimal solution from the OptimizationAlgorithm.  These are the
        //    optimal weights found for this network.
        Instance opt = o.getOptimal();
        network.setWeights(opt.getData());
        
        //10) Run the training data through the network with the weights discovered through optimization, and
        //    print out the expected label and result of the classifier for each instance.
//        TestMetric acc = new AccuracyTestMetric();
//        TestMetric cm  = new ConfusionMatrixTestMetric(labels);
        TestMetric raw = new RawOutputTestMetric();
        Tester t = new NeuralNetworkTester(network, raw);
        t.test(set.getInstances());
        
        raw.printResults();
//        acc.printResults();
//        cm.printResults();
    }
}
