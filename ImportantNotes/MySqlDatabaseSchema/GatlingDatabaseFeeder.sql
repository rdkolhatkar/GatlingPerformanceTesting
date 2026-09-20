CREATE SCHEMA IF NOT EXISTS gatlingdatabasefeeder;

USE gatlingdatabasefeeder;

CREATE TABLE IF NOT EXISTS userDataFeeder (
    user_id BIGINT NOT NULL AUTO_INCREMENT,
    user_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    mobile VARCHAR(10) NOT NULL,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_user_name (user_name)
);
