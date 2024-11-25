let meetings = []; // Store fetched meetings

// Function to get the value of a cookie by name
function getCookie(name) {
  const cookieArr = document.cookie.split(";");
  for (let i = 0; i < cookieArr.length; i++) {
    let cookie = cookieArr[i].trim();
    if (cookie.startsWith(name + "=")) {
      return cookie.substring(name.length + 1);
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

// Display the meetings on the page
function displayMeetings(meetings) {
  const meetingList = document.getElementById("meetingList");
  meetingList.innerHTML = ""; // Clear existing list

  const organizerName = getCookie("name"); // Get the organizer name from the cookie

  if (meetings.length === 0) {
    meetingList.innerHTML = "<p>No meetings found.</p>";
    return;
  }

  // Generate meeting list
  meetings.forEach(meeting => {
    const meetingDiv = document.createElement("div");
    meetingDiv.className = "meeting-item";
    console.log(meeting);
    console.log(meeting.organizer);

    meetingDiv.innerHTML = `
      <p><strong>Organizer:</strong> ${organizerName || "N/A"}</p>
      <p><strong>Type:</strong> ${meeting.type || "N/A"}</p>
      <p><strong>Description:</strong> ${meeting.description || "N/A"}</p>
      <p><strong>Start Day:</strong> ${meeting.startDay || "N/A"}</p>
      <p><strong>End Day:</strong> ${meeting.endDay || "N/A"}</p>
      <p><strong>Start Time:</strong> ${meeting.startTime || "N/A"}</p>
      <p><strong>End Time:</strong> ${meeting.endTime || "N/A"}</p>
      <p><strong>Recurrence:</strong> ${meeting.recurrence || "N/A"}</p>
      <p><strong>Status:</strong> ${meeting.status || "N/A"}</p>
    `;
    meetingList.appendChild(meetingDiv);
  });
}

// Attach event listener to fetch meetings when the page loads
document.addEventListener("DOMContentLoaded", fetchMyMeetings);
