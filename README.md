# spring-boot-rest-weka-prediction
Weka Timeseries forecasting project using spring boot and rest

Sample Json Request
{
	"numberOfPointsToPredict":Integer, [Number of data point to predict]
	"timePeriodType":String, [Year,Month,Week,Day]
	"fromDate":"01-01-2019",[From Date Of Existing Data]
	"toDate":"01-01-2018",[To date of Existing Data]
	"predictionData":[
		{
		"intValue":5, (Integer Value )
		"timeLine":"01-01-2019" (Date of Dataset)
	},{
		"intValue":5,(Integer Value )
		"timeLine":"01-02-2019"(Date of Dataset)
	}
		
		]
}