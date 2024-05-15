document.addEventListener('DOMContentLoaded', function() {
    // Reference to the title field
    const titleField = document.querySelector('[data-cmp-hook-yourcomponent="title"]');
    // Reference to the image field
    const imageField = document.querySelector('[data-cmp-hook-yourcomponent="image"]');
    // Reference to the price field
    const priceField = document.querySelector('[data-cmp-hook-yourcomponent="price"]');
    // Reference to the description field
    const descriptionField = document.querySelector('[data-cmp-hook-yourcomponent="description"]');
    // Reference to the quantity field
    const quantityField = document.querySelector('[data-cmp-hook-yourcomponent="quantity"]');
    // Reference to the submit button
    const submitButton = document.querySelector('.addprod button');

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
    function submitProduct() {
        // Get values from fields
        const title = titleField.value;
        const price = parseFloat(priceField.value);
        const description = descriptionField.value;
        const quantity = parseInt(quantityField.value);

        // Construct FormData object to handle file upload
        const formData = new FormData();
        formData.append('title', title);
        formData.append('price', price);
        formData.append('description', description);
        formData.append('quantity', quantity);
        formData.append('image', imageField.files[0]); // Get the first file from the file input
		console.log(imageField.files[0]);
        console.log(formData);
        console.log(csrfToken);

        const username = 'admin';
        const password = 'admin';

        // Send POST request to endpoint
        fetch('/bin/addproduct?page=/content/aemstore/us/en', {
            method: 'POST',
            headers: {
                'Authorization': 'Basic ' + btoa(username + ':' + password), // Encode username and password
                'CSRF-Token': csrfToken
            },
            body: formData // Convert JavaScript object to JSON string
        })
        .then(response => {
            if (response.ok) {
                // Request successful
                console.log('Product submitted successfully');
            } else {
                // Request failed
                response.text().then(errorMessage => {
                    console.error('Failed to submit product:', errorMessage);
                }).catch(error => {
                    console.error('Error retrieving error message:', error);
                });
            }
        })
        .catch(error => {
            console.error('Error submitting product:', error);
        });
    }

    // Add event listener to submit button
    submitButton.addEventListener('click', function(event) {
        event.preventDefault(); // Prevent default form submission
        submitProduct(); // Call function to submit product
    });
});