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
let bikeMarker = null;
let animationInterval = null;
let routeCoordinates = [];

// DOM Elements
const routeForm = document.getElementById('routeForm');
const poiForm = document.getElementById('poiForm');
const resultsDiv = document.getElementById('results');
const animationControls = document.getElementById('animationControls');
const goButton = document.getElementById('goButton');

// Event Listeners
routeForm.addEventListener('submit', calculateRoute);
poiForm.addEventListener('submit', searchPOIs);
goButton.addEventListener('click', animateBike);

// Calculate Route Function
function calculateRoute(e) {
    e.preventDefault();
    
    const start = document.getElementById('start').value;
    const end = document.getElementById('end').value;
    
    if (!start || !end) {
        showResult('Please enter both start and end locations', 'error');
        return;
    }
    
    // Clear previous results
    clearMap();
    
    // Show loading
    showResult('Calculating route...', 'info');
    
    // Call the routeserver API with mock=false for real calculation
    fetch(`http://localhost:8090/calculateRoute?start=${encodeURIComponent(start)}&end=${encodeURIComponent(end)}&mock=false`)
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
    
    // Parse coordinates from the response for map markers
    try {
        const startCoordsData = JSON.parse(routeData.startCoordinatesResponse);
        const endCoordsData = JSON.parse(routeData.endCoordinatesResponse);
        
        if (startCoordsData.length > 0 && endCoordsData.length > 0) {
            const startCoords = [parseFloat(startCoordsData[0].lat), parseFloat(startCoordsData[0].lon)];
            const endCoords = [parseFloat(endCoordsData[0].lat), parseFloat(endCoordsData[0].lon)];
            
            startMarker = L.marker(startCoords).addTo(map)
                .bindPopup(`<b>Start: ${routeData.start}</b><br>Distance: ${routeData.distance}<br>Duration: ${routeData.duration}`)
                .openPopup();
            
            endMarker = L.marker(endCoords).addTo(map)
                .bindPopup(`<b>End: ${routeData.end}</b>`);
            
            // Create route coordinates for animation
            routeCoordinates = [
                startCoords,
                [startCoords[0] + (endCoords[0] - startCoords[0]) * 0.25, startCoords[1] + (endCoords[1] - startCoords[1]) * 0.25],
                [startCoords[0] + (endCoords[0] - startCoords[0]) * 0.5, startCoords[1] + (endCoords[1] - startCoords[1]) * 0.5],
                [startCoords[0] + (endCoords[0] - startCoords[0]) * 0.75, startCoords[1] + (endCoords[1] - startCoords[1]) * 0.75],
                endCoords
            ];
            
            routeLine = L.polyline(routeCoordinates, {color: 'blue'}).addTo(map);
            
            // Fit map to route bounds
            map.fitBounds(routeLine.getBounds());
            
            // Show animation controls
            animationControls.style.display = 'block';
        }
    } catch (e) {
        console.error('Error parsing coordinates:', e);
        // Fallback to default coordinates if parsing fails
        const startCoords = [40.7580, -73.9855]; // Times Square
        const endCoords = [40.7484, -73.9857]; // Empire State Building
        
        startMarker = L.marker(startCoords).addTo(map)
            .bindPopup(`<b>Start: ${routeData.start}</b><br>Distance: ${routeData.distance}<br>Duration: ${routeData.duration}`)
            .openPopup();
        
        endMarker = L.marker(endCoords).addTo(map)
            .bindPopup(`<b>End: ${routeData.end}</b>`);
        
        // Create route coordinates for animation
        routeCoordinates = [
            startCoords,
            [40.7550, -73.9840],
            [40.7520, -73.9835],
            [40.7500, -73.9845],
            endCoords
        ];
        
        routeLine = L.polyline(routeCoordinates, {color: 'blue'}).addTo(map);
        
        // Fit map to route bounds
        map.fitBounds(routeLine.getBounds());
        
        // Show animation controls
        animationControls.style.display = 'block';
    }
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

// Animate Bike Along Route
function animateBike() {
    if (!routeCoordinates || routeCoordinates.length === 0) {
        showResult('No route available for animation', 'error');
        return;
    }
    
    // Disable button during animation
    goButton.disabled = true;
    goButton.textContent = 'Riding...';
    
    // Clear any existing animation
    if (animationInterval) {
        clearInterval(animationInterval);
    }
    
    // Remove existing bike marker if present
    if (bikeMarker) {
        map.removeLayer(bikeMarker);
    }
    
    // Create bike marker using emoji
    bikeMarker = L.marker(routeCoordinates[0], {
        icon: L.divIcon({
            className: 'bike-icon',
            html: '🚲',
            iconSize: [24, 24]
        })
    }).addTo(map);
    
    let currentIndex = 0;
    const totalPoints = routeCoordinates.length;
    const stepsPerSegment = 20;
    let currentSegment = 0;
    let currentStep = 0;
    
    // Start animation
    animationInterval = setInterval(() => {
        if (currentSegment < totalPoints - 1) {
            const start = routeCoordinates[currentSegment];
            const end = routeCoordinates[currentSegment + 1];
            
            // Calculate position along current segment
            const progress = currentStep / stepsPerSegment;
            const lat = start[0] + (end[0] - start[0]) * progress;
            const lng = start[1] + (end[1] - start[1]) * progress;
            
            // Update bike position
            bikeMarker.setLatLng([lat, lng]);
            
            // Pan map to follow bike
            map.panTo([lat, lng]);
            
            currentStep++;
            
            if (currentStep > stepsPerSegment) {
                currentSegment++;
                currentStep = 0;
            }
        } else {
            // Animation complete
            clearInterval(animationInterval);
            goButton.disabled = false;
            goButton.textContent = 'Go!';
        }
    }, 100);
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
    if (bikeMarker) {
        map.removeLayer(bikeMarker);
        bikeMarker = null;
    }
    clearPoiMarkers();
    
    // Hide animation controls
    animationControls.style.display = 'none';
    
    // Clear animation interval
    if (animationInterval) {
        clearInterval(animationInterval);
        animationInterval = null;
    }
    
    // Reset route coordinates
    routeCoordinates = [];
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
