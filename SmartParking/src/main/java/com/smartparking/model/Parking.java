package com.smartparking.model;

import jakarta.persistence.*;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "parkings", indexes = {
    @Index(name = "idx_parkings_geom", columnList = "geom"),
    @Index(name = "idx_parkings_category", columnList = "category"),
    @Index(name = "idx_parkings_available", columnList = "available_spots")
})
public class Parking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 500)
    private String address;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 50)
    private String category; // paid, free, underground, ev, disabled

    @Column(name = "parking_type", nullable = false, length = 100)
    private String parkingType; // Strisce Blu, Strisce Bianche, Garage, ecc.

    @Column(name = "hourly_rate", precision = 6, scale = 2)
    private BigDecimal hourlyRate;

    @Column(name = "is_free")
    private boolean isFree;

    @Column(name = "opening_time")
    private LocalTime openingTime;

    @Column(name = "closing_time")
    private LocalTime closingTime;

    @Column(name = "valid_24h")
    private boolean valid24h;

    @Column(name = "total_spots")
    private Integer totalSpots;

    @Column(name = "available_spots")
    private Integer availableSpots;

    // Geometria PostGIS - punto in WGS84 (SRID 4326)
    @Column(nullable = false)
    private Point geom;

    @Column(name = "has_ev_charging")
    private boolean hasEvCharging;

    @Column(name = "has_disabled_access")
    private boolean hasDisabledAccess;

    @Column(name = "is_covered")
    private boolean isCovered;

    @Column(name = "is_guarded")
    private boolean isGuarded;

    @Column(name = "has_parcometro")
    private boolean hasParcometro;

    @Column(name = "has_video_surveillance")
    private boolean hasVideoSurveillance;

    @Column(name = "restriction_note", columnDefinition = "TEXT")
    private String restrictionNote;

    @Column(name = "restriction_day", length = 20)
    private String restrictionDay;

    @Column(name = "restriction_start")
    private LocalTime restrictionStart;

    @Column(name = "restriction_end")
    private LocalTime restrictionEnd;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // --- Getters e Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getParkingType() { return parkingType; }
    public void setParkingType(String parkingType) { this.parkingType = parkingType; }

    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }

    public boolean isFree() { return isFree; }
    public void setFree(boolean free) { isFree = free; }

    public LocalTime getOpeningTime() { return openingTime; }
    public void setOpeningTime(LocalTime openingTime) { this.openingTime = openingTime; }

    public LocalTime getClosingTime() { return closingTime; }
    public void setClosingTime(LocalTime closingTime) { this.closingTime = closingTime; }

    public boolean isValid24h() { return valid24h; }
    public void setValid24h(boolean valid24h) { this.valid24h = valid24h; }

    public Integer getTotalSpots() { return totalSpots; }
    public void setTotalSpots(Integer totalSpots) { this.totalSpots = totalSpots; }

    public Integer getAvailableSpots() { return availableSpots; }
    public void setAvailableSpots(Integer availableSpots) { this.availableSpots = availableSpots; }

    public Point getGeom() { return geom; }
    public void setGeom(Point geom) { this.geom = geom; }

    public boolean isHasEvCharging() { return hasEvCharging; }
    public void setHasEvCharging(boolean hasEvCharging) { this.hasEvCharging = hasEvCharging; }

    public boolean isHasDisabledAccess() { return hasDisabledAccess; }
    public void setHasDisabledAccess(boolean hasDisabledAccess) { this.hasDisabledAccess = hasDisabledAccess; }

    public boolean isCovered() { return isCovered; }
    public void setCovered(boolean covered) { isCovered = covered; }

    public boolean isGuarded() { return isGuarded; }
    public void setGuarded(boolean guarded) { isGuarded = guarded; }

    public boolean isHasParcometro() { return hasParcometro; }
    public void setHasParcometro(boolean hasParcometro) { this.hasParcometro = hasParcometro; }

    public boolean isHasVideoSurveillance() { return hasVideoSurveillance; }
    public void setHasVideoSurveillance(boolean hasVideoSurveillance) { this.hasVideoSurveillance = hasVideoSurveillance; }

    public String getRestrictionNote() { return restrictionNote; }
    public void setRestrictionNote(String restrictionNote) { this.restrictionNote = restrictionNote; }

    public String getRestrictionDay() { return restrictionDay; }
    public void setRestrictionDay(String restrictionDay) { this.restrictionDay = restrictionDay; }

    public LocalTime getRestrictionStart() { return restrictionStart; }
    public void setRestrictionStart(LocalTime restrictionStart) { this.restrictionStart = restrictionStart; }

    public LocalTime getRestrictionEnd() { return restrictionEnd; }
    public void setRestrictionEnd(LocalTime restrictionEnd) { this.restrictionEnd = restrictionEnd; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
