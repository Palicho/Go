USE goRecords;

DROP TABLE IF EXISTS move;

CREATE TABLE move (
    gameId INTEGER NOT NULL ,
    moveNumber INTEGER NOT NULL,
    message VARCHAR(20) NOT NULL,

   PRIMARY KEY (gameId, moveNumber)
);