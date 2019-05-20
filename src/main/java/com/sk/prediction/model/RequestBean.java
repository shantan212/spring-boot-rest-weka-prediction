
package com.sk.prediction.model;

import java.util.List;

public class RequestBean {

    private String fileName;
    private int numberOfPointsToPredict;
    private String timePeriodType;
    private String fromDate;
    private String toDate;
    private List<PredictionEngineData> predictionData;

    public List<PredictionEngineData> getPredictionData() {
        return predictionData;
    }

    public void setPredictionData(List<PredictionEngineData> predictionData) {
        this.predictionData = predictionData;
    }

    public String getTimePeriodType() {
        return timePeriodType;
    }

    public void setTimePeriodType(String timePeriodType) {
        this.timePeriodType = timePeriodType;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

   

    public int getNumberOfPointsToPredict() {
        return numberOfPointsToPredict;
    }

    public void setNumberOfPointsToPredict(int numberOfPointsToPredict) {
        this.numberOfPointsToPredict = numberOfPointsToPredict;
    }
    
    
    public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	@Override
    public String toString() {
    
    	
    	String print ="numberOfPointsToPredict : "+numberOfPointsToPredict +", timePeriodType :"+timePeriodType+", fromDate:"+fromDate;
    	
    	for (int i=0;i<predictionData.size();i++){
    		print += "\n For Prediction dataset "+i+1 +"\n";
    		print += " intValue :"+predictionData.get(i).getIntValue()+ ", timeLine:"+predictionData.get(i).getTimeLine();
    	}
    	
    	return print;
    }
    

}
