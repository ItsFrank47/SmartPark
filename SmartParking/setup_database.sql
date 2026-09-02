-- ============================================
-- SmartParking Map - Database Setup Completo
-- ============================================
-- Esegui questo script con:
--   psql -U postgres -f setup_database.sql
-- ============================================

-- Crea il database (se non esiste)
SELECT 'CREATE DATABASE smartparking'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'smartparking')\gexec

-- Connettiti al database smartparking
\c smartparking

-- Abilita le estensioni PostGIS
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS postgis_topology;

-- Sequenza per l'ID
CREATE SEQUENCE IF NOT EXISTS parkings_id_seq START WITH 1 INCREMENT BY 1;

-- Tabella principale parkings
CREATE TABLE IF NOT EXISTS parkings (
    id                      BIGINT PRIMARY KEY DEFAULT nextval('parkings_id_seq'),
    name                    VARCHAR(255)    NOT NULL,
    address                 VARCHAR(500),
    description             TEXT,
    category                VARCHAR(50)     NOT NULL,
    parking_type            VARCHAR(100)    NOT NULL,

    hourly_rate             DECIMAL(6,2),
    is_free                 BOOLEAN         DEFAULT FALSE,
    opening_time            TIME,
    closing_time            TIME,
    valid_24h               BOOLEAN         DEFAULT FALSE,

    total_spots             INTEGER         DEFAULT 0,
    available_spots         INTEGER         DEFAULT 0,

    geom                    GEOMETRY(Point, 4326) NOT NULL,

    has_ev_charging         BOOLEAN         DEFAULT FALSE,
    has_disabled_access     BOOLEAN         DEFAULT FALSE,
    is_covered              BOOLEAN         DEFAULT FALSE,
    is_guarded              BOOLEAN         DEFAULT FALSE,
    has_parcometro          BOOLEAN         DEFAULT FALSE,
    has_video_surveillance  BOOLEAN         DEFAULT FALSE,

    restriction_note        TEXT,
    restriction_day         VARCHAR(20),
    restriction_start       TIME,
    restriction_end         TIME,

    created_at              TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);

-- Indici
CREATE INDEX IF NOT EXISTS idx_parkings_geom ON parkings USING GIST (geom);
CREATE INDEX IF NOT EXISTS idx_parkings_category ON parkings (category);
CREATE INDEX IF NOT EXISTS idx_parkings_available ON parkings (available_spots);

-- ============================================
-- SEED: 40 Parcheggi di Test su Milano e dintorni
-- ============================================
INSERT INTO parkings (name, address, category, parking_type, hourly_rate, is_free, opening_time, closing_time, valid_24h, total_spots, available_spots, geom, has_ev_charging, has_disabled_access, is_covered, is_guarded, has_parcometro, has_video_surveillance, restriction_note, restriction_day, restriction_start, restriction_end)
VALUES
-- 1. Strisce Blu - Piazza Duomo
('Parcheggio Piazza Duomo', 'Piazza del Duomo, Milano', 'paid', 'Strisce Blu',
 2.00, false, '08:00', '20:00', false,
 150, 90,
 ST_SetSRID(ST_MakePoint(9.1919, 45.4641), 4326),
 false, false, false, false, true, true,
 'Pulizia strada', 'Lunedi', '00:00', '06:00'),

-- 2. Garage Sotterraneo - Via Torino
('Garage Via Torino Park', 'Via Torino 15, Milano', 'underground', 'Garage Sotterraneo',
 3.50, false, NULL, NULL, true,
 300, 18,
 ST_SetSRID(ST_MakePoint(9.1940, 45.4608), 4326),
 true, true, true, true, false, true,
 NULL, NULL, NULL, NULL),

-- 3. Strisce Bianche - Corso Buenos Aires
('Parcheggio Corso Buenos Aires', 'Corso Buenos Aires, Milano', 'free', 'Strisce Bianche',
 NULL, true, NULL, NULL, false,
 50, 28,
 ST_SetSRID(ST_MakePoint(9.2080, 45.4781), 4326),
 false, false, false, false, false, false,
 'Divieto di sosta', 'Sabato', '14:00', '18:00'),

