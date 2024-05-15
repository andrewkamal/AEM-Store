document.addEventListener("DOMContentLoaded", function() {
    // Select the form element
    var form = document.getElementById("register-form");

    // Attach an event listener to the form's submit event
    form.addEventListener("submit", async function(event) {
        // Prevent the default form submission behavior
        event.preventDefault();

        // Retrieve form data
        var firstname = document.getElementById("firstname").value;
        var lastname = document.getElementById("lastname").value;
        var email = document.getElementById("email").value;
        var password = document.getElementById("password").value;
        var confirmPassword = document.getElementById("confirm-password").value;

        // Check if passwords match
        if (password !== confirmPassword) {
            alert("Passwords do not match");
            return;
        }

        // Create JSON object
        var userData = {
            firstname: firstname,
            lastname: lastname,
            email: email,
            password: password
        };

        try {
            // Fetch CSRF token
            const csrfToken = await getCsrfToken();

            // Make POST request using fetch API
            const response = await fetch('/bin/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'CSRF-Token': csrfToken // Include CSRF token in headers
                },
                body: JSON.stringify(userData)
            });

            if (response.ok) {
                // Registration successful
                showSuccessMessage();
                window.location.href = "http://localhost:4502/content/aemstore/language-masters/login.html";
            } else {
                const errorText = await response.text();
                throw new Error(errorText);
            }
        } catch (error) {
            // Error occurred
            alert("Error: " + error.message);
        }
    });
});

async function getCsrfToken() {
    const response = await fetch('/libs/granite/csrf/token.json');
    const json = await response.json();
    return json.token;
}

function showSuccessMessage() {
    // Hide the registration form
    document.querySelector(".register-form").style.display = "none";
    // Show the success message
    document.querySelector(".success-message").style.display = "block";

    window.location.href = "http://localhost:4502/content/aemstore/language-masters/login.html";
}
