/**
 * SmartParking Map - Frontend Application
 * Gestisce chiamate API CRUD, mappa Leaflet, filtri, bottom sheet, creazione e cancellazione parcheggi.
 */

const API_BASE = 'http://localhost:8080/api/parkings';

// ============================================
// CONFIGURAZIONE COLORI E ICONE PER CATEGORIA
// ============================================
const CATEGORY_CONFIG = {
    paid:        { color: '#3B82F6', icon: 'fa-credit-card',  label: 'A pagamento',   coverLabel: "All'aperto",  coverIcon: 'fa-cloud-sun' },
    free:        { color: '#10B981', icon: 'fa-check-circle', label: 'Gratuito',       coverLabel: "All'aperto",  coverIcon: 'fa-cloud-sun' },
    underground: { color: '#8B5CF6', icon: 'fa-warehouse',    label: 'Coperto',        coverLabel: 'Sotterraneo', coverIcon: 'fa-building' },
    ev:          { color: '#F97316', icon: 'fa-bolt',         label: 'Ricarica EV',    coverLabel: "All'aperto",  coverIcon: 'fa-cloud-sun' },
    disabled:    { color: '#F97316', icon: 'fa-wheelchair',   label: 'Disabili',       coverLabel: "All'aperto",  coverIcon: 'fa-cloud-sun' },
};

const SERVICES_MAP = {
    ev_charging:          { icon: 'fa-plug-circle-bolt', label: 'Ricarica EV' },
    disabled_access:      { icon: 'fa-wheelchair',       label: 'Disabili' },
    covered:              { icon: 'fa-umbrella',         label: 'Coperto' },
    guarded:              { icon: 'fa-shield-halved',    label: 'Custodito' },
    parcometro:           { icon: 'fa-coins',            label: 'Parcometro' },
    video_surveillance:   { icon: 'fa-video',            label: 'Videosorveglianza' },
};

const STATUS_STYLES = {
    'Libero':    { bg: 'bg-emerald-100 text-emerald-700', icon: 'fa-circle text-[6px]' },
    'Parziale':  { bg: 'bg-orange-100 text-orange-700',   icon: 'fa-circle text-[6px]' },
    'Affollato': { bg: 'bg-red-100 text-red-700',         icon: 'fa-circle text-[6px]' },
};

// ============================================
// STATE
// ============================================
let map;
let markersLayer;
let userMarker = null;
let addModeMarker = null;   // marker temporaneo durante la creazione
let searchMarker = null;    // marker posizionato dalla ricerca
let activeFilter = 'all';
let currentCenter = { lat: 45.4642, lng: 9.1900 };
let parkingList = [];
let isAddMode = false;
let currentDetailParking = null;  // parcheggio attualmente visualizzato nel bottom sheet

// ============================================
// MAP INITIALIZATION
// ============================================
function initMap() {
    map = L.map('map', {
        center: [currentCenter.lat, currentCenter.lng],
        zoom: 15,
        zoomControl: false,
        attributionControl: false,
    });

    L.control.zoom({ position: 'topright' }).addTo(map);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
        subdomains: 'abc',
        maxZoom: 19,
    }).addTo(map);

    markersLayer = L.layerGroup().addTo(map);

    map.on('moveend', () => {
        const c = map.getCenter();
        currentCenter = { lat: c.lat, lng: c.lng };
    });

    // Click sulla mappa: se in modalita add, posiziona il marker
    map.on('click', onMapClick);
}

// ============================================
// API CALLS
// ============================================
async function apiGet(url) {
    try {
        const res = await fetch(url);
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        return await res.json();
    } catch (err) {
        console.error('[API GET] Errore:', err);
        return null;
    }
}

async function apiPost(url, body) {
    try {
        const res = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body),
        });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        return await res.json();
    } catch (err) {
        console.error('[API POST] Errore:', err);
        return null;
    }
}

async function apiDelete(url) {
    try {
        const res = await fetch(url, { method: 'DELETE' });
        return res.ok;
    } catch (err) {
        console.error('[API DELETE] Errore:', err);
        return false;
    }
}

async function checkApiHealth() {
    try {
        const res = await fetch(`${API_BASE}/health`);
        updateApiStatus(res.ok);
    } catch (e) {
        updateApiStatus(false);
    }
}

async function fetchParkingsNearby(lat, lng, radius = 25000) {
    return apiGet(`${API_BASE}/nearby?lat=${lat}&lng=${lng}&radius=${radius}`);
}

