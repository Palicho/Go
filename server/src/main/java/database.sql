DROP DATABASE goRecords;
DROP USER 'go'@'localhost';
CREATE DATABASE goRecords;
USE goRecords;
CREATE TABLE games (
    id int PRIMARY KEY AUTO_INCREMENT NOT NULL,
    date date NOT NULL
);
CREATE TABLE moves (
    gameID int,
    moveID int NOT NULL,
    command varchar(20) NOT NULL,
    FOREIGN KEY (gameID) REFERENCES games (id)
);
CREATE USER 'go'@'localhost' IDENTIFIED BY 'game';
GRANT ALL PRIVILEGES ON goRecords.* TO 'go'@'localhost';