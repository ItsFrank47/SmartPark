package com.smartparking.dto;

import com.smartparking.model.Parking;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO che converte il Point JTS in coordinate lat/lng per il client.
 */
public class ParkingDTO {

    private Long id;
    private String name;
    private String address;
    private String description;
    private String category;
    private String parkingType;
    private BigDecimal hourlyRate;
    private boolean isFree;
    private String openingTime;
    private String closingTime;
    private boolean valid24h;
    private Integer totalSpots;
    private Integer availableSpots;
    private double lat;
    private double lng;
    private boolean hasEvCharging;
    private boolean hasDisabledAccess;
    private boolean isCovered;
    private boolean isGuarded;
    private boolean hasParcometro;
    private boolean hasVideoSurveillance;
    private String restrictionNote;
    private String restrictionDay;
    private String restrictionStart;
    private String restrictionEnd;
    private String status; // DERIVATO: Libero, Parziale, Affollato

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public static ParkingDTO fromEntity(Parking p) {
        ParkingDTO dto = new ParkingDTO();
        dto.id = p.getId();
        dto.name = p.getName();
        dto.address = p.getAddress();
        dto.description = p.getDescription();
        dto.category = p.getCategory();
        dto.parkingType = p.getParkingType();
        dto.hourlyRate = p.getHourlyRate();
        dto.isFree = p.isFree();
        dto.valid24h = p.isValid24h();
        dto.totalSpots = p.getTotalSpots();
        dto.availableSpots = p.getAvailableSpots();
        dto.hasEvCharging = p.isHasEvCharging();
        dto.hasDisabledAccess = p.isHasDisabledAccess();
        dto.isCovered = p.isCovered();
        dto.isGuarded = p.isGuarded();
        dto.hasParcometro = p.isHasParcometro();
        dto.hasVideoSurveillance = p.isHasVideoSurveillance();
        dto.restrictionNote = p.getRestrictionNote();
        dto.restrictionDay = p.getRestrictionDay();

        // Converti orari
        dto.openingTime = p.getOpeningTime() != null ? p.getOpeningTime().format(TIME_FMT) : null;
        dto.closingTime = p.getClosingTime() != null ? p.getClosingTime().format(TIME_FMT) : null;
        dto.restrictionStart = p.getRestrictionStart() != null ? p.getRestrictionStart().format(TIME_FMT) : null;
        dto.restrictionEnd = p.getRestrictionEnd() != null ? p.getRestrictionEnd().format(TIME_FMT) : null;

        // Estrai lat/lng dal Point JTS
        if (p.getGeom() != null) {
            dto.lat = p.getGeom().getY(); // Y = latitudine
            dto.lng = p.getGeom().getX(); // X = longitudine
        }

        // Calcola status in base ai posti disponibili
        dto.status = computeStatus(p);

        return dto;
    }

    private static String computeStatus(Parking p) {
        if (p.getTotalSpots() == null || p.getTotalSpots() == 0) return "Sconosciuto";
        int total = p.getTotalSpots();
        int available = p.getAvailableSpots() != null ? p.getAvailableSpots() : 0;
        double ratio = (double) available / total;
        if (ratio > 0.3) return "Libero";
        if (ratio > 0.0) return "Parziale";
        return "Affollato";
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

    public String getOpeningTime() { return openingTime; }
    public void setOpeningTime(String openingTime) { this.openingTime = openingTime; }

    public String getClosingTime() { return closingTime; }
    public void setClosingTime(String closingTime) { this.closingTime = closingTime; }

    public boolean isValid24h() { return valid24h; }
    public void setValid24h(boolean valid24h) { this.valid24h = valid24h; }

    public Integer getTotalSpots() { return totalSpots; }
    public void setTotalSpots(Integer totalSpots) { this.totalSpots = totalSpots; }

    public Integer getAvailableSpots() { return availableSpots; }
    public void setAvailableSpots(Integer availableSpots) { this.availableSpots = availableSpots; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }

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

    public String getRestrictionStart() { return restrictionStart; }
    public void setRestrictionStart(String restrictionStart) { this.restrictionStart = restrictionStart; }

    public String getRestrictionEnd() { return restrictionEnd; }
    public void setRestrictionEnd(String restrictionEnd) { this.restrictionEnd = restrictionEnd; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