async function fetchParkingsFiltered(lat, lng, radius = 25000, type = 'all') {
    return apiGet(`${API_BASE}/filter?lat=${lat}&lng=${lng}&radius=${radius}&type=${type}`);
}

async function fetchParkingById(id) {
    return apiGet(`${API_BASE}/${id}`);
}

async function createParking(data) {
    return apiPost(API_BASE, data);
}

async function deleteParking(id) {
    return apiDelete(`${API_BASE}/${id}`);
}

// ============================================
// MARKER RENDERING
// ============================================
function createMarkerIcon(color, iconClass) {
    return L.divIcon({
        className: '',
        html: `<div class="custom-marker" style="background:${color};"><i class="fas ${iconClass}"></i></div>`,
        iconSize: [32, 32],
        iconAnchor: [16, 16],
        popupAnchor: [0, -20],
    });
}

function renderMarkers(data) {
    markersLayer.clearLayers();
    data.forEach(p => {
        const config = CATEGORY_CONFIG[p.category] || CATEGORY_CONFIG.paid;
        const icon = createMarkerIcon(config.color, config.icon);
        const marker = L.marker([p.lat, p.lng], { icon }).addTo(markersLayer);
        marker.on('click', () => openBottomSheet(p));
        marker._parkingId = p.id;
    });
}

// ============================================
// LOAD DATA
// ============================================
async function loadData(filter = 'all', center = null) {
    activeFilter = filter;
    if (center) currentCenter = center;
    const data = filter === 'all'
        ? await fetchParkingsNearby(currentCenter.lat, currentCenter.lng, 25000)
        : await fetchParkingsFiltered(currentCenter.lat, currentCenter.lng, 25000, filter);

    if (data && data.length > 0) {
        parkingList = data;
        renderMarkers(data);
        updateApiStatus(true);
        console.log(`[API] Caricati ${data.length} parcheggi dal backend.`);
    } else {
        console.warn('[API] Nessuna risposta dal backend. Uso dati fallback.');
        updateApiStatus(false);
        loadFallbackData(filter);
    }
}

function showParkingOnMap(p) {
    if (!p || p.lat == null || p.lng == null) return;
    if (activeFilter === 'all' || p.category === activeFilter) {
        if (!parkingList.some(x => x.id === p.id)) {
            parkingList.push(p);
        }
        renderMarkers(parkingList);
    }
    if (map) map.flyTo([p.lat, p.lng], Math.max(map.getZoom(), 15), { duration: 0.8 });
}

function updateApiStatus(online) {
    const dot = document.getElementById('connDot');
    const label = document.getElementById('connLabel');
    if (!dot || !label) return;
    if (online) {
        dot.classList.add('online');
        dot.classList.remove('offline');
        label.textContent = 'Online';
        label.classList.add('text-emerald-400');
    } else {
        dot.classList.add('offline');
        dot.classList.remove('online');
        label.textContent = 'Offline';
        label.classList.remove('text-emerald-400');
    }
}

// ============================================
// FALLBACK DATA
// ============================================
const FALLBACK_DATA = [
    { id: 1, name: "Parcheggio Piazza Duomo", address: "Piazza del Duomo, Milano", category: "paid", parkingType: "Strisce Blu", hourlyRate: 2.00, isFree: false, openingTime: "08:00", closingTime: "20:00", valid24h: false, totalSpots: 150, availableSpots: 90, lat: 45.4641, lng: 9.1919, hasEvCharging: false, hasDisabledAccess: false, isCovered: false, isGuarded: false, hasParcometro: true, hasVideoSurveillance: true, restrictionNote: "Pulizia strada", restrictionDay: "Lunedi", restrictionStart: "00:00", restrictionEnd: "06:00", status: "Libero" },
    { id: 2, name: "Garage Via Torino Park", address: "Via Torino 15, Milano", category: "underground", parkingType: "Garage Sotterraneo", hourlyRate: 3.50, isFree: false, valid24h: true, totalSpots: 300, availableSpots: 18, lat: 45.4608, lng: 9.1940, hasEvCharging: true, hasDisabledAccess: true, isCovered: true, isGuarded: true, hasParcometro: false, hasVideoSurveillance: true, status: "Affollato" },
    { id: 3, name: "Parcheggio Corso Buenos Aires", address: "Corso Buenos Aires, Milano", category: "free", parkingType: "Strisce Bianche", isFree: true, totalSpots: 50, availableSpots: 28, lat: 45.4781, lng: 9.2080, hasEvCharging: false, hasDisabledAccess: false, isCovered: false, isGuarded: false, hasParcometro: false, hasVideoSurveillance: false, restrictionNote: "Divieto di sosta", restrictionDay: "Sabato", restrictionStart: "14:00", restrictionEnd: "18:00", status: "Libero" },
    { id: 4, name: "Parcheggio EV Stazione Centrale", address: "Piazza Duca d'Aosta, Milano", category: "ev", parkingType: "Ricarica EV", hourlyRate: 2.50, isFree: false, openingTime: "06:00", closingTime: "23:00", valid24h: false, totalSpots: 30, availableSpots: 10, lat: 45.4856, lng: 9.2050, hasEvCharging: true, hasDisabledAccess: true, isCovered: false, isGuarded: false, hasParcometro: false, hasVideoSurveillance: true, restrictionNote: "Max 4h sosta - Dopo 4h sovrapprezzo 50%", status: "Parziale" },
    { id: 5, name: "Parcheggio Riservato Brera", address: "Via Brera 28, Milano", category: "disabled", parkingType: "Disabili", isFree: true, valid24h: true, totalSpots: 12, availableSpots: 8, lat: 45.4722, lng: 9.1875, hasEvCharging: false, hasDisabledAccess: true, isCovered: false, isGuarded: false, hasParcometro: false, hasVideoSurveillance: true, status: "Libero" },
];

