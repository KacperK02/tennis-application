package com.application.tennisApplication;

import com.application.tennisApplication.API.APIConnection;
import com.application.tennisApplication.service.PlayerService;
import com.application.tennisApplication.service.PlayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class TennisApplication implements CommandLineRunner{

	@Autowired
	private PlayerService playerService;

	public static void main(String[] args) throws IOException{

		/*(APIConnection apiConnection = new APIConnection();
		try{
			apiConnection.getWTARanking();
			apiConnection.getATPRanking();
		}
		catch(InterruptedException e){
			System.out.println("Failed to get WTA Ranking");
		}*/

		SpringApplication.run(TennisApplication.class, args);

	}

	@Override
	public void run(String... args) throws IOException {
		if(playerService.getAllPlayers().size() == 0){
			playerService.savePlayersFromFile();
		}

	}
}
