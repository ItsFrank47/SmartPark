package com.smartparking.controller;

import com.smartparking.dto.CreateParkingRequest;
import com.smartparking.dto.ParkingDTO;
import com.smartparking.model.Parking;
import com.smartparking.repository.ParkingRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/parkings")
@CrossOrigin(origins = {"*"})
public class ParkingController {

    private final ParkingRepository parkingRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public ParkingController(ParkingRepository parkingRepository) {
        this.parkingRepository = parkingRepository;
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<ParkingDTO>> getNearby(
            @RequestParam(defaultValue = "45.4642") double lat,
            @RequestParam(defaultValue = "9.1900") double lng,
            @RequestParam(defaultValue = "5000") double radius
    ) {
        List<Parking> parkings = parkingRepository.findNearby(lng, lat, radius);
        return ResponseEntity.ok(parkings.stream().map(ParkingDTO::fromEntity).collect(Collectors.toList()));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ParkingDTO>> getFiltered(
            @RequestParam(defaultValue = "45.4642") double lat,
            @RequestParam(defaultValue = "9.1900") double lng,
            @RequestParam(defaultValue = "5000") double radius,
            @RequestParam(required = false) String type
    ) {
        List<Parking> parkings;
        if (type == null || type.isEmpty() || "all".equals(type)) {
            parkings = parkingRepository.findNearby(lng, lat, radius);
        } else {
            switch (type) {
                case "free" -> parkings = parkingRepository.findNearbyFree(lng, lat, radius);
                case "ev" -> parkings = parkingRepository.findNearbyEV(lng, lat, radius);
                case "disabled" -> parkings = parkingRepository.findNearbyDisabled(lng, lat, radius);
                case "underground" -> parkings = parkingRepository.findNearbyCovered(lng, lat, radius);
                case "available" -> parkings = parkingRepository.findNearbyAvailable(lng, lat, radius);
                default -> parkings = parkingRepository.findNearby(lng, lat, radius);
            }
        }
        return ResponseEntity.ok(parkings.stream().map(ParkingDTO::fromEntity).collect(Collectors.toList()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ParkingDTO>> getAll() {
        List<Parking> parkings = parkingRepository.findAllByOrderByAvailableSpotsDesc();
        return ResponseEntity.ok(parkings.stream().map(ParkingDTO::fromEntity).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingDTO> getById(@PathVariable Long id) {
        return parkingRepository.findById(id)
                .map(ParkingDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        long count = parkingRepository.count();
        return ResponseEntity.ok(Map.of("status", "online", "totalParkings", count));
    }

    // ============================================
    // CREATE
    // ============================================
    @PostMapping
    public ResponseEntity<ParkingDTO> create(@RequestBody CreateParkingRequest req) {
        Parking p = new Parking();
        p.setName(req.getName());
        p.setAddress(req.getAddress());
        p.setDescription(req.getDescription());
        p.setCategory(req.getCategory());
        p.setParkingType(req.getParkingType());
        p.setFree(req.isFree());
        p.setHourlyRate(req.getHourlyRate());
        p.setValid24h(req.isValid24h());

        if (req.getOpeningTime() != null) {
            p.setOpeningTime(LocalTime.parse(req.getOpeningTime()));
        }
        if (req.getClosingTime() != null) {
            p.setClosingTime(LocalTime.parse(req.getClosingTime()));
        }

        p.setTotalSpots(req.getTotalSpots() != null ? req.getTotalSpots() : 0);
        p.setAvailableSpots(req.getAvailableSpots() != null ? req.getAvailableSpots() : 0);
        p.setHasEvCharging(req.isHasEvCharging());
        p.setHasDisabledAccess(req.isHasDisabledAccess());
        p.setCovered(req.isCovered());
        p.setGuarded(req.isGuarded());
        p.setHasParcometro(req.isHasParcometro());
        p.setHasVideoSurveillance(req.isHasVideoSurveillance());
        p.setRestrictionNote(req.getRestrictionNote());
        p.setRestrictionDay(req.getRestrictionDay());

        if (req.getRestrictionStart() != null) {
            p.setRestrictionStart(LocalTime.parse(req.getRestrictionStart()));
        }
        if (req.getRestrictionEnd() != null) {
            p.setRestrictionEnd(LocalTime.parse(req.getRestrictionEnd()));
        }

        p.setGeom(geometryFactory.createPoint(new Coordinate(req.getLng(), req.getLat())));

        Parking saved = parkingRepository.save(p);
        return ResponseEntity.ok(ParkingDTO.fromEntity(saved));
    }

    // ============================================
    // DELETE
    // ============================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!parkingRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        parkingRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
