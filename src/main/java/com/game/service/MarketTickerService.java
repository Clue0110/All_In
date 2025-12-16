package com.game.service;
import com.game.model.Stock;
import com.game.repo.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;

@Service
public class MarketTickerService {
    @Autowired private StockRepository stockRepo;
    @Autowired private SimpMessagingTemplate messagingTemplate;
    private final Random random = new Random();

    @Scheduled(fixedRate = 2000) 
    public void updateMarket() {
        List<Stock> stocks = stockRepo.findAll();
        for (Stock stock : stocks) {
            
            double changeFactor;
            
            // Check against this specific stock's Probability of Volatility
            if (random.nextDouble() < stock.getVolatilityChance()) {
                
                // Boom or Crash?
                boolean isBoom = random.nextBoolean();
                
                // Calculate random swing up to the Max Volatility
                double magnitude = random.nextDouble() * stock.getMaxVolatility(); 

                // Visual Impact for Prototype
                if (magnitude < 0.02) magnitude = 0.02;

                if (isBoom) {
                    changeFactor = 1 + magnitude;
                    System.out.println("BOOM! " + stock.getTicker() + " jumped " + (int)(magnitude*100) + "%");
                } else {
                    changeFactor = 1 - magnitude;
                    System.out.println("CRASH! " + stock.getTicker() + " dropped " + (int)(magnitude*100) + "%");
                }
            } 
            else {
                // Normal Trading
                // Swing between -2% and +2%
                // random.nextDouble() * 0.04 -> 0.0 to 0.04
                // - 0.02 -> -0.02 to +0.02
                changeFactor = 1 + (random.nextDouble() * 0.04 - 0.02);
            }

            double newPrice = Math.round(stock.getPrice() * changeFactor * 100.0) / 100.0;
            if (newPrice < 1.00) newPrice = 1.00; // Floor price
            
            stock.setPrice(newPrice);
        }
        stockRepo.saveAll(stocks);
        messagingTemplate.convertAndSend("/topic/ticker", stocks);
    }
}
