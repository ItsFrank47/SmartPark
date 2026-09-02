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

            Parking p6 = new Parking();
            p6.setName("Parcheggio Porta Garibaldi");
            p6.setAddress("Via Valtellina 12, Milano");
            p6.setCategory("paid");
            p6.setParkingType("Strisce Blu");
            p6.setHourlyRate(new BigDecimal("1.50"));
            p6.setFree(false);
            p6.setOpeningTime(LocalTime.of(8, 0));
            p6.setClosingTime(LocalTime.of(21, 0));
            p6.setTotalSpots(40);
            p6.setAvailableSpots(15);
            p6.setGeom(gf.createPoint(new Coordinate(9.1870, 45.4840)));
            p6.setHasParcometro(true);
            p6.setHasVideoSurveillance(true);
            repository.save(p6);

            Parking p7 = new Parking();
            p7.setName("Parcheggio Darsena Navigli");
            p7.setAddress("Alzaia Naviglio Grande, Milano");
            p7.setCategory("paid");
            p7.setParkingType("Strisce Blu");
            p7.setHourlyRate(new BigDecimal("1.20"));
            p7.setFree(false);
            p7.setOpeningTime(LocalTime.of(8, 0));
            p7.setClosingTime(LocalTime.of(19, 0));
            p7.setTotalSpots(60);
            p7.setAvailableSpots(25);
            p7.setGeom(gf.createPoint(new Coordinate(9.1690, 45.4515)));
            p7.setHasVideoSurveillance(true);
            repository.save(p7);

            Parking p8 = new Parking();
            p8.setName("Parcheggio Arco della Pace");
            p8.setAddress("Corso Sempione 30, Milano");
            p8.setCategory("paid");
            p8.setParkingType("Strisce Blu");
            p8.setHourlyRate(new BigDecimal("1.50"));
            p8.setFree(false);
            p8.setOpeningTime(LocalTime.of(8, 0));
            p8.setClosingTime(LocalTime.of(20, 0));
            p8.setTotalSpots(35);
            p8.setAvailableSpots(12);
            p8.setGeom(gf.createPoint(new Coordinate(9.1740, 45.4755)));
            p8.setHasParcometro(true);
            repository.save(p8);

            Parking p9 = new Parking();
            p9.setName("Parcheggio San Babila");
            p9.setAddress("Corso Venezia 4, Milano");
            p9.setCategory("paid");
            p9.setParkingType("Strisce Blu");
            p9.setHourlyRate(new BigDecimal("2.00"));
            p9.setFree(false);
            p9.setOpeningTime(LocalTime.of(8, 0));
            p9.setClosingTime(LocalTime.of(20, 0));
            p9.setTotalSpots(45);
            p9.setAvailableSpots(5);
            p9.setGeom(gf.createPoint(new Coordinate(9.1965, 45.4660)));
            p9.setHasParcometro(true);
            p9.setHasVideoSurveillance(true);
            repository.save(p9);

            Parking p10 = new Parking();
            p10.setName("Parcheggio Sant'Ambrogio");
            p10.setAddress("Via Carducci, Milano");
            p10.setCategory("paid");
            p10.setParkingType("Strisce Blu");
            p10.setHourlyRate(new BigDecimal("1.50"));
            p10.setFree(false);
            p10.setOpeningTime(LocalTime.of(8, 0));
            p10.setClosingTime(LocalTime.of(19, 0));
            p10.setTotalSpots(30);
            p10.setAvailableSpots(20);
            p10.setGeom(gf.createPoint(new Coordinate(9.1760, 45.4635)));
            repository.save(p10);

            Parking p11 = new Parking();
            p11.setName("Parcheggio Porta Romana");
            p11.setAddress("Corso di Porta Romana 120, Milano");
            p11.setCategory("paid");
            p11.setParkingType("Strisce Blu");
            p11.setHourlyRate(new BigDecimal("1.30"));
            p11.setFree(false);
            p11.setOpeningTime(LocalTime.of(8, 0));
            p11.setClosingTime(LocalTime.of(20, 0));
            p11.setTotalSpots(55);
            p11.setAvailableSpots(33);
            p11.setGeom(gf.createPoint(new Coordinate(9.2000, 45.4535)));
            p11.setHasParcometro(true);
            repository.save(p11);

            Parking p12 = new Parking();
            p12.setName("Parcheggio Parco Sempione");
            p12.setAddress("Viale Alemagna, Milano");
            p12.setCategory("free");
            p12.setParkingType("Strisce Bianche");
            p12.setFree(true);
            p12.setTotalSpots(80);
            p12.setAvailableSpots(60);
            p12.setGeom(gf.createPoint(new Coordinate(9.1790, 45.4725)));
            repository.save(p12);

            Parking p13 = new Parking();
            p13.setName("Parcheggio Darsena Sud");
            p13.setAddress("Via Gorini, Milano");
            p13.setCategory("free");
            p13.setParkingType("Strisce Bianche");
            p13.setFree(true);
            p13.setTotalSpots(25);
            p13.setAvailableSpots(10);
            p13.setGeom(gf.createPoint(new Coordinate(9.1690, 45.4515)));
            repository.save(p13);

            Parking p14 = new Parking();
            p14.setName("Parcheggio Isola");
            p14.setAddress("Via Borsieri 16, Milano");
            p14.setCategory("free");
            p14.setParkingType("Strisce Bianche");
            p14.setFree(true);
            p14.setTotalSpots(25);
            p14.setAvailableSpots(22);
            p14.setGeom(gf.createPoint(new Coordinate(9.1850, 45.4890)));
            repository.save(p14);

            Parking p15 = new Parking();
            p15.setName("Parcheggio Cascina Gobba");
            p15.setAddress("Via Giacosa, Milano");
            p15.setCategory("free");
            p15.setParkingType("Strisce Bianche");
            p15.setFree(true);
            p15.setTotalSpots(90);
            p15.setAvailableSpots(70);
            p15.setGeom(gf.createPoint(new Coordinate(9.2470, 45.4880)));
            p15.setRestrictionNote("Divieto di sosta");
            p15.setRestrictionDay("Sabato");
            p15.setRestrictionStart(LocalTime.of(14, 0));
            p15.setRestrictionEnd(LocalTime.of(18, 0));
            repository.save(p15);

            Parking p16 = new Parking();
            p16.setName("Garage CityLife Tre Torri");
            p16.setAddress("Via Federico Tesio, Milano");
            p16.setCategory("underground");
            p16.setParkingType("Garage Sotterraneo");
            p16.setHourlyRate(new BigDecimal("4.00"));
            p16.setFree(false);
            p16.setValid24h(true);
            p16.setTotalSpots(400);
            p16.setAvailableSpots(240);
            p16.setGeom(gf.createPoint(new Coordinate(9.1560, 45.4780)));
            p16.setHasEvCharging(true);
            p16.setHasDisabledAccess(true);
            p16.setCovered(true);
            p16.setGuarded(true);
            p16.setHasVideoSurveillance(true);
            repository.save(p16);

            Parking p17 = new Parking();
            p17.setName("Garage Corso Como");
            p17.setAddress("Via Guglielmo Pepe 30, Milano");
            p17.setCategory("underground");
            p17.setParkingType("Garage Sotterraneo");
            p17.setHourlyRate(new BigDecimal("3.00"));
            p17.setFree(false);
            p17.setValid24h(true);
            p17.setTotalSpots(150);
            p17.setAvailableSpots(90);
            p17.setGeom(gf.createPoint(new Coordinate(9.1860, 45.4845)));
            p17.setCovered(true);
            p17.setGuarded(true);
            p17.setHasVideoSurveillance(true);
            repository.save(p17);

            Parking p18 = new Parking();
            p18.setName("Autorimessa Lambrate");
            p18.setAddress("Via Rombon 48, Milano");
            p18.setCategory("underground");
            p18.setParkingType("Garage Sotterraneo");
            p18.setHourlyRate(new BigDecimal("2.50"));
            p18.setFree(false);
            p18.setOpeningTime(LocalTime.of(6, 0));
            p18.setClosingTime(LocalTime.of(23, 0));
            p18.setTotalSpots(120);
            p18.setAvailableSpots(45);
            p18.setGeom(gf.createPoint(new Coordinate(9.2360, 45.4850)));
            p18.setHasDisabledAccess(true);
            p18.setCovered(true);
            repository.save(p18);

            Parking p19 = new Parking();
            p19.setName("Parcheggio Università Bicocca");
            p19.setAddress("Viale dell'Innovazione, Milano");
            p19.setCategory("underground");
            p19.setParkingType("Garage Sotterraneo");
            p19.setHourlyRate(new BigDecimal("2.00"));
            p19.setFree(false);
            p19.setOpeningTime(LocalTime.of(7, 0));
            p19.setClosingTime(LocalTime.of(22, 0));
            p19.setTotalSpots(250);
            p19.setAvailableSpots(130);
            p19.setGeom(gf.createPoint(new Coordinate(9.2110, 45.5140)));
            p19.setHasEvCharging(true);
            p19.setCovered(true);
            repository.save(p19);

            Parking p20 = new Parking();
            p20.setName("Park & Charge Milanofiori");
            p20.setAddress("Via Giuseppe Di Vittorio, Assago");
            p20.setCategory("ev");
            p20.setParkingType("Ricarica EV");
            p20.setHourlyRate(new BigDecimal("3.00"));
            p20.setFree(false);
            p20.setOpeningTime(LocalTime.of(6, 0));
            p20.setClosingTime(LocalTime.of(23, 0));
            p20.setTotalSpots(40);
            p20.setAvailableSpots(18);
            p20.setGeom(gf.createPoint(new Coordinate(9.1470, 45.4030)));
            p20.setHasEvCharging(true);
            p20.setCovered(true);
            p20.setGuarded(true);
            p20.setHasVideoSurveillance(true);
            repository.save(p20);

            Parking p21 = new Parking();
            p21.setName("Parcheggio EV Metanopoli");
            p21.setAddress("Via Mario Idiomi, San Donato Milanese");
            p21.setCategory("ev");
            p21.setParkingType("Ricarica EV");
            p21.setHourlyRate(new BigDecimal("2.20"));
            p21.setFree(false);
            p21.setValid24h(true);
            p21.setTotalSpots(25);
            p21.setAvailableSpots(9);
            p21.setGeom(gf.createPoint(new Coordinate(9.2630, 45.4170)));
            p21.setHasEvCharging(true);
            p21.setHasDisabledAccess(true);
            p21.setHasVideoSurveillance(true);
            repository.save(p21);

            Parking p22 = new Parking();
            p22.setName("Rho Green EV Park");
            p22.setAddress("Via Europa, Rho");
            p22.setCategory("ev");
            p22.setParkingType("Ricarica EV");
            p22.setHourlyRate(new BigDecimal("2.00"));
            p22.setFree(false);
            p22.setOpeningTime(LocalTime.of(7, 0));
            p22.setClosingTime(LocalTime.of(21, 0));
            p22.setTotalSpots(20);
            p22.setAvailableSpots(6);
            p22.setGeom(gf.createPoint(new Coordinate(9.0400, 45.5300)));
            p22.setHasEvCharging(true);
            p22.setCovered(true);
            repository.save(p22);

            Parking p23 = new Parking();
            p23.setName("Parcheggio Disabili Melegnano");
            p23.setAddress("Via Cavour 3, Melegnano");
            p23.setCategory("disabled");
            p23.setParkingType("Disabili");
            p23.setFree(true);
            p23.setValid24h(true);
            p23.setTotalSpots(8);
            p23.setAvailableSpots(4);
            p23.setGeom(gf.createPoint(new Coordinate(9.3240, 45.3590)));
            p23.setHasDisabledAccess(true);
            p23.setHasVideoSurveillance(true);
            repository.save(p23);

            Parking p24 = new Parking();
            p24.setName("Parcheggio Disabili Monza");
            p24.setAddress("Viale della Stazione, Monza");
            p24.setCategory("disabled");
            p24.setParkingType("Disabili");
            p24.setFree(true);
            p24.setValid24h(true);
            p24.setTotalSpots(10);
            p24.setAvailableSpots(7);
            p24.setGeom(gf.createPoint(new Coordinate(9.2740, 45.5840)));
            p24.setHasDisabledAccess(true);
            p24.setCovered(true);
            repository.save(p24);

            Parking p25 = new Parking();
            p25.setName("Parcheggio Disabili Sesto");
            p25.setAddress("Piazza della Resistenza, Sesto San Giovanni");
            p25.setCategory("disabled");
            p25.setParkingType("Disabili");
            p25.setFree(true);
            p25.setValid24h(true);
            p25.setTotalSpots(6);
            p25.setAvailableSpots(1);
            p25.setGeom(gf.createPoint(new Coordinate(9.2390, 45.5200)));
            p25.setHasDisabledAccess(true);
            p25.setHasVideoSurveillance(true);
            repository.save(p25);

            Parking p26 = new Parking();
            p26.setName("Parcheggio Naviglio Pavese");
            p26.setAddress("Via Ascanio Sforza 90, Milano");
            p26.setCategory("free");
            p26.setParkingType("Strisce Bianche");
            p26.setFree(true);
            p26.setTotalSpots(40);
            p26.setAvailableSpots(18);
            p26.setGeom(gf.createPoint(new Coordinate(9.1640, 45.4450)));
            repository.save(p26);

            Parking p27 = new Parking();
            p27.setName("Autorimessa Corso Italia");
            p27.setAddress("Corso Italia 15, Milano");
            p27.setCategory("underground");
            p27.setParkingType("Garage Sotterraneo");
            p27.setHourlyRate(new BigDecimal("2.80"));
            p27.setFree(false);
            p27.setOpeningTime(LocalTime.of(7, 0));
            p27.setClosingTime(LocalTime.of(22, 0));
            p27.setTotalSpots(100);
            p27.setAvailableSpots(60);
            p27.setGeom(gf.createPoint(new Coordinate(9.1970, 45.4590)));
            p27.setCovered(true);
            p27.setGuarded(true);
            p27.setHasVideoSurveillance(true);
            repository.save(p27);

            Parking p28 = new Parking();
            p28.setName("Parcheggio Stazione Lambrate");
            p28.setAddress("Piazza Bottini, Milano");
            p28.setCategory("paid");
            p28.setParkingType("Strisce Blu");
            p28.setHourlyRate(new BigDecimal("1.00"));
            p28.setFree(false);
            p28.setOpeningTime(LocalTime.of(8, 0));
            p28.setClosingTime(LocalTime.of(20, 0));
            p28.setTotalSpots(60);
            p28.setAvailableSpots(40);
            p28.setGeom(gf.createPoint(new Coordinate(9.2370, 45.4845)));
            p28.setHasParcometro(true);
            repository.save(p28);

            Parking p29 = new Parking();
            p29.setName("Garage Corso Vercelli");
            p29.setAddress("Corso Vercelli 50, Milano");
            p29.setCategory("underground");
            p29.setParkingType("Garage Sotterraneo");
            p29.setHourlyRate(new BigDecimal("3.20"));
            p29.setFree(false);
            p29.setValid24h(true);
            p29.setTotalSpots(180);
            p29.setAvailableSpots(100);
            p29.setGeom(gf.createPoint(new Coordinate(9.1680, 45.4660)));
            p29.setHasEvCharging(true);
            p29.setHasDisabledAccess(true);
            p29.setCovered(true);
            p29.setGuarded(true);
            p29.setHasVideoSurveillance(true);
            repository.save(p29);

            Parking p30 = new Parking();
            p30.setName("Parcheggio Piazzale Loreto");
            p30.setAddress("Piazzale Loreto, Milano");
            p30.setCategory("paid");
            p30.setParkingType("Strisce Blu");
            p30.setHourlyRate(new BigDecimal("1.50"));
            p30.setFree(false);
            p30.setOpeningTime(LocalTime.of(8, 0));
            p30.setClosingTime(LocalTime.of(21, 0));
            p30.setTotalSpots(50);
            p30.setAvailableSpots(23);
            p30.setGeom(gf.createPoint(new Coordinate(9.2160, 45.4870)));
            p30.setHasParcometro(true);
            repository.save(p30);

            Parking p31 = new Parking();
            p31.setName("Parcheggio Pero Expo");
            p31.setAddress("Viale Europa, Pero");
            p31.setCategory("free");
            p31.setParkingType("Strisce Bianche");
            p31.setFree(true);
            p31.setTotalSpots(200);
            p31.setAvailableSpots(150);
            p31.setGeom(gf.createPoint(new Coordinate(9.0800, 45.5100)));
            repository.save(p31);

            Parking p32 = new Parking();
            p32.setName("Park & Ride Abbiategrasso");
            p32.setAddress("Via Lorenteggio, Milano");
            p32.setCategory("free");
            p32.setParkingType("Strisce Bianche");
            p32.setFree(true);
            p32.setTotalSpots(120);
            p32.setAvailableSpots(90);
            p32.setGeom(gf.createPoint(new Coordinate(9.1200, 45.4380)));
            repository.save(p32);

            Parking p33 = new Parking();
            p33.setName("Parcheggio Ospedale San Raffaele");
            p33.setAddress("Via Olgettina, Milano");
            p33.setCategory("paid");
            p33.setParkingType("Strisce Blu");
            p33.setHourlyRate(new BigDecimal("1.00"));
            p33.setFree(false);
            p33.setOpeningTime(LocalTime.of(7, 0));
            p33.setClosingTime(LocalTime.of(22, 0));
            p33.setTotalSpots(150);
            p33.setAvailableSpots(86);
            p33.setGeom(gf.createPoint(new Coordinate(9.2820, 45.4820)));
            p33.setHasParcometro(true);
            p33.setHasVideoSurveillance(true);
            repository.save(p33);

            Parking p34 = new Parking();
            p34.setName("Ricarica EV Naviglio Grande");
            p34.setAddress("Alzaia Naviglio Grande 220, Milano");
            p34.setCategory("ev");
            p34.setParkingType("Ricarica EV");
            p34.setHourlyRate(new BigDecimal("2.00"));
            p34.setFree(false);
            p34.setValid24h(true);
            p34.setTotalSpots(15);
            p34.setAvailableSpots(4);
            p34.setGeom(gf.createPoint(new Coordinate(9.1590, 45.4410)));
            p34.setHasEvCharging(true);
            p34.setCovered(true);
            repository.save(p34);

            Parking p35 = new Parking();
            p35.setName("Parcheggio Disabili Cinisello");
            p35.setAddress("Piazza Gramsci, Cinisello Balsamo");
            p35.setCategory("disabled");
            p35.setParkingType("Disabili");
            p35.setFree(true);
            p35.setValid24h(true);
            p35.setTotalSpots(8);
            p35.setAvailableSpots(5);
            p35.setGeom(gf.createPoint(new Coordinate(9.2180, 45.5580)));
            p35.setHasDisabledAccess(true);
            repository.save(p35);

            Parking p36 = new Parking();
            p36.setName("Parcheggio EV Monza");
            p36.setAddress("Viale Cesare Battisti, Monza");
            p36.setCategory("ev");
            p36.setParkingType("Ricarica EV");
            p36.setHourlyRate(new BigDecimal("2.30"));
            p36.setFree(false);
            p36.setOpeningTime(LocalTime.of(7, 0));
            p36.setClosingTime(LocalTime.of(22, 0));
            p36.setTotalSpots(22);
            p36.setAvailableSpots(11);
            p36.setGeom(gf.createPoint(new Coordinate(9.2830, 45.5730)));
            p36.setHasEvCharging(true);
            p36.setHasDisabledAccess(true);
            repository.save(p36);

            Parking p37 = new Parking();
            p37.setName("Parcheggio Stazione Certosa");
            p37.setAddress("Via Casella, Milano");
            p37.setCategory("paid");
            p37.setParkingType("Strisce Blu");
            p37.setHourlyRate(new BigDecimal("1.20"));
            p37.setFree(false);
            p37.setOpeningTime(LocalTime.of(8, 0));
            p37.setClosingTime(LocalTime.of(20, 0));
            p37.setTotalSpots(45);
            p37.setAvailableSpots(17);
            p37.setGeom(gf.createPoint(new Coordinate(9.1500, 45.5040)));
            p37.setHasParcometro(true);
            repository.save(p37);

            Parking p38 = new Parking();
            p38.setName("Parcheggio Cascina Merlata");
            p38.setAddress("Via Cilea 20, Milano");
            p38.setCategory("free");
            p38.setParkingType("Strisce Bianche");
            p38.setFree(true);
            p38.setTotalSpots(70);
            p38.setAvailableSpots(48);
            p38.setGeom(gf.createPoint(new Coordinate(9.1350, 45.5150)));
            repository.save(p38);

            Parking p39 = new Parking();
            p39.setName("Parcheggio Disabili Duomo");
            p39.setAddress("Via Dogana 2, Milano");
            p39.setCategory("disabled");
            p39.setParkingType("Disabili");
            p39.setFree(true);
            p39.setValid24h(true);
            p39.setTotalSpots(6);
            p39.setAvailableSpots(3);
            p39.setGeom(gf.createPoint(new Coordinate(9.1906, 45.4643)));
            p39.setHasDisabledAccess(true);
            p39.setHasVideoSurveillance(true);
            repository.save(p39);

            Parking p40 = new Parking();
            p40.setName("Parcheggio EV Melegnano");
            p40.setAddress("Via Emilia 48, Melegnano");
            p40.setCategory("ev");
            p40.setParkingType("Ricarica EV");
            p40.setHourlyRate(new BigDecimal("2.10"));
            p40.setFree(false);
            p40.setValid24h(true);
            p40.setTotalSpots(10);
            p40.setAvailableSpots(2);
            p40.setGeom(gf.createPoint(new Coordinate(9.3220, 45.3610)));
            p40.setHasEvCharging(true);
            p40.setHasDisabledAccess(true);
            p40.setCovered(true);
            repository.save(p40);

            System.out.println(">>> SmartParking: 40 parcheggi di test inseriti nel database.");
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
