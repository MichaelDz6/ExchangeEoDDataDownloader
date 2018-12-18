package com.amazonaws.lambda.downloader;

import com.amazonaws.lambda.database.DBSaver;
import com.amazonaws.lambda.entities.EoDOHLC;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LambdaFunctionHandler implements RequestHandler<Object, String> {

    public static LambdaLogger logger;
    public static String EXCHANGE;
    public static String DATE;
    private static String URL = "https://eodhistoricaldata.com/api/eod-bulk-last-day/$exchange$?api_token=$api_token$&fmt=json&date=$date$";
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String handleRequest(Object input, Context context) {
        logger = context.getLogger();

        DayOfWeek yesterday = LocalDate.now().minus(Period.ofDays(1)).getDayOfWeek();
        if(yesterday == DayOfWeek.SATURDAY || yesterday == DayOfWeek.SUNDAY) {
        	return "Job finished because yesterday was weekend";
        }
        
        
        long start = System.currentTimeMillis();

		final String API_TOKEN = System.getenv("API_TOKEN");
		if(API_TOKEN == null || API_TOKEN.isEmpty()){
		    logger.log("Missing API_TOKEN parameter");
		    return "Failed because of missing API_TOKEN";
        }

		EXCHANGE = System.getenv("EXCHANGE");
        if(API_TOKEN == null || API_TOKEN.isEmpty()){
            logger.log("Missing EXCHANGE parameter");
            return "Failed because of missing EXCHANGE";
        }


        DATE = getLastDate();

        URL = URL.replace("$exchange$", EXCHANGE);
        URL = URL.replace("$api_token$", API_TOKEN);
        URL = URL.replace("$date$", DATE);

        logger.log("Created URL: " + URL);

        URL obj = null;
        try {
            obj = new URL(URL);
        } catch (MalformedURLException e) {
            logger.log(e.toString());
            return "An exception occurred while creating URL: " + e;
        }
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) obj.openConnection();
        } catch (IOException e) {
            logger.log(e.toString());
            return "An Exception occurred while opening HTTPS connection: " + e;
        }
        try {
            con.setRequestMethod("GET");
        } catch (ProtocolException e) {
            logger.log(e.toString());
            return "An exception occurred while setting request method: " + e;
        }
        int responseCode = 0;

        try {
            responseCode = con.getResponseCode();
        } catch (IOException e) {
            logger.log(e.toString());
            return "An exception occurred while getting response code: " + e;
        }

        logger.log("Response code: " + responseCode);

        List<EoDOHLC> values = null;
        try(InputStream inputStream = con.getInputStream()){
            ObjectMapper objectMapper = new ObjectMapper();
            values = objectMapper.readValue(inputStream, objectMapper.getTypeFactory().constructCollectionType(List.class, EoDOHLC.class));
        } catch (IOException e) {
            logger.log(e.toString());
            return "An exception occurred while reading input stream: " + e;
        }


        if(values == null){
            System.out.println("Values null");
            return "Returned response from the server was empty";
        }

        validateRecords(values);

        try {
            DBSaver.setup();
        } catch (IllegalArgumentException e){
            logger.log(e.toString());
            return "Failed while setting up DB: " + e;
        }

        try {
            DBSaver.saveBatch(values.toArray(new EoDOHLC[values.size()]));
        } catch (SQLException e){
            logger.log(e.toString());
            return "Failed while saving the batch: " + e;
        }

        logger.log("Finished the job after " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start) + " seconds");
        return "Job finished";
    }


    private String getLastDate(){
        return LocalDate.now().minus(Period.ofDays(1)).format(dateTimeFormatter);
    }

    private static void validateRecords(List<EoDOHLC> values){

        for(Iterator<EoDOHLC> iterator = values.iterator (); iterator.hasNext();){

            EoDOHLC record = iterator.next();

            if(record.getSymbol() == null || record.getSymbol().isEmpty()){
                iterator.remove();
                continue;
            }

            if(record.getExchange() == null || record.getExchange().isEmpty()){
                record.setExchange(EXCHANGE);
            }

        }


    }
}