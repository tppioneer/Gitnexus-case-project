package com.example.telecom.gateway.dto;

import java.time.LocalDateTime;
import java.util.List;

public class DashboardTrendResponse {

    private String trendType;
    private List<DataPoint> dataPoints;
    private String period;
    private Double changePercent;
    private String summary;

    public DashboardTrendResponse() {
    }

    public DashboardTrendResponse(String trendType, List<DataPoint> dataPoints,
                                  String period, Double changePercent, String summary) {
        this.trendType = trendType;
        this.dataPoints = dataPoints;
        this.period = period;
        this.changePercent = changePercent;
        this.summary = summary;
    }

    public String getTrendType() {
        return trendType;
    }

    public void setTrendType(String trendType) {
        this.trendType = trendType;
    }

    public List<DataPoint> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(List<DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Double getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(Double changePercent) {
        this.changePercent = changePercent;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public static class DataPoint {
        private LocalDateTime timestamp;
        private Number value;
        private String label;

        public DataPoint() {
        }

        public DataPoint(LocalDateTime timestamp, Number value, String label) {
            this.timestamp = timestamp;
            this.value = value;
            this.label = label;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        public Number getValue() {
            return value;
        }

        public void setValue(Number value) {
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }
}
