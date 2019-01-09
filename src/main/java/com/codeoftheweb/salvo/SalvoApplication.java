package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepo) {
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

}

