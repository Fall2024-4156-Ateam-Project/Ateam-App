let meetings = []; // Store fetched meetings

// Get cookie value by name
function getCookie(name) {
  const cookieArr = document.cookie.split(";");
  for (let i = 0; i < cookieArr.length; i++) {
    let cookie = cookieArr[i].trim();
    if (cookie.startsWith(name + "=")) {
      return decodeURIComponent(cookie.substring(name.length + 1));
    }
  }
  return null; // Return null if cookie is not found
}

// Fetch and display all meetings with roles
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

    // Check if response is valid JSON
    try {
      meetings = JSON.parse(xhr.responseText);
      displayMeetings(meetings);
    } catch (error) {
      console.error("Error parsing JSON:", error);
      alert("Failed to parse meetings data.");
    }
  };
  xhr.open("GET", "/view_my_meetings"); // Ensure this URL matches the backend endpoint
  xhr.send();
}

// Display meeting list with role
function displayMeetings(meetingsToDisplay) {
  const meetingList = document.getElementById("meetingList");
  meetingList.innerHTML = ""; // Clear existing list

  const organizerName = getCookie("name"); // Get organizer's name from cookie

  if (meetingsToDisplay.length === 0) {
    meetingList.innerHTML = "<p>No meetings found.</p>";
    return;
  }

  // Generate the meeting list
  meetingsToDisplay.forEach(meetingRoleMap => {
    const meeting = meetingRoleMap.meeting; // Extract meeting object
    const role = meetingRoleMap.role; // Extract role

    const meetingDiv = document.createElement("div");
    meetingDiv.className = "meeting-item";
    console.log(meeting);
    console.log(meeting.organizer);

    // Basic meeting information
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
      <p><strong>Role:</strong> ${role || "N/A"}</p>
    `;

    // Participant information (if available)
    if (role !== "participant" && meeting.participants && meeting.participants.length > 0) {
      let participantsHTML = '<p><strong>Participants:</strong></p><ul>';
      meeting.participants.forEach(participant => {
        // Ensure participant.user exists
        if (participant.user) {
          participantsHTML += `<li>Name: ${participant.user.name || "N/A"}, Email: ${participant.user.email || "N/A"}</li>`;
        } else {
          participantsHTML += `<li>Name: N/A, Email: N/A</li>`;
        }
      });
      participantsHTML += '</ul>';
      meetingDiv.innerHTML += participantsHTML;
    } else if (role === "participant") {
      meetingDiv.innerHTML += `<p><strong>Participants:</strong> As a participant, you are not allowed to see other participants' information.</p>`;
    } else {
      meetingDiv.innerHTML += `<p><strong>Participants:</strong> No participants found.</p>`;
    }

    // Delete button
    if (role !== "participant") {
      meetingDiv.innerHTML += `
        <button class="delete-button" data-mid="${meeting.mid}">Delete</button>
      `;
    }

    meetingList.appendChild(meetingDiv);
  });

  // Attach delete button listeners
  attachDeleteButtonListeners();
}

// Capitalize the first letter of a string
function capitalizeFirstLetter(string) {
  if (!string) return "";
  return string.charAt(0).toUpperCase() + string.slice(1);
}

// Apply filters to the meetings
function applyFilters() {
  const type = document.getElementById("typeFilter").value;
  const status = document.getElementById("statusFilter").value;
  const recurrence = document.getElementById("recurrenceFilter").value;

  let filteredMeetings = meetings;

  if (type) {
    filteredMeetings = filteredMeetings.filter(m => m.meeting.type === type);
  }

  if (status) {
    filteredMeetings = filteredMeetings.filter(m => m.meeting.status === status);
  }

  if (recurrence) {
    filteredMeetings = filteredMeetings.filter(m => m.meeting.recurrence === recurrence);
  }

  displayMeetings(filteredMeetings);
}

// Reset filters to default
function resetFilters() {
  document.getElementById("filterForm").reset();
  displayMeetings(meetings);
}

// Attach filter button event listeners
function attachFilterListeners() {
  document.getElementById("applyFilters").addEventListener("click", applyFilters);
  document.getElementById("resetFilters").addEventListener("click", resetFilters);
}

// Attach delete button event listeners
function attachDeleteButtonListeners() {
  const deleteButtons = document.querySelectorAll(".delete-button");
  deleteButtons.forEach(button => {
    button.addEventListener("click", (event) => {
      const meetingId = event.target.getAttribute("data-mid");
      deleteMeeting(meetingId);
    });
  });
}

// Delete a meeting
function deleteMeeting(meetingId) {
  if (!confirm("Are you sure you want to delete this meeting?")) {
    return; // User canceled delete
  }

  console.log(`Deleting meeting with ID: ${meetingId}`);

  let xhr = new XMLHttpRequest();
  xhr.onreadystatechange = () => {
    if (xhr.readyState !== 4) {
      return;
    }

    if (xhr.status === 204) { // No Content, success
      alert("Meeting deleted successfully.");
      // Remove the deleted meeting from the list
      meetings = meetings.filter(m => m.meeting.mid !== parseInt(meetingId));
      // Refresh the meeting list
      displayMeetings(meetings);
    } else if (xhr.status === 401) {
      alert("Unauthorized: Please log in again.");
      window.location.href = "/login_form"; // Redirect to login page
    } else if (xhr.status === 403) {
      alert("Forbidden: You are not authorized to delete this meeting.");
    } else if (xhr.status === 404) {
      alert("Meeting not found.");
    } else {
      console.error("Error deleting meeting:", xhr.statusText);
      alert("Failed to delete the meeting. Please try again.");
    }
  };

  xhr.open("DELETE", `/delete_meeting?mid=${meetingId}`, true); // Call the backend endpoint
  xhr.send();
}

// On page load, bind events and fetch meetings
document.addEventListener("DOMContentLoaded", () => {
  fetchMyMeetings();
  attachFilterListeners();
});
