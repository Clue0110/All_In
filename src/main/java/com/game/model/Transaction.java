package com.game.model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity @Data
public class Transaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Stock stock;

    private String type; // "BUY" or "SELL"
    private int quantity;
    private double price; // Price per share at the moment of trade
    private LocalDateTime timestamp;
}
