
package com.sk.prediction.model;

import java.util.ArrayList;
import java.util.List;

public class PredictionModel {

    private List<PredictionData> data = new ArrayList<PredictionData>();

    public List<PredictionData> getData() {
        return data;
    }

    public void setData(List<PredictionData> data) {
        this.data = data;
    }

    public class PredictionData {

        private double size;

        private String timeStamp;
        private List<String> timeLineDate;

        public List<String> getTimeLineDate() {
            return timeLineDate;
        }

        public void setTimeLineDate(List<String> timeLineDate) {
            this.timeLineDate = timeLineDate;
        }

        private boolean isPredicted;

        public String getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
        }

        public boolean isPredicted() {
            return isPredicted;
        }

        public void setPredicted(boolean isPredicted) {
            this.isPredicted = isPredicted;
        }

        public double getSize() {
            return size;
        }

        public void setSize(double size) {
            this.size = size;
        }
    }

}
