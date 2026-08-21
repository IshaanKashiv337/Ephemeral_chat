-- init.sql

-- USERS TABLE
-- Stores mandatory fields like Phone No. and pincode as integers, and others as strings[cite: 1].
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    phone_no BIGINT NOT NULL,
    state VARCHAR(100),
    city VARCHAR(100),
    pincode INT NOT NULL,
    street_no VARCHAR(255),
    building_no VARCHAR(255)
);

-- COMMUNITIES TABLE
-- Stores community creation fields including the protected/public toggle[cite: 1].
CREATE TABLE communities (
    id INT AUTO_INCREMENT PRIMARY KEY,
    creator_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    locality VARCHAR(255),
    pincode INT NOT NULL,
    description TEXT,
    is_protected BOOLEAN DEFAULT FALSE,
    photo_path VARCHAR(255),
    FOREIGN KEY (creator_id) REFERENCES users(id)
);

-- COMMUNITY MEMBERS TABLE
-- Tracks which users are in which communities[cite: 1].
CREATE TABLE community_members (
    community_id INT NOT NULL,
    user_id INT NOT NULL,
    status VARCHAR(50) DEFAULT 'APPROVED', -- 'PENDING' for protected communities
    PRIMARY KEY (community_id, user_id),
    FOREIGN KEY (community_id) REFERENCES communities(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- BOOKS TABLE
-- Stores book details. Name, author, and edition are mandatory[cite: 1].
CREATE TABLE books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    owner_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    edition INT NOT NULL,
    genre VARCHAR(100),
    description TEXT,
    image_path VARCHAR(255),
    language VARCHAR(100),
    condition_desc VARCHAR(255),
    times_lent INT DEFAULT 0,
    is_available BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (owner_id) REFERENCES users(id)
);

-- COMMUNITY BOOKS TABLE
-- Maps which books are made available to which communities[cite: 1].
CREATE TABLE community_books (
    book_id INT NOT NULL,
    community_id INT NOT NULL,
    is_available_to_community BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (book_id, community_id),
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (community_id) REFERENCES communities(id)
);

-- REQUESTS (TRANSACTIONS) TABLE
-- Tracks requests, acknowledgements, and timestamps for the 48-hour window[cite: 1].
CREATE TABLE requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    borrower_id INT NOT NULL,
    owner_id INT NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING', -- PENDING, ACCEPTED, REJECTED, EXCHANGED, RETURNED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exchange_deadline TIMESTAMP NULL,
    borrower_acknowledged BOOLEAN DEFAULT FALSE,
    owner_acknowledged BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (borrower_id) REFERENCES users(id),
    FOREIGN KEY (owner_id) REFERENCES users(id)
);

-- NOTIFICATIONS TABLE
-- Stores linear notifications for users[cite: 1].
CREATE TABLE notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);