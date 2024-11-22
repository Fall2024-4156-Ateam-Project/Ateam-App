document.addEventListener("DOMContentLoaded", () => {
  // Check if the request is coming from home.html (via cookie) or directly with query parameters
  const urlParams = new URLSearchParams(window.location.search);
  const doctorEmailFromUrl = urlParams.get("doctorEmail");

  console.log(urlParams);
  console.log(doctorEmailFromUrl);

  // If the email is passed in the URL, use it
  if (doctorEmailFromUrl) {
      console.log(`Fetching time slots for doctor from URL: ${doctorEmailFromUrl}`);
      fetchDoctorTimeSlots(doctorEmailFromUrl, 'search');
  } else {
      // Otherwise, try to get it from the cookie (which is the case for navigation from home.html)
      const doctorEmailFromCookie = getCookie("email");

      if (doctorEmailFromCookie) {
          console.log(`Fetching time slots for doctor from Cookie: ${doctorEmailFromCookie}`);
          fetchDoctorTimeSlots(doctorEmailFromCookie, 'home');
      } else {
          alert("Doctor email not found. Please make sure the doctor is logged in or the email is set in cookies.");
      }
  }
});

function fetchDoctorTimeSlots(doctorEmail, source) {
  console.log(`Fetching time slots for doctor: ${doctorEmail}`);

  // Make an AJAX request to fetch and display time slots
  const url = `/search/doctor/timeSlots?doctorEmail=${encodeURIComponent(doctorEmail)}`;
  
  let xhr = new XMLHttpRequest();
  xhr.onreadystatechange = () => {
      if (xhr.readyState !== 4) return;

      if (xhr.status === 200) {
          // Parse the server response (assuming it's JSON with time slot data)
          const timeSlots = JSON.parse(xhr.responseText);
          const timeSlotsContainer = document.getElementById("timeSlotsContainer");
          timeSlotsContainer.innerHTML = ""; // Clear previous content

          if (timeSlots.length > 0) {
              timeSlots.forEach((slot, index) => {
                  const slotElement = document.createElement("div");
                  slotElement.classList.add("time-slot");

                  // Insert slot information
                  slotElement.innerHTML = `
                    <p><strong>Day:</strong> ${slot.day}</p>
                    <p><strong>Start Time:</strong> ${slot.startTime}</p>
                    <p><strong>End Time:</strong> ${slot.endTime}</p>
                    <p><strong>Availability:</strong> ${slot.availability}</p>
                    <hr>
                  `;

                  // Create and add the button (Update or Register)
                  const button = document.createElement("button");
                  button.classList.add("time-slot-button");
                  button.textContent = (source === 'home') ? "Update" : "Register";
                  
                  // Handle button actions
                  button.addEventListener("click", () => {
                      console.log(`Button clicked for slot ${index}: ${button.textContent}`);
                      // Handle button actions like registering or updating
                  });

                  slotElement.appendChild(button); // Append button to slot
                  timeSlotsContainer.appendChild(slotElement); // Append slot to container
              });
          } else {
              document.getElementById("noSlotsMessage").style.display = "block";
          }
      } else {
          console.error(`Failed to fetch time slots: ${xhr.status}`);
          document.getElementById("errorMessage").style.display = "block";
      }
  };
  
  xhr.open("GET", url, true); // Send a GET request to the server
  xhr.send();
}

// Function to retrieve cookie value by name
function getCookie(name) {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(';').shift();
  return null;
}
