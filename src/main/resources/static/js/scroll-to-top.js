// Find the Button with id "scrollToTopBtn"
const scrollToTopButton = document.getElementById('scrollToTopBtn');

// Function to show the scroll-to-top button when scrolling down by more than 100vh
window.onscroll = function () {
    if (scrollToTopButton !== null) {
        if (document.body.scrollTop > window.innerHeight || document.documentElement.scrollTop > window.innerHeight) {
            scrollToTopButton.style.display = 'block';
        } else {
            scrollToTopButton.style.display = 'none';
        }
    }
};

// Function to scroll to the top of the page
function scrollToTop() {
    window.scrollTo({
        top: 0,
        behavior: 'smooth'
    });
}

// Attach event listener to the button
if (scrollToTopButton !== null) {
    scrollToTopButton.addEventListener('click', scrollToTop);
}