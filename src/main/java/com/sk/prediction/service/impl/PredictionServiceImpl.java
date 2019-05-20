package com.sk.prediction.service.impl;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sk.prediction.constants.ErrorConstants;
import com.sk.prediction.constants.PredictionConstants;
import com.sk.prediction.exception.PredictionException;
import com.sk.prediction.model.PredictionModel;
import com.sk.prediction.model.PredictionModel.PredictionData;
import com.sk.prediction.model.RequestBean;
import com.sk.prediction.service.PredictionService;
import com.sk.prediction.util.ArffFileData;
import com.sk.prediction.util.Utils;

import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.timeseries.WekaForecaster;

import weka.core.Instances;
import weka.filters.supervised.attribute.TSLagMaker.Periodicity;

@Service
public class PredictionServiceImpl implements PredictionService{
	private static final String dateFormate = "yyyy-MM-dd";
	@Autowired
	private ArffFileData arffFileData;
	
	@Autowired
	private Utils utils;
	
	public PredictionModel getTimeSeriesPredictionData(RequestBean requestBean) throws  PredictionException {
		requestBean.setFileName(Utils.generateArFFileName());
		boolean arffFilegenerateStatus = false;
		PredictionModel obj =null;
		try {
			arffFilegenerateStatus = arffFileData.creatArffFile(requestBean.getPredictionData(), requestBean.getFileName());
		} catch (ParseException e1) {
			throw new PredictionException(ErrorConstants.INTERNAL_EXCEPTION);
		}
		if(arffFilegenerateStatus == true){
			Instances instances= null;
			try {
				instances = utils.loadInstnaces(requestBean.getFileName() );
				System.out.println("instances ::"+instances);
			} catch (IOException e) {
				throw new PredictionException(ErrorConstants.INTERNAL_EXCEPTION);
			}
			obj = getPredictionData(instances, requestBean);
			System.out.println("obj ::"+obj.getData().size());
		}
		return obj;
	}
	
