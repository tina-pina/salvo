package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;


@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initPlayerData(PlayerRepository playerRepo) {
		return (args) -> {
			// save a couple of Players
			Player p1 = new Player("Jack", "Bauer");
			Player p2 = new Player("Chloe", "O'Brian");
			Player p3 = new Player("Kim", "Bauer");
			Player p4 = new Player("David", "Palmer");
			Player p5 = new Player("Michelle", "Dessler");

			playerRepo.save(p1);
			playerRepo.save(p2);
			playerRepo.save(p3);
			playerRepo.save(p4);
			playerRepo.save(p5);

		};
	}

	@Bean
	public CommandLineRunner initGameData(GamesRepository gamesRepo) {
		return (args) -> {
			// save a couple of Games
			Date date = new Date();
			Games g1 = new Games(date);
			Games g2 = new Games(Date.from(date.toInstant().plusSeconds(3600)));
			Games g3 = new Games(Date.from(date.toInstant().plusSeconds(7200)));
			Games g4 = new Games(Date.from(date.toInstant().plusSeconds(10800)));
			Games g5 = new Games(Date.from(date.toInstant().plusSeconds(14400)));

			gamesRepo.save(g1);
			gamesRepo.save(g2);
			gamesRepo.save(g3);
			gamesRepo.save(g4);
			gamesRepo.save(g5);

		};
	}

}

