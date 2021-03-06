package br.com.marketmining.dsaflow.forward;

import static org.nd4j.linalg.ops.transforms.Transforms.normalizeZeroMeanAndUnitVariance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import br.com.marketmining.dsaflow.AnnUtil;
import br.com.marketmining.dsaflow.DatasetUtil;
import br.com.marketmining.dsaflow.Input;
import br.com.marketmining.dsaflow.Linear;
import br.com.marketmining.dsaflow.Mse;
import br.com.marketmining.dsaflow.Node;
import br.com.marketmining.dsaflow.Sigmoid;

public class DsaFlowBoston {

	public void execute() {

		// values
		INDArray instances = DatasetUtil.getHousingInstances();
		INDArray inputs = normalizeZeroMeanAndUnitVariance(
				instances.getColumns(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 12));
		INDArray outputs = instances.getColumn(13);

		// 1st layer
		int nFeatures = (int) inputs.shape()[1];
		int nHidden = 20;
		INDArray weights1 = Nd4j.rand(nFeatures, nHidden);
		INDArray bias1 = Nd4j.zeros(nHidden);

		// hidden layer
		int nLastHidden = 1;
		INDArray weights2 = Nd4j.rand(nHidden, nLastHidden);
		INDArray bias2 = Nd4j.zeros(nLastHidden);

		// buiding net
		Input x = new Input("X");
		Input y = new Input("Y");
		Input w1 = new Input("W1");
		Input w2 = new Input("W2");
		Input b1 = new Input("B1");
		Input b2 = new Input("B2");

		Linear l1 = new Linear("l1", x, w1, b1);
		Sigmoid s1 = new Sigmoid("s1", l1);

		Linear l2 = new Linear("l2", s1, w2, b2);
		Mse cost = new Mse("mse", l2, y);

		// binding values to inputs
		Map<Node, INDArray> feed = new HashMap<Node, INDArray>();
		feed.put(x, inputs);
		feed.put(y, outputs);
		feed.put(w1, weights1);
		feed.put(b1, bias1);
		feed.put(w2, weights2);
		feed.put(b2, bias2);

		// creating graph
		ArrayList<Node> graph = AnnUtil.sortNodes(feed);

		// defining training nodes
		Node[] nodesToTraining = { w1, b1, w2, b2 };

		int epochs = 10;

		for (int i = 0; i < epochs; i++) {

			AnnUtil.callForwardBackward(graph);

			AnnUtil.gradientUpdate(nodesToTraining, 0.05);

			System.out.println("epoch=" + i + " cost=" + cost.value);
		}

	}

}
