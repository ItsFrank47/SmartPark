# 🅿️ SmartParking Map

Applicazione web full-stack per la gestione e visualizzazione interattiva dei parcheggi su una mappa. Consente di cercare, filtrare, aggiungere ed eliminare parcheggi, mostrando per ciascuno informazioni reali (tariffe, orari, posti disponibili, servizi) e la geolocalizzazione precisa su mappa OpenStreetMap.

La città di riferimento per i dati di esempio è **Milano** (con estensione a Rho, Sesto San Giovanni, Monza, Melegnano e dintorni).

---

## 🎯 Obiettivo del progetto

SmartParking nasce come dimostrazione di una **piattaforma "smart city"** per i parcheggi. Il progetto combina un **frontend interattivo basato su mappa** con un **backend REST + database geospaziale PostGIS**, permettendo di:

- ricercare un parcheggio per **indirizzo, via o numero civico** grazie al geocoding;
- filtrarne i risultati per **categoria** (gratuiti, sotterranei, ricarica EV, disabili);
- **creare ed eliminare** parcheggi direttamente dalla mappa (CRUD), con salvataggio istantaneo nel database e marker subito visibile;
- consultare per ciascun parcheggio **tariffe, orari, posti e servizi**.

---

## 🏗️ Architettura

Il progetto è un'applicazione **full-stack** in un'unica cartella:

```
SmartParking/
├── index.html                  # Frontend SPA vanilla (HTML)
├── app.js                      # Logica frontend (JavaScript)
├── styles.css                  # Stili CSS personalizzati
├── pom.xml                     # Build Maven backend
├── setup_database.sql          # Script SQL autonomo (schema + trigger + seed 40)
└── src/main/
    ├── java/com/smartparking/
    │   ├── SmartParkingApplication.java   # Main Spring Boot + seed a DB vuoto
    │   ├── config/CorsConfig.java         # Configurazione CORS
    │   ├── controller/ParkingController.java  # REST API
    │   ├── dto/ParkingDTO.java            # DTO di risposta
    │   ├── dto/CreateParkingRequest.java  # DTO di richiesta
    │   ├── model/Parking.java             # Entity JPA (Point JTS / PostGIS)
    │   └── repository/ParkingRepository.java  # Query spaziali native
    └── resources/
        ├── application.properties         # Configurazione (porta 5433)
        └── schema.sql                     # DDL tabella + indici (all'avvio)
```

Il **frontend** è una SPA "zero-build" (HTML + CSS + JavaScript vanilla) servita come file statico, mentre il **backend Spring Boot** espone le API REST e gestisce il database.

---

## 🧩 Tecnologie utilizzate

### Backend
| Tecnologia | Ruolo |
|-----------|-------|
| **Java 17** | Linguaggio |
| **Spring Boot 3.2.5** | Framework applicativo |
| **Spring Data JPA / Hibernate** | ORM e persistenza |
| **Spring Validation** | Validazione input |
| **PostgreSQL 15 + PostGIS 3.4** | Database con estensione geospaziale |
| **Hibernate Spatial 6.4.4** | Supporto geometrie PostGIS |
| **JTS (Java Topology Suite) 1.19** | Gestione punti/geometrie (Point SRID 4326) |
| **Maven** | Build / dependency management |

### Frontend
| Tecnologia | Ruolo |
|-----------|-------|
| **HTML5 / CSS3** | Struttura e stile |
| **JavaScript vanilla** | Logica (nessun framework) |
| **Tailwind CSS** (CDN) | Utility-first styling |
| **Leaflet 1.9.4** | Mappa interattiva |
| **Nominatim (OpenStreetMap)** | Geocoding / reverse geocoding (gratuito, senza API key) |
| **Font Awesome 6.5.1** | Icone |

### Infrastruttura
| Tecnologia | Ruolo |
|-----------|-------|
| **Docker + Docker Desktop** | Esecuzione localizzata del database e del backend |

---

## 🗄️ Il modello dei dati

La tabella `parkings` contiene **26 colonne**. Le principali:

