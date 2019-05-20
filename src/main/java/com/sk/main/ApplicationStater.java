package com.sk.main;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.sk.prediction.util.ArffFileData;
import com.sk.prediction.util.Utils;




@SpringBootApplication
@ComponentScan("com.sk")
@EnableAutoConfiguration
@Configuration
public class ApplicationStater {
	
	
	@Bean
	public ArffFileData getArffFileData(){
		return new ArffFileData();
	}
	
	@Bean
	public Utils getUtils(){
		return new Utils();
	}
	
	
	
	public static void main(String args[]){
		SpringApplication.run(ApplicationStater.class);
		
	}
	

}
