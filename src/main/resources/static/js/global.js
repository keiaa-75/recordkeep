document.addEventListener('DOMContentLoaded', () => {
    const snackbar = document.getElementById('snackbar');

    if (snackbar) {
        // Show the snackbar
        snackbar.classList.add('show');

        // Hide it after 3 seconds
        setTimeout(() => {
            snackbar.classList.remove('show');
        }, 3000);
    }
});