-- 4. EV Station - Stazione Centrale
('Parcheggio EV Stazione Centrale', 'Piazza Duca d''Aosta, Milano', 'ev', 'Ricarica EV',
 2.50, false, '06:00', '23:00', false,
 30, 10,
 ST_SetSRID(ST_MakePoint(9.2050, 45.4856), 4326),
 true, true, false, false, false, true,
 'Max 4h sosta - Dopo 4h sovrapprezzo 50%', NULL, NULL, NULL),

-- 5. Disabili - Brera
('Parcheggio Riservato Brera', 'Via Brera 28, Milano', 'disabled', 'Disabili',
 NULL, true, NULL, NULL, true,
 12, 8,
 ST_SetSRID(ST_MakePoint(9.1875, 45.4722), 4326),
 false, true, false, false, false, true,
 NULL, NULL, NULL, NULL),

-- 6. Strisce Blu - Porta Garibaldi
('Parcheggio Porta Garibaldi', 'Via Valtellina 12, Milano', 'paid', 'Strisce Blu',
 1.50, false, '08:00', '21:00', false,
 40, 15,
 ST_SetSRID(ST_MakePoint(9.1870, 45.4840), 4326),
 false, false, false, false, true, true,
 NULL, NULL, NULL, NULL),

-- 7. Strisce Blu - Navigli
('Parcheggio Darsena Navigli', 'Alzaia Naviglio Grande, Milano', 'paid', 'Strisce Blu',
 1.20, false, '08:00', '19:00', false,
 60, 25,
 ST_SetSRID(ST_MakePoint(9.1690, 45.4515), 4326),
 false, false, false, false, false, true,
 NULL, NULL, NULL, NULL),

-- 8. Strisce Blu - Arco della Pace
('Parcheggio Arco della Pace', 'Corso Sempione 30, Milano', 'paid', 'Strisce Blu',
 1.50, false, '08:00', '20:00', false,
 35, 12,
 ST_SetSRID(ST_MakePoint(9.1740, 45.4755), 4326),
 false, false, false, false, true, false,
 NULL, NULL, NULL, NULL),

-- 9. Strisce Blu - San Babila
('Parcheggio San Babila', 'Corso Venezia 4, Milano', 'paid', 'Strisce Blu',
 2.00, false, '08:00', '20:00', false,
 45, 5,
 ST_SetSRID(ST_MakePoint(9.1965, 45.4660), 4326),
 false, false, false, false, true, true,
 NULL, NULL, NULL, NULL),

-- 10. Strisce Blu - Sant'Ambrogio
('Parcheggio Sant''Ambrogio', 'Via Carducci, Milano', 'paid', 'Strisce Blu',
 1.50, false, '08:00', '19:00', false,
 30, 20,
 ST_SetSRID(ST_MakePoint(9.1760, 45.4635), 4326),
 false, false, false, false, false, false,
 NULL, NULL, NULL, NULL),

-- 11. Strisce Blu - Porta Romana
('Parcheggio Porta Romana', 'Corso di Porta Romana 120, Milano', 'paid', 'Strisce Blu',
 1.30, false, '08:00', '20:00', false,
 55, 33,
 ST_SetSRID(ST_MakePoint(9.2000, 45.4535), 4326),
 false, false, false, false, true, false,
 NULL, NULL, NULL, NULL),

-- 12. Strisce Bianche - Parco Sempione
('Parcheggio Parco Sempione', 'Viale Alemagna, Milano', 'free', 'Strisce Bianche',
 NULL, true, NULL, NULL, false,
 80, 60,
 ST_SetSRID(ST_MakePoint(9.1790, 45.4725), 4326),
 false, false, false, false, false, false,
 NULL, NULL, NULL, NULL),

