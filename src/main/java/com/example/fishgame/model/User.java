package com.example.fishgame.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String userId;

    private String userName;
    private int coins;
    private int diamonds;
    private int level;
    private int currentExperience;
    private int experienceForNextLevel;
    private String rodType;

    @ElementCollection
    private List<String> fishInventory;

}
