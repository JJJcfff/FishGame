package com.example.fishgame.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.Random;

@RequestMapping("/fish")
public class FishController {

    // fish has weight and rarity -> weight and rarity are normally distributed, and has a min and max range.
    // uncaught fish model: name/species, imgurl, price, weight range(max, min)
    // caught fish model: name/species, imgurl, price, weight, rarity, caught time

    // randomly choose a fish type-> rarity(a,b,c,d,e,f,g) determines the card color, then randomly generate weight.
    //weight determines rarity.

    // one endpoint
    //return caught fish model.

    private final List<String> fishTypes = List.of("Tuna", "Salmon", "Trout", "Bass", "Cod", "Shark", "Goldfish");
    private final Map<String, Map<String, Double>> fishWeightRanges = Map.of(
        "Tuna", Map.of("min", 10.0, "max", 100.0),
        "Salmon", Map.of("min", 4.0, "max", 25.0),
        "Trout", Map.of("min", 1.0, "max", 10.0),
        "Bass", Map.of("min", 2.0, "max", 15.0),
        "Cod", Map.of("min", 3.0, "max", 30.0),
        "Shark", Map.of("min", 50.0, "max", 250.0),
        "Goldfish", Map.of("min", 0.1, "max", 0.5)
    );
    private final List<String> rarityLevels = List.of("S", "A", "B", "C", "D", "E", "F");


    @GetMapping("/catch")
    public Map<String, Object> catchFish() {
        String fishType = fishTypes.get((int) (Math.random() * fishTypes.size()));
        Map<String, Double> weightRange = fishWeightRanges.get(fishType);
        double minWeight = weightRange.get("min");
        double maxWeight = weightRange.get("max");

        Random random = new Random();
        double mean = (minWeight + maxWeight) / 2.0;
        double stdDev = (maxWeight - minWeight) / 6.0;
        double weight = random.nextGaussian() * stdDev + mean;
        weight = Math.max(minWeight, Math.min(maxWeight, weight));
        String rarity = rarityLevels.get((int) (Math.random() * rarityLevels.size()));
        String imgUrl = "https://example.com/fish/" + fishType.toLowerCase() + ".png"; // Placeholder URL
        double price = weight * 10; // Placeholder price calculation

        Map<String, Object> caughtFish = Map.of(
            "name", fishType,
            "imgUrl", imgUrl,
            "price", price,
            "weight", weight,
            "rarity", rarity,
            "caughtTime", System.currentTimeMillis()
        );

        return Map.of(
            "code", 200,
            "msg", "Fish caught successfully",
            "fish", caughtFish
        );
    }


}
