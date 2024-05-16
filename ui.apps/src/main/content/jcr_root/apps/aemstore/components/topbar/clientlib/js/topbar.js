document.addEventListener("DOMContentLoaded", function() {
    // Make AJAX request to servlet
    var xhr = new XMLHttpRequest();
    xhr.open("GET", "/bin/sessiontest", true);
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4 && xhr.status === 200) {
            // Parse JSON response
            var response = JSON.parse(xhr.responseText);
               // User name retrieved successfully
                var userName = response.email;
                if (typeof userName !== 'undefined') {
                    displayUserName(userName);
    				console.log('myVariable is defined');
				} else {
    				console.log('myVariable is not defined');
                    //window.location.href = "http://localhost:4502/content/aemstore/language-masters/login.html";
				}
        }
    };
    xhr.send();
});

function displayUserName(userName) {
    // Display the user name
    var userNameElement = document.getElementById("username-display");
    userNameElement.textContent = "Welcome, " + userName + "!";
}

function displayErrorMessage(errorMessage) {
    // Display error message
    var errorElement = document.getElementById("username-display");
    errorElement.textContent = "Error: " + errorMessage;
}
