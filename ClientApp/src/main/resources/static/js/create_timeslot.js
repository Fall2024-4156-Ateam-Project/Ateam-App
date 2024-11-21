console.log("Cookies: ", document.cookie);

// Utility function to get cookies by name
function getCookie(name) {
    let value = `; ${document.cookie}`;
    let parts = value.split(`; ${name}=`);
    if (parts.length === 2) {
        return parts.pop().split(';').shift();
    }
    return null;
}

document.addEventListener("DOMContentLoaded", () => {
    const form = document.querySelector("form");
    const successMessage = document.createElement("div");
    const errorMessage = document.createElement("div");

    // Styling for success and error messages
    successMessage.style.color = "green";
    successMessage.style.marginTop = "15px";
    errorMessage.style.color = "red";
    errorMessage.style.marginTop = "15px";

    form.appendChild(successMessage);
    form.appendChild(errorMessage);

    // Add form submit event listener
    form.addEventListener("submit", (event) => {
        event.preventDefault(); // Prevent default form submission

        // Retrieve email from cookies
        const email = getCookie('email'); // Get the email from cookies
        console.log('Retrieved email from cookies:', email);

        // Collect form data
        const formData = new FormData(form);
        const timeslotData = {
            email: email,
            day: formData.get("day"),
            startTime: formData.get("startTime"),
            endTime: formData.get("endTime"),
            availability: formData.get("availability"),
        };

        console.log("Submitting timeslot:", timeslotData);

        // Send POST request
        const xhr = new XMLHttpRequest();
        xhr.onreadystatechange = () => {
            if (xhr.readyState !== 4) {
                return;
            }

            if (xhr.status === 200) {
                // On success, show success message and clear the form
                successMessage.textContent = "Timeslot created successfully!";
                errorMessage.textContent = "";
                form.reset();
            } else {
                // On error, show error message
                errorMessage.textContent = `Error: ${xhr.statusText}`;
                successMessage.textContent = "";
            }
        };

        // Send the data to the backend
        xhr.open("POST", "/timeslot_create_form");
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded"); // Sending as form-urlencoded
        xhr.send(new URLSearchParams(timeslotData).toString()); // Convert data to URL-encoded format
    });
});
