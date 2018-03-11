package org.superbiz.dl4j.ex05;

import lombok.val;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.superbiz.dl4j.ex01.HelloDL4J;
import org.superbiz.util.LoggingConfig;

import java.io.IOException;
import java.util.logging.Logger;

public class MnistExample {
    static {
        System.setProperty("java.util.logging.config.class", LoggingConfig.class.getName());
    }

    private static final Logger log = Logger.getLogger(MnistExample.class.getName());

    public static void main(String[] args) throws IOException {
        int numRows = 28;
        int numColumns = 28;
        int outputNum = 10; // number of output classes
        int batchSize = 128; // batch size for each epoch
        int rngSeed = 123; // random number seed for reproducibility
        int numEpochs = 15; // number of epochs to perform

        //Get the DataSetIterators:
        MnistDataSetIterator mnistTrain = new MnistDataSetIterator(batchSize, true, rngSeed);
        MnistDataSetIterator mnistTest = new MnistDataSetIterator(batchSize, false, rngSeed);

        log.info("Build model....");
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(rngSeed) //include a random seed for reproducibility
                // use stochastic gradient descent as an optimization algorithm
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .iterations(1)
                .learningRate(0.006) //specify the learning rate
                .updater(Updater.NESTEROVS).momentum(0.9) //specify the rate of change of the learning rate.
                .regularization(true).l2(1e-4)
                .list()
                .layer(0, new DenseLayer.Builder() //create the first, input layer with xavier initialization
                        .nIn(numRows * numColumns)
                        .nOut(1000)
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD) //create hidden layer
                        .nIn(1000)
                        .nOut(outputNum)
                        .activation(Activation.SOFTMAX)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .pretrain(false).backprop(true) //use backpropagation to adjust weights
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        //print the score with every 1 iteration
        model.setListeners(new ScoreIterationListener(1));

        log.info("Train model....");
        for (int i = 0; i < numEpochs; i++) { //numEpochs - 1 ?
            model.fit(mnistTrain);
        }

        log.info("Evaluate model....");
        Evaluation eval = new Evaluation(outputNum); //create an evaluation object with 10 possible classes
        while (mnistTest.hasNext()) {
            DataSet next = mnistTest.next();
            INDArray output = model.output(next.getFeatureMatrix()); //get the networks prediction
            eval.eval(next.getLabels(), output); //check the prediction against the true class
        }

        log.info(eval.stats());
        log.info("****************Example finished********************");

    }
}

//
//    object MLPMnistTwoLayerExample {
//
//private val log = LoggerFactory.getLogger(MLPMnistTwoLayerExample::class.java)
//
//@Throws(Exception::class)
//@JvmStatic fun main(args: Array<String>) {
//        //number of rows and columns in the input pictures
//        val numRows = 28
//        val numColumns = 28
//        val outputNum = 10 // number of output classes
//        val batchSize = 64 // batch size for each epoch
//        val rngSeed = 123 // random number seed for reproducibility
//        val numEpochs = 15 // number of epochs to perform
//        val rate = 0.0015 // learning rate
//
//        //Get the DataSetIterators:
//        val mnistTrain = MnistDataSetIterator(batchSize, true, rngSeed)
//        val mnistTest = MnistDataSetIterator(batchSize, false, rngSeed)
//
//
//        log.info("Build model....")
//        val conf = NeuralNetConfiguration.Builder()
//        .seed(rngSeed) //include a random seed for reproducibility
//        .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT) // use stochastic gradient descent as an optimization algorithm
//        .iterations(1)
//        .activation(Activation.RELU)
//        .weightInit(WeightInit.XAVIER)
//        .learningRate(rate) //specify the learning rate
//        .updater(Updater.NESTEROVS).momentum(0.98) //specify the rate of change of the learning rate.
//        .regularization(true).l2(rate * 0.005) // regularize learning model
//        .list()
//        .layer(0, DenseLayer.Builder() //create the first input layer.
//        .nIn(numRows * numColumns)
//        .nOut(500)
//        .build())
//        .layer(1, DenseLayer.Builder() //create the second input layer
//        .nIn(500)
//        .nOut(100)
//        .build())
//        .layer(2, OutputLayer.Builder(LossFunction.NEGATIVELOGLIKELIHOOD) //create hidden layer
//        .activation(Activation.SOFTMAX)
//        .nIn(100)
//        .nOut(outputNum)
//        .build())
//        .pretrain(false).backprop(true) //use backpropagation to adjust weights
//        .build()
//
//        val model = MultiLayerNetwork(conf)
//        model.init()
//        model.setListeners(ScoreIterationListener(5))  //print the score with every iteration
//
//        log.info("Train model....")
//        for (i in 0..numEpochs - 1) {
//        log.info("Epoch " + i)
//        model.fit(mnistTrain)
//        }
//
//
//        log.info("Evaluate model....")
//        val eval = Evaluation(outputNum) //create an evaluation object with 10 possible classes
//        while (mnistTest.hasNext()) {
//        val next = mnistTest.next()
//        val output = model.output(next.getFeatureMatrix()) //get the networks prediction
//        eval.eval(next.getLabels(), output) //check the prediction against the true class
//        }
//
//        log.info(eval.stats())
//        log.info("****************Example finished********************")
//
//        }
//
//        }

