package tp.server;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class Move {


    @Id
    @GeneratedValue
    private int gameId;
    private int moveNumber;
    private String message;


    public Move(){

    }

    public Move(int gameId, int moveNumber, String message){

        this.moveNumber= moveNumber;
        this.message =message;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMoveNumber(int moveNumber) {
        this.moveNumber = moveNumber;
    }

    public int getGameId() {
        return gameId;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public String getMessage() {
        return message;
    }
}
