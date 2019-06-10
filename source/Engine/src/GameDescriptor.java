import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class GameDescriptor implements Serializable {
    private String GameType;
    private Game game;
    private Players players;
    private DynamicPlayers dinamycPlayers;

    public void setGameType(String gameType) {
        GameType = gameType;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setPlayers(Players players) {
        this.players = players;
    }

    public void setDinamycPlayers(DynamicPlayers dinamycPlayers) {
        this.dinamycPlayers = dinamycPlayers;
    }

    public Game getGame() { return game; }

    public String getGameType() { return GameType; }

    public Players getPlayers() { return players; }

    public DynamicPlayers getDinamycPlayers() { return dinamycPlayers; }

    public List<Position> getListOfInitialPositions(){
        List<Position> list = new ArrayList<>();
        for(Participant participant: game.getInitialPositions().getParticipant()){
            for(Position position:participant.getPositions()){
                list.add(position);
            }
        }
        return  list;
    }

}
