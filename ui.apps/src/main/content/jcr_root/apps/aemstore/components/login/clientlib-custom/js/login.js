//handle csrf token
async function getCsrfToken() {
    const response = await fetch('/libs/granite/csrf/token.json');
    const json = await response.json();
    return json.token;
}
let csrfToken;

async function initializeCsrfToken() {
    try {
        csrfToken = await getCsrfToken();
        console.log('CSRF token:', csrfToken);
    } catch (error) {
        console.error('Error fetching CSRF token:', error);
    }
}

initializeCsrfToken();

// Function to send email when submit button is clicked
function doLogin() {
    // Get values from email and message fields
    const email = document.querySelector('.cmp-login__email-field').value;
    const message = document.querySelector('.cmp-login__password-field').value;

    // Create data object to jsonify
    const data = {
        email: email,
        password: message
    };

    const username = 'admin';
    const password = 'admin';

    console.log(csrfToken)
    // Send POST request to servlet endpoint
    fetch('/bin/v1login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Basic ' + btoa(username + ':' + password), // Encode username and password
            'CSRF-Token': csrfToken
        },
        body: JSON.stringify(data) // Convert JavaScript object to JSON string
    })
    .then(response => {
        if (response.ok) {
            // Request successful
            console.log('Login Successfully');
        	window.location.href = "http://localhost:4502/content/aemstore/language-masters/home.html";
        response.message
        } else {
            // Request failed
            response.text().then(errorMessage => {
                        console.error('Failed login:', errorMessage);
                    }).catch(error => {
                        console.error('Error retrieving error message:', error);
                    });
                }
    })
    .catch(error => {
        console.error('Error:', error);
    });
}

// Add event listener to submit button
document.addEventListener('DOMContentLoaded', function() {
    const submitButton = document.getElementById("login-button");
    submitButton.addEventListener('click', function(event) {
        console.log("Clicked !!");
        event.preventDefault();
        doLogin(); 
    });
});