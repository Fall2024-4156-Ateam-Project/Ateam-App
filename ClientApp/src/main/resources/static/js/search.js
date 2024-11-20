let doctors = []; // Store fetched doctors

function fetchAllDoctors() {
    console.log("Fetching all doctors...");
    
    let xhr = new XMLHttpRequest();
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== 4) {
            return;
        }
        if (xhr.status !== 200) {
            alert("Server error: " + xhr.statusText);
            return;
        }
        
        // Parse and display fetched doctors
        doctors = JSON.parse(xhr.responseText);
        displayDoctors(doctors);
    };
    xhr.open("GET", "/all_doctors");
    xhr.send();
}

function searchDoctors() {
    const query = document.getElementById("searchInput").value.trim();
    const searchType = document.getElementById("searchType").value;

    console.log(`Searching for doctors by ${searchType}: ${query}`);
    
    let xhr = new XMLHttpRequest();
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== 4) {
            return;
        }
        if (xhr.status !== 200) {
            alert("Server error: " + xhr.statusText);
            return;
        }
        
        // Parse and display search results
        const searchResults = JSON.parse(xhr.responseText);
        displayDoctors(searchResults);
    };

    // Determine the appropriate endpoint based on search type
    if (query) {
        const endpoint = searchType === "name" 
            ? `/search/name?name=${encodeURIComponent(query)}`
            : `/search/specialty?specialty=${encodeURIComponent(query)}`;
        xhr.open("GET", endpoint);
    } else {
        xhr.open("GET", "/all_doctors");
    }

    xhr.send();
}

function displayDoctors(doctors) {
    const doctorList = document.getElementById("doctorList");
    doctorList.innerHTML = ""; // Clear existing list

    if (doctors.length === 0) {
        doctorList.innerHTML = "<p>No doctors found.</p>";
        return;
    }

    // Generate doctor list
    doctors.forEach(doctor => {
        const doctorDiv = document.createElement("div");
        doctorDiv.className = "doctor-item";
        doctorDiv.innerHTML = `
            <p><strong>Name:</strong> ${doctor.name}</p>
            <p><strong>Specialty:</strong> ${doctor.specialty}</p>
        `;
        doctorList.appendChild(doctorDiv);
    });
}

// Attach event listener to the search button
document.getElementById("searchButton").addEventListener("click", searchDoctors);

document.addEventListener("DOMContentLoaded", fetchAllDoctors);
