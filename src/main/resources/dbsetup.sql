CREATE TABLE IF NOT EXISTS satoruspleef_database.Arenas
(
    ArenaID INT NOT NULL AUTO_INCREMENT,
    x DOUBLE NOT NULL,
    y DOUBLE NOT NULL,
    z DOUBLE NOT NULL,
    block_material VARCHAR(255) NOT NULL,
    PRIMARY KEY(ArenaID)
);