package com.game.service;
import com.game.model.*;
import com.game.repo.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    @Autowired private StockRepository stockRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private PortfolioRepository portfolioRepo;

    @Transactional // Atomosicity across multiple DB operations
    public void buyStock(Long userId, Long stockId, int quantity) throws Exception {
        User user = userRepo.findById(userId).orElseThrow();
        Stock stock = stockRepo.findByIdWithLock(stockId).orElseThrow(); // The OG -> Locking

        double totalCost = stock.getPrice() * quantity;
        
        if (user.getBalance() < totalCost) throw new Exception("Insufficient funds!");
        if (stock.getAvailableShares() < quantity) throw new Exception("Not enough shares!");

        user.setBalance(user.getBalance() - totalCost);
        userRepo.save(user);

        stock.setAvailableShares(stock.getAvailableShares() - quantity);
        stockRepo.save(stock);

        Portfolio portfolio = portfolioRepo.findByUserAndStock(user, stock);
        if (portfolio == null) { // If user doesnt own this stock yet
            portfolio = new Portfolio();
            portfolio.setUser(user);
            portfolio.setStock(stock);
            portfolio.setQuantity(0);
            portfolio.setTotalInvestment(0.0);
        }
        
        portfolio.setQuantity(portfolio.getQuantity() + quantity);
        portfolio.setTotalInvestment(portfolio.getTotalInvestment() + totalCost);
        portfolioRepo.save(portfolio);
    }

    @Transactional
    public void sellStock(Long userId, Long stockId, int quantity) throws Exception {
        User user = userRepo.findById(userId).orElseThrow();
        Stock stock = stockRepo.findByIdWithLock(stockId).orElseThrow(); // The OG -> Locking
        Portfolio portfolio = portfolioRepo.findByUserAndStock(user, stock);

        if (portfolio == null || portfolio.getQuantity() < quantity) {
            throw new Exception("Not enough shares to sell!");
        }

        double saleValue = stock.getPrice() * quantity;
        
        user.setBalance(user.getBalance() + saleValue);
        userRepo.save(user);

        stock.setAvailableShares(stock.getAvailableShares() + quantity);
        stockRepo.save(stock);

        // Update Portfolio (Proportional Cost Basis Reduction)
        double costPerShare = portfolio.getTotalInvestment() / portfolio.getQuantity();
        double costOfSoldShares = costPerShare * quantity;

        portfolio.setQuantity(portfolio.getQuantity() - quantity);
        portfolio.setTotalInvestment(portfolio.getTotalInvestment() - costOfSoldShares);

        if (portfolio.getQuantity() == 0) {
            portfolioRepo.delete(portfolio);
        } else {
            portfolioRepo.save(portfolio);
        }
    }
}
