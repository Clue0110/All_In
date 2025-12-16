package com.game.model;
import jakarta.persistence.*;
import lombok.Data;

@Entity @Data
public class Stock {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String ticker;
    private String name;
    private double price;
    private int availableShares;
    private double volatilityChance; 
    private double maxVolatility;    

    public Stock() {}

    public Stock(String ticker, String name, double price, int availableShares, double volatilityChance, double maxVolatility) {
        this.ticker = ticker;
        this.name = name;
        this.price = price;
        this.availableShares = availableShares;
        this.volatilityChance = volatilityChance;
        this.maxVolatility = maxVolatility;
    }
}