-- 13. Strisce Bianche - Darsena Sud
('Parcheggio Darsena Sud', 'Via Gorini, Milano', 'free', 'Strisce Bianche',
 NULL, true, NULL, NULL, false,
 25, 10,
 ST_SetSRID(ST_MakePoint(9.1690, 45.4515), 4326),
 false, false, false, false, false, false,
 NULL, NULL, NULL, NULL),

-- 14. Strisce Bianche - Isola
('Parcheggio Isola', 'Via Borsieri 16, Milano', 'free', 'Strisce Bianche',
 NULL, true, NULL, NULL, false,
 25, 22,
 ST_SetSRID(ST_MakePoint(9.1850, 45.4890), 4326),
 false, false, false, false, false, false,
 NULL, NULL, NULL, NULL),

-- 15. Strisce Bianche - Cascina Gobba
('Parcheggio Cascina Gobba', 'Via Giacosa, Milano', 'free', 'Strisce Bianche',
 NULL, true, NULL, NULL, false,
 90, 70,
 ST_SetSRID(ST_MakePoint(9.2470, 45.4880), 4326),
 false, false, false, false, false, false,
 'Divieto di sosta', 'Sabato', '14:00', '18:00'),

-- 16. Garage Sotterraneo - CityLife
('Garage CityLife Tre Torri', 'Via Federico Tesio, Milano', 'underground', 'Garage Sotterraneo',
 4.00, false, NULL, NULL, true,
 400, 240,
 ST_SetSRID(ST_MakePoint(9.1560, 45.4780), 4326),
 true, true, true, true, false, true,
 NULL, NULL, NULL, NULL),

-- 17. Garage Sotterraneo - Corso Como
('Garage Corso Como', 'Via Guglielmo Pepe 30, Milano', 'underground', 'Garage Sotterraneo',
 3.00, false, NULL, NULL, true,
 150, 90,
 ST_SetSRID(ST_MakePoint(9.1860, 45.4845), 4326),
 false, false, true, true, false, true,
 NULL, NULL, NULL, NULL),

-- 18. Garage Sotterraneo - Lambrate
('Autorimessa Lambrate', 'Via Rombon 48, Milano', 'underground', 'Garage Sotterraneo',
 2.50, false, '06:00', '23:00', false,
 120, 45,
 ST_SetSRID(ST_MakePoint(9.2360, 45.4850), 4326),
 false, true, true, false, false, false,
 NULL, NULL, NULL, NULL),

-- 19. Garage Sotterraneo - Bicocca
('Parcheggio Università Bicocca', 'Viale dell''Innovazione, Milano', 'underground', 'Garage Sotterraneo',
 2.00, false, '07:00', '22:00', false,
 250, 130,
 ST_SetSRID(ST_MakePoint(9.2110, 45.5140), 4326),
 true, false, true, false, false, false,
 NULL, NULL, NULL, NULL),

-- 20. Ricarica EV - Assago Milanofiori
('Park & Charge Milanofiori', 'Via Giuseppe Di Vittorio, Assago', 'ev', 'Ricarica EV',
 3.00, false, '06:00', '23:00', false,
 40, 18,
 ST_SetSRID(ST_MakePoint(9.1470, 45.4030), 4326),
 true, false, true, true, false, true,
 NULL, NULL, NULL, NULL),

-- 21. Ricarica EV - San Donato Milanese
('Parcheggio EV Metanopoli', 'Via Mario Idiomi, San Donato Milanese', 'ev', 'Ricarica EV',
 2.20, false, NULL, NULL, true,
 25, 9,
 ST_SetSRID(ST_MakePoint(9.2630, 45.4170), 4326),
 true, true, false, false, false, true,
 NULL, NULL, NULL, NULL),

-- 22. Ricarica EV - Rho
('Rho Green EV Park', 'Via Europa, Rho', 'ev', 'Ricarica EV',
 2.00, false, '07:00', '21:00', false,
 20, 6,
 ST_SetSRID(ST_MakePoint(9.0400, 45.5300), 4326),
 true, false, true, false, false, false,
 NULL, NULL, NULL, NULL),

