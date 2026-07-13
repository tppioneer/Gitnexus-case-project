package com.example.telecom.common.gateway;

import java.util.Objects;

public class DashboardWidgetConfig {

    private final String widgetId;
    private final String dashboardId;
    private final String widgetType;
    private final String title;
    private final int position;
    private final int width;
    private final int height;
    private final String configJson;

    public DashboardWidgetConfig(String widgetId, String dashboardId, String widgetType,
                                 String title, int position, int width,
                                 int height, String configJson) {
        this.widgetId = widgetId;
        this.dashboardId = dashboardId;
        this.widgetType = widgetType;
        this.title = title;
        this.position = position;
        this.width = width;
        this.height = height;
        this.configJson = configJson;
    }

    public String getWidgetId() { return widgetId; }
    public String getDashboardId() { return dashboardId; }
    public String getWidgetType() { return widgetType; }
    public String getTitle() { return title; }
    public int getPosition() { return position; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public String getConfigJson() { return configJson; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DashboardWidgetConfig that = (DashboardWidgetConfig) o;
        return Objects.equals(widgetId, that.widgetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(widgetId);
    }

    @Override
    public String toString() {
        return "DashboardWidgetConfig{" +
                "widgetId='" + widgetId + '\'' +
                ", dashboardId='" + dashboardId + '\'' +
                ", widgetType='" + widgetType + '\'' +
                ", title='" + title + '\'' +
                ", position=" + position +
                ", width=" + width +
                ", height=" + height +
                ", configJson='" + configJson + '\'' +
                '}';
    }
}
