CREATE DATABASE IF NOT EXISTS event_booking_db;
USE event_booking_db;

CREATE TABLE IF NOT EXISTS venues (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    city VARCHAR(255),
    capacity INT,
    description VARCHAR(1000)
    );

CREATE TABLE IF NOT EXISTS events (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    event_code VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    event_date DATETIME NOT NULL,
    venue_id BIGINT NOT NULL,

    CONSTRAINT fk_event_venue
        FOREIGN KEY (venue_id)
        REFERENCES venues(id)
        ON DELETE CASCADE
    );