let fallbackIdCounter = 100;

function loadFallbackData(filter = 'all') {
    let data = [...FALLBACK_DATA];
    if (filter !== 'all') {
        data = data.filter(p => p.category === filter);
    }
    parkingList = data;
    renderMarkers(data);
}

// ============================================
// FILTER
// ============================================
function toggleFilter(el, filter) {
    document.querySelectorAll('.filter-chip').forEach(c => c.classList.remove('active'));
    el.classList.add('active');
    loadData(filter);
}

// ============================================
// ADD MODE - Click mappa per posizionare parcheggio
// ============================================
function toggleAddMode() {
    isAddMode = !isAddMode;
    const btn = document.getElementById('addModeBtn');
    const addPanel = document.getElementById('addPanel');

    if (isAddMode) {
        btn.classList.add('bg-red-500');
        btn.classList.remove('bg-accent');
        btn.innerHTML = '<i class="fas fa-times text-xl"></i>';
        addPanel.classList.add('show');
        map.getContainer().style.cursor = 'crosshair';
        // Disabilita click sui marker esistenti in modalita add
        markersLayer.eachLayer(l => { l.off('click'); });
    } else {
        btn.classList.remove('bg-red-500');
        btn.classList.add('bg-accent');
        btn.innerHTML = '<i class="fas fa-plus text-xl"></i>';
        addPanel.classList.remove('show');
        map.getContainer().style.cursor = '';
        if (addModeMarker) { map.removeLayer(addModeMarker); addModeMarker = null; }
        // Riabilita click sui marker
        loadData(activeFilter);
    }
}

function onMapClick(e) {
    if (!isAddMode) return;

    if (addModeMarker) map.removeLayer(addModeMarker);

    const icon = L.divIcon({
        className: '',
        html: `<div class="custom-marker" style="background:#EF4444;border-color:white;animation:pulse-add 1s infinite;"><i class="fas fa-plus"></i></div>`,
        iconSize: [32, 32],
        iconAnchor: [16, 16],
    });

    addModeMarker = L.marker(e.latlng, { icon }).addTo(map);

    // Compila i campi nascosti con le coordinate
    document.getElementById('addLat').value = e.latlng.lat.toFixed(6);
    document.getElementById('addLng').value = e.latlng.lng.toFixed(6);
    document.getElementById('addCoordDisplay').textContent = `${e.latlng.lat.toFixed(5)}, ${e.latlng.lng.toFixed(5)}`;
    document.getElementById('addCoordDisplay').classList.remove('text-slate-400');
    document.getElementById('addCoordDisplay').classList.add('text-accent', 'font-semibold');

    // Reverse geocoding: compila automaticamente l'indirizzo
    const addressInput = document.querySelector('#addForm input[name="address"]');
    if (addressInput && !addressInput.value.trim()) {
        reverseGeocode(e.latlng.lat, e.latlng.lng).then(addr => {
            if (addr) addressInput.value = addr;
        });
    }

    // Abilita il bottone salva
    document.getElementById('addSaveBtn').disabled = false;
}

