package com.game.model;
import jakarta.persistence.*;
import lombok.Data;
@Entity @Data @Table(name = "game_user")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private double balance;
}
