package tp.server;

public class Move {
    private int gameID;
    private int moveNumber;
    private String message;


    public Move(){

    }

    public Move(int gameID, int moveNumber, String message){
        this.gameID=gameID;
        this.moveNumber= moveNumber;
        this.message =message;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMoveNumber(int moveNumber) {
        this.moveNumber = moveNumber;
    }

    public int getGameID() {
        return gameID;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public String getMessage() {
        return message;
    }
}
