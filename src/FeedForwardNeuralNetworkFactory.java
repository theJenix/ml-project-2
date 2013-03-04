import func.nn.FeedForwardLayer;
import func.nn.FeedForwardNetwork;
import func.nn.FeedForwardNode;
import func.nn.Layer;
import func.nn.activation.DifferentiableActivationFunction;
import func.nn.activation.HyperbolicTangentSigmoid;
import func.nn.activation.LinearActivationFunction;
import func.nn.activation.LogisticSigmoid;
import func.nn.backprop.BackPropagationSoftMaxOutputLayer;


public class FeedForwardNeuralNetworkFactory {

    /**
     * Create a multilayer perceptron
     * @param nodeCounts the number of nodes in each layer
     * @param transfer the transfer function
     * @param outputLayer the output layer of the network
     * @param outputFunction the output transfer function
     * @return a multilayer perceptron with nodeCounts.length layers
     */
    private FeedForwardNetwork createNetwork(int[] nodeCounts,
           DifferentiableActivationFunction transfer, Layer outputLayer,
           DifferentiableActivationFunction outputFunction) {
        if (nodeCounts.length < 2) {
            throw new IllegalArgumentException();
        }
        FeedForwardNetwork network = new FeedForwardNetwork();
        
        // create the input layer
        Layer inputLayer = new FeedForwardLayer();
        for (int i = 0; i < nodeCounts[0]; i++) {
            inputLayer.addNode(new FeedForwardNode(null));
        }
        inputLayer.addNode(new FeedForwardBiasNode(1));
        network.setInputLayer(inputLayer);
        
        // create hidden layers
        for (int i = 1; i < nodeCounts.length - 1; i++) {
            Layer hiddenLayer = new FeedForwardLayer();
            for (int j = 0; j < nodeCounts[i]; j++) {
                hiddenLayer.addNode(new FeedForwardNode(transfer));
            }
            hiddenLayer.addNode(new FeedForwardBiasNode(1));
            network.addHiddenLayer(hiddenLayer);
        }
        
        // create the output layer
       for (int i = 0; i < nodeCounts[nodeCounts.length - 1]; i++) {
            outputLayer.addNode(new FeedForwardNode(outputFunction));
        }
        network.setOutputLayer(outputLayer);
        network.connect();
        return network;
    }
    
    /**
     * Create a multilayer perceptron
     * @param nodeCounts the number of nodes in each layer
     * @param transfer the transfer function
     * @return a multilayer perceptron with nodeCounts.length layers
     */
    public FeedForwardNetwork createRegressionNetwork(int[] nodeCounts, 
            DifferentiableActivationFunction transfer) {
        return createNetwork(nodeCounts, transfer, new FeedForwardLayer(),
            new LinearActivationFunction());
    }

    /**
     * Create a multilayer perceptron
     * @param nodeCounts the number of nodes in each layer
     * @return a multilayer perceptron with nodeCounts.length layers
     */
    public FeedForwardNetwork createRegressionNetwork(int[] nodeCounts) {
        return createRegressionNetwork(nodeCounts, new HyperbolicTangentSigmoid());
    }
    
    /**
     * Create a multilayer perceptron
     * with a softmax output layer
     * @param nodeCounts the number of nodes in each layer
     * @param transfer the transfer function
     * @return a multilayer perceptron with nodeCounts.length layers
     */
    public FeedForwardNetwork createClassificationNetwork(int[] nodeCounts,
           DifferentiableActivationFunction transfer) {
       if (nodeCounts[nodeCounts.length - 1] == 1) {
           return createNetwork(nodeCounts, transfer, new FeedForwardLayer(),
             new LogisticSigmoid());     
       } else {
           //TODO: need a FeedForward equivalent of this layer
           return createNetwork(nodeCounts, transfer, new BackPropagationSoftMaxOutputLayer(),
               new LinearActivationFunction());
       }
    }

    /**
     * Create a multilayer perceptron
     * @param nodeCounts the number of nodes in each layer
     * @return a multilayer perceptron with nodeCounts.length layers
     */
    public FeedForwardNetwork createClassificationNetwork(int[] nodeCounts) {
        return createClassificationNetwork(nodeCounts, new HyperbolicTangentSigmoid());
    }

}