async function handleAddParking() {
    const form = document.getElementById('addForm');
    const formData = new FormData(form);

    const lat = parseFloat(document.getElementById('addLat').value);
    const lng = parseFloat(document.getElementById('addLng').value);

    if (isNaN(lat) || isNaN(lng)) {
        alert('Inserisci un indirizzo valido per posizionare il parcheggio.');
        return;
    }

    const category = formData.get('category');
    const isFree = category === 'free' || category === 'disabled';

    const payload = {
        name: formData.get('name'),
        address: formData.get('address') || '',
        category: category,
        parkingType: formData.get('parkingType'),
        hourlyRate: isFree ? null : parseFloat(formData.get('hourlyRate')) || null,
        isFree: isFree,
        valid24h: formData.get('valid24h') === 'on',
        openingTime: formData.get('openingTime') || null,
        closingTime: formData.get('closingTime') || null,
        totalSpots: parseInt(formData.get('totalSpots')) || 50,
        availableSpots: parseInt(formData.get('totalSpots')) || 50,
        lat: lat,
        lng: lng,
        hasEvCharging: formData.get('hasEvCharging') === 'on',
        hasDisabledAccess: formData.get('hasDisabledAccess') === 'on',
        isCovered: formData.get('isCovered') === 'on',
        isGuarded: formData.get('isGuarded') === 'on',
        hasParcometro: formData.get('hasParcometro') === 'on',
        hasVideoSurveillance: formData.get('hasVideoSurveillance') === 'on',
        restrictionNote: formData.get('restrictionNote') || null,
        restrictionDay: formData.get('restrictionDay') || null,
        restrictionStart: formData.get('restrictionStart') || null,
        restrictionEnd: formData.get('restrictionEnd') || null,
    };

    const saved = await createParking(payload);

    if (saved) {
        showNotification('Parcheggio creato con successo e salvato nel database!', 'success');
        toggleAddMode();
        form.reset();
        document.getElementById('addLat').value = '';
        document.getElementById('addLng').value = '';
        document.getElementById('addCoordDisplay').textContent = 'Inserisci un indirizzo qui sotto';
        document.getElementById('addCoordDisplay').className = 'text-xs text-slate-400';
        document.getElementById('addSaveBtn').disabled = true;
        showParkingOnMap(saved);
        loadData(activeFilter, { lat: saved.lat, lng: saved.lng });
    } else {
        const retry = await createParking(payload);
        if (retry) {
            showNotification('Parcheggio creato con successo e salvato nel database!', 'success');
            toggleAddMode();
            form.reset();
            document.getElementById('addLat').value = '';
            document.getElementById('addLng').value = '';
            document.getElementById('addCoordDisplay').textContent = 'Inserisci un indirizzo qui sotto';
            document.getElementById('addCoordDisplay').className = 'text-xs text-slate-400';
            document.getElementById('addSaveBtn').disabled = true;
            showParkingOnMap(retry);
            loadData(activeFilter, { lat: retry.lat, lng: retry.lng });
            return;
        }
        showNotification('Errore: backend non raggiungibile, il parcheggio NON è stato salvato nel database. Riprova.', 'error');
    }
}

// ============================================
// DELETE PARKING
// ============================================
async function handleDeleteParking(id) {
    if (!confirm('Vuoi davvero eliminare questo parcheggio?')) return;

    const success = await deleteParking(id);

    if (success) {
        showNotification('Parcheggio eliminato.', 'success');
    } else {
        // Elimina dal fallback locale
        const idx = FALLBACK_DATA.findIndex(p => p.id === id);
        if (idx > -1) FALLBACK_DATA.splice(idx, 1);
        showNotification('Eliminato in modalita offline', 'warning');
    }

    closeBottomSheet();
    loadData(activeFilter);
}

// ============================================
// NOTIFICHE
// ============================================
function showNotification(msg, type = 'info') {
    const container = document.getElementById('notificationContainer');
    const colors = {
        success: 'bg-emerald-500',
        warning: 'bg-amber-500',
        error: 'bg-red-500',
        info: 'bg-accent',
    };
    const icons = {
        success: 'fa-check-circle',
        warning: 'fa-exclamation-triangle',
        error: 'fa-times-circle',
        info: 'fa-info-circle',
    };

    const el = document.createElement('div');
    el.className = `flex items-center gap-2 px-4 py-3 rounded-2xl text-white text-sm font-medium shadow-lg ${colors[type]} transform translate-x-full transition-transform duration-300`;
    el.innerHTML = `<i class="fas ${icons[type]}"></i> ${msg}`;
    container.appendChild(el);

    requestAnimationFrame(() => el.classList.remove('translate-x-full'));

    setTimeout(() => {
        el.classList.add('translate-x-full');
        setTimeout(() => el.remove(), 300);
    }, 3000);
}

