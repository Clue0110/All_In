package com.game.repo;
import com.game.model.Transaction;
import com.game.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Fetch history for a specific user, newest first
    List<Transaction> findByUserOrderByTimestampDesc(User user);
    
    // For deleting a user, we need to delete their history too
    void deleteByUser(User user);
}
