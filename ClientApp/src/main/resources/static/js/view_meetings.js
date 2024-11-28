let meetings = []; // Store fetched meetings

// Function to get the value of a cookie by name
function getCookie(name) {
  const cookieArr = document.cookie.split(";");
  for (let i = 0; i < cookieArr.length; i++) {
    let cookie = cookieArr[i].trim();
    if (cookie.startsWith(name + "=")) {
      return decodeURIComponent(cookie.substring(name.length + 1));
    }
  }
  return null; // Return null if the cookie is not found
}

// Fetch meetings for the logged-in user
function fetchMyMeetings() {
  console.log("Fetching all meetings...");

  let xhr = new XMLHttpRequest();
  xhr.onreadystatechange = () => {
    if (xhr.readyState !== 4) {
      return;
    }
    if (xhr.status !== 200) {
      alert("Server error: " + xhr.statusText);
      return;
    }

    // Check if the response is in valid JSON format
    try {
      meetings = JSON.parse(xhr.responseText);
      displayMeetings(meetings);
    } catch (error) {
      console.error("Error parsing JSON:", error);
      alert("Failed to parse meetings data.");
    }
  };
  xhr.open("GET", "/view_my_meetings"); // Ensure this matches the controller URL
  xhr.send();
}

// Display the meetings on the page with optional filters
function displayMeetings(meetingsToDisplay) {
  const meetingList = document.getElementById("meetingList");
  meetingList.innerHTML = ""; // Clear existing list

  const organizerName = getCookie("name"); // Get the organizer name from the cookie

  if (meetingsToDisplay.length === 0) {
    meetingList.innerHTML = "<p>No meetings found.</p>";
    return;
  }

  // Generate meeting list
  meetingsToDisplay.forEach(meeting => {
    const meetingDiv = document.createElement("div");
    meetingDiv.className = "meeting-item";
    console.log(meeting);
    console.log(meeting.organizer);

    meetingDiv.innerHTML = `
      <p><strong>Organizer:</strong> ${organizerName || "N/A"}</p>
      <p><strong>Type:</strong> ${capitalizeFirstLetter(meeting.type) || "N/A"}</p>
      <p><strong>Description:</strong> ${meeting.description || "N/A"}</p>
      <p><strong>Start Day:</strong> ${meeting.startDay || "N/A"}</p>
      <p><strong>End Day:</strong> ${meeting.endDay || "N/A"}</p>
      <p><strong>Start Time:</strong> ${meeting.startTime || "N/A"}</p>
      <p><strong>End Time:</strong> ${meeting.endTime || "N/A"}</p>
      <p><strong>Recurrence:</strong> ${capitalizeFirstLetter(meeting.recurrence) || "N/A"}</p>
      <p><strong>Status:</strong> ${meeting.status || "N/A"}</p>
      <button class="delete-button" data-mid="${meeting.mid}">Delete</button>
    `;
    meetingList.appendChild(meetingDiv);
  });

  // Attach event listeners to delete buttons
  attachDeleteButtonListeners();
}

// Capitalize the first letter of a string
function capitalizeFirstLetter(string) {
  if (!string) return "";
  return string.charAt(0).toUpperCase() + string.slice(1);
}

// Apply filters based on user selection
function applyFilters() {
  const type = document.getElementById("typeFilter").value;
  const status = document.getElementById("statusFilter").value;
  const recurrence = document.getElementById("recurrenceFilter").value;

  let filteredMeetings = meetings;

  if (type) {
    filteredMeetings = filteredMeetings.filter(meeting => meeting.type === type);
  }

  if (status) {
    filteredMeetings = filteredMeetings.filter(meeting => meeting.status === status);
  }

  if (recurrence) {
    filteredMeetings = filteredMeetings.filter(meeting => meeting.recurrence === recurrence);
  }

  displayMeetings(filteredMeetings);
}

// Reset filters and display all meetings
function resetFilters() {
  document.getElementById("filterForm").reset();
  displayMeetings(meetings);
}

// Attach event listeners to filter buttons
function attachFilterListeners() {
  document.getElementById("applyFilters").addEventListener("click", applyFilters);
  document.getElementById("resetFilters").addEventListener("click", resetFilters);
}

// Attach event listeners to delete buttons
function attachDeleteButtonListeners() {
  const deleteButtons = document.querySelectorAll(".delete-button");
  deleteButtons.forEach(button => {
    button.addEventListener("click", (event) => {
      const meetingId = event.target.getAttribute("data-mid");
      deleteMeeting(meetingId);
    });
  });
}

// Function to delete a meeting
function deleteMeeting(meetingId) {
  if (!confirm("Are you sure you want to delete this meeting?")) {
    return; // User canceled the deletion
  }

  console.log(`Deleting meeting with ID: ${meetingId}`);

  let xhr = new XMLHttpRequest();
  xhr.onreadystatechange = () => {
    if (xhr.readyState !== 4) {
      return;
    }

    if (xhr.status === 204) { // No Content, successful deletion
      alert("Meeting deleted successfully.");
      // Remove the meeting from the meetings array
      meetings = meetings.filter(meeting => meeting.mid !== parseInt(meetingId));
      // Refresh the meeting list
      displayMeetings(meetings);
    } else if (xhr.status === 401) {
      alert("Unauthorized: Please log in again.");
      window.location.href = "/login_form"; // Redirect to login
    } else if (xhr.status === 403) {
      alert("Forbidden: You are not authorized to delete this meeting.");
    } else if (xhr.status === 404) {
      alert("Meeting not found.");
    } else {
      console.error("Error deleting meeting:", xhr.statusText);
      alert("Failed to delete the meeting. Please try again.");
    }
  };

  xhr.open("DELETE", `/delete_meeting?mid=${meetingId}`, true); // Call the app's backend endpoint
  xhr.send();
}

// Attach event listener to fetch meetings when the page loads
document.addEventListener("DOMContentLoaded", () => {
  fetchMyMeetings();
  attachFilterListeners();
});
