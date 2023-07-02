package com.example.hs.loadbalancer.model;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;

import java.time.Duration;

public enum LimiterMode {

    LEADERBOARD,
    SET_UP_FAVOURITE_DRINK ,
    OTHER;

    public static LimiterMode resolvePlanFromApiKey(String apiKey) {
        if (apiKey == null || apiKey.contains("LEADERBOARD")) {
            return LEADERBOARD;
        } else if (apiKey.contains("SET_UP_FAVOURITE_DRINK")) {
            return SET_UP_FAVOURITE_DRINK;
        }
        return OTHER;
    }

    public Bandwidth getLimit() {
        if(this.equals(LEADERBOARD)) {
            return Bandwidth.classic(3, Refill.intervally(3, Duration.ofMinutes(1)));
        } else if (this.equals(SET_UP_FAVOURITE_DRINK)) {
            return Bandwidth.classic(10, Refill.intervally(100, Duration.ofMinutes(1)));
        }
        return Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
    }

}
