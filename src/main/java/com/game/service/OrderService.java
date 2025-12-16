package com.game.service;
import com.game.model.*;
import com.game.repo.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class OrderService {
    @Autowired private StockRepository stockRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private PortfolioRepository portfolioRepo;
    @Autowired private TransactionRepository transactionRepo;

    @Transactional // Atomosicity across multiple DB operations
    public Transaction buyStock(Long userId, Long stockId, int quantity) throws Exception {
        User user = userRepo.findById(userId).orElseThrow();
        Stock stock = stockRepo.findByIdWithLock(stockId).orElseThrow();

        double totalCost = stock.getPrice() * quantity;
        
        if (user.getBalance() < totalCost) throw new Exception("Insufficient funds!");
        if (stock.getAvailableShares() < quantity) throw new Exception("Not enough shares!");

        user.setBalance(user.getBalance() - totalCost);
        stock.setAvailableShares(stock.getAvailableShares() - quantity);
        
        userRepo.save(user);
        stockRepo.save(stock);

        Portfolio portfolio = portfolioRepo.findByUserAndStock(user, stock);
        if (portfolio == null) {
            portfolio = new Portfolio();
            portfolio.setUser(user);
            portfolio.setStock(stock);
            portfolio.setQuantity(0);
            portfolio.setTotalInvestment(0.0);
        }
        portfolio.setQuantity(portfolio.getQuantity() + quantity);
        portfolio.setTotalInvestment(portfolio.getTotalInvestment() + totalCost);
        portfolioRepo.save(portfolio);

        Transaction t = new Transaction();
        t.setUser(user);
        t.setStock(stock);
        t.setType("BUY");
        t.setQuantity(quantity);
        t.setPrice(stock.getPrice());
        t.setTimestamp(LocalDateTime.now());
        return transactionRepo.save(t);
    }

    @Transactional
    public Transaction sellStock(Long userId, Long stockId, int quantity) throws Exception {
        User user = userRepo.findById(userId).orElseThrow();
        Stock stock = stockRepo.findByIdWithLock(stockId).orElseThrow();
        Portfolio portfolio = portfolioRepo.findByUserAndStock(user, stock);

        if (portfolio == null || portfolio.getQuantity() < quantity) {
            throw new Exception("Not enough shares to sell!");
        }

        double saleValue = stock.getPrice() * quantity;
        
        user.setBalance(user.getBalance() + saleValue);
        stock.setAvailableShares(stock.getAvailableShares() + quantity);
        
        userRepo.save(user);
        stockRepo.save(stock);

        double costPerShare = portfolio.getTotalInvestment() / portfolio.getQuantity();
        double costOfSoldShares = costPerShare * quantity;

        portfolio.setQuantity(portfolio.getQuantity() - quantity);
        portfolio.setTotalInvestment(portfolio.getTotalInvestment() - costOfSoldShares);

        if (portfolio.getQuantity() == 0) {
            portfolioRepo.delete(portfolio);
        } else {
            portfolioRepo.save(portfolio);
        }

        Transaction t = new Transaction();
        t.setUser(user);
        t.setStock(stock);
        t.setType("SELL");
        t.setQuantity(quantity);
        t.setPrice(stock.getPrice());
        t.setTimestamp(LocalDateTime.now());
        return transactionRepo.save(t);
    }
}
