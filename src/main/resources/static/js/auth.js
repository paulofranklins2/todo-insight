/**
 * Authentication Module
 * Handles token storage and session management.
 */
const Auth = (() => {
    const ACCESS_TOKEN_KEY = 'accessToken';
    const REFRESH_TOKEN_KEY = 'refreshToken';
    const USER_EMAIL_KEY = 'userEmail';

    /**
     * Check if user is authenticated with a valid-looking token.
     */
    const isAuthenticated = () => {
        const token = localStorage.getItem(ACCESS_TOKEN_KEY);
        return token && token !== 'undefined' && token !== 'null' && (token.match(/\./g) || []).length === 2;
    };

    /**
     * Store tokens from login/signup response.
     * Handles both camelCase and snake_case response formats.
     */
    const setTokens = (response) => {
        if (!response) return false;

        const accessToken = response.accessToken || response.access_token;
        const refreshToken = response.refreshToken || response.refresh_token;
        const email = response.email;

        if (!accessToken) return false;

        localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
        localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
        localStorage.setItem(USER_EMAIL_KEY, email);

        return true;
    };

    /**
     * Get current user email.
     */
    const getUserEmail = () => {
        return localStorage.getItem(USER_EMAIL_KEY);
    };

    /**
     * Clear tokens and redirect to login.
     */
    const logout = () => {
        localStorage.removeItem(ACCESS_TOKEN_KEY);
        localStorage.removeItem(REFRESH_TOKEN_KEY);
        localStorage.removeItem(USER_EMAIL_KEY);
        window.location.href = '/login';
    };

    /**
     * Require authentication - redirect if not logged in.
     */
    const requireAuth = () => {
        if (!isAuthenticated()) {
            window.location.href = '/login';
            return false;
        }
        return true;
    };

    /**
     * Initialize auth UI elements (user email, logout button, mobile menu).
     */
    const initAuthUI = () => {
        const email = getUserEmail() || '';

        // Desktop elements
        const emailEl = document.getElementById('userEmail');
        if (emailEl) emailEl.textContent = email;

        const logoutBtn = document.getElementById('logoutBtn');
        if (logoutBtn) logoutBtn.addEventListener('click', logout);

        // Mobile elements
        const emailElMobile = document.getElementById('userEmailMobile');
        if (emailElMobile) emailElMobile.textContent = email;

        const logoutBtnMobile = document.getElementById('logoutBtnMobile');
        if (logoutBtnMobile) logoutBtnMobile.addEventListener('click', logout);

        // Hamburger menu toggle
        const navbarToggle = document.getElementById('navbarToggle');
        const navbarMenu = document.getElementById('navbarMenu');
        if (navbarToggle && navbarMenu) {
            navbarToggle.addEventListener('click', () => {
                navbarToggle.classList.toggle('active');
                navbarMenu.classList.toggle('active');
            });
        }
    };

    return {
        isAuthenticated,
        setTokens,
        getUserEmail,
        logout,
        requireAuth,
        initAuthUI
    };
})();

