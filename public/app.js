// app.js

// --- SPA NAVIGATION ---
function navigateTo(screenId) {
    // Hide all screens
    document.querySelectorAll('.screen').forEach(screen => {
        screen.classList.remove('active');
        screen.classList.add('hidden');
    });
    // Show the target screen
    const target = document.getElementById(screenId);
    if (target) {
        target.classList.remove('hidden');
        target.classList.add('active');
    }
}

// Global back button listener[cite: 1]
document.querySelectorAll('.back-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        // In a full implementation, you would use a stack to track history.
        // For now, returning to home as a fallback.
        navigateTo('home-screen');
    });
});

// --- ONBOARDING SLIDES ---
let currentSlide = 0;
const totalSlides = 3; // Mock number of slides
const slideDotsContainer = document.getElementById('slide-dots');

// Initialize dots[cite: 1]
for (let i = 0; i < totalSlides; i++) {
    const dot = document.createElement('div');
    dot.className = 'dot' + (i === 0 ? ' active-dot' : '');
    slideDotsContainer.appendChild(dot);
}

document.getElementById('next-slide-btn').addEventListener('click', () => {
    currentSlide++;
    if (currentSlide < totalSlides) {
        // Update dots[cite: 1]
        document.querySelectorAll('.dot').forEach((dot, index) => {
            dot.className = 'dot' + (index === currentSlide ? ' active-dot' : '');
        });
    } else {
        navigateTo('login-screen');
    }
});

// --- AUTHENTICATION ---
// Login Logic[cite: 1]
document.getElementById('login-btn').addEventListener('click', () => {
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/; // REGEX for email[cite: 1]
    
    if (!emailRegex.test(email)) {
        alert("Invalid email format");
        return;
    }
    
    // Mock API call to backend
    // If wrong password, show label: document.getElementById('wrong-password-label').classList.remove('hidden');[cite: 1]
    // On success:
    navigateTo('home-screen');
});

document.getElementById('goto-signup-btn').addEventListener('click', () => navigateTo('signup-screen'));
document.getElementById('forgot-password-btn').addEventListener('click', () => navigateTo('forgot-password-screen'));

// Sign Up Dynamic Button Color[cite: 1]
const signupInputs = ['signup-phone', 'signup-pincode', 'signup-street', 'signup-building'];
signupInputs.forEach(id => {
    document.getElementById(id).addEventListener('input', checkSignupFields);
});

function checkSignupFields() {
    const allFilled = signupInputs.every(id => document.getElementById(id).value.trim() !== '');
    const regBtn = document.getElementById('register-btn');
    if (allFilled) {
        regBtn.classList.remove('greyed-out');
        regBtn.classList.add('active-color'); // Turns to color scheme[cite: 1]
    } else {
        regBtn.classList.add('greyed-out');
        regBtn.classList.remove('active-color');
    }
}

// --- HOME & DRAWER ---
const drawer = document.getElementById('nav-drawer');
document.getElementById('nav-drawer-btn').addEventListener('click', () => {
    // Toggles drawer open/close[cite: 1]
    drawer.classList.toggle('hidden');
});

// Drawer Tabs[cite: 1]
document.getElementById('tab-home').addEventListener('click', () => { navigateTo('home-screen'); drawer.classList.add('hidden'); });
document.getElementById('tab-request-history').addEventListener('click', () => { navigateTo('request-history-screen'); drawer.classList.add('hidden'); });
document.getElementById('tab-genre').addEventListener('click', () => { navigateTo('genre-screen'); drawer.classList.add('hidden'); });
document.getElementById('tab-your-books').addEventListener('click', () => { navigateTo('your-books-screen'); drawer.classList.add('hidden'); });

// Home Search Bar click takes to search screen[cite: 1]
document.getElementById('home-search-bar').addEventListener('click', () => navigateTo('search-screen'));
document.getElementById('add-book-icon').addEventListener('click', () => navigateTo('upload-screen'));
document.getElementById('create-community-fab').addEventListener('click', () => navigateTo('create-community-screen'));
document.getElementById('notifications-icon').addEventListener('click', () => navigateTo('notifications-screen'));

// --- SEARCH SCREEN ---
document.getElementById('active-search-bar').addEventListener('input', (e) => {
    // Show input in real time and populate suggestions[cite: 1]
    const suggestions = document.getElementById('search-suggestions');
    if (e.target.value.length > 0) {
        suggestions.classList.remove('hidden');
        suggestions.innerHTML = `<li>Suggestion for ${e.target.value}</li>`;
    } else {
        suggestions.classList.add('hidden');
    }
});

// --- COMMUNITY CHAT ---
document.getElementById('chat-plus-icon').addEventListener('click', () => {
    // Toggles the options window[cite: 1]
    document.getElementById('chat-plus-options').classList.toggle('hidden');
});
document.getElementById('chat-new-book').addEventListener('click', () => navigateTo('upload-screen'));
document.getElementById('chat-uploaded-books').addEventListener('click', () => navigateTo('your-books-screen'));

// --- YOUR BOOKS (Availability Toggle) ---
function toggleAvailability(bookElement, isAvailable) {
    // To avoid confusion, available will be in green color and unavailable will be in red color[cite: 1].
    if (isAvailable) {
        bookElement.classList.add('status-available');
        bookElement.classList.remove('status-unavailable');
        bookElement.innerText = "Available";
    } else {
        bookElement.classList.remove('status-available');
        bookElement.classList.add('status-unavailable');
        bookElement.innerText = "Handed to {Borrower's name}";
    }
}