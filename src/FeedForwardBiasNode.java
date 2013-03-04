import func.nn.FeedForwardNode;


public class FeedForwardBiasNode extends FeedForwardNode {

    public FeedForwardBiasNode(double activation) {
        super(null);
        super.setActivation(activation);
    }

    /**
     * @see nn.FeedForwardNode#feedforward()
     */    
    public void feedforward() { }
}
