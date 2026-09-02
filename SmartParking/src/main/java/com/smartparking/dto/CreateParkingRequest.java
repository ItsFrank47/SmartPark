package com.smartparking.dto;

import java.math.BigDecimal;

/**
 * DTO per la richiesta di creazione di un nuovo parcheggio.
 */
public class CreateParkingRequest {

    private String name;
    private String address;
    private String description;
    private String category;        // paid, free, underground, ev, disabled
    private String parkingType;     // Strisce Blu, Strisce Bianche, Garage, ecc.

    private BigDecimal hourlyRate;
    private boolean isFree;
    private String openingTime;     // "HH:mm" o null per 24h
    private String closingTime;     // "HH:mm" o null per 24h
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
    private String restrictionStart;   // "HH:mm"
    private String restrictionEnd;     // "HH:mm"

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
}
