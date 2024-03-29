package com.application.tennisApplication;

import com.application.tennisApplication.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class TennisApplication implements CommandLineRunner{

	@Autowired
	private PlayerService playerService;

	public static void main(String[] args) {

		SpringApplication.run(TennisApplication.class, args);
	}

	@Override
	public void run(String... args) {

		/*try{
			playerService.updateRanking();
		} catch (Exception e) {
			System.out.println("Cannot update ranking. " + e);
		}*/


	}
}
