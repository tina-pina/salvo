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

//	@Bean
//	public CommandLineRunner initPlayerData(PlayerRepository playerRepo) {
//		return (args) -> {
//			// save a couple of Players
//			Player p1 = new Player("Jack", "Bauer");
//			Player p2 = new Player("Chloe", "O'Brian");
//			Player p3 = new Player("Kim", "Bauer");
//			Player p4 = new Player("David", "Palmer");
//			Player p5 = new Player("Michelle", "Dessler");
//
//			playerRepo.save(p1);
//			playerRepo.save(p2);
//			playerRepo.save(p3);
//			playerRepo.save(p4);
//			playerRepo.save(p5);
//
//		};
//	}
//
//	@Bean
//	public CommandLineRunner initGameData(GameRepository gameRepo) {
//		return (args) -> {
//			// save a couple of Games
//			Date date = new Date();
//			Game g1 = new Game(date);
//			Game g2 = new Game(Date.from(date.toInstant().plusSeconds(3600)));
//			Game g3 = new Game(Date.from(date.toInstant().plusSeconds(7200)));
//			Game g4 = new Game(Date.from(date.toInstant().plusSeconds(10800)));
//			Game g5 = new Game(Date.from(date.toInstant().plusSeconds(14400)));
//
//			gameRepo.save(g1);
//			gameRepo.save(g2);
//			gameRepo.save(g3);
//			gameRepo.save(g4);
//			gameRepo.save(g5);
//
//		};
//	}

	@Bean
	public CommandLineRunner initGamePlayerData(PlayerRepository playerRepo,
												GameRepository gameRepo,
												GamePlayerRepository gamePlayerRepo) {
		return (args) -> {

			Date date = new Date();
			Player p1 = new Player("Jack", "Bauer", "j.bauer@ctu.gov");
			Player p2 = new Player("Chloe", "O'Brian", "c.obrian@ctu.gov");
			Player p3 = new Player("Kim", "Bauer", "kim_bauer@gmail.com");
			playerRepo.save(p1);
			playerRepo.save(p2);
			playerRepo.save(p3);

			Game g1 = new Game(Date.from(date.toInstant().plusSeconds(3600)));
			Game g2 = new Game(Date.from(date.toInstant().plusSeconds(7200)));
			Game g3 = new Game(Date.from(date.toInstant().plusSeconds(10800)));
			gameRepo.save(g1);
			gameRepo.save(g2);
			gameRepo.save(g3);

			GamePlayer gp1 = new GamePlayer(p1, g1);
			GamePlayer gp2 = new GamePlayer(p2, g1);
			GamePlayer gp3 = new GamePlayer(p2, g2);
			GamePlayer gp4 = new GamePlayer(p2, g3);
			gamePlayerRepo.save(gp1);
			gamePlayerRepo.save(gp2);
			gamePlayerRepo.save(gp3);
			gamePlayerRepo.save(gp4);

		};
	}
}