-- 23. Disabili - Melegnano
('Parcheggio Disabili Melegnano', 'Via Cavour 3, Melegnano', 'disabled', 'Disabili',
 NULL, true, NULL, NULL, true,
 8, 4,
 ST_SetSRID(ST_MakePoint(9.3240, 45.3590), 4326),
 false, true, false, false, false, true,
 NULL, NULL, NULL, NULL),

-- 24. Disabili - Monza
('Parcheggio Disabili Monza', 'Viale della Stazione, Monza', 'disabled', 'Disabili',
 NULL, true, NULL, NULL, true,
 10, 7,
 ST_SetSRID(ST_MakePoint(9.2740, 45.5840), 4326),
 false, true, true, false, false, false,
 NULL, NULL, NULL, NULL),

-- 25. Disabili - Sesto San Giovanni
('Parcheggio Disabili Sesto', 'Piazza della Resistenza, Sesto San Giovanni', 'disabled', 'Disabili',
 NULL, true, NULL, NULL, true,
 6, 1,
 ST_SetSRID(ST_MakePoint(9.2390, 45.5200), 4326),
 false, true, false, false, false, true,
 NULL, NULL, NULL, NULL),

-- 26. Strisce Bianche - Naviglio Pavese
('Parcheggio Naviglio Pavese', 'Via Ascanio Sforza 90, Milano', 'free', 'Strisce Bianche',
 NULL, true, NULL, NULL, false,
 40, 18,
 ST_SetSRID(ST_MakePoint(9.1640, 45.4450), 4326),
 false, false, false, false, false, false,
 NULL, NULL, NULL, NULL),

-- 27. Garage Sotterraneo - Corso Italia
('Autorimessa Corso Italia', 'Corso Italia 15, Milano', 'underground', 'Garage Sotterraneo',
 2.80, false, '07:00', '22:00', false,
 100, 60,
 ST_SetSRID(ST_MakePoint(9.1970, 45.4590), 4326),
 false, false, true, true, false, true,
 NULL, NULL, NULL, NULL),

-- 28. Strisce Blu - Stazione Lambrate
('Parcheggio Stazione Lambrate', 'Piazza Bottini, Milano', 'paid', 'Strisce Blu',
 1.00, false, '08:00', '20:00', false,
 60, 40,
 ST_SetSRID(ST_MakePoint(9.2370, 45.4845), 4326),
 false, false, false, false, true, false,
 NULL, NULL, NULL, NULL),

-- 29. Garage Sotterraneo - Corso Vercelli
('Garage Corso Vercelli', 'Corso Vercelli 50, Milano', 'underground', 'Garage Sotterraneo',
 3.20, false, NULL, NULL, true,
 180, 100,
 ST_SetSRID(ST_MakePoint(9.1680, 45.4660), 4326),
 true, true, true, true, false, true,
 NULL, NULL, NULL, NULL),

-- 30. Strisce Blu - Piazzale Loreto
('Parcheggio Piazzale Loreto', 'Piazzale Loreto, Milano', 'paid', 'Strisce Blu',
 1.50, false, '08:00', '21:00', false,
 50, 23,
 ST_SetSRID(ST_MakePoint(9.2160, 45.4870), 4326),
 false, false, false, false, true, false,
 NULL, NULL, NULL, NULL),

-- 31. Strisce Bianche - Pero Expo
('Parcheggio Pero Expo', 'Viale Europa, Pero', 'free', 'Strisce Bianche',
 NULL, true, NULL, NULL, false,
 200, 150,
 ST_SetSRID(ST_MakePoint(9.0800, 45.5100), 4326),
 false, false, false, false, false, false,
 NULL, NULL, NULL, NULL),

