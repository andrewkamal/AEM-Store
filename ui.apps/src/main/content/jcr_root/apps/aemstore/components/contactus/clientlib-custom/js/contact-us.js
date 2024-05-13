// Get reference to the email field
const emailField = document.querySelector('.cmp-contactus__email-field');
// Set initial state of email field based on the value passed from AEM
emailReadOnly = document.querySelector('.cmp-contactus').getAttribute('data-email-read-only')
emailField.readOnly = (emailReadOnly === 'true');
console.log(emailReadOnly)
console.log(emailField.readOnly)

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

// Function to handle changes in the checkbox state
function handleCheckboxChange() {
    // Update read-only attribute of email field based on checkbox state
    emailField.readOnly = this.checked;
}

// Function to send email when submit button is clicked
function sendEmail() {
    // Get values from email and message fields
    const email = document.querySelector('.cmp-contactus__email-field').value;
    const message = document.querySelector('.cmp-contactus__message-field').value;

    // Create data object to jsonify
    const data = {
        email: email,
        message: message
    };

    const username = 'admin';
    const password = 'admin';

    console.log(csrfToken)
    // Send POST request to servlet endpoint
    fetch('/bin/executeworkflow?page=/content/testsite/us/en', {
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
            console.log('Email sent successfully');
        } else {
            // Request failed
            response.text().then(errorMessage => {
                        console.error('Failed to send email:', errorMessage);
                    }).catch(error => {
                        console.error('Error retrieving error message:', error);
                    });
                }
    })
    .catch(error => {
        console.error('Error sending email:', error);
    });
}

// Add event listener to submit button
document.addEventListener('DOMContentLoaded', function() {
    const submitButton = document.querySelector('.cmp-contactus__submit-button');
    submitButton.addEventListener('click', function(event) {
        event.preventDefault(); // Prevent default form submission
        sendEmail(); // Call function to send email
    });
});