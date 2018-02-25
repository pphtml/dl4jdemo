package org.flexdata.transform;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.datavec.api.writable.DoubleWritable;
import org.datavec.api.writable.IntWritable;
import org.datavec.api.writable.Writable;
import org.flexdata.csv.DataRecord;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Schema {
    private final List<Column> columns;
    private final String[] reorderedColumnNames;
    private List<Transformation> transformationList;
    private int featuresCount = 0;
    private int classesCount = 0;
    private Integer featuresCountExpanded = null;
    private Integer classesCountExpanded = null;
    private boolean preprocessingNeeded = false;

    public Schema(List<Column> columns, String[] reorderedColumnNames) {
        this.columns = columns;
        this.reorderedColumnNames = reorderedColumnNames;
    }

    public List<Transformation> getTransformationList() {
        return this.transformationList;
    }

    public Integer getFeaturesCountExpanded() {
        return featuresCountExpanded;
    }

    public Integer getClassesCountExpanded() {
        return classesCountExpanded;
    }

//    public int getFeaturesCount() {
//        return featuresCount;
//    }
//
//    public int getClassesCount() {
//        return classesCount;
//    }

    public boolean isPreprocessingNeeded() {
        return preprocessingNeeded;
    }

    public void preprocess2() {
        featuresCountExpanded = 0;
        classesCountExpanded = 0;
        for (Schema.Transformation instruction : this.getTransformationList()) {
            instruction.preprocess2();
            featuresCountExpanded += instruction.getFeaturesCount();
            classesCountExpanded += instruction.getClassesCount();
        }
    }

    public static class SchemaBuilder {
        private List<Column> columns = new ArrayList<>();
        private String[] reorderedColumnNames;

        public static SchemaBuilder createSchema() {
            return new SchemaBuilder();
        }

        public SchemaBuilder addColumn(ColumnBuilder columnBuilder) {
            columns.add(columnBuilder.build());
            return this;
        }

        public Schema build() {
            Schema schema = new Schema(this.columns, this.reorderedColumnNames);
            schema.compile();
            return schema;
        }

        public SchemaBuilder reorderColumns(String... reorderedColumnNames) {
            this.reorderedColumnNames = reorderedColumnNames;
            return this;
        }
    }

    private void compile() {
        this.transformationList = new ArrayList<>();
        for (int index = 0; index < columns.size(); index++) {
            Column column = columns.get(index);
            if (column.isForProcessing()) {
                Transformation transformation = new Transformation(index, column);
                this.transformationList.add(transformation);

                if (column.typeFeature) {
                    this.featuresCount++;
                } else if (column.typeClass) {
                    this.classesCount++;
                }

                if (column.categorical) {
                    this.preprocessingNeeded = true;
                }
            }
        }

        if (this.reorderedColumnNames != null) {
            Map<String, Integer> orderingPriorityByNames = IntStream.range(0, this.reorderedColumnNames.length)
                    .mapToObj(orderIndex -> ImmutablePair.of(this.reorderedColumnNames[orderIndex], new Integer(orderIndex + 1)))
                    .collect(Collectors.toMap(p -> p.getLeft(), p -> p.getRight()));

            Collections.sort(this.transformationList, (o1, o2) -> {
                int p1 = orderingPriorityByNames.getOrDefault(o1.column.columnName, 0);
                int p2 = orderingPriorityByNames.getOrDefault(o2.column.columnName, 0);
                return p1 - p2;
            });

        }
    }

    public static class Column {

        private String columnName;
        private boolean typeFeature;
        private boolean typeClass;
        private DataType dataType;
        private boolean oneHot;
        private String defaultValue;
        private boolean categorical;
        private Set<String> possibleValues = new HashSet<>();
        private List<ImmutablePair<Integer, String>> categoricalRelations;
        private Map<String, Integer> categoricalMappingTo;
        private Map<Integer, String> categoricalMappingFrom;
        private boolean skipCategoricalMapping;

        public boolean isForProcessing() {
            return typeFeature || typeClass;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public void setTypeClass(boolean typeClass) {
            this.typeClass = typeClass;
        }

        public void setTypeFeature(boolean typeFeature) {
            this.typeFeature = typeFeature;
        }

        public void setOneHot(boolean oneHot) {
            this.oneHot = oneHot;
        }

        public void setDataType(DataType dataType) {
            this.dataType = dataType;
        }

        public List<Writable> convertDataToWritable(String value, DataRecord lineString) {

            if (this.categorical && !this.skipCategoricalMapping) {
                Integer categoricalValue = this.categoricalMappingTo.get(value);
                if (this.oneHot) {
                    return createOneHot(categoricalValue, this.categoricalRelations.size());
                } else {
                    return Collections.singletonList(new IntWritable(categoricalValue));
                }
            } else {
                if (dataType == null) {
                    throw new RuntimeException(String.format("Data type for column %s must be provided", columnName));
                }

                if (value.length() == 0) {
                    if (this.defaultValue == null || this.defaultValue.length() == 0) {
                        throw new RuntimeException(String.format("Default value is needed, but not available :( . Column %s, line: %s", columnName, lineString));
                    } else {
                        return Collections.singletonList(dataType.convertToWritable(this.defaultValue));
                    }
                } else {
                    return Collections.singletonList(dataType.convertToWritable(value));
                }
            }
        }

        private List<Writable> createOneHot(Integer categoricalValue, int size) {
            List<Writable> oneHotVector = IntStream.range(0, size)
                    .map(index -> categoricalValue == index ? 1 : 0)
                    .mapToObj(value -> new IntWritable(value))
                    .collect(Collectors.toList());
            return oneHotVector;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public void setCategorical(boolean categorical) {
            this.categorical = categorical;
        }

        public void preprocess(String value) {
            this.possibleValues.add(value);
        }

        public void preprocess2() {
            List<String> values = new ArrayList<>(this.possibleValues);
            Collections.sort(values);
            this.categoricalRelations = IntStream.range(0, values.size())
                    .mapToObj(index -> ImmutablePair.of(new Integer(index), values.get(index)))
                    .collect(Collectors.toList());
                    //.collect(Collectors.toMap(p -> p.getLeft(), p -> p.getRight()));
            this.categoricalMappingTo = this.categoricalRelations.stream()
                    .collect(Collectors.toMap(p -> p.getRight(), p -> p.getLeft()));
            this.categoricalMappingFrom = this.categoricalRelations.stream()
                    .collect(Collectors.toMap(p -> p.getLeft(), p -> p.getRight()));
        }

        public void setSkipCategoricalMapping(boolean skipCategoricalMapping) {
            this.skipCategoricalMapping = skipCategoricalMapping;
        }

        public Integer getFeaturesCount() {
            return typeFeature ? (categoricalRelations != null ? categoricalRelations.size() : 1) : 0;
        }

        public Integer getClassesCount() {
            return typeClass ? (categoricalRelations != null ? categoricalRelations.size() : 1) : 0;
        }
    }

    public static class Transformation {

        private final int index;
        private final Column column;

        public Transformation(int index, Column column) {
            this.index = index;
            this.column = column;
        }

        public List<Writable> process(DataRecord inputDataRecord) {
            String value = inputDataRecord.get(index);
            return column.convertDataToWritable(value, inputDataRecord);
        }

        public void preprocess(DataRecord inputDataRecord) {
            if (column.categorical) {
                String value = inputDataRecord.get(index);
                column.preprocess(value);
            }
        }

        public void preprocess2() {
            if (column.categorical) {
                column.preprocess2();
            }
        }

        public Integer getFeaturesCount() {
            return column.getFeaturesCount();
        }

        public Integer getClassesCount() {
            return column.getClassesCount();
        }
    }

//    private static class NoOperation extends Transformation {}

    public static class ColumnBuilder {
        private final String columnName;
        private boolean typeClass;
        private boolean typeFeature;
        private boolean oneHot;
        private DataType dataType;
        private String defaultValue;
        private boolean categorical;
        private boolean skipCategoricalMapping;

        public ColumnBuilder(String columnName) {
            this.columnName = columnName;
        }

        public static ColumnBuilder classNamed(String columnName) {
            ColumnBuilder columnBuilder = new ColumnBuilder(columnName);
            columnBuilder.typeClass = true;
            return columnBuilder;
        }

        public static ColumnBuilder featureNamed(String columnName) {
            ColumnBuilder columnBuilder = new ColumnBuilder(columnName);
            columnBuilder.typeFeature = true;
            return columnBuilder;
        }

        public static ColumnBuilder named(String columnName) {
            return new ColumnBuilder(columnName);
        }

        public Column build() {
            Column result = new Column();
            result.setColumnName(columnName);
            result.setTypeClass(typeClass);
            result.setTypeFeature(typeFeature);
            result.setOneHot(oneHot);
            result.setCategorical(categorical);
            result.setDataType(dataType);
            result.setDefaultValue(defaultValue);
            result.setSkipCategoricalMapping(skipCategoricalMapping);
            return result;
        }

        public ColumnBuilder oneHot() {
            this.oneHot = true;
            return this;
        }

        public ColumnBuilder setCategorical(boolean categorical) {
            this.categorical = categorical;
            return this;
        }

        public ColumnBuilder defaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public ColumnBuilder setDataType(DataType dataType) {
            this.dataType = dataType;
            return this;
        }

        public ColumnBuilder defaultValue(Double defaultValue) {
            this.defaultValue = defaultValue.toString();
            return this;
        }

        public ColumnBuilder skipCategoricalMapping() {
            this.skipCategoricalMapping = true;
            return this;
        }
    }
}
