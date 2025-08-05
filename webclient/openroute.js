// Initialize the map
const map = L.map('map').setView([40.7812, -73.9665], 13);

// Add OpenStreetMap tiles
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
}).addTo(map);

// Global variables for markers and routes
let startMarker = null;
let endMarker = null;
let poiMarkers = [];
let routeLine = null;

// DOM Elements
const routeForm = document.getElementById('routeForm');
const poiForm = document.getElementById('poiForm');
const resultsDiv = document.getElementById('results');
const mockModeCheckbox = document.getElementById('mockMode');

// Event Listeners
routeForm.addEventListener('submit', calculateRoute);
poiForm.addEventListener('submit', searchPOIs);

// Calculate Route Function
function calculateRoute(e) {
    e.preventDefault();
    
    const start = document.getElementById('start').value;
    const end = document.getElementById('end').value;
    const mock = mockModeCheckbox.checked;
    
    if (!start || !end) {
        showResult('Please enter both start and end locations', 'error');
        return;
    }
    
    // Clear previous results
    clearMap();
    
    // Show loading
    showResult('Calculating route...', 'info');
    
    // Call the routeserver API
    fetch(`http://localhost:8090/calculateRoute?start=${encodeURIComponent(start)}&end=${encodeURIComponent(end)}&mock=${mock}`)
        .then(response => response.json())
        .then(data => {
            displayRoute(data);
        })
        .catch(error => {
            console.error('Error:', error);
            showResult('Error calculating route: ' + error.message, 'error');
        });
}

// Search POIs Function
function searchPOIs(e) {
    e.preventDefault();
    
    const lat = document.getElementById('poiLat').value;
    const lon = document.getElementById('poiLon').value;
    const limit = document.getElementById('poiLimit').value;
    
    // Clear previous POI markers
    clearPoiMarkers();
    
    // Show loading
    showResult('Searching for POIs...', 'info');
    
    // Call the routeserver API
    fetch(`http://localhost:8090/getPoi?lat=${lat}&lon=${lon}&limit=${limit}`)
        .then(response => response.json())
        .then(data => {
            displayPOIs(data);
        })
        .catch(error => {
            console.error('Error:', error);
            showResult('Error searching POIs: ' + error.message, 'error');
        });
}

// Display Route on Map
function displayRoute(routeData) {
    // Clear previous results
    clearMap();
    
    // Display route info in sidebar
    let routeInfo = `
        <div class="route-info">
            <h3>Route from ${routeData.start} to ${routeData.end}</h3>
            <p><strong>Distance:</strong> ${routeData.distance}</p>
            <p><strong>Duration:</strong> ${routeData.duration}</p>
            <p><strong>Source:</strong> ${routeData.source}</p>
    `;
    
    if (routeData.route && Array.isArray(routeData.route)) {
        routeInfo += `<p><strong>Route:</strong></p><ul>`;
        routeData.route.forEach(point => {
            routeInfo += `<li>${point}</li>`;
        });
        routeInfo += `</ul>`;
    }
    
    routeInfo += `</div>`;
    showResult(routeInfo, 'success');
    
    // Add markers for start and end (mock coordinates for demo)
    const startCoords = [40.7580, -73.9855]; // Times Square
    const endCoords = [40.7484, -73.9857]; // Empire State Building
    
    startMarker = L.marker(startCoords).addTo(map)
        .bindPopup(`<b>Start: ${routeData.start}</b><br>Distance: ${routeData.distance}<br>Duration: ${routeData.duration}`)
        .openPopup();
    
    endMarker = L.marker(endCoords).addTo(map)
        .bindPopup(`<b>End: ${routeData.end}</b>`);
    
    // Draw route line (mock for demo)
    const routePoints = [
        startCoords,
        [40.7530, -73.9832],
        endCoords
    ];
    
    routeLine = L.polyline(routePoints, {color: 'blue'}).addTo(map);
    
    // Fit map to route bounds
    map.fitBounds(routeLine.getBounds());
}

// Display POIs on Map
function displayPOIs(poiData) {
    // Clear previous POI markers
    clearPoiMarkers();
    
    // Display POI info in sidebar
    let poiInfo = `<div class="poi-info"><h3>Points of Interest</h3>`;
    
    if (Array.isArray(poiData) && poiData.length > 0) {
        poiData.forEach((poi, index) => {
            // Add marker to map
            const marker = L.marker([poi.lat, poi.lon]).addTo(map)
                .bindPopup(`<b>${poi.name}</b><br>Type: ${poi.type}`);
            
            poiMarkers.push(marker);
            
            // Add to sidebar info
            poiInfo += `
                <div style="margin-bottom: 10px; padding: 5px; border-left: 3px solid #3498db;">
                    <h4>${poi.name}</h4>
                    <p>Type: ${poi.type}</p>
                    <p>Coordinates: ${poi.lat}, ${poi.lon}</p>
                </div>
            `;
        });
    } else {
        poiInfo += `<p>No POIs found</p>`;
    }
    
    poiInfo += `</div>`;
    showResult(poiInfo, 'success');
    
    // Fit map to POI bounds if we have markers
    if (poiMarkers.length > 0) {
        const group = new L.featureGroup(poiMarkers);
        map.fitBounds(group.getBounds().pad(0.1));
    }
}

// Helper Functions
function showResult(content, type) {
    resultsDiv.innerHTML = content;
    
    // Add styling based on type
    resultsDiv.className = '';
    if (type === 'error') {
        resultsDiv.style.borderLeft = '4px solid #e74c3c';
        resultsDiv.style.backgroundColor = '#fdf2f2';
    } else if (type === 'success') {
        resultsDiv.style.borderLeft = '4px solid #27ae60';
        resultsDiv.style.backgroundColor = '#f2f9f2';
    } else {
        resultsDiv.style.borderLeft = '4px solid #3498db';
        resultsDiv.style.backgroundColor = '#f2f8fb';
    }
}

function clearMap() {
    if (startMarker) {
        map.removeLayer(startMarker);
        startMarker = null;
    }
    if (endMarker) {
        map.removeLayer(endMarker);
        endMarker = null;
    }
    if (routeLine) {
        map.removeLayer(routeLine);
        routeLine = null;
    }
    clearPoiMarkers();
}

function clearPoiMarkers() {
    poiMarkers.forEach(marker => {
        map.removeLayer(marker);
    });
    poiMarkers = [];
}

// Add a scale control to the map
L.control.scale({imperial: true, metric: true}).addTo(map);

// Add a simple geocoder control (mock for demo)
const geocoder = L.Control.extend({
    onAdd: function(map) {
        const div = L.DomUtil.create('div', 'leaflet-bar leaflet-control leaflet-control-custom');
        div.innerHTML = '<button style="background:white; border:0; width:30px; height:30px; line-height:30px;">🔍</button>';
        div.onclick = function() {
            const location = prompt('Enter location to search:');
            if (location) {
                // Mock geocoding - in a real app you would call a geocoding API
                alert(`Searching for: ${location}\n(Mock implementation - would center map on location)`);
            }
        };
        return div;
    }
});

new geocoder({position: 'topleft'}).addTo(map);