-- 32. Strisce Bianche - Abbiategrasso (Milano sud)
('Park & Ride Abbiategrasso', 'Via Lorenteggio, Milano', 'free', 'Strisce Bianche',
 NULL, true, NULL, NULL, false,
 120, 90,
 ST_SetSRID(ST_MakePoint(9.1200, 45.4380), 4326),
 false, false, false, false, false, false,
 NULL, NULL, NULL, NULL),

-- 33. Strisce Blu - Ospedale San Raffaele
('Parcheggio Ospedale San Raffaele', 'Via Olgettina, Milano', 'paid', 'Strisce Blu',
 1.00, false, '07:00', '22:00', false,
 150, 86,
 ST_SetSRID(ST_MakePoint(9.2820, 45.4820), 4326),
 false, false, false, false, true, true,
 NULL, NULL, NULL, NULL),

-- 34. Ricarica EV - Naviglio Grande
('Ricarica EV Naviglio Grande', 'Alzaia Naviglio Grande 220, Milano', 'ev', 'Ricarica EV',
 2.00, false, NULL, NULL, true,
 15, 4,
 ST_SetSRID(ST_MakePoint(9.1590, 45.4410), 4326),
 true, false, true, false, false, false,
 NULL, NULL, NULL, NULL),

-- 35. Disabili - Cinisello Balsamo
('Parcheggio Disabili Cinisello', 'Piazza Gramsci, Cinisello Balsamo', 'disabled', 'Disabili',
 NULL, true, NULL, NULL, true,
 8, 5,
 ST_SetSRID(ST_MakePoint(9.2180, 45.5580), 4326),
 false, true, false, false, false, false,
 NULL, NULL, NULL, NULL),

-- 36. Ricarica EV - Monza
('Parcheggio EV Monza', 'Viale Cesare Battisti, Monza', 'ev', 'Ricarica EV',
 2.30, false, '07:00', '22:00', false,
 22, 11,
 ST_SetSRID(ST_MakePoint(9.2830, 45.5730), 4326),
 true, true, false, false, false, false,
 NULL, NULL, NULL, NULL),

-- 37. Strisce Blu - Stazione Certosa
('Parcheggio Stazione Certosa', 'Via Casella, Milano', 'paid', 'Strisce Blu',
 1.20, false, '08:00', '20:00', false,
 45, 17,
 ST_SetSRID(ST_MakePoint(9.1500, 45.5040), 4326),
 false, false, false, false, true, false,
 NULL, NULL, NULL, NULL),

-- 38. Strisce Bianche - Cascina Merlata
('Parcheggio Cascina Merlata', 'Via Cilea 20, Milano', 'free', 'Strisce Bianche',
 NULL, true, NULL, NULL, false,
 70, 48,
 ST_SetSRID(ST_MakePoint(9.1350, 45.5150), 4326),
 false, false, false, false, false, false,
 NULL, NULL, NULL, NULL),

-- 39. Disabili - Duomo
('Parcheggio Disabili Duomo', 'Via Dogana 2, Milano', 'disabled', 'Disabili',
 NULL, true, NULL, NULL, true,
 6, 3,
 ST_SetSRID(ST_MakePoint(9.1906, 45.4643), 4326),
 false, true, false, false, false, true,
 NULL, NULL, NULL, NULL),

-- 40. Ricarica EV - Melegnano
('Parcheggio EV Melegnano', 'Via Emilia 48, Melegnano', 'ev', 'Ricarica EV',
 2.10, false, NULL, NULL, true,
 10, 2,
 ST_SetSRID(ST_MakePoint(9.3220, 45.3610), 4326),
 true, true, true, false, false, false,
 NULL, NULL, NULL, NULL);

-- Trigger per aggiornamento timestamp
CREATE OR REPLACE FUNCTION update_available_spots()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trigger_update_parkings ON parkings;
CREATE TRIGGER trigger_update_parkings
    BEFORE UPDATE ON parkings
    FOR EACH ROW
    EXECUTE FUNCTION update_available_spots();

-- Verifica
SELECT id, name, category, parking_type, available_spots, total_spots FROM parkings;
