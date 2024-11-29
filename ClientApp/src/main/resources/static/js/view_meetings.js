let meetings = []; // 存储已获取的会议

// 获取指定名称的cookie值
function getCookie(name) {
  const cookieArr = document.cookie.split(";");
  for (let i = 0; i < cookieArr.length; i++) {
    let cookie = cookieArr[i].trim();
    if (cookie.startsWith(name + "=")) {
      return decodeURIComponent(cookie.substring(name.length + 1));
    }
  }
  return null; // 如果未找到cookie则返回null
}

// 获取并显示登录用户的所有会议
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

    // 检查响应是否为有效的JSON格式
    try {
      meetings = JSON.parse(xhr.responseText);
      displayMeetings(meetings);
    } catch (error) {
      console.error("Error parsing JSON:", error);
      alert("Failed to parse meetings data.");
    }
  };
  xhr.open("GET", "/view_my_meetings"); // 确保此URL与后端控制器匹配
  xhr.send();
}

// 显示会议列表
function displayMeetings(meetingsToDisplay) {
  const meetingList = document.getElementById("meetingList");
  meetingList.innerHTML = ""; // 清除现有列表

  const organizerName = getCookie("name"); // 从cookie获取组织者姓名

  if (meetingsToDisplay.length === 0) {
    meetingList.innerHTML = "<p>No meetings found.</p>";
    return;
  }

  // 生成会议列表
  meetingsToDisplay.forEach(meeting => {
    const meetingDiv = document.createElement("div");
    meetingDiv.className = "meeting-item";
    console.log(meeting);
    console.log(meeting.organizer);

    // 基本会议信息
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
    `;

    // 参与者信息
    if (meeting.participants && meeting.participants.length > 0) {
      let participantsHTML = '<p><strong>Participants:</strong></p><ul>';
      meeting.participants.forEach(participant => {
        // 确保 participant.user 存在
        if (participant.user) {
          participantsHTML += `<li>Name: ${participant.user.name || "N/A"}, Email: ${participant.user.email || "N/A"}</li>`;
        } else {
          participantsHTML += `<li>Name: N/A, Email: N/A</li>`;
        }
      });
      participantsHTML += '</ul>';
      meetingDiv.innerHTML += participantsHTML;
    } else {
      meetingDiv.innerHTML += `<p><strong>Participants:</strong> No participants found.</p>`;
    }

    // 删除按钮
    meetingDiv.innerHTML += `
      <button class="delete-button" data-mid="${meeting.mid}">Delete</button>
    `;

    meetingList.appendChild(meetingDiv);
  });

  // 绑定删除按钮的事件监听器
  attachDeleteButtonListeners();
}

// 首字母大写
function capitalizeFirstLetter(string) {
  if (!string) return "";
  return string.charAt(0).toUpperCase() + string.slice(1);
}

// 应用过滤器
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

// 重置过滤器
function resetFilters() {
  document.getElementById("filterForm").reset();
  displayMeetings(meetings);
}

// 绑定过滤按钮的事件监听器
function attachFilterListeners() {
  document.getElementById("applyFilters").addEventListener("click", applyFilters);
  document.getElementById("resetFilters").addEventListener("click", resetFilters);
}

// 绑定删除按钮的事件监听器
function attachDeleteButtonListeners() {
  const deleteButtons = document.querySelectorAll(".delete-button");
  deleteButtons.forEach(button => {
    button.addEventListener("click", (event) => {
      const meetingId = event.target.getAttribute("data-mid");
      deleteMeeting(meetingId);
    });
  });
}

// 删除会议的函数
function deleteMeeting(meetingId) {
  if (!confirm("Are you sure you want to delete this meeting?")) {
    return; // 用户取消删除
  }

  console.log(`Deleting meeting with ID: ${meetingId}`);

  let xhr = new XMLHttpRequest();
  xhr.onreadystatechange = () => {
    if (xhr.readyState !== 4) {
      return;
    }

    if (xhr.status === 204) { // No Content, 成功删除
      alert("Meeting deleted successfully.");
      // 从会议数组中移除已删除的会议
      meetings = meetings.filter(meeting => meeting.mid !== parseInt(meetingId));
      // 刷新会议列表
      displayMeetings(meetings);
    } else if (xhr.status === 401) {
      alert("Unauthorized: Please log in again.");
      window.location.href = "/login_form"; // 重定向到登录页面
    } else if (xhr.status === 403) {
      alert("Forbidden: You are not authorized to delete this meeting.");
    } else if (xhr.status === 404) {
      alert("Meeting not found.");
    } else {
      console.error("Error deleting meeting:", xhr.statusText);
      alert("Failed to delete the meeting. Please try again.");
    }
  };

  xhr.open("DELETE", `/delete_meeting?mid=${meetingId}`, true); // 调用应用的后端端点
  xhr.send();
}

// 页面加载完成后绑定事件和获取会议
document.addEventListener("DOMContentLoaded", () => {
  fetchMyMeetings();
  attachFilterListeners();
});
