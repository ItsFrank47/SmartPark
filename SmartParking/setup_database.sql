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
-- SEED: 5 Parcheggi di Test su Milano
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
