// JavaScript to handle card click events
document.addEventListener('DOMContentLoaded', function() {
    // Get all card elements
    var cards = document.getElementById('card');

    // Attach click event listener to each card
    cards.forEach(function(card) {
        card.addEventListener('click', function() {
            // Retrieve data associated with the clicked card
            var cardId = card.dataset.cardId;
            var cardTitle = card.dataset.cardTitle;

            // Example: Log card data to console
            console.log('Clicked Card ID:', cardId);
            console.log('Clicked Card Title:', cardTitle);
            // Perform further actions with the data as needed
            // For example, make an AJAX request to retrieve more details
            // Or navigate to a different page using the retrieved data
        });
    });
});