package com.sk.prediction.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;


import com.google.gson.Gson;
import com.sk.prediction.constants.ErrorConstants;
import com.sk.prediction.constants.PredictionConstants;
import com.sk.prediction.exception.PredictionException;
import com.sk.prediction.model.PredictionModel;
import com.sk.prediction.model.PredictionModel.PredictionData;
import com.sk.prediction.model.RequestBean;


import weka.core.Instances;
import weka.filters.supervised.attribute.TSLagMaker;


public class Utils {

    public static String objectToJson(Object Obj) {
        Gson gsonObj = new Gson();
        String stringJson = gsonObj.toJson(Obj);
        return stringJson;
    }

    public static RequestBean jsonToObject(String requestBeanJsonString) {
        Gson gsonObj = new Gson();
        RequestBean requestBeanObject = gsonObj.fromJson(requestBeanJsonString, RequestBean.class);
        return requestBeanObject;

    }
    
    public static String generateArFFileName(){
    	long timeStamp = System.currentTimeMillis()+ new Date().getTime() / 1000;
    	String fileName = String.valueOf(timeStamp)+".arff";
    	return fileName;
    }
    
    /**
     * <p>
     * Load the data into model for the data is already existed.
     * @param fileName
     *            Absolute path of the arff file to load Instances
     * @return Instances Object loaded from file.
     * @throws IOException
     */

