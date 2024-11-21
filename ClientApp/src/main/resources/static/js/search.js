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
            <p><strong>Email:</strong> ${doctor.email}</p>
            <p><strong>Specialty:</strong> ${doctor.specialty}</p>
          <button class="time-slot-button">Show Time Slots</button>
          <div class="time-slots"></div>
        `;
    doctorList.appendChild(doctorDiv);
    const timeSlotButton = doctorDiv.querySelector(".time-slot-button");
    timeSlotButton.addEventListener("click",
        () => fetchDoctorTimeSlots(doctor.email, doctorDiv));

    doctorList.appendChild(doctorDiv);
  });
}

function fetchDoctorTimeSlots(email, doctorDiv) {
  // Ensure doctorDiv and timeSlotsDiv are properly referenced
  const timeSlotsDiv = doctorDiv.querySelector(".time-slots");
  if (!timeSlotsDiv) {
    console.error("Time slots placeholder not found in doctorDiv");
    return;
  }

  // Clear existing content in the time slots container
  timeSlotsDiv.innerHTML = "<p>Loading...</p>";

  // Fetch data from the server
  fetch(`/search/doctor/timeSlots?doctorEmail=${encodeURIComponent(email)}`)
  .then(response => {
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return response.json(); // Parse JSON response
  })
  .then(data => {
    // Check if the response indicates success
    if (!data.status) {
      timeSlotsDiv.innerHTML = `<p style="color: red;">Error: ${data.message}</p>`;
      return;
    }

    // Call displayTimeSlots with parsed data
    displayTimeSlots(data.data, doctorDiv);
  })
  .catch(error => {
    console.error("Error fetching time slots:", error);
    timeSlotsDiv.innerHTML = `<p style="color: red;">An error occurred while fetching time slots.</p>`;
  });
}

function displayTimeSlots(timeSlots, doctorDiv) {
  const timeSlotsDiv = doctorDiv.querySelector(".time-slots");
  if (!timeSlotsDiv) {
    console.error("Time slots placeholder not found in doctorDiv");
    return;
  }

  // Clear the loading or previous content
  timeSlotsDiv.innerHTML = "";

  // Handle empty time slots array
  if (timeSlots.length === 0) {
    timeSlotsDiv.innerHTML = "<p>No time slots available.</p>";
    return;
  }

  // Generate and append time slot elements
  timeSlots.forEach(slot => {
    const slotDiv = document.createElement("div");
    slotDiv.className = "time-slot-item";
    slotDiv.innerHTML = `
      <p><strong>Day:</strong> ${slot.day}</p>
      <p><strong>Start Time:</strong> ${slot.startTime}</p>
      <p><strong>End Time:</strong> ${slot.endTime}</p>
      <p><strong>Availability:</strong> ${slot.availability}</p>
    `;
    timeSlotsDiv.appendChild(slotDiv);
  });
}
// Attach event listener to the search button
document.getElementById("searchButton").addEventListener("click",
    searchDoctors);

document.addEventListener("DOMContentLoaded", fetchAllDoctors);