// ============================================
// BOTTOM SHEET
// ============================================
const sheetEl = () => document.getElementById('bottomSheet');
const overlayEl = () => document.getElementById('sheetOverlay');

function openBottomSheet(p) {
    currentDetailParking = p;
    const config = CATEGORY_CONFIG[p.category] || CATEGORY_CONFIG.paid;
    const statusStyle = STATUS_STYLES[p.status] || STATUS_STYLES['Libero'];

    document.getElementById('sheetTitle').textContent = p.name;
    document.getElementById('sheetSubtitle').textContent = `${p.parkingType} - ${config.label}`;

    const badge = document.getElementById('sheetBadge');
    badge.style.backgroundColor = `${config.color}15`;
    badge.style.color = config.color;
    badge.innerHTML = `<i class="fas ${config.icon}"></i> ${config.label}`;

    const coverBadge = document.getElementById('sheetCoverBadge');
    coverBadge.innerHTML = `<i class="fas ${config.coverIcon}"></i> ${config.coverLabel}`;

    const statusEl = document.getElementById('sheetStatus');
    statusEl.className = `status-badge px-3 py-1 rounded-full text-xs font-semibold flex items-center gap-1 ml-auto ${statusStyle.bg}`;
    statusEl.innerHTML = `<i class="fas ${statusStyle.icon}"></i> ${p.status}`;

    document.getElementById('sheetTariff').textContent = p.isFree ? 'Gratuito' : `${(p.hourlyRate || 0).toFixed(2)} €/h`;
    document.getElementById('sheetHours').textContent = p.valid24h ? '24/7' : (p.openingTime && p.closingTime ? `${p.openingTime} - ${p.closingTime}` : 'N/D');
    document.getElementById('sheetSpots').textContent = `${p.availableSpots || 0} / ${p.totalSpots || 0}`;
    document.getElementById('sheetDistance').textContent = p.address || 'N/D';

    const warningsEl = document.getElementById('sheetWarnings');
    if (p.restrictionNote) {
        let text = p.restrictionNote;
        if (p.restrictionDay) {
            text += `: ${p.restrictionDay}`;
            if (p.restrictionStart && p.restrictionEnd) text += ` ${p.restrictionStart} - ${p.restrictionEnd}`;
        }
        warningsEl.innerHTML = `<div class="bg-amber-50 border border-amber-200 rounded-2xl p-3.5 flex items-start gap-3">
            <div class="w-8 h-8 rounded-full bg-amber-100 flex items-center justify-center flex-shrink-0 mt-0.5"><i class="fas fa-exclamation-triangle text-amber-600 text-xs"></i></div>
            <div><p class="text-sm font-semibold text-amber-800">Avviso</p><p class="text-xs text-amber-600 mt-0.5">${text}</p></div>
        </div>`;
    } else {
        warningsEl.innerHTML = '';
    }

    const servicesEl = document.getElementById('sheetServices');
    const services = [];
    if (p.hasEvCharging) services.push(SERVICES_MAP.ev_charging);
    if (p.hasDisabledAccess) services.push(SERVICES_MAP.disabled_access);
    if (p.isCovered) services.push(SERVICES_MAP.covered);
    if (p.isGuarded) services.push(SERVICES_MAP.guarded);
    if (p.hasParcometro) services.push(SERVICES_MAP.parcometro);
    if (p.hasVideoSurveillance) services.push(SERVICES_MAP.video_surveillance);

    servicesEl.innerHTML = services.length > 0
        ? services.map(s => `<div class="flex items-center gap-1.5 bg-slate-100 px-3 py-1.5 rounded-full"><i class="fas ${s.icon} text-accent text-xs"></i><span class="text-xs font-medium text-slate-600">${s.label}</span></div>`).join('')
        : '<span class="text-xs text-slate-400">Nessun servizio aggiuntivo</span>';

    // Imposta pulsante elimina
    document.getElementById('deleteBtn').onclick = () => handleDeleteParking(p.id);

    // Salva coord per navigazione
    sheetEl().dataset.currentLat = p.lat;
    sheetEl().dataset.currentLng = p.lng;

    sheetEl().classList.add('open');
    overlayEl().classList.add('open');
    map.panTo([p.lat, p.lng], { animate: true });
}