| Campo | Tipo | Descrizione |
|-------|------|-------------|
| `id` | BIGINT / sequence | Identificativo |
| `name` | VARCHAR | Nome del parcheggio |
| `address` | VARCHAR | Indirizzo |
| `category` | VARCHAR | `paid / free / underground / ev / disabled` |
| `parkingType` | VARCHAR | Strisce Blu, Garage, ecc. |
| `hourlyRate` | DECIMAL(6,2) | Tariffa oraria (NULL = gratuito) |
| `isFree` | BOOLEAN | Gratuito |
| `openingTime` / `closingTime` | TIME | Orari (NULL = 24h) |
| `valid24h` | BOOLEAN | Aperto 24/7 |
| `totalSpots` / `availableSpots` | INTEGER | Posti totali / disponibili |
| `geom` | GEOMETRY(Point,4326) | Posizione geografica (PostGIS) |
| `hasEvCharging`, `hasDisabledAccess`, `isCovered`, `isGuarded`, `hasParcometro`, `hasVideoSurveillance` | BOOLEAN | Servizi |
| `restrictionNote`, `restrictionDay`, `restrictionStart`, `restrictionEnd` | — | Restrizioni |
| `createdAt` / `updatedAt` | TIMESTAMP | Audit |

Sulla tabella sono definiti un **indice GIST** su `geom`, un indice su `category` e uno su `available_spots`. Un **trigger PL/pgSQL** (`update_available_spots`) aggiorna automaticamente `updated_at` a ogni modifica: è definito in `setup_database.sql`, mentre `schema.sql` (eseguito all'avvio) crea solo DDL e indici.

Il **campo derivato `status`** viene calcolato dal backend a ogni richiesta, in base al rapporto `availableSpots/totalSpots` salvato nel database (valori statici, non real-time):

- **Libero** → più del 30% dei posti liberi (verde)
- **Parziale** → tra 0% e 30% (arancione)
- **Affollato** → 0% posti liberi (rosso)

---

## 🔌 API REST

Base URL: `http://localhost:8080/api/parkings`

| Metodo | Endpoint | Descrizione |
|--------|----------|-------------|
| GET | `/health` | Stato backend + numero parcheggi |
| GET | `/nearby?lat&lng&radius` | Parcheggi nel raggio (metri), ordinati per distanza |
| GET | `/filter?lat&lng&radius&type` | Parcheggi filtrati per categoria |
| GET | `/all` | Tutti i parcheggi, per posti disponibili |
| GET | `/{id}` | Singolo parcheggio |
| POST | `/` | Crea un parcheggio |
| DELETE | `/{id}` | Elimina un parcheggio |

Le query spaziali (native) usano **ST_DWithin** e **ST_Distance** su `CAST(geom AS geography)`. Il cast è espresso con la sintassi `CAST(... AS geography)`: il classico `geom::geography` non è utilizzabile nelle query native Hibernate perché il doppio `:` verrebbe interpretato come parametro named e causerebbe un `SQLState 42601` (syntax error) su tutti gli endpoint geospaziali.

Il frontend effettua le ricerche con un **raggio di default di 25 km** (Milano e dintorni), ordinando i risultati per distanza dal centro.

---

## ⚙️ Funzionalità principali

1. **Mappa interattiva** — Leaflet + OpenStreetMap con marker colorati per categoria.
2. **Ricerca & geocoding** — ricerca locale tra i parcheggi e geocoding globale via Nominatim, con autocompletamento e **supporto numero civico** (es. *"via emilia 48 melegnano"* → "Via Emilia 48, Melegnano").
3. **Reverse geocoding** — cliccando sulla mappa in modalità inserimento, l'indirizzo viene compilato automaticamente.
4. **Filtri per categoria** — Tutti / Gratuiti / Sotterranei / Ricarica EV / Disabili (query spaziali sul backend).
5. **CRUD parcheggi** — creazione (form + marker) ed eliminazione; dopo il salvataggio il nuovo parcheggio viene **subito mostrato e la mappa si centra su di esso** (`showParkingOnMap`).
6. **Bottom sheet di dettaglio** — tariffa, orari, posti, indirizzo, servizi, restrizioni e pulsante "Naviga" (apre Google Maps Navigation).
7. **Badge di stato e connessione** — badge Libero/Parziale/Affollato calcolato dai dati salvati e indicatore Online/Offline del backend.
8. **Notifiche toast** — conferme di successo/errore.
9. **Salvataggio nel database** — ogni parcheggio aggiunto viene persistito via API in PostgreSQL/PostGIS (se il backend non è raggiungibile viene mostrato un errore e **non** si salva nulla in locale).
10. **Retry e gestione errori** — chiamate API con retry e ricaduta solo in caso di backend realmente non disponibile (mai in caso di risposta 500).
11. **Design mobile-first** — responsive, ottimizzato per smartphone (header fisso, barra filtri, FAB, bottom sheet).

---

## 🚀 Come avviare il progetto

### Opzione A — Docker (consigliata)

Prerequisito: **Docker Desktop** in esecuzione.

**1. Avviare il database PostGIS** (container `smartparking-postgres`, porta host **5433**):

```bash
docker run -d --name smartparking-postgres --restart always \
  -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=smartparking \
  -p 5433:5432 \
  -v smartparking_pgdata:/var/lib/postgresql/data \
  postgis/postgis:15-3.4
```

**2. (Facoltativo) Seed dei 40 parcheggi di esempio** (o usando il seed automatico del backend, vedi sotto):

```bash
# PowerShell (Windows):
cmd /c "docker exec -i smartparking-postgres psql -U postgres -d smartparking < setup_database.sql"

# Bash / Linux:
docker exec -i smartparking-postgres psql -U postgres -d smartparking < setup_database.sql
```

Se invece si parte da un database già inizializzato dai test precedenti, per resettare e ricaricare i 40 parcheggi puliti:

```bash
docker exec smartparking-postgres psql -U postgres -d smartparking -c "TRUNCATE parkings RESTART IDENTITY;"
cmd /c "docker exec -i smartparking-postgres psql -U postgres -d smartparking < setup_database.sql"
```

**3. Avviare il backend** (container `smartparking-backend`, porta **8080**). Monta la cartella del progetto in `/app` e compila/esegue con Maven; la cache delle dipendenze è nel volume `m2cache`:

```bash
# PowerShell (Windows):
docker run -d --name smartparking-backend --restart unless-stopped -p 8080:8080 \
  -v "${PWD}":/app -v m2cache:/root/.m2 -w /app \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5433/smartparking \
  -e SPRING_DATASOURCE_USERNAME=postgres -e SPRING_DATASOURCE_PASSWORD=postgres \
  maven:3.9-eclipse-temurin-17 mvn spring-boot:run

# Bash / Linux: stessa env ma con "$PWD"
```

> ⚠️ `host.docker.internal` permette al container di raggiungere Postgres sulla porta 5433 della macchina host. Per l'esecuzione locale usare invece `jdbc:postgresql://localhost:5433/smartparking` (valore già presente in `application.properties`).

Riavvio / log / stop:

```bash
docker restart smartparking-backend
docker logs -f smartparking-backend
docker stop smartparking-backend smartparking-postgres
```

**4. Aprire il frontend** — aprire `index.html` nel browser (es. con Live Server). Se il backend è raggiungibile su `http://localhost:8080`, i dati vengono caricati dalle API; altrimenti l'app passa in modalità offline con un set minimale di prova.

### Opzione B — Avvio locale (senza Docker)

Prerequisiti: **Java 17+**, **Maven 3.8+**, **PostgreSQL 15 con PostGIS 3.x**.

1. Create/avviate il database `smartparking` ed eseguite lo script di setup:
   ```bash
   psql -U postgres -f setup_database.sql
   ```
   in alternativa, lo schema viene creato automaticamente all'avvio da `schema.sql` (`spring.sql.init.mode=always`).
2. Avviate il backend:
   ```bash
   mvn spring-boot:run
   ```
   Alla prima esecuzione, se la tabella è vuota, vengono inseriti automaticamente i **40 parcheggi di esempio**.
3. Aprite `index.html` nel browser.

> ⚠️ Credenziali di default in `application.properties`: `postgres / postgres` sulla porta **5433** (per Postgres locale, spostare il DB sulla porta 5432 o cambiare porta nel file).

---

## 🏙️ Dati di esempio (seed)

40 parcheggi su Milano e dintorni, in 5 categorie. Il seed viene inserito in due modi equivalenti:

- **Tramite backend**: `SmartParkingApplication` esegue il seed solo a **database vuoto** (`repository.count() == 0`).
- **Tramite SQL**: `setup_database.sql` (schema + trigger + INSERT, con `\gexec` per creare il database se assente).

### Milano
| Parcheggio | Zona | Categoria |
|-----------|------|-----------|
| Piazza Duomo | Centro | A pagamento (Strisce Blu) |
| Via Torino | Centro | Sotterraneo (Garage) |
| Corso Buenos Aires | Loreto | Gratuito (Strisce Bianche) |
| Stazione Centrale | Centrale | Ricarica EV |
| Brera | Brera | Disabili |
| Porta Garibaldi | Garibaldi | A pagamento (Strisce Blu) |
| Darsena Navigli | Navigli | A pagamento (Strisce Blu) |
| Arco della Pace | Sempione | A pagamento (Strisce Blu) |
| San Babila | Centro | A pagamento (Strisce Blu) |
| Sant'Ambrogio | Magenta | A pagamento (Strisce Blu) |
| Porta Romana | Porta Romana | A pagamento (Strisce Blu) |
| Piazzale Loreto | Loreto | A pagamento (Strisce Blu) |
| Stazione Lambrate | Lambrate | A pagamento (Strisce Blu) |
| Stazione Certosa | Certosa | A pagamento (Strisce Blu) |
| Ospedale San Raffaele | Olgettina | A pagamento (Strisce Blu) |
| Parco Sempione | Sempione | Gratuito (Strisce Bianche) |
| Darsena Sud | Navigli | Gratuito (Strisce Bianche) |
| Isola | Isola | Gratuito (Strisce Bianche) |
| Cascina Gobba | Lambrate | Gratuito (Strisce Bianche) |
| Naviglio Pavese | Navigli | Gratuito (Strisce Bianche) |
| Park & Ride Abbiategrasso | Milano sud | Gratuito (Strisce Bianche) |
| Cascina Merlata | Merlata | Gratuito (Strisce Bianche) |
| CityLife Tre Torri | CityLife | Sotterraneo (Garage) |
| Corso Como | Garibaldi | Sotterraneo (Garage) |
| Lambrate | Lambrate | Sotterraneo (Garage) |
| Università Bicocca | Bicocca | Sotterraneo (Garage) |
| Corso Italia | Centro | Sotterraneo (Garage) |
| Corso Vercelli | Vercelli | Sotterraneo (Garage) |
| Disabili Duomo | Centro | Disabili |
| Naviglio Grande | Navigli | Ricarica EV |

### Dintorni
| Parcheggio | Comune | Categoria |
|-----------|--------|-----------|
| Milanofiori | Assago | Ricarica EV |
| Metanopoli | San Donato Milanese | Ricarica EV |
| Rho Green EV Park | Rho | Ricarica EV |
| Pero Expo | Pero | Gratuito |
| Melegnano Centro | Melegnano | Disabili |
| EV Melegnano | Melegnano | Ricarica EV |
| Stazione Monza | Monza | Disabili |
| EV Monza | Monza | Ricarica EV |
| Piazza della Resistenza | Sesto San Giovanni | Disabili |
| Cinisello Balsamo | Cinisello Balsamo | Disabili |

---

## 🧪 Verifica rapida

```bash
# Stato backend + conteggio
curl http://localhost:8080/api/parkings/health

# Parcheggi entro 25 km dal centro Milano (Duomo)
curl "http://localhost:8080/api/parkings/nearby?lat=45.4642&lng=9.1900&radius=25000"
```

Il DB dovrebbe rispondere `online` con `totalParkings = 40` e la query `nearby` deve restituire 40 parcheggi ordinati per distanza.

---

## 🛠️ Note di sviluppo e bug noti

- **Query native e PostGIS**: non usare mai `::geography` nelle `@Query` native: Hibernate lo interpreta come parametro named (`:geography`) → `SQLState 42601`. Usare `CAST(expr AS geography)`. Le query spaziali sono in `ParkingRepository.java`.
- **Entity senza Hibernate Spatial `@Type`**: in Hibernate 6 `org.hibernate.spatial.JtsGeometryType` non esiste più; la geometria Point è mappata direttamente con JTS + `PostgreSQLDialect`.
- **`schema.sql` senza trigger**: il file viene parsato da Spring ScriptUtils, che non gestisce blocchi PL/pgSQL con `$$`; la funzione/trigger `updated_at` vive solo in `setup_database.sql`.
- **Porta DB 5433**: scelta per evitare conflitto con altri progetti locali su 5432.

---

## 🔮 Sviluppi futuri

- **Occupazione in tempo reale** — aggiornamento dinamico dei posti disponibili (badge Libero/Parziale/Affollato live), ad esempio tramite **sensori IoT**. È già presente uno scheletro `@Scheduled` in `SmartParkingApplication`.
- Pagine di amministrazione e autenticazione utenti.
- Preferiti e notifiche push.
- Espansione del geocoding ad altri paesi.
- Aggiornamento dei parcheggi (modifica dei dati esistenti) dal frontend.