    public Instances loadInstnaces(String fileName) throws IOException {
        String path = Utils.getArffFilePath() + "/" + fileName;

        BufferedReader reader = new BufferedReader(new FileReader(new File(path)));

        Instances instances = new Instances(reader);
        return instances;
    }
    
    
    /**
     * @param noofDays
     * @return
     */
    @SuppressWarnings("deprecation")
    public static List<Date> getFutureListOfDays(String startDate, String endDate) throws PredictionException {

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d-M-uuuu", Locale.ROOT);
        DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        LocalDate localStartDate = LocalDate.parse(startDate, dateFormatter);
        LocalDate localEndDate = LocalDate.parse(endDate, dateFormatter);
        if (localStartDate.isBefore(localEndDate) || localStartDate.equals(localEndDate)) {
            Date date = null;
            try {
                date = sdf.parse(endDate);
            } catch (ParseException e) {

            }
            List<Date> dates = new ArrayList<Date>();
            int loop = 0;
            for (LocalDate localdate = localStartDate; localdate.isBefore(localEndDate)
                    || localdate.isEqual(localEndDate); localdate = localdate.plusDays(1)) {
                dates.add(Date.from(localdate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                loop++;
            }
            if (dates.get(loop - 1).before(date) || dates.get(loop - 1).equals(date)) {
                date = addDays(date, 1);
                dates.add(date);
            }
            return dates;
        } else {
            throw new PredictionException(ErrorConstants.INV_DATE);
        }
    }
    public static Date addDays(Date date, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);

        return cal.getTime();
    }

    
   

    public static String getArffFilePath(){
    	
    	return "C:/arff";
    }

    public static boolean writeStringJsonToFile(String stringJson, String fileName) {

        File file = new File(getArffFilePath() + "/" + fileName);

        // Create the file
        try {
            if (file.createNewFile()) {
                // Write Content
                FileWriter writer = new FileWriter(file);
                writer.write(stringJson);
                writer.close();
                return true;
            } else {

                return false;
            }

        } catch (IOException e) {

            return false;
        }

    }

   

    public static String readFileAsString(String fileName) throws IOException {
        String fileJsonData = "";
        String finalFilePath = fileName;
        fileJsonData = new String(Files.readAllBytes(Paths.get(finalFilePath)));
        return fileJsonData;
    }

    public static boolean deleteFile(String fileName) {

        File fileObjForDelete = new File(fileName);
        if (fileObjForDelete.exists()) {

            fileObjForDelete.delete();
            return true;
        }
        return false;
    }
    
    /**
     * <p>
     * Load the data into model for the data is already existed.
     * @param predictionModel
     *            Prediction Model which contain PredictionData List Object.
     * @param predictionModel
     *            Instances Object which has dataset in raw format.
     * @param requestBean
     *            RequestBean Object which comes as the parameter for this
     *            project.
     */
    public void populateExistedData(PredictionModel predictionModel, Instances instances, RequestBean requestBean,
            List<Date> dateList, Map<String, ArrayList<String>> dateMap) {
        List<PredictionData> predictionModelArrayList = new ArrayList<PredictionData>();

        for (int i = 0; i < instances.numInstances(); i++) {
            String rawData = instances.instance(i).toString();

            StringTokenizer strToken = new StringTokenizer(rawData, ",");
            String data = strToken.nextToken();
            String ss = strToken.nextToken(",");
            StringTokenizer dateFormated = new StringTokenizer(ss, "T");
            String finalDate = dateFormated.nextToken();
            PredictionModel.PredictionData predictionData = predictionModel.new PredictionData();
            predictionData.setTimeStamp(finalDate);
            String date;
            date = finalDate;
            System.out.println("date::"+date);
            System.out.println("dateMap.get(date) ::"+dateMap.get(date));
            predictionData.setTimeLineDate(dateMap.get(date));

            predictionData.setSize(Double.parseDouble(data));

            predictionData.setPredicted(false);
            predictionModelArrayList.add(predictionData);

        }

        predictionModel.setData(predictionModelArrayList);
    }

   

    /**
     * <p>
     * Formats a Date into a date/time string. Format is dd-MM-yyyy
     * @param date
     *            the Date value to be formatted into a time string.
     * @return the formatted string in dd-MM-yyyy HH:mm:ss format
     * @throws ParseException
     */
    public static String getDateToString(Date date) {
        SimpleDateFormat mdyFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date1 = mdyFormat.format(date);
        return date1;
    }
    
    /**
     * <p>
     * Calculates Future date based on specified timestamp and timePeriodType
     * and converts to string using dd-MM-yyyy format.
     * @param timestamp
     *            no of DAYS/WEEK/MONTH/YEAR to be added.
     * @param timePeriodType
     *            supported types are DAY/WEEK/MONTH/YEAR
     * @return Future date based on given values and in date/time string.
     * @throws ParseException
     */
    public static String getFutureDateWithFirstDate(int timestamp, String timePeriodType) throws ParseException {

        LocalDate localDate = LocalDate.now();
        LocalDate futureDate = null;

        if (timePeriodType.equalsIgnoreCase(PredictionConstants.DAY)) {
            localDate = localDate.plusDays(timestamp);
            futureDate = localDate;
        } else if (timePeriodType.equalsIgnoreCase(PredictionConstants.WEEK)) {

            localDate = localDate.plusWeeks(timestamp);
            futureDate = localDate;
        } else if (timePeriodType.equalsIgnoreCase(PredictionConstants.MONTH)) {
            localDate = localDate.plusMonths(timestamp);
            futureDate = localDate.with(TemporalAdjusters.firstDayOfMonth());
        } else if (timePeriodType.equalsIgnoreCase(PredictionConstants.YEAR)) {
            localDate = localDate.plusYears(timestamp);
            futureDate = localDate.with(TemporalAdjusters.firstDayOfYear());

        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedString = futureDate.format(formatter);
        return formattedString;

    }

    /**
     * <p>
     * Formats a Date into a date/time string. Format is dd-MM-yyyy
     * @param date
     *            the Date value to be formatted into a time string.
     * @param RequestBean
     *            Request Bean Object
     * @return the formatted Date format
     * @throws ParseException
     */
    private static DateTime getCurrentDateTime(TSLagMaker lm, RequestBean requestBean) throws Exception {

    	
    	
    	org.joda.time.format.DateTimeFormatter formatter = org.joda.time.format.DateTimeFormat.forPattern("dd-MM-yyyy");
        DateTime dt = null;
        if (requestBean.getTimePeriodType().equalsIgnoreCase(PredictionConstants.DAY)) {
            String date = getFutureDateWithFirstDate(1, PredictionConstants.DAY);
            dt = formatter.parseDateTime(date);
        } else if (requestBean.getTimePeriodType().equalsIgnoreCase(PredictionConstants.WEEK)) {
            String date = getFutureDateWithFirstDate(1, PredictionConstants.WEEK);
            dt = formatter.parseDateTime(date);
        } else if (requestBean.getTimePeriodType().equalsIgnoreCase(PredictionConstants.MONTH)) {
            String date = getFutureDateWithFirstDate(1, PredictionConstants.MONTH);
            dt = formatter.parseDateTime(date);
        } else if (requestBean.getTimePeriodType().equalsIgnoreCase(PredictionConstants.YEAR)) {
            String date = getFutureDateWithFirstDate(1, PredictionConstants.YEAR);
            dt = formatter.parseDateTime(date);
        }
        return dt;
    }
    
    
    public static List<Date> getFutureListOfWeeks(String startDate, String endDate) throws PredictionException {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d-M-uuuu", Locale.ROOT);
        LocalDate localStartDate = LocalDate.parse(startDate, dateFormatter);
        LocalDate localEndDate = LocalDate.parse(endDate, dateFormatter);
        if (localStartDate.isBefore(localEndDate) || localStartDate.equals(localEndDate)) {
            DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date startdate = null;
            Date enddate = null;
            try {
                startdate = sdf.parse(startDate);
                enddate = sdf.parse(endDate);
            } catch (ParseException e) {

            }

            long days = ChronoUnit.DAYS.between(localStartDate, localEndDate);
            int noofweeks = (int) (days / 7);
            List<Date> dates = new ArrayList<Date>();
            int i = 0;
            int noOfLoop = noofweeks;
            for (int week = 0; week <= noOfLoop; week++) {
                Calendar calender = Calendar.getInstance();
                calender.setTime(startdate);
                calender.add(Calendar.DATE, i);
                Date weekStartDate = calender.getTime();

                dates.add(getStringToDate(getDateToString(weekStartDate)));
                i += 7;
            }
            if (dates.get(noOfLoop - 1).before(enddate)) {
                enddate = addDays(enddate, 1);
                dates.add(enddate);
            }
            return dates;
        } else {
            throw new PredictionException(ErrorConstants.INV_DATE);
        }
    }

    /**
     * <p>
     * Attempts to obtain Date object from specified Date in string format.
     * @param date
     *            the Date in String to be returned as Date object.
     * @return the Date object for input date in string format.
     * @throws ParseException
     */
    public static Date getStringToDate(String date) {
        try {
            SimpleDateFormat mdyFormat = new SimpleDateFormat("dd-MM-yyyy");
            String ss = date;

            Date finalDate;

            finalDate = mdyFormat.parse(ss);
            return finalDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }
    
    
    public static List<Date> getFutureListOfMonths(String startDate, String endDate)
            throws ParseException, PredictionException {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d-M-uuuu", Locale.ROOT);
        LocalDate localStartDate = LocalDate.parse(startDate, dateFormatter);
        LocalDate localEndDate = LocalDate.parse(endDate, dateFormatter);
        if (localStartDate.isBefore(localEndDate) || localStartDate.equals(localEndDate)) {
            DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date startdate = null;
            Date enddate = null;
            try {
                startdate = sdf.parse(startDate);
                enddate = sdf.parse(endDate);
            } catch (ParseException e) {

            }

            List<Date> dateList = new ArrayList<Date>();
            YearMonth startMonth = YearMonth.parse(startDate, dateFormatter);
            YearMonth endMonth = YearMonth.parse(endDate, dateFormatter);

            DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM/uuuu", Locale.ROOT);

            int date = startdate.getDate();
            int loop = 0;

            for (YearMonth month = startMonth; !month.isAfter(endMonth); month = month.plusMonths(1)) {
                SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");
                String ss = date + "/" + month.format(monthFormatter);
                Date finalDate = format2.parse(ss);
                dateList.add(finalDate);
                loop++;
            }
            if (dateList.get(loop - 1).before(enddate) || dateList.get(loop - 1).equals(enddate)) {
                enddate = addDays(enddate, 1);
                dateList.add(enddate);
            }
            return dateList;
        } else {
        	System.out.println("Throuwing exp");
            throw new PredictionException(ErrorConstants.INV_DATE);
        }
    }
    
    public static List<Date> getFutureListOfYears(String startDate, String endDate) throws PredictionException {

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d-M-uuuu", Locale.ROOT);
        LocalDate localStartDate = LocalDate.parse(startDate, dateFormatter);
        LocalDate localEndDate = LocalDate.parse(endDate, dateFormatter);
        if (localStartDate.isBefore(localEndDate) || localStartDate.equals(localEndDate)) {
            DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date startdate = null;
            Date enddate = null;
            try {
                startdate = sdf.parse(startDate);
                enddate = sdf.parse(endDate);
            } catch (ParseException e) {

            }
            List<Date> dateList = new ArrayList<Date>();
            Year endyear = Year.parse(endDate, dateFormatter);

            int date = startdate.getDate();
            int month = startdate.getMonth() + 1;
            int loop = 0;

            DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("uuuu", Locale.ROOT);
            for (Year year = Year.parse(startDate, dateFormatter); !year.isAfter(endyear); year = year.plusYears(1)) {
                String finalDate = date + "-" + month + "-" + year.format(yearFormatter);
                dateList.add(getStringToDate(finalDate));
                loop++;
            }
            if (dateList.get(loop - 1).before(enddate) || dateList.get(loop - 1).equals(enddate)) {
                enddate = addDays(enddate, 1);
                dateList.add(enddate);
            }
            return dateList;
        } else {
            throw new PredictionException(ErrorConstants.INV_DATE);
        }
    }
    
    
    /**
     * @param dateList
     * @param timePeriodtype
     * @return
     */
    public Map<String, ArrayList<String>> processDatesFuture(List<Date> dateList, String timePeriodtype,
            String stringEndDate) {

        Collections.sort(dateList, Collections.reverseOrder());
        Map<String, ArrayList<String>> dateMap = new HashMap<String, ArrayList<String>>();

        Date endDate = getStringToDate(stringEndDate);

        for (int i = 0; i < dateList.size(); i++) {

            ArrayList<String> list = new ArrayList<String>();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            boolean status = true;

            String toDate = null, fromDate = null, strDate = null;

            try {
                if (timePeriodtype.equalsIgnoreCase(PredictionConstants.WEEK)) {

                    strDate = getDateToString(dateList.get(i));
                    Instant instant = Instant.ofEpochMilli(dateList.get(i).getTime());
                    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                    LocalDate localDate = localDateTime.toLocalDate();
                    localDate = localDate.plusWeeks(1).minusDays(1);
                    fromDate = strDate;
                    if (i == 0) {
                        toDate = stringEndDate;
                    } else {
                        toDate = getDateToString(java.sql.Date.valueOf(localDate));
                    }

                } else if (timePeriodtype.equalsIgnoreCase(PredictionConstants.MONTH)) {
                    strDate = getDateToString(dateList.get(i));
                    Instant instant = Instant.ofEpochMilli(dateList.get(i).getTime());
                    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                    LocalDate localDate = localDateTime.toLocalDate();
                    localDate = localDate.plusMonths(1).minusDays(1);
                    fromDate = strDate;
                    if (i == 0) {
                        toDate = stringEndDate;
                    } else {
                        toDate = getDateToString(java.sql.Date.valueOf(localDate));
                    }

                } else if (timePeriodtype.equalsIgnoreCase(PredictionConstants.YEAR)) {

                    strDate = getDateToString(dateList.get(i));
                    Instant instant = Instant.ofEpochMilli(dateList.get(i).getTime());
                    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                    LocalDate localDate = localDateTime.toLocalDate();
                    localDate = localDate.plusYears(1).minusDays(1);
                    fromDate = strDate;
                    if (i == 0) {
                        toDate = stringEndDate;
                    } else {
                        toDate = getDateToString(java.sql.Date.valueOf(localDate));
                    }
                } else {
                    strDate = getDateToString(dateList.get(i));
                    toDate = getDateToString(dateList.get(i));
                    toDate = getLastDayOfMonth(toDate, timePeriodtype);
                    fromDate = getDateToString(dateList.get(i));
                }

                if (status) {
                    list.add(fromDate);
                    list.add("|");
                    list.add(toDate);

                    dateMap.put(strDate, list);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        
        return dateMap;
    }
    /**
     * @param dateString
     * @param timeperiodType
     * @return
     * @throws ParseException
     */
    public String getLastDayOfMonth(String dateString, String timeperiodType) throws ParseException {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.US);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        LocalDate date = LocalDate.parse(dateString, dateFormat);
        LocalDate localDate = LocalDate.now();

        ValueRange range = null;
        if (timeperiodType.equalsIgnoreCase(PredictionConstants.MONTH)) {
            range = date.range(ChronoField.DAY_OF_MONTH);
            Long max = range.getMaximum();
            LocalDate newDate = date.withDayOfMonth(max.intValue());
            String ss = newDate.toString();

            Date date1 = getStringToDate(dateString);
            Date date2 = new Date();
            String strdate = sdf.format(date2);
            Date dat3 = getStringToDate(strdate);

            String finalDate = null;
            if (dat3.getMonth() == date1.getMonth()) {

                finalDate = getDateToString(date2);
            } else {
                finalDate = dateFormateChange(ss);

            }

            return finalDate;
        } else if (timeperiodType.equalsIgnoreCase(PredictionConstants.WEEK)) {
            Date date1 = getStringToDate(dateString);

            LocalDate current = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate finalDate = current.minusDays(1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-YYYY");
            String finalDate1 = formatter.format(finalDate);

            return finalDate1;
        } else if (timeperiodType.equalsIgnoreCase(PredictionConstants.YEAR)) {
            range = date.range(ChronoField.DAY_OF_YEAR);
            Long max = range.getMaximum();
            LocalDate newDate = date.withDayOfYear(max.intValue());
            String ss = newDate.toString();

            Date date1 = getStringToDate(dateString);
            Date date2 = new Date();
            String strdate = sdf.format(date2);
            Date dat3 = getStringToDate(strdate);

            String finalDate = null;

            if (dat3.getYear() == date1.getYear()) {

                finalDate = getDateToString(date2);
            } else {
                finalDate = dateFormateChange(ss);

            }
            return finalDate;
        } else if (timeperiodType.equalsIgnoreCase(PredictionConstants.DAY)) {
            String finalDate = dateString;
            return finalDate;
        }
        return timeperiodType;

    }
    
    /**
     * @param date
     * @return
     * @throws ParseException
     */
    public static String dateFormateChange(String date) throws ParseException {
        /*** for parsing input */
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat df2 = new SimpleDateFormat("dd-MM-yyyy");
        Date d = df1.parse(date);
        String outputDate = df2.format(d);
        return outputDate;
    }

    /**
     * @param dateList
     * @param timePeriodtype
     * @return
     */
    public  Map<String, ArrayList<String>> processDatesPrevious(List<Date> dateList, String timePeriodtype,
            String stringEndDate) {

        Collections.sort(dateList, Collections.reverseOrder());
        Map<String, ArrayList<String>> dateMap = new HashMap<String, ArrayList<String>>();

        Date endDate = getStringToDate(stringEndDate);

        for (int i = 0; i < dateList.size(); i++) {

            ArrayList<String> list = new ArrayList<String>();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            boolean status = true;

            String toDate = null, fromDate = null, strDate = null;

            try {
                if (timePeriodtype.equalsIgnoreCase(PredictionConstants.WEEK)) {

                    strDate = getDateToString(dateList.get(i));
                    Instant instant = Instant.ofEpochMilli(dateList.get(i).getTime());
                    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                    LocalDate localDate = localDateTime.toLocalDate();
                    localDate = localDate.plusWeeks(1).minusDays(1);
                    fromDate = strDate;
                    if (i == 0) {
                        toDate = stringEndDate;
                    } else {
                        toDate = getDateToString(java.sql.Date.valueOf(localDate));
                    }

                } else if (timePeriodtype.equalsIgnoreCase(PredictionConstants.MONTH)) {
                    strDate = getDateToString(dateList.get(i));
                    Instant instant = Instant.ofEpochMilli(dateList.get(i).getTime());
                    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                    LocalDate localDate = localDateTime.toLocalDate();
                    localDate = localDate.plusMonths(1).minusDays(1);
                    fromDate = strDate;
                    if (i == 0) {
                        toDate = stringEndDate;
                    } else {
                        toDate = getDateToString(java.sql.Date.valueOf(localDate));
                    }

                } else if (timePeriodtype.equalsIgnoreCase(PredictionConstants.YEAR)) {

                    strDate = getDateToString(dateList.get(i));
                    Instant instant = Instant.ofEpochMilli(dateList.get(i).getTime());
                    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                    LocalDate localDate = localDateTime.toLocalDate();
                    localDate = localDate.plusYears(1).minusDays(1);
                    fromDate = strDate;
                    if (i == 0) {
                        toDate = stringEndDate;
                    } else {
                        toDate = getDateToString(java.sql.Date.valueOf(localDate));
                    }
                } else {
                    strDate = getDateToString(dateList.get(i));
                    toDate = getDateToString(dateList.get(i));
                    toDate = getLastDayOfMonth(toDate, timePeriodtype);
                    fromDate = getDateToString(dateList.get(i));
                }

                if (status) {
                    list.add(fromDate);
                    list.add("|");
                    if (getStringToDate(toDate).after(new Date())) {
                        list.add(getDateToString(new Date()));
                    } else {
                        list.add(toDate);
                    }

                    dateMap.put(strDate, list);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
       
        return dateMap;
    }
    
    /**
     * <p>
     * Convert DateTime to string date in yyyy-MM-dd format
     * @param date
     *            date Value to be converted.
     * @param formate
     *            Format in which it has to be converted
     * @return the formatted string in dd-MM-yyyy format
     */
    public String dateConvertString(DateTime date, String formate) {
        DateFormat format = new SimpleDateFormat(formate);
        String date1 = format.format(date.toDate());
        return date1;
    }
    
    /**
     * <p>
     * To get the Advanced next interval Date from the context of WEKA
     * @param TSLagMaker
     *            TSLagMaker Object.
     * @param dateTime
     *            Current element's datetime object
     * @return the DateTime in long format
     */
    public static DateTime advanceTime(TSLagMaker lm, DateTime dateTime) {
        return new DateTime((long) lm.advanceSuppliedTimeValue(dateTime.getMillis()));
    }


    public static String getServerContextPath(HttpServletRequest request) {
        ServletContext context = request.getServletContext();
        String path = context.getRealPath("/") + "/";
        return path;
    }
    
    public static ResourceBundle getBundleValue(String path, String lang) {
        FileInputStream fis = null;
        ResourceBundle bundle = null;
        path = path + "WEB-INF/classes/";
        try {
            if (lang.startsWith(PredictionConstants.CHINESE_LOCALE_FILENAME)) {
                lang = PredictionConstants.CHINESE_LOCALE_FILENAME;
            } else if (lang.startsWith(PredictionConstants.FRENCH_LOCALE_FILENAME)) {
                lang = PredictionConstants.FRENCH_LOCALE_FILENAME;
            }
            fis = new FileInputStream(path + "/Messages_" + lang + ".properties");
            bundle = new PropertyResourceBundle(fis);
        } catch (IOException e) {
            try {

                fis = new FileInputStream(path + "/Messages_en.properties");
                bundle = new PropertyResourceBundle(fis);
            } catch (IOException e1) {
            }

        }
        return bundle;
    }
    
}
