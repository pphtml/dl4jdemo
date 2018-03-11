package org.superbiz.dl4j.ex04;

//import org.apache.spark.SparkConf;
//import org.apache.spark.api.java.JavaRDD;
//import org.apache.spark.api.java.JavaSparkContext;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.util.ClassPathResource;
//import org.datavec.api.writable.Writable;
//import org.datavec.spark.transform.SparkTransformExecutor;
//import org.datavec.spark.transform.misc.StringToWritablesFunction;
//import org.datavec.spark.transform.misc.WritablesToStringFunction;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.superbiz.dl4j.ex01.HelloDL4J;
import org.superbiz.util.LoggingConfig;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Titanic {
    static {
        System.setProperty("java.util.logging.config.class", LoggingConfig.class.getName());
    }

    private static final Logger LOGGER = Logger.getLogger(Titanic.class.getName());

    private static final int FEATURES_COUNT = 1;
    private static final int CLASSES_COUNT = 2;
    private static final int RECORDS_COUNT = 150;

    public static void main(String[] args) throws Exception {
        // https://github.com/saurav09/Kaggle-Titanic-Solution-Using-Spark-Java/blob/master/src/main/java/com/learning/spark/TitanicProblemSpark.java

        try (RecordReader recordReader = new CSVRecordReader(1, ',')) {
            recordReader.initialize(new FileSplit(
                    new ClassPathResource("org/superbiz/dl4j/ex04/train.csv").getFile()));

            Schema schema = new Schema.Builder()
                    .addColumnInteger("survived")
//                    .addColumnCategorical("survived", Arrays.asList("0", "1"))
//                    .addColumnCategorical("pclass", Arrays.asList("1", "2", "3"))
                    .addColumnString("pclass")
                    .addColumnString("name")
                    .addColumnCategorical("sex", Arrays.asList("male", "female"))
                    .addColumnFloat("age")
                    .addColumnString("sibsp")
                    .addColumnString("parch")
                    .addColumnString("ticket")
                    .addColumnString("fare")
                    .addColumnString("cabin")
                    .addColumnString("embarked")
                    .build();

            Map<String, String > mapping = new HashMap<>();
            mapping.put("", "33"); // TODO odhadnuto
            TransformProcess process = new TransformProcess.Builder(schema)
//                    .removeColumns("customerID")
//                    .categoricalToInteger("label")
//                    .stringToTimeTransform("dateTime","YYYY-MM-dd HH:mm:ss", DateTimeZone.UTC)
//                    .transform(new DeriveColumnsFromTimeTransform.Builder("dateTime")
//                            .addIntegerDerivedColumn("hourOfDay", DateTimeFieldType.hourOfDay()).build())
                    .removeColumns("sibsp", "parch", "ticket", "fare", "cabin", "embarked")
                    .removeColumns("name")
                    .removeColumns("pclass", "sex")
                    //.reorderColumns("pclass", "sex", "age", "survived")
                    .reorderColumns("age", "survived")
                    //.categoricalToOneHot("survived")
                    .stringMapTransform("age", mapping)
//                    .conditionalReplaceValueTransform(
//                            "age",     //Column to operate on
//                            new DoubleWritable(0.0),    //New value to use, when the condition is satisfied
//                            new DoubleColumnCondition("TransactionAmountUSD",ConditionOp.LessThan, 0.0))
                    .build();


//            SparkConf conf = new SparkConf();
//            conf.setMaster("local[*]");
//            conf.setAppName("DataVec Example");
//            JavaSparkContext sc = new JavaSparkContext(conf);
////Load our data using Spark
//            String path = "src/main/resources/org/superbiz/dl4j/ex04/train.csv";
//            JavaRDD<String> lines = sc.textFile(path);
//            JavaRDD<List<Writable>> examples =
//                    lines.map(new StringToWritablesFunction(new CSVRecordReader()));
//
//            //Execute the operations
//            //SparkTransformExecutor executor = new SparkTransformExecutor();
//            JavaRDD<List<Writable>> processed = SparkTransformExecutor.execute(examples, process);
//            //JavaRDD<List<Writable>> processed = executor.execute(examples, process);
////Save the processed data:
//            JavaRDD<String> toSave = processed.map(new WritablesToStringFunction(",", "\""));
//            toSave.saveAsTextFile("src/main/resources/org/superbiz/dl4j/ex04/transformed.csv");

//            List<Writable> line = ColumnWrapper.one(schema, recordReader.next());
//            List<Writable> processed = process.execute(line);
//            System.out.println(processed);
//

            RecordReader ble = new MyRecordReader(recordReader, process);
            DataSetIterator iterator = new RecordReaderDataSetIterator(
                    ble, RECORDS_COUNT, FEATURES_COUNT, CLASSES_COUNT);



            //toSave.
            DataSet allData = iterator.next();
            allData.shuffle(42);
//
//            DataNormalization normalizer = new NormalizerStandardize();
//            normalizer.fit(allData);
//            normalizer.transform(allData);
//
//            SplitTestAndTrain testAndTrain = allData.splitTestAndTrain(0.65);
//            DataSet trainingData = testAndTrain.getTrain();
//            DataSet testData = testAndTrain.getTest();
//
//            MultiLayerConfiguration configuration
//                    = new NeuralNetConfiguration.Builder()
//                    .iterations(1000)
//                    .activation(Activation.TANH)
//                    .weightInit(WeightInit.XAVIER)
//                    .learningRate(0.1)
//                    .regularization(true).l2(0.0001)
//                    .list()
//                    .layer(0, new DenseLayer.Builder().nIn(FEATURES_COUNT).nOut(3).build())
//                    .layer(1, new DenseLayer.Builder().nIn(3).nOut(3).build())
//                    .layer(2, new OutputLayer.Builder(
//                            LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
//                            .activation(Activation.SOFTMAX)
//                            .nIn(3).nOut(CLASSES_COUNT).build())
//                    .backprop(true).pretrain(false)
//                    .build();
//
//            MultiLayerNetwork model = new MultiLayerNetwork(configuration);
//            model.init();
//            model.fit(trainingData);
//
//            INDArray output = model.output(testData.getFeatureMatrix());
//            Evaluation eval = new Evaluation(3);
//            eval.eval(testData.getLabels(), output);
//
//            LOGGER.info(eval.stats());
        }
    }

}
