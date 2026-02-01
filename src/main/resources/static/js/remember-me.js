const checkboxRememberMe = document.getElementById('remember-me');
const STORAGE_KEY = 'preference-remember-me';

// Load preference on page load
window.addEventListener('DOMContentLoaded', () => {
    const remembered = localStorage.getItem(STORAGE_KEY);
    if (remembered === 'true') {
        checkboxRememberMe.checked = true;
        // console.log('Remember Me is ON');
    }
});

// Save preference when checkbox changes
checkboxRememberMe.addEventListener('change', () => {
    if (checkboxRememberMe.checked) {
        localStorage.setItem(STORAGE_KEY, 'true');
    } else {
        localStorage.removeItem(STORAGE_KEY);
        // console.log('Remember Me is removed!');
    }
});