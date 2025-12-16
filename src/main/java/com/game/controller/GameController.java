package com.game.controller;
import com.game.service.*;
import com.game.repo.*;
import com.game.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.transaction.Transactional;

@Controller
public class GameController {
    // Dependencies
    @Autowired private StockRepository stockRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private PortfolioRepository portfolioRepo;
    @Autowired private TransactionRepository transactionRepo;
    @Autowired private OrderService orderService;

    // Dashboard
    @GetMapping("/")
    public String dashboard(@RequestParam(required = false) Long userId, Model model) {
        List<User> allUsers = userRepo.findAll();
        if (allUsers.isEmpty()) {
            User defaultUser = new User(); defaultUser.setUsername("PlayerOne"); defaultUser.setBalance(10000.0);
            userRepo.save(defaultUser); allUsers.add(defaultUser);
        }
        User currentUser = (userId != null) ? userRepo.findById(userId).orElse(allUsers.get(0)) : allUsers.get(0);

        List<Portfolio> userPortfolio = portfolioRepo.findAll().stream()
            .filter(p -> p.getUser().getId().equals(currentUser.getId()))
            .collect(Collectors.toList());
        List<Transaction> history = transactionRepo.findByUserOrderByTimestampDesc(currentUser);

        model.addAttribute("user", currentUser);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("stocks", stockRepo.findAll());
        model.addAttribute("portfolio", userPortfolio);
        model.addAttribute("history", history);
        return "index";
    }

    // Create User
    @PostMapping("/api/user/create")
    @ResponseBody
    public ResponseEntity<?> createUser(@RequestParam String username) {
        if (username == null || username.trim().isEmpty()) return ResponseEntity.badRequest().body("Empty name");
        User newUser = new User(); newUser.setUsername(username); newUser.setBalance(10000.0);
        userRepo.save(newUser);
        return ResponseEntity.ok(newUser);
    }

    // Delete User
    @PostMapping("/api/user/delete")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> deleteUser(@RequestParam Long userId) {
        if (userRepo.count() <= 1) return ResponseEntity.badRequest().body("Cannot delete last player!");
        User user = userRepo.findById(userId).orElseThrow();
        
        // Fetch user's portfolio
        List<Portfolio> userPortfolio = portfolioRepo.findAll().stream()
            .filter(p -> p.getUser().getId().equals(userId)).collect(Collectors.toList());
        // Return stocks to market! Dont want them to dissapear lol
        for (Portfolio p : userPortfolio) {
            Stock stock = p.getStock();
            if (p.getQuantity() > 0) {
                stock.setAvailableShares(stock.getAvailableShares() + p.getQuantity());
                stockRepo.save(stock);
            }
        }
        // Delete user's portfolio entries
        portfolioRepo.deleteAll(userPortfolio);
        // Delete user's transactions
        transactionRepo.deleteByUser(user);
        // Delete the user
        userRepo.delete(user);
        return ResponseEntity.ok("Deleted");
    }

    // trade: buy/sell logic
    @PostMapping("/api/trade")
    @ResponseBody
    public ResponseEntity<?> trade(@RequestParam String action, @RequestParam Long userId, @RequestParam Long stockId, @RequestParam int quantity) {
        try {
            Transaction tx = null;
            if ("buy".equalsIgnoreCase(action)) {
                tx = orderService.buyStock(userId, stockId, quantity);
            } else if ("sell".equalsIgnoreCase(action)) {
                tx = orderService.sellStock(userId, stockId, quantity);
            }
            
            User user = userRepo.findById(userId).orElseThrow();
            List<Portfolio> portfolios = portfolioRepo.findAll().stream()
                .filter(p -> p.getUser().getId().equals(userId))
                .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("balance", user.getBalance());
            response.put("portfolio", portfolios);
            response.put("transaction", tx);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
