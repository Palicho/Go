package tp.server;

import javax.persistence.*;

@Entity
@Table(name = "moves")
public class Move {
    @Id @Column(name = "gameId")
    private int gameId;
    @Id @Column(name = "moveNumber")
    private int moveNumber;
    @Column(name = "message")
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
