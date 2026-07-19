package com.example.telecom.change.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;

/**
 * Embeddable value object representing a maintenance window.
 * Used inside {@code NetworkChangeEntity}.
 */
@Embeddable
public class ChangeWindowEmbeddable {

    @Column(name = "window_start")
    private LocalDateTime windowStart;

    @Column(name = "window_end")
    private LocalDateTime windowEnd;

    @Column(name = "timezone")
    private String timezone;

    public ChangeWindowEmbeddable() {}

    public ChangeWindowEmbeddable(LocalDateTime windowStart, LocalDateTime windowEnd, String timezone) {
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
        this.timezone = timezone;
    }

    public LocalDateTime getWindowStart() { return windowStart; }
    public void setWindowStart(LocalDateTime windowStart) { this.windowStart = windowStart; }
    public LocalDateTime getWindowEnd() { return windowEnd; }
    public void setWindowEnd(LocalDateTime windowEnd) { this.windowEnd = windowEnd; }
    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
}