function closeBottomSheet() {
    sheetEl().classList.remove('open');
    overlayEl().classList.remove('open');
    currentDetailParking = null;
}

// ============================================
// BOTTOM SHEET DRAG
// ============================================
function initBottomSheetDrag() {
    const handle = sheetEl().querySelector('.drag-handle');
    let startY = 0;
    let deltaY = 0;

    handle.addEventListener('touchstart', e => {
        startY = e.touches[0].clientY;
        sheetEl().style.transition = 'none';
    }, { passive: true });

    handle.addEventListener('touchmove', e => {
        deltaY = e.touches[0].clientY - startY;
        if (deltaY > 0) sheetEl().style.transform = `translateY(${deltaY}px)`;
    }, { passive: true });

    handle.addEventListener('touchend', () => {
        sheetEl().style.transition = '';
        if (deltaY > 100) {
            closeBottomSheet();
        } else {
            const isDesktop = window.innerWidth >= 768;
            sheetEl().style.transform = isDesktop ? 'translateX(-50%) translateY(0)' : 'translateY(0)';
        }
        deltaY = 0;
    });

    overlayEl().addEventListener('click', closeBottomSheet);
}

// ============================================
// GEOLOCALIZZAZIONE
// ============================================
function centerOnGPS() {
    if (!navigator.geolocation) {
        map.setView([currentCenter.lat, currentCenter.lng], 15, { animate: true });
        return;
    }

    navigator.geolocation.getCurrentPosition(
        pos => {
            const { latitude, longitude } = pos.coords;
            map.setView([latitude, longitude], 16, { animate: true });
            currentCenter = { lat: latitude, lng: longitude };

            if (userMarker) map.removeLayer(userMarker);
            const userIcon = L.divIcon({
                className: '',
                html: `<div style="width:18px;height:18px;background:#2563EB;border:3px solid white;border-radius:50%;box-shadow:0 0 0 6px rgba(37,99,235,0.2), 0 2px 8px rgba(0,0,0,0.3);"></div>`,
                iconSize: [18, 18],
                iconAnchor: [9, 9],
            });
            userMarker = L.marker([latitude, longitude], { icon: userIcon }).addTo(map);
            loadData(activeFilter);
        },
        () => { map.setView([currentCenter.lat, currentCenter.lng], 15, { animate: true }); },
        { enableHighAccuracy: true, timeout: 10000 }
    );
}

// ============================================
// NAVIGAZIONE
// ============================================
function openNavigation() {
    const lat = sheetEl().dataset.currentLat;
    const lng = sheetEl().dataset.currentLng;
    if (lat && lng) window.open(`https://www.google.com/maps/dir/?api=1&destination=${lat},${lng}`, '_blank');
}

// ============================================
// SEARCH
// ============================================
function handleSearch(query) {
    if (!query || query.length < 2) { loadData(activeFilter); return; }
    const q = query.toLowerCase();
    const filtered = parkingList.filter(p =>
        p.name.toLowerCase().includes(q) ||
        (p.address && p.address.toLowerCase().includes(q)) ||
        (p.parkingType && p.parkingType.toLowerCase().includes(q))
    );
    if (filtered.length > 0) {
        renderMarkers(filtered);
    }
    photonGeocode(query).then(result => {
        if (result) {
            map.flyTo([result.lat, result.lng], 16, { duration: 0.8 });
        }
    });
}

// ============================================
// GEOCODING (Nominatim OpenStreetMap - gratuito, senza API key)
// ============================================
const NOMINATIM_BASE = 'https://nominatim.openstreetmap.org/';

function buildDisplayName(f, query) {
    const addr = f.address || {};
    let street = addr.road || addr.pedestrian || addr.square || addr.neighbourhood || f.name || '';
    const civico = addr.house_number || extractCivicoFromQuery(query);
    let label;
    if (street && civico) {
        label = `${street} ${civico}`;
    } else if (street) {
        label = street;
    } else {
        label = f.display_name || f.name || '';
    }
    const city = addr.city || addr.town || addr.village || addr.municipality || '';
    if (city) {
        let c = city;
        if (addr.postcode) c += ` (${addr.postcode})`;
        label += `, ${c}`;
    }
    return label;
}

function civicoAppearsInQuery(civico, query) {
    if (!civico) return false;
    try {
        return new RegExp(`(^|[^\\d])${civico}([^\\d]|$)`).test(String(query || ''));
    } catch (e) {
        return false;
    }
}

