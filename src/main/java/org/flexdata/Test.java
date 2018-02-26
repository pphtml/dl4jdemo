package org.flexdata;

import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.flexdata.csv.DataReader;
import org.flexdata.csv.Resource;
import org.flexdata.transform.DataType;
import org.flexdata.transform.Schema;
import org.flexdata.transform.Transformator;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.superbiz.util.LoggingConfig;

import java.util.logging.Logger;

import static org.flexdata.csv.CSVReader.CSVReaderBuilder.createCSVReader;
import static org.flexdata.transform.Schema.ColumnBuilder.*;
import static org.flexdata.transform.Schema.SchemaBuilder.createSchema;

public class Test {
    static {
        System.setProperty("java.util.logging.config.class", LoggingConfig.class.getName());
    }

    private static final Logger LOGGER = Logger.getLogger(Test.class.getName());

    public static void main(String[] args) {

        try (DataReader dataReader = createCSVReader()
                .withSkipLines(1)
                .withResource(Resource.fromClasspath("org/superbiz/dl4j/ex04/train.csv"))
                .build()) {

// survived,pclass,name,sex,age,sibsp,parch,ticket,fare,cabin,embarked
// 0,3,"Braund, Mr. Owen Harris",male,22,1,0,A/5 21171,7.25,,S
            Schema schema = createSchema()
                .addColumn(classNamed("survived").setDataType(DataType.INTEGER).setCategorical(true).skipCategoricalMapping())
                .addColumn(featureNamed("pclass").setCategorical(true).oneHot())
                .addColumn(named("name"))
                .addColumn(featureNamed("sex").setCategorical(true).oneHot())
                .addColumn(featureNamed("age").setDataType(DataType.DOUBLE).defaultValue(29.7))
                .addColumn(featureNamed("sibsp").setDataType(DataType.INTEGER))
                .addColumn(featureNamed("parch").setDataType(DataType.INTEGER))
                .addColumn(named("ticket"))
                .addColumn(featureNamed("fare").setDataType(DataType.DOUBLE))
                .addColumn(named("cabin"))
                .addColumn(named("embarked"))
                .reorderColumns("age", "sex", "pclass", "sibsp", "parch", "fare", "survived")
                .build();


            DataSetIterator iterator = Transformator.createDataSetIterator(dataReader, schema);

            //toSave.
            DataSet allData = iterator.next();
            allData.shuffle(42);

            int CLASSES_COUNT = schema.getClassesCountExpanded(); // TODO FIX ME

            DataNormalization normalizer = new NormalizerStandardize();
            normalizer.fit(allData);
            normalizer.transform(allData);

            SplitTestAndTrain testAndTrain = allData.splitTestAndTrain(0.8);
            DataSet trainingData = testAndTrain.getTrain();
            DataSet testData = testAndTrain.getTest();

            int FIRST_LAYER_WIDTH = 8;
            int SECOND_LAYER_WIDTH = 8;
            MultiLayerConfiguration configuration
                    = new NeuralNetConfiguration.Builder()
                    .iterations(1000)
                    .activation(Activation.TANH)
                    .weightInit(WeightInit.XAVIER)
                    .learningRate(0.1)
                    .regularization(true).l2(0.0002)
                    .list()
                    .layer(0, new DenseLayer.Builder().nIn(schema.getFeaturesCountExpanded()).nOut(FIRST_LAYER_WIDTH).build())
                    .layer(1, new DenseLayer.Builder().nIn(FIRST_LAYER_WIDTH).nOut(SECOND_LAYER_WIDTH).build())
                    .layer(2, new OutputLayer.Builder(
                            LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                            .activation(Activation.SOFTMAX)
                            .nIn(SECOND_LAYER_WIDTH).nOut(CLASSES_COUNT).build())
                    .backprop(true).pretrain(false)
                    .build();

            MultiLayerNetwork model = new MultiLayerNetwork(configuration);
            model.setListeners(new ScoreIterationListener(100));
            model.init();
            model.fit(trainingData);

            INDArray output = model.output(testData.getFeatureMatrix());
            Evaluation eval = new Evaluation(2);
            eval.eval(testData.getLabels(), output);

            LOGGER.info(eval.stats());
        }
    }
}
