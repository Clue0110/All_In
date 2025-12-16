package com.game.repo;
import com.game.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    Portfolio findByUserAndStock(User user, Stock stock);
}
