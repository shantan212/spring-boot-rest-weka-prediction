package com.sk.prediction.controller;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sk.prediction.exception.PredictionException;
import com.sk.prediction.model.PredictionModel;
import com.sk.prediction.model.RequestBean;
import com.sk.prediction.service.PredictionService;

@RestController
public class HomeController {

	@Autowired
	private PredictionService predictionService;
	
	@GetMapping(path="/echoTest")
	ResponseEntity<String> getTestExco(){
		
		return ResponseEntity.ok("Hi Application Started and all set to GO..... !!!!");
	}
	
	@RequestMapping(path="/predictFutureData",method=RequestMethod.POST)
	PredictionModel pridectModel(@RequestBody RequestBean requestBean) throws PredictionException{
		
		return predictionService.getTimeSeriesPredictionData(requestBean);
	}
	
	
	
	
	
	
	
}

