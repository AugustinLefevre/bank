-- Création de la table users
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- Création de la table accounts
CREATE TABLE IF NOT EXISTS accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    balance DECIMAL(10, 2) DEFAULT 0.00,
    user_id BIGINT,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Création de la table transactions
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    balance DECIMAL(10, 2) DEFAULT 0.00,
    amount DECIMAL(10, 2) DEFAULT 0.00,
    transaction_type VARCHAR(50) NOT NULL,
    transaction_status VARCHAR(50) NOT NULL,
    from_username VARCHAR(255),
    message VARCHAR(255),
    transaction_date TIMESTAMP NOT NULL,
    account_id BIGINT,
    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES accounts(id)
);

-- Insertion d'un utilisateur
INSERT INTO users (id, username, password) VALUES (1, 'john_doe', '$2a$10$Q9eM0YXfBdBmEOMR8hLq/uYKZqsmz2OW/XrXb/MRJyCmR7L39Sy32');

-- Insertion des comptes associés à l'utilisateur
INSERT INTO accounts (id, balance, user_id) VALUES (1, 1000.50, 1);
INSERT INTO accounts (id, balance, user_id) VALUES (2, 250.75, 1);

-- Insertion de transactions
INSERT INTO transactions (id,
                          balance,
                          amount,
                          transaction_type,
                          transaction_status,
                          from_username,
                          message,
                          transaction_date,
                          account_id) VALUES (
                            11,
                            1000.50,
                            100.50,
                            'DEPOSIT',
                            'APPROVED',
                            'john_doe',
                            'Transaction approved',
                            '2024-02-18 14:35:45',
                            1);

INSERT INTO transactions (id,
                          balance,
                          amount,
                          transaction_type,
                          transaction_status,
                          from_username,
                          message,
                          transaction_date,
                          account_id) VALUES (
                            12,
                            1000.50,
                            100.50,
                            'DEPOSIT',
                            'APPROVED',
                            'john_doe',
                            'Transaction approved',
                            '2024-02-18 14:35:45',
                            2);

