document.addEventListener("DOMContentLoaded", function() {
    initUserName();
    logoutHandler();
});

let Email;
async function getEmail() {
    const response = await fetch('/bin/sessiontest');
    const json = await response.json();
    console.log(json);
    console.log(json.email);
    return json.email;
}

async function initUserName() {
    try {
        email = await getEmail();
        console.log(email);
        displayUserName(email);
    } catch (error) {
        console.error('Error fetching Email:', error);
    }
}



function displayUserName(object) {
    // Display the user name
    var userNameElement = document.getElementById("username-display");
    if(typeof object !== 'undefined' || object === null){
        userNameElement.textContent = "Welcome, " + object + "!";
    }else{
        userNameElement.textContent = "Please login";
		window.location.href = "http://localhost:4502/content/aemstore/us/login.html";
    }

}

function logoutHandler() {
    document.getElementById("logout").addEventListener("click", function(event) {
    event.preventDefault();
    doLogout();
    });
}


function doLogout() {
    return fetch('/bin/v1logout')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            console.log('logging out..');
			window.location.href = "http://localhost:4502/content/aemstore/us/login.html";
            return response.json();
        })
        .catch(error => {
            console.error('There was a problem with the fetch operation:', error);
        });
}