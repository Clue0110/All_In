package com.game.config;
import com.game.model.*;
import com.game.repo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner initDatabase(UserRepository uRepo, StockRepository sRepo) {
        return args -> {
            if(uRepo.count() == 0) {
                User u = new User(); u.setUsername("DefaultPlayer"); u.setBalance(10000.0);
                uRepo.save(u);
                // 5% chance of volatility, max 5% swing
                sRepo.save(new Stock("RMN", "Ramen Inc.", 50.0, 1000, 0.05, 0.05));
                // 10% chance of volatility, max 10% swing
                sRepo.save(new Stock("SOM", "SomeTech", 120.0, 500, 0.10, 0.10));
                // 30% chance of volatility, max 50% swing!
                sRepo.save(new Stock("WAI", "WeaveAI", 20.0, 5000, 0.30, 0.50));
            }
        };
    }
}