function extractCivicoFromQuery(query) {
    if (!query) return null;
    const match = query.match(/\b(\d{1,5}(?:\/[a-zA-Z])?)\b/);
    return match ? match[1] : null;
}

async function photonSearch(query, limit = 5) {
    if (!query || query.length < 2) return [];
    try {
        const viewbox = `${currentCenter.lng - 0.15},${currentCenter.lat + 0.15},${currentCenter.lng + 0.15},${currentCenter.lat - 0.15}`;
        const url = `${NOMINATIM_BASE}search?q=${encodeURIComponent(query)}&format=jsonv2&limit=${limit + 5}&accept-language=it&addressdetails=1&countrycodes=it&viewbox=${viewbox}`;
        const res = await fetch(url);
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const data = await res.json();
        if (!Array.isArray(data)) return [];

        return data
            .map(f => {
                const addr = f.address || {};
                const localCivico = extractCivicoFromQuery(query);
                const hasCivico = Boolean(addr.house_number) || Boolean(localCivico);
                const matchesCivico = civicoAppearsInQuery(addr.house_number || localCivico, query);
                return {
                    lat: parseFloat(f.lat),
                    lng: parseFloat(f.lon),
                    displayName: buildDisplayName(f, query),
                    hasCivico: hasCivico,
                    matchesCivico: matchesCivico,
                    originalQuery: query,
                };
            })
            .sort((a, b) => {
                if (a.matchesCivico !== b.matchesCivico) return a.matchesCivico ? -1 : 1;
                if (a.hasCivico !== b.hasCivico) return a.hasCivico ? -1 : 1;
                return 0;
            })
            .slice(0, limit);
    } catch (e) {
        console.error('[Nominatim] Errore:', e);
        return [];
    }
}

async function photonGeocode(query) {
    if (!query || query.length < 2) return null;
    const results = await photonSearch(query, 1);
    return results.length > 0 ? results[0] : null;
}

async function reverseGeocode(lat, lng) {
    try {
        const url = `${NOMINATIM_BASE}reverse?lat=${lat}&lon=${lng}&format=jsonv2&accept-language=it`;
        const res = await fetch(url);
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const data = await res.json();
        return buildDisplayName(data) || data.display_name || '';
    } catch (e) {
        console.error('[Reverse Geocoding] Errore:', e);
    }
    return null;
}

function showDropdown(dropdown, results, onSelect) {
    dropdown.innerHTML = '';
    if (!results || results.length === 0) { dropdown.style.display = 'none'; return; }
    results.forEach(item => {
        const el = document.createElement('div');
        el.className = 'suggestion-item';
        el.innerHTML = `<i class="fas fa-map-marker-alt text-slate-400 text-xs flex-shrink-0"></i><span>${item.displayName}</span>`;
        el.addEventListener('click', () => onSelect(item));
        dropdown.appendChild(el);
    });
    dropdown.style.display = 'block';
}

function hideDropdown(dropdown) {
    dropdown.innerHTML = '';
    dropdown.style.display = 'none';
}

function placeSearchMarker(lat, lng) {
    if (searchMarker) map.removeLayer(searchMarker);
    const icon = L.divIcon({
        className: '',
        html: `<div class="custom-marker" style="background:#EF4444;border-color:white;"><i class="fas fa-search-location"></i></div>`,
        iconSize: [32, 32],
        iconAnchor: [16, 16],
    });
    searchMarker = L.marker([lat, lng], { icon }).addTo(map);
}

function clearSearchMarker() {
    if (searchMarker) { map.removeLayer(searchMarker); searchMarker = null; }
}

function placeAddMarker(lat, lng) {
    if (addModeMarker) map.removeLayer(addModeMarker);

    const icon = L.divIcon({
        className: '',
        html: `<div class="custom-marker" style="background:#EF4444;border-color:white;animation:pulse-add 1s infinite;"><i class="fas fa-plus"></i></div>`,
        iconSize: [32, 32],
        iconAnchor: [16, 16],
    });

    addModeMarker = L.marker([lat, lng], { icon }).addTo(map);

    document.getElementById('addLat').value = lat.toFixed(6);
    document.getElementById('addLng').value = lng.toFixed(6);
    document.getElementById('addCoordDisplay').textContent = `${lat.toFixed(5)}, ${lng.toFixed(5)}`;
    document.getElementById('addCoordDisplay').classList.remove('text-slate-400');
    document.getElementById('addCoordDisplay').classList.add('text-accent', 'font-semibold');
    document.getElementById('addSaveBtn').disabled = false;
}

