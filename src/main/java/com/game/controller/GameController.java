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
    // Dependecies
    @Autowired private StockRepository stockRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private PortfolioRepository portfolioRepo;
    @Autowired private OrderService orderService;

    // Dashboard
    @GetMapping("/")
    public String dashboard(@RequestParam(required = false) Long userId, Model model) {
        List<User> allUsers = userRepo.findAll();
        
        if (allUsers.isEmpty()) { // Default user creation
            User defaultUser = new User();
            defaultUser.setUsername("DefaultPlayer");
            defaultUser.setBalance(10000.0);
            userRepo.save(defaultUser);
            allUsers.add(defaultUser);
        }

        User currentUser;
        if (userId != null) {
            currentUser = userRepo.findById(userId).orElse(allUsers.get(0));
        } else {
            currentUser = allUsers.get(0);
        }

        List<Portfolio> userPortfolio = portfolioRepo.findAll().stream()
            .filter(p -> p.getUser().getId().equals(currentUser.getId()))
            .collect(Collectors.toList());

        model.addAttribute("user", currentUser);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("stocks", stockRepo.findAll());
        model.addAttribute("portfolio", userPortfolio);
        
        return "index";
    }

    // Create User
    @PostMapping("/api/user/create")
    @ResponseBody
    public ResponseEntity<?> createUser(@RequestParam String username) {
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Username cannot be empty");
        }
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setBalance(10000.0);
        userRepo.save(newUser);
        return ResponseEntity.ok(newUser);
    }

    // Delete User
    @PostMapping("/api/user/delete")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> deleteUser(@RequestParam Long userId) {
        if (userRepo.count() <= 1) {
            return ResponseEntity.badRequest().body("Cannot delete the last player!");
        }
        
        // Fetch user's portfolio
        List<Portfolio> userPortfolio = portfolioRepo.findAll().stream()
            .filter(p -> p.getUser().getId().equals(userId))
            .collect(Collectors.toList());

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

        // Delete the user
        userRepo.deleteById(userId);
        
        return ResponseEntity.ok("Deleted");
    }

    // trade: buy/sell logic
    @PostMapping("/api/trade")
    @ResponseBody
    public ResponseEntity<?> trade(@RequestParam String action, @RequestParam Long userId, @RequestParam Long stockId, @RequestParam int quantity) {
        try {
            if ("buy".equalsIgnoreCase(action)) {
                orderService.buyStock(userId, stockId, quantity);
            } else if ("sell".equalsIgnoreCase(action)) {
                orderService.sellStock(userId, stockId, quantity);
            }
            
            User user = userRepo.findById(userId).orElseThrow();
            List<Portfolio> portfolios = portfolioRepo.findAll().stream()
                .filter(p -> p.getUser().getId().equals(userId))
                .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("balance", user.getBalance());
            response.put("portfolio", portfolios);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
