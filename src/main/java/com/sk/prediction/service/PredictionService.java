package com.sk.prediction.service;



import com.sk.prediction.exception.PredictionException;
import com.sk.prediction.model.PredictionModel;
import com.sk.prediction.model.RequestBean;

public interface PredictionService {
	
	public PredictionModel getTimeSeriesPredictionData(RequestBean requestBean) throws PredictionException;

}
