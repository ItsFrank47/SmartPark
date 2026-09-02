package com.smartparking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import com.smartparking.model.Parking;
import com.smartparking.repository.ParkingRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootApplication
@EnableScheduling
public class SmartParkingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartParkingApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(ParkingRepository repository) {
        return args -> {
            if (repository.count() > 0) return;

            GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);

            Parking p1 = new Parking();
            p1.setName("Parcheggio Piazza Duomo");
            p1.setAddress("Piazza del Duomo, Milano");
            p1.setCategory("paid");
            p1.setParkingType("Strisce Blu");
            p1.setHourlyRate(new BigDecimal("2.00"));
            p1.setFree(false);
            p1.setOpeningTime(LocalTime.of(8, 0));
            p1.setClosingTime(LocalTime.of(20, 0));
            p1.setValid24h(false);
            p1.setTotalSpots(150);
            p1.setAvailableSpots(90);
            p1.setGeom(gf.createPoint(new Coordinate(9.1919, 45.4641)));
            p1.setHasEvCharging(false);
            p1.setHasDisabledAccess(false);
            p1.setCovered(false);
            p1.setGuarded(false);
            p1.setHasParcometro(true);
            p1.setHasVideoSurveillance(true);
            p1.setRestrictionNote("Pulizia strada");
            p1.setRestrictionDay("Lunedi");
            p1.setRestrictionStart(LocalTime.of(0, 0));
            p1.setRestrictionEnd(LocalTime.of(6, 0));
            repository.save(p1);

            Parking p2 = new Parking();
            p2.setName("Garage Via Torino Park");
            p2.setAddress("Via Torino 15, Milano");
            p2.setCategory("underground");
            p2.setParkingType("Garage Sotterraneo");
            p2.setHourlyRate(new BigDecimal("3.50"));
            p2.setFree(false);
            p2.setValid24h(true);
            p2.setTotalSpots(300);
            p2.setAvailableSpots(18);
            p2.setGeom(gf.createPoint(new Coordinate(9.1940, 45.4608)));
            p2.setHasEvCharging(true);
            p2.setHasDisabledAccess(true);
            p2.setCovered(true);
            p2.setGuarded(true);
            p2.setHasParcometro(false);
            p2.setHasVideoSurveillance(true);
            repository.save(p2);

            Parking p3 = new Parking();
            p3.setName("Parcheggio Corso Buenos Aires");
            p3.setAddress("Corso Buenos Aires, Milano");
            p3.setCategory("free");
            p3.setParkingType("Strisce Bianche");
            p3.setFree(true);
            p3.setTotalSpots(50);
            p3.setAvailableSpots(28);
            p3.setGeom(gf.createPoint(new Coordinate(9.2080, 45.4781)));
            p3.setRestrictionNote("Divieto di sosta");
            p3.setRestrictionDay("Sabato");
            p3.setRestrictionStart(LocalTime.of(14, 0));
            p3.setRestrictionEnd(LocalTime.of(18, 0));
            repository.save(p3);

            Parking p4 = new Parking();
            p4.setName("Parcheggio EV Stazione Centrale");
            p4.setAddress("Piazza Duca d'Aosta, Milano");
            p4.setCategory("ev");
            p4.setParkingType("Ricarica EV");
            p4.setHourlyRate(new BigDecimal("2.50"));
            p4.setFree(false);
            p4.setOpeningTime(LocalTime.of(6, 0));
            p4.setClosingTime(LocalTime.of(23, 0));
            p4.setValid24h(false);
            p4.setTotalSpots(30);
            p4.setAvailableSpots(10);
            p4.setGeom(gf.createPoint(new Coordinate(9.2050, 45.4856)));
            p4.setHasEvCharging(true);
            p4.setHasDisabledAccess(true);
            p4.setCovered(false);
            p4.setGuarded(false);
            p4.setHasVideoSurveillance(true);
            p4.setRestrictionNote("Max 4h sosta - Dopo 4h sovrapprezzo 50%");
            repository.save(p4);

            Parking p5 = new Parking();
            p5.setName("Parcheggio Riservato Brera");
            p5.setAddress("Via Brera 28, Milano");
            p5.setCategory("disabled");
            p5.setParkingType("Disabili");
            p5.setFree(true);
            p5.setValid24h(true);
            p5.setTotalSpots(12);
            p5.setAvailableSpots(8);
            p5.setGeom(gf.createPoint(new Coordinate(9.1875, 45.4722)));
            p5.setHasDisabledAccess(true);
            p5.setCovered(false);
            p5.setGuarded(false);
            p5.setHasVideoSurveillance(true);
            repository.save(p5);

            System.out.println(">>> SmartParking: 5 parcheggi di test inseriti nel database.");
        };
    }

    /**
     * Aggiorna periodicamente i posti disponibili per simulare varianza in tempo reale.
     */
    @Scheduled(fixedRate = 30000)
    public void simulateAvailabilityChanges() {
        // Placeholder: in produzione qui ci sarebbe la logica IoT/SENSORI
    }
}