function initAddressGeocoding() {
    const addressInput = document.querySelector('#addForm input[name="address"]');
    if (!addressInput) return;

    const wrapper = addressInput.parentElement;
    wrapper.style.position = 'relative';

    const dropdown = document.createElement('div');
    dropdown.id = 'addAddressSuggestions';
    dropdown.className = 'suggestions-dropdown';
    wrapper.appendChild(dropdown);

    let addressTimeout;

    addressInput.addEventListener('input', e => {
        clearTimeout(addressTimeout);
        const val = e.target.value.trim();
        if (val.length < 2) { hideDropdown(dropdown); return; }
        addressTimeout = setTimeout(async () => {
            const results = await photonSearch(val);
            showDropdown(dropdown, results, (item) => {
                addressInput.value = item.displayName;
                hideDropdown(dropdown);
                if (isAddMode) {
                    placeAddMarker(item.lat, item.lng);
                    map.flyTo([item.lat, item.lng], 17, { duration: 0.8 });
                }
            });
        }, 400);
    });

    addressInput.addEventListener('keydown', e => {
        if (e.key === 'Escape') { hideDropdown(dropdown); return; }
        if (e.key === 'Enter') {
            e.preventDefault();
            clearTimeout(addressTimeout);
            hideDropdown(dropdown);
            const val = addressInput.value.trim();
            if (val.length >= 2) {
                photonGeocode(val).then(result => {
                    if (result && isAddMode) {
                        placeAddMarker(result.lat, result.lng);
                        map.flyTo([result.lat, result.lng], 17, { duration: 0.8 });
                    }
                });
            }
        }
    });

    addressInput.addEventListener('focus', () => {
        if (dropdown.children.length > 0) dropdown.style.display = 'block';
    });

    document.addEventListener('click', e => {
        if (!wrapper.contains(e.target)) hideDropdown(dropdown);
    });
}

// ============================================
// INIT
// ============================================
document.addEventListener('DOMContentLoaded', () => {
    initMap();
    initBottomSheetDrag();
    initAddressGeocoding();
    checkApiHealth();
    loadData('all');

    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        const wrapper = searchInput.parentElement;
        wrapper.style.position = 'relative';

        const dropdown = document.createElement('div');
        dropdown.id = 'searchSuggestions';
        dropdown.className = 'suggestions-dropdown';
        dropdown.style.top = '100%';
        dropdown.style.left = '0';
        dropdown.style.right = '0';
        dropdown.style.borderRadius = '0 0 16px 16px';
        dropdown.style.borderTop = 'none';
        wrapper.appendChild(dropdown);

        let searchTimeout;

        searchInput.addEventListener('input', e => {
            clearTimeout(searchTimeout);
            const val = e.target.value.trim();
            if (val.length < 2) { hideDropdown(dropdown); clearSearchMarker(); loadData(activeFilter); return; }
            searchTimeout = setTimeout(async () => {
                const filtered = parkingList.filter(p =>
                    p.name.toLowerCase().includes(val.toLowerCase()) ||
                    (p.address && p.address.toLowerCase().includes(val.toLowerCase()))
                );
                if (filtered.length > 0) renderMarkers(filtered);

                const results = await photonSearch(val);
                showDropdown(dropdown, results, (item) => {
                    searchInput.value = item.displayName;
                    hideDropdown(dropdown);
                    placeSearchMarker(item.lat, item.lng);
                    map.flyTo([item.lat, item.lng], 17, { duration: 0.8 });
                    setTimeout(() => loadData(activeFilter), 900);
                });
            }, 400);
        });

        searchInput.addEventListener('keydown', e => {
            if (e.key === 'Escape') { hideDropdown(dropdown); clearSearchMarker(); return; }
            if (e.key === 'Enter') {
                e.preventDefault();
                clearTimeout(searchTimeout);
                hideDropdown(dropdown);
                const val = searchInput.value.trim();
                if (val.length >= 2) {
                    photonGeocode(val).then(result => {
                        if (result) {
                            placeSearchMarker(result.lat, result.lng);
                            map.flyTo([result.lat, result.lng], 16, { duration: 0.8 });
                            setTimeout(() => loadData(activeFilter), 900);
                        }
                    });
                } else {
                    clearSearchMarker();
                }
            }
        });

        searchInput.addEventListener('focus', () => {
            if (dropdown.children.length > 0) dropdown.style.display = 'block';
        });

        document.addEventListener('click', e => {
            if (!wrapper.contains(e.target)) hideDropdown(dropdown);
        });
    }
});
