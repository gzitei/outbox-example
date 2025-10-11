DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS outbox_messages;
DROP TABLE IF EXISTS users_aud;
DROP TABLE IF EXISTS outbox_messages_aud;

CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR,
    name VARCHAR,
    password VARCHAR,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE outbox_messages (
    outbox_message_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    recipient_id BIGINT,
    content VARCHAR,
    status VARCHAR DEFAULT 'PENDING',
    sent_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