	 public PredictionModel getPredictionData(Instances instances, RequestBean requestBean)
	            throws PredictionException {

	        PredictionModel predictionModel = new PredictionModel();
	        try {
	            List<Date> dateList = null;
	            if (requestBean.getTimePeriodType().equalsIgnoreCase(PredictionConstants.DAY)) {
	                dateList = Utils.getFutureListOfDays(requestBean.getFromDate(),
	                		requestBean.getToDate());
	            } else if (requestBean.getTimePeriodType().equalsIgnoreCase(PredictionConstants.WEEK)) {

	                dateList = Utils.getFutureListOfWeeks(requestBean.getFromDate(),
	                		requestBean.getToDate());
	            } else if (requestBean.getTimePeriodType().equalsIgnoreCase(PredictionConstants.MONTH)) {
	               
	                    dateList = Utils.getFutureListOfMonths(requestBean.getFromDate(),
	                    		requestBean.getToDate());

	            } else if (requestBean.getTimePeriodType().equalsIgnoreCase(PredictionConstants.YEAR)) {
	                dateList = Utils.getFutureListOfYears(requestBean.getFromDate(),
	                		requestBean.getToDate());

	            }

	            System.out.println("dateList ::"+dateList.size());
	            Map<String, ArrayList<String>> dateMap = null;

	            dateMap = utils.processDatesPrevious(dateList, requestBean.getTimePeriodType(),
	                    utils.getDateToString(new Date()));
	            System.out.println("dateMap ::"+dateMap);
	            System.out.println("dateList 1::"+dateMap.size());
	            utils.populateExistedData(predictionModel, instances, requestBean, dateList, dateMap);
	            System.out.println("dateList ::"+predictionModel.getData().size());
	            Instances toAnalysis = new Instances(instances);
	            WekaForecaster forecaster = new WekaForecaster();
	            ArrayList<PredictionData> predictionData = null;

	            forecaster.setFieldsToForecast("intValue");
	            forecaster.setBaseForecaster(new LinearRegression());
	            forecaster.getTSLagMaker().setTimeStampField("TimeLine");
	            if (requestBean.getTimePeriodType().equalsIgnoreCase(PredictionConstants.DAY)) {
	                forecaster.getTSLagMaker().setPeriodicity(Periodicity.DAILY);
	            } else if (requestBean.getTimePeriodType().equalsIgnoreCase(PredictionConstants.MONTH)) {
	                forecaster.getTSLagMaker().setPeriodicity(Periodicity.MONTHLY);
	            } else if (requestBean.getTimePeriodType().equalsIgnoreCase(PredictionConstants.WEEK)) {
	                forecaster.getTSLagMaker().setPeriodicity(Periodicity.WEEKLY);
	            } else if (requestBean.getTimePeriodType().equalsIgnoreCase(PredictionConstants.YEAR)) {
	                forecaster.getTSLagMaker().setPeriodicity(Periodicity.YEARLY);
	            }

	            List<Date> dateListFuture = null;
	            Map<String, ArrayList<String>> futureDateMap = null;
	            if (requestBean.getTimePeriodType().equalsIgnoreCase(PredictionConstants.DAY)) {
	                Calendar curDate = Calendar.getInstance();
	                DateTime currToDate = new DateTime(curDate.getTime())
	                        .plusDays(requestBean.getNumberOfPointsToPredict());

	                dateListFuture = utils.getFutureListOfDays(
	                		utils.getDateToString(new Date()),
	                		utils.getDateToString(currToDate.toDate()));
	                futureDateMap = utils.processDatesFuture(dateListFuture, requestBean.getTimePeriodType(),
	                		utils.getDateToString(currToDate.toDate()));

	            } else if (requestBean.getTimePeriodType().equalsIgnoreCase(PredictionConstants.WEEK)) {
	                Calendar curDate = Calendar.getInstance();
	                DateTime currToDate = new DateTime(curDate.getTime())
	                        .plusWeeks(requestBean.getNumberOfPointsToPredict());
	                dateListFuture = utils.getFutureListOfWeeks(
	                        utils.getDateToString(new Date()),
	                        utils.getDateToString(currToDate.toDate()));
	                futureDateMap = utils.processDatesFuture(dateListFuture, requestBean.getTimePeriodType(),
	                        utils.getDateToString(currToDate.toDate()));
	            } else if (requestBean.getTimePeriodType().equalsIgnoreCase(PredictionConstants.MONTH)) {
	                try {
	                    Calendar curDate = Calendar.getInstance();
	                    DateTime currToDate = new DateTime(curDate.getTime())
	                            .plusMonths(requestBean.getNumberOfPointsToPredict());
	                    dateListFuture = utils.getFutureListOfMonths(
	                            utils.getDateToString(new Date()),
	                            utils.getDateToString(currToDate.toDate()));
	                    futureDateMap = utils.processDatesFuture(dateListFuture,
	                            requestBean.getTimePeriodType(), utils.getDateToString(currToDate.toDate()));
	                } catch (ParseException e) {

	                }
	            } else if (requestBean.getTimePeriodType().equalsIgnoreCase(PredictionConstants.YEAR)) {
	                Calendar curDate = Calendar.getInstance();
	                DateTime currToDate = new DateTime(curDate.getTime())
	                        .plusYears(requestBean.getNumberOfPointsToPredict());
	                dateListFuture = utils.getFutureListOfYears(
	                        utils.getDateToString(new Date()),
	                        utils.getDateToString(currToDate.toDate()));
	                futureDateMap = utils.processDatesFuture(dateListFuture, requestBean.getTimePeriodType(),
	                        utils.getDateToString(currToDate.toDate()));

	            }
	            System.out.println("futureDateMap ::"+futureDateMap.size());
	            int loopCount = requestBean.getNumberOfPointsToPredict();
	            List<List<NumericPrediction>> forecast =null;
	            DateTime currentDt =null;
	            try{
	            forecaster.buildForecaster(toAnalysis, System.out);
	            forecaster.primeForecaster(toAnalysis);
	           
	             forecast = forecaster.forecast(loopCount, System.out);
	             currentDt = new DateTime();
	            predictionData = new ArrayList<PredictionData>();
	            }catch(Exception e){
	            	throw new PredictionException(ErrorConstants.INV_DATE_ORDER);
	            }
	            System.out.println("predictionData ::"+predictionData.size());
	            
	            for (int i = 0; i < loopCount; i++) {
	                PredictionData predictionDataObject = predictionModel.new PredictionData();
	                List<NumericPrediction> predsAtStep = forecast.get(i);
	                String date = utils.dateConvertString(currentDt, dateFormate);
	                predictionDataObject.setTimeStamp(date);
	                date = utils.dateFormateChange(date);
	                predictionDataObject.setTimeLineDate(futureDateMap.get(date));
	                for (int j = 0; j < 1; j++) {
	                    NumericPrediction predForTarget = predsAtStep.get(j);
	                    long predictValue = Math.round(predForTarget.predicted());
	                    predictionDataObject.setSize(predictValue);
	                    predictionDataObject.setPredicted(true);
	                }
	                currentDt = utils.advanceTime(forecaster.getTSLagMaker(), currentDt);
	                predictionData.add(predictionDataObject);
	            }
	            predictionModel.getData().addAll(predictionData);
	            System.out.println("predictionModel ::sd "+predictionModel.getData().size());
	        } catch (Exception e) {

	            return null;

	        }

	        return predictionModel;
	    }

}
