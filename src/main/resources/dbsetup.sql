-- create a table
CREATE TABLE IF NOT EXISTS Arenas (
    ArenaID INT PRIMARY KEY,
);

-- Creates a table for Blocks within each Arena
CREATE TABLE IF NOT EXISTS Blocks (
    BlockID INT AUTO_INCREMENT PRIMARY KEY,
    ArenaID INT,
    x DOUBLE NOT NULL,
    y DOUBLE NOT NULL,
    z DOUBLE NOT NULL,
    block_material VARCHAR(255) NOT NULL,
    FOREIGN KEY (ArenaID) REFERENCES Arenas(ArenaID)
);