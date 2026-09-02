-- ============================================
-- SmartParking Map - PostgreSQL + PostGIS Schema
-- ============================================

-- Abilita l'estensione PostGIS
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS postgis_topology;

-- Sequenza per l'ID
CREATE SEQUENCE IF NOT EXISTS parkings_id_seq START WITH 1 INCREMENT BY 1;

-- Tabella principale parkings
CREATE TABLE IF NOT EXISTS parkings (
    id              BIGINT PRIMARY KEY DEFAULT nextval('parkings_id_seq'),
    name            VARCHAR(255)    NOT NULL,
    address         VARCHAR(500),
    description     TEXT,
    category        VARCHAR(50)     NOT NULL,           -- paid, free, underground, ev, disabled
    parking_type    VARCHAR(100)    NOT NULL,           -- Strisce Blu, Strisce Bianche, Garage Sotterraneo, Ricarica EV, Disabili

    -- Tariffe e orari
    hourly_rate     DECIMAL(6,2),                       -- tariffa oraria in EUR (NULL = gratuito)
    is_free         BOOLEAN         DEFAULT FALSE,
    opening_time    TIME,                               -- orario apertura (NULL = 24h)
    closing_time    TIME,                               -- orario chiusura (NULL = 24h)
    valid_24h       BOOLEAN         DEFAULT FALSE,      -- aperto 24/7

    -- Capacita e stato
    total_spots     INTEGER         DEFAULT 0,
    available_spots INTEGER         DEFAULT 0,

    -- Geolocalizzazione (PostGIS)
    geom            GEOMETRY(Point, 4326)    NOT NULL,

    -- Servizi e vincoli
    has_ev_charging     BOOLEAN     DEFAULT FALSE,
    has_disabled_access BOOLEAN     DEFAULT FALSE,
    is_covered          BOOLEAN     DEFAULT FALSE,
    is_guarded          BOOLEAN     DEFAULT FALSE,
    has_parcometro      BOOLEAN     DEFAULT FALSE,
    has_video_surveillance BOOLEAN  DEFAULT FALSE,

    -- Avvisi e restrizioni
    restriction_note    TEXT,                           -- es. "Pulizia strada: Lunedì 00:00 - 06:00"
    restriction_day     VARCHAR(20),                    -- Lunedi, Martedi, ...
    restriction_start   TIME,
    restriction_end     TIME,

    -- Timestamps
    created_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);

-- Indice spaziale GIST per ricerche geospaziali efficienti
CREATE INDEX IF NOT EXISTS idx_parkings_geom ON parkings USING GIST (geom);

-- Indice sulla categoria per filtri
CREATE INDEX IF NOT EXISTS idx_parkings_category ON parkings (category);

-- Indice sui posti disponibili
CREATE INDEX IF NOT EXISTS idx_parkings_available ON parkings (available_spots);

-- ============================================
-- Funzione per aggiornare lo stato dei posti
-- (simulazione: variazione casuale per demo)
-- ============================================
CREATE OR REPLACE FUNCTION update_available_spots()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_parkings
    BEFORE UPDATE ON parkings
    FOR EACH ROW
    EXECUTE FUNCTION update_available_spots();
