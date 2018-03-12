package org.flexdata.nn;

//import org.deeplearning4j.eval.Evaluation;
//import org.deeplearning4j.nn.api.OptimizationAlgorithm;
//import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
//import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
//import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
//import org.deeplearning4j.nn.conf.layers.DenseLayer;
//import org.deeplearning4j.nn.conf.layers.OutputLayer;
//import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
//import org.deeplearning4j.nn.weights.WeightInit;
//import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
//import org.nd4j.linalg.activations.Activation;
//import org.nd4j.linalg.api.ndarray.INDArray;
//import org.nd4j.linalg.dataset.DataSet;
//import org.nd4j.linalg.factory.Nd4j;
//import org.nd4j.linalg.lossfunctions.LossFunctions;


import org.flexdata.data.DataRow;
import org.flexdata.data.DataSet;
import org.flexdata.data.Features;
import org.flexdata.data.Labels;
import org.flexdata.nn.activation.Relu;
import org.flexdata.nn.activation.Softmax;
import org.flexdata.nn.initialization.SequenceDistribution;
import org.flexdata.nn.initialization.UniformDistribution;
import org.flexdata.nn.initialization.WeightInit;
import org.superbiz.util.LoggingConfig;

// <dependency>
//    <groupId>com.github.bentorfs</groupId>
//    <artifactId>ai-algorithms</artifactId>
//    <version>0.2.0</version>
//</dependency>

public class NeuralNetClient {
    static {
        System.setProperty("java.util.logging.config.class", LoggingConfig.class.getName());
    }

    public static void main(String[] args) {
        DataSet xorDataSet = DataSet.ofList(
                DataRow.of(Features.from(0.0, 0.0), Labels.of(1.0, 0.0)),
                DataRow.of(Features.from(0.0, 1.0), Labels.of(0.0, 1.0)),
                DataRow.of(Features.from(1.0, 0.0), Labels.of(0.0, 1.0)),
                DataRow.of(Features.from(1.0, 1.0), Labels.of(1.0, 0.0))
        );
//        DataSet xorDataSet = DataSet.ofList(
//                DataRow.of(Features.from(0.0, 0.0), Labels.of(0.0)),
//                DataRow.of(Features.from(0.0, 1.0), Labels.of(1.0)),
//                DataRow.of(Features.from(1.0, 0.0), Labels.of(1.0)),
//                DataRow.of(Features.from(1.0, 1.0), Labels.of(0.0))
//        );

        NeuralNet net = NeuralNet.Builder.create()
                .withRandomSeed(42)
                .withInputLayer(Layer.fromDataSet(xorDataSet))
                .addHiddenLayer(HiddenLayer.Builder.create()
                        .withNeuronCount(4)
                        .withActivationFunction(Relu.class)
                        .withWeightInitialization(WeightInit.RELU)
                        //.setInitialParamsList(0.02, -2.49, 1.65, 2.80, -2.98, 2.98, 2.99, 0.35, -0.03, -1.65, -0.00, -0.00)
                        .build()
                )
                .withOutputLayer(OutputLayer.Builder.create()
                        .withNeuronCount(2)
                        .withActivationFunction(Softmax.class)
                        //.withWeightInitialization(WeightInit.DISTRIBUTION, new UniformDistribution(0, 1))
                        .withWeightInitialization(WeightInit.DISTRIBUTION, SequenceDistribution.of(0.53196114,
                                0.35115042, 0.8987584, 0.17555624, 0.1691667, 0.7674509, 0.6325145, 0.6179164))
                        .build()
                )
                .build();

        net.fit();

//        System.out.println(net.evaluate(Features.from(0.0, 0.0)));
//        System.out.println(net.evaluate(Features.from(0.0, 1.0)));
//        System.out.println(net.evaluate(Features.from(1.0, 0.0)));
//        System.out.println(net.evaluate(Features.from(1.0, 1.0)));
        //System.out.println(result);

//        INDArray inputs = Nd4j.create(new float[]{0,0, 0,1, 1,0, 1,1},new int[]{4, 2});
//        INDArray labels = Nd4j.create(new float[]{1,0, 0,1, 0,1, 1,0},new int[]{4, 2});
//        DataSet ds = new DataSet(inputs, labels);
//        NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder()
//                .iterations(1000)
//                .learningRate(0.1)
//                .seed(42)
//                .useDropConnect(false)
//                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
//                //.biasInit(0)
//                // from "http://deeplearning4j.org/architecture";;: The networks can
//                // process the input more quickly and more accurately by ingesting
//                // minibatches 5-10 elements at a time in parallel.
//                // this example runs better without, because the dataset is smaller than
//                // the mini batch size
//                .miniBatch(false);
//
//        NeuralNetConfiguration.ListBuilder listBuilder = builder.list();
//        DenseLayer.Builder hiddenLayerBuilder = new DenseLayer.Builder()
//                .nIn(2)
//                .nOut(HIDDEN_LAYER_NEURON_COUNT)
//                .activation(Activation.RELU)
//                .weightInit(WeightInit.RELU);
//        //.weightInit(WeightInit.DISTRIBUTION)
//        //.dist(new UniformDistribution(0, 1));
//        // build and set as layer 0
//        listBuilder.layer(0, hiddenLayerBuilder.build());
//
//        OutputLayer.Builder outputLayerBuilder = new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
//                .nIn(HIDDEN_LAYER_NEURON_COUNT)
//                .nOut(2)
//                .activation(Activation.SOFTMAX)
//                .weightInit(WeightInit.DISTRIBUTION)
//                .dist(new UniformDistribution(0, 1));
//        listBuilder.layer(1, outputLayerBuilder.build());
//        listBuilder.pretrain(false);
//
//        listBuilder.backprop(true);
//
//        MultiLayerConfiguration conf = listBuilder.build();
//        MultiLayerNetwork net = new MultiLayerNetwork(conf);
//        net.init();
//
//        net.setListeners(new ScoreIterationListener(100));
//
//        Layer[] layers = net.getLayers();
//        int totalNumParams = 0;
//        for (int i = 0; i < layers.length; i++) {
//            int nParams = layers[i].numParams();
//            LOGGER.info("Number of parameters in layer " + i + ": " + nParams);
//            totalNumParams += nParams;
//        }
//        LOGGER.info("Total number of network parameters: " + totalNumParams);
//
//        net.fit(ds);
//
//        INDArray output = net.output(ds.getFeatureMatrix());
//        LOGGER.info(String.format("%s", output));
//
//        Evaluation eval = new Evaluation(2);
//        eval.eval(ds.getLabels(), output);
//        LOGGER.info(eval.stats());
//        LOGGER.info(String.format("%s", layers[0].params()));

    }
}
