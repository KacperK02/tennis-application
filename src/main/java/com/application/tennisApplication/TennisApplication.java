package com.application.tennisApplication;

import com.application.tennisApplication.API.APIConnection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class TennisApplication {

	public static void main(String[] args) throws IOException{
		SpringApplication.run(TennisApplication.class, args);

		APIConnection apiConnection = new APIConnection();
		try{
			apiConnection.getWTARanking();
		}
		catch(InterruptedException e){
			System.out.println("Failed to get WTA Ranking");
		}

	}

}
