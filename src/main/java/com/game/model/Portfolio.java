package com.game.model;
import jakarta.persistence.*;
import lombok.Data;
@Entity @Data
public class Portfolio {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne 
    private User user;
    @ManyToOne 
    private Stock stock;
    private int quantity;
    private double totalInvestment; 
}
