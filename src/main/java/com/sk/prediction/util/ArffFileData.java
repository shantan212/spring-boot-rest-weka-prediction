package com.sk.prediction.util;

import java.text.ParseException;
import java.util.List;

import org.springframework.context.annotation.Scope;

import com.sk.prediction.model.PredictionEngineData;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instances;

@Scope(value="prototype")
public class ArffFileData {

    public boolean creatArffFile(List<PredictionEngineData> dataList, String fileName) throws ParseException {
        try {

            FastVector attributes;
            Instances data;
            double[] vals;
            // Meta Data For ARFF File
            // 1. set up attributes
            attributes = new FastVector();
            // - numeric
            attributes.addElement(new Attribute("intValue"));
            // - date
            attributes.addElement(new Attribute("TimeLine", "dd-MM-yyyy"));
            // 2. create Instances object
            data = new Instances("LinearPrediction", attributes, 0);
            // Data Adding Proccess
            for (int i = 0; i < dataList.size(); i++) {

                vals = new double[data.numAttributes()];
                // - Adding intValue

                vals[0] = dataList.get(i).getIntValue();
                // - Adding TimeLine

                vals[1] = data.attribute(1).parseDate(dataList.get(i).getTimeLine());
                // Adding Data
                data.add(new DenseInstance(1.0, vals));

            }

            // 4. output data

            return Utils.writeStringJsonToFile(data.toString(), fileName );
            /*
             * if (status == true) { return true; } else { return false; }
             */
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }

}
