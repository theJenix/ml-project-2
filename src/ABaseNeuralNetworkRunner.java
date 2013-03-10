import java.io.File;

import opt.OptimizationAlgorithm;
import opt.example.NeuralNetworkOptimizationProblem;
import shared.DataSet;
import shared.DataSetDescription;
import shared.ErrorMeasure;
import shared.FixedIterationTrainer;
import shared.Instance;
import shared.SumOfSquaresError;
import shared.filt.LabelSplitFilter;
import shared.filt.TestTrainSplitFilter;
import shared.reader.ArffDataSetReader;
import shared.reader.DataSetLabelBinarySeperator;
import shared.runner.Runner;
import shared.tester.AccuracyTestMetric;
import shared.tester.ConfusionMatrixTestMetric;
import shared.tester.NeuralNetworkTester;
import shared.tester.RawOutputTestMetric;
import shared.tester.Tester;
import func.nn.feedfwd.FeedForwardNetwork;
import func.nn.feedfwd.FeedForwardNeuralNetworkFactory;

/**
 * Base class runner object, used to run optimization experiments.
 * 
 * @author Jesse Rosalia
 *
 */
public abstract class ABaseNeuralNetworkRunner implements Runner {

    private String dataFilePath;
    private AccuracyTestMetric accuracyMetric;
    private ConfusionMatrixTestMetric confusionMatrix;
    private RawOutputTestMetric rawOutput;
    private long trainTimeMillis;
    private long testTimeMillis;

    public ABaseNeuralNetworkRunner(String dataFilePath) {
        this.dataFilePath = dataFilePath;
    }

    @Override
    public String getName() {
        File file = new File(this.dataFilePath);
        return getClass().getName() + "_" + file.getName();
    }

    /* (non-Javadoc)
     * @see Runner#run()
     */
    @Override
    public void run(int iterations, int pctTrain) throws Exception {
        // 1) Construct data instances for training.  These will also be run
        //    through the network at the bottom to verify the output
        ArffDataSetReader reader = new ArffDataSetReader(this.dataFilePath);
        DataSet set = reader.read();
        LabelSplitFilter flt = new LabelSplitFilter();
        flt.filter(set);
        DataSetLabelBinarySeperator.seperateLabels(set);

        DataSetDescription desc = set.getDescription();
        DataSetDescription labelDesc = desc.getLabelDescription();

        DataSet training;
        DataSet testing;
        // 1a) Split the data into a testing and training set
        if (pctTrain > 0 && pctTrain < 100) {
            TestTrainSplitFilter ttsf = new TestTrainSplitFilter(pctTrain);
            ttsf.filter(set);
            training = ttsf.getTrainingSet();
            testing  = ttsf.getTestingSet();
        } else {
            training = set;
            testing  = set;
        }
        // 2) Instantiate a network using the FeedForwardNeuralNetworkFactory.  This network
        //    will be our classifier.
        FeedForwardNeuralNetworkFactory factory = new FeedForwardNeuralNetworkFactory();
        // 2a) These numbers correspond to the number of nodes in each layer.
        //     As a rule of thumb, use a hidden layer with n = 2/3 # input nodes + # output nodes
        FeedForwardNetwork network = factory.createClassificationNetwork(new int[] {
                desc.getAttributeCount(),
                factory.getOptimalHiddenLayerNodes(desc, labelDesc),
                labelDesc.getDiscreteRange() });
        
        // 3) Instantiate a measure, which is used to evaluate each possible set of weights.
        ErrorMeasure measure = new SumOfSquaresError();
        
        // 5) Instantiate an optimization problem, which is used to specify the dataset, evaluation
        //    function, mutator and crossover function (for Genetic Algorithms), and any other
        //    parameters used in optimization.
        NeuralNetworkOptimizationProblem nno = new NeuralNetworkOptimizationProblem(
            training, network, measure);
        
        // 6) Instantiate a specific OptimizationAlgorithm, which defines how we pick our next potential
        //    hypothesis.
        OptimizationAlgorithm o = newAlgorithmInstance(nno);
        
        // 7) Instantiate a trainer.  The FixtIterationTrainer takes another trainer (in this case,
        //    an OptimizationAlgorithm) and executes it a specified number of times.
        FixedIterationTrainer fit = new FixedIterationTrainer(o, iterations);
        
        long trainTime = System.currentTimeMillis();

        // 8) Run the trainer.  This may take a little while to run, depending on the OptimizationAlgorithm,
        //    size of the data, and number of iterations.
        System.out.println(fit.train());
        
        trainTime = System.currentTimeMillis() - trainTime;

        // 9) Once training is done, get the optimal solution from the OptimizationAlgorithm.  These are the
        //    optimal weights found for this network.
        Instance opt = o.getOptimal();
        network.setWeights(opt.getData());
        
        long testTime = System.currentTimeMillis();

        //10) Run the training data through the network with the weights discovered through optimization, and
        //    print out the expected label and result of the classifier for each instance.
        this.accuracyMetric  = new AccuracyTestMetric();
        this.confusionMatrix = new ConfusionMatrixTestMetric(labelDesc);
        this.rawOutput       = new RawOutputTestMetric();
        Tester t = new NeuralNetworkTester(network, this.rawOutput, this.accuracyMetric, this.confusionMatrix);
        t.test(testing.getInstances());
        
        testTime = testTime - System.currentTimeMillis();
        
        this.trainTimeMillis = trainTime;
        this.testTimeMillis  = testTime;
    }

    @Override
    public AccuracyTestMetric getAccuracyMetric() {
        return this.accuracyMetric;
    }
    @Override
    public ConfusionMatrixTestMetric getConfusionMatrix() {
        return this.confusionMatrix;
    }

    @Override
    public RawOutputTestMetric getRawOutput() {
        return rawOutput;
    }

    @Override
    public long getTestTime() {
        return testTimeMillis;
    }
    
    @Override
    public long getTrainingTime() {
        return trainTimeMillis;
    }
    
    protected abstract OptimizationAlgorithm newAlgorithmInstance(
            NeuralNetworkOptimizationProblem nno);
}
