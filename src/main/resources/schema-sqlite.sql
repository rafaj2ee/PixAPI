CREATE TABLE IF NOT EXISTS purchase_transaction (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    description TEXT,
    amount NUMERIC,
    transaction_date DATETIME
);
CREATE TABLE IF NOT EXISTS command (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    command_line VARCHAR(255),
    executed_at DATETIME,
    result CLOB
);