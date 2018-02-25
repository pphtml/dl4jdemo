package org.superbiz.dl4j.ex03;

import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.superbiz.dl4j.ex03.functions.MathFunction;
import org.superbiz.dl4j.ex03.functions.SinXDivXMathFunction;
import org.superbiz.util.LoggingConfig;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Adapted from https://github.com/deeplearning4j/dl4j-examples.git
 */
public class RegressionMathFunctions {
    static {
        System.setProperty("java.util.logging.config.class", LoggingConfig.class.getName());
    }

    private static final Logger LOGGER = Logger.getLogger(RegressionMathFunctions.class.getName());

    public static final int SEED = 12345;
    public static final int N_EPOCHS = 200;
    public static final int PLOT_FREQUENCY = 50;
    public static final int N_SAMPLES = 1000;
    public static final int BATCH_SIZE = 100;
    public static final double LEARNING_RATE = 0.01;
    public static final int NUM_INPUTS = 1;
    public static final int NUM_OUTPUTS = 1;
    private static final int NUM_HIDDEN_NODES = 50;

    public static final Random RANDOM = new Random(SEED);


    public static void main(final String[] args){
        final MathFunction fn = new SinXDivXMathFunction();
        final MultiLayerConfiguration conf = getDeepDenseLayerNetworkConfiguration();

        //Generate the training data
        final INDArray x = Nd4j.linspace(-10,10, N_SAMPLES).reshape(N_SAMPLES, 1);
        //final DataSetIterator iterator = getTrainingData(x, fn, BATCH_SIZE, RANDOM);
        final INDArray y = fn.getFunctionValues(x);
        final DataSet allData = new DataSet(x,y);

        final List<DataSet> list = allData.asList();
        Collections.shuffle(list, RANDOM);
        final DataSetIterator iterator = new ListDataSetIterator(list, BATCH_SIZE);

        //Create the network
        final MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        net.setListeners(new ScoreIterationListener(10));

        //Train the network on the full data set, and evaluate in periodically
        final INDArray[] networkPredictions = new INDArray[N_EPOCHS / PLOT_FREQUENCY];
        for(int i = 0; i< N_EPOCHS; i++ ){
            iterator.reset();
            net.fit(iterator);
            if((i+1) % PLOT_FREQUENCY == 0) networkPredictions[i/ PLOT_FREQUENCY] = net.output(x, false);
        }

//        Evaluation eval = new Evaluation(2);
//        eval.eval(y.getLabels(), output);
//        LOGGER.info(eval.stats());


        //Plot the target data and the network predictions
        plot(fn,x,fn.getFunctionValues(x),networkPredictions);
    }

    /** Returns the network configuration, 2 hidden DenseLayers of size 50.
     */
    private static MultiLayerConfiguration getDeepDenseLayerNetworkConfiguration() {
        return new NeuralNetConfiguration.Builder()
                .seed(SEED)
                .iterations(1)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .learningRate(LEARNING_RATE)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(0.9))
                .list()
                .layer(0, new DenseLayer.Builder().nIn(NUM_INPUTS).nOut(NUM_HIDDEN_NODES)
                        .activation(Activation.TANH).build())
                .layer(1, new DenseLayer.Builder().nIn(NUM_HIDDEN_NODES).nOut(NUM_HIDDEN_NODES)
                        .activation(Activation.TANH).build())
                .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nIn(NUM_HIDDEN_NODES).nOut(NUM_OUTPUTS).build())
                .pretrain(false).backprop(true).build();
    }

//    /** Create a DataSetIterator for training
//     * @param x X values
//     * @param function Function to evaluate
//     * @param batchSize Batch size (number of examples for every call of DataSetIterator.next())
//     * @param rng Random number generator (for repeatability)
//     */
//    private static DataSetIterator getTrainingData(final INDArray x, final MathFunction function, final int batchSize, final Random rng) {
//        final INDArray y = function.getFunctionValues(x);
//        final DataSet allData = new DataSet(x,y);
//
//        final List<DataSet> list = allData.asList();
//        Collections.shuffle(list,rng);
//        return new ListDataSetIterator(list, BATCH_SIZE);
//    }

    //Plot the data
    private static void plot(final MathFunction function, final INDArray x, final INDArray y, final INDArray... predicted) {
        final XYSeriesCollection dataSet = new XYSeriesCollection();
        addSeries(dataSet,x,y,"True Function (Labels)");

        for( int i=0; i<predicted.length; i++ ){
            addSeries(dataSet,x,predicted[i],String.valueOf(i));
        }

        final JFreeChart chart = ChartFactory.createXYLineChart(
                "Regression Example - " + function.getName(),      // chart title
                "X",                        // x axis label
                function.getName() + "(X)", // y axis label
                dataSet,                    // data
                PlotOrientation.VERTICAL,
                true,                       // include legend
                true,                       // tooltips
                false                       // urls
        );

        final ChartPanel panel = new ChartPanel(chart);

        final JFrame f = new JFrame();
        f.add(panel);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.pack();

        f.setVisible(true);
    }

    private static void addSeries(final XYSeriesCollection dataSet, final INDArray x, final INDArray y, final String label){
        final double[] xd = x.data().asDouble();
        final double[] yd = y.data().asDouble();
        final XYSeries s = new XYSeries(label);
        for( int j=0; j<xd.length; j++ ) s.add(xd[j],yd[j]);
        dataSet.addSeries(s);
    }
}
