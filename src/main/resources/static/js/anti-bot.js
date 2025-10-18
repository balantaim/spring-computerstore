function isBotClient() {
    const FORBIDDEN_PATH = '/403';
    if (navigator.webdriver && window.location.pathname !== FORBIDDEN_PATH) {
        window.location.href = FORBIDDEN_PATH;
    }
}

isBotClient();
