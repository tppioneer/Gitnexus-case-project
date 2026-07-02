package com.example.telecom.gateway.service;

import com.example.telecom.gateway.client.*;
import com.example.telecom.gateway.dto.*;

public class RegionHealthEvaluator {

    private final DashboardHealthScoreService healthScoreService;

    public RegionHealthEvaluator(DashboardHealthScoreService healthScoreService) {
        this.healthScoreService = healthScoreService;
    }

    public String evaluate(String regionCode) {
        int score = healthScoreService.calculateOverallScore(regionCode);
        if (score >= 80) return "HEALTHY: Region " + regionCode + " score=" + score;
        if (score >= 50) return "DEGRADED: Region " + regionCode + " score=" + score;
        return "CRITICAL: Region " + regionCode + " score=" + score;
    }
}
