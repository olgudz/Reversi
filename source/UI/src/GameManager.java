import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.util.Pair;
import java.io.File;
import java.io.Serializable;
import java.util.*;

public class GameManager implements Serializable {

    protected static class State{
       private String type;
       private List<Player> players = new ArrayList<>();
       private Board board;
       private Player current;

       public State(List<Player> players, Board board, Player current,String type){
           for(Player p : players){
               Player clone = Player.clonePlayer(p);
               this.players.add(clone);
           }
           this.current = Player.clonePlayer(current);
           this.board = Board.cloneBoard(board);
           this.type = type;
       }

       protected List<Player> getPlayers(){
           return  players;
       }

       protected Board getBoard(){
           return board;
       }

       protected Player getCurrent(){
           return current;
       }

       protected String getType(){return type;}
   }

    protected static List<State> states = new ArrayList<>();

    protected static List<State> getStates(){
        return states;
    }

    private static String[] colorPool = {"BLACK", "YELLOW", "BLUE", "RED"};

    private static boolean moveDone = false;

    protected static boolean gameOver = false;

    protected static boolean isGameOver(){return gameOver;}

    protected static Runnable setGameDescriptor(File file,ValidateFile valid, GameDescriptor gameDescriptor){
        return () -> {
            generated.GameDescriptor gd =  valid.unmarshallFile(file);
            if (gd == null) return;
            Board board = setBoard(gd);
            String variant = gd.getGame().getVariant();
            InitialPositions positions = setInitialPositions(gd);

            Game game = new Game();
            game.setBoard(board);
            game.setVariant(variant);
            game.setInitialPositions(positions);

            gameDescriptor.setGameType(gd.getGameType());
            gameDescriptor.setGame(game);

            DynamicPlayers dp = setDynamicPlayers(gd);
            if (dp != null) { gameDescriptor.setDinamycPlayers(dp); }

            Players players = setPlayersArray(gd);
            gameDescriptor.setPlayers(players);

            int size = gameDescriptor.getGame().getInitialPositions().getParticipant().size();
            for (int i = 0; i < size; i++) {
                List<Position> list = gameDescriptor.getGame().getInitialPositions().getParticipant().get(i).getPositions();
                int id = gameDescriptor.getPlayers().getPlayer().get(i).getId();
                gameDescriptor.getGame().getBoard().setInitialPositions(list, id);
            }
        };
    }

    private static Players setPlayersArray(generated.GameDescriptor gd) {
        Player player;
        Players players = new Players();

        int size = gd.getPlayers().getPlayer().size();
        for(int i = 0; i < size; i++){
            int id = gd.getPlayers().getPlayer().get(i).getId().intValue();
            String name = gd.getPlayers().getPlayer().get(i).getName();
            String type = gd.getPlayers().getPlayer().get(i).getType();
            String color = colorPool[i];
            player = new Player(id, name,type, color);
            players.getPlayer().add(player);
        }
        return players;
    }

    private static Board setBoard(generated.GameDescriptor gd){
        int rows = gd.getGame().getBoard().getRows();
        int columns = gd.getGame().getBoard().getColumns();
        Board board = new Board(rows, columns);
        if(gd.getGame().getVariant().equals("Islands"))
            board.initBoard(0);
        return board;
    }

    private  static InitialPositions setInitialPositions(generated.GameDescriptor gd){
        InitialPositions positions = new InitialPositions();
        List<Participant> participants = new ArrayList<>();
        int size = gd.getGame().getInitialPositions().getParticipant().size();
        for(int i = 0; i < size; i++){
            Participant participant = setParticipant(gd,i);
            participants.add(participant);
        }
        positions.setParticipant(participants);
        return positions;
    }

    private static DynamicPlayers setDynamicPlayers(generated.GameDescriptor gd) {
        DynamicPlayers dp = new DynamicPlayers();
        if (gd.getDynamicPlayers() != null) {
            String name = gd.getDynamicPlayers().getGameTitle();
            int num = gd.getDynamicPlayers().getTotalPlayers();
            dp.setGameTitle(name);
            dp.setTotalPlayers(num);
        }
        return dp;
    }

    private static Participant setParticipant(generated.GameDescriptor gd, int index){
        Participant participant = new Participant();
        participant.setNumber(gd.getGame().getInitialPositions().getParticipant().get(index).getNumber());
        List<Position> positions = setParticipantPositions(gd, index);
        participant.setPosition(positions);
        return participant;
    }

    private static List<Position> setParticipantPositions(generated.GameDescriptor gd, int index){
        List<Position> positions = new ArrayList<>();
        for(generated.Position pos: gd.getGame().getInitialPositions().getParticipant().get(index).getPosition()){
            Position position = new Position();
            position.setCols(pos.getColumn());
            position.setRows(pos.getRow());
            positions.add(position);
        }
        return positions;
    }

    private static void printWin(GameDescriptor gd) {
        int size = gd.getPlayers().getPlayer().size();
        List<Pair<String,Integer>> namesAndScores= new ArrayList<>();
        for(Player player:gd.getPlayers().getPlayer()){
            String name = player.getName();
            Integer score = player.getScore();
            Pair pair = new Pair(name,score);
            namesAndScores.add(pair);
        }
        Collections.sort(namesAndScores, (a,b) ->  a.getValue() > b.getValue() ? -1 : a.getValue() == b.getValue() ? 1 : 0);

        Platform.runLater(createWinAlert(namesAndScores));
        gameOver = true;
    }

    protected static Player nextPlayer(Player oldCurrent, GameDescriptor gameDescriptor) {
        int size = gameDescriptor.getPlayers().getPlayer().size();

        for(int i = 0; i < size; i++){
            if (oldCurrent == gameDescriptor.getPlayers().getPlayer().get(i)){
                if(i + 1 < size) return gameDescriptor.getPlayers().getPlayer().get(i + 1);
                else return gameDescriptor.getPlayers().getPlayer().get(0);
            }
        }
        return null;   // will never come here
    }

    protected static Player prevPlayer(Player player, GameDescriptor gameDescriptor){
        int size = gameDescriptor.getPlayers().getPlayer().size();
        Player prev;

        for(int i = 0; i < size; i++){
            prev = gameDescriptor.getPlayers().getPlayer().get(i);
            if(i != size-1 && player == gameDescriptor.getPlayers().getPlayer().get(i+1)){
                return prev;
            }
            if(i == size-1 && player == gameDescriptor.getPlayers().getPlayer().get(0)){
                return prev;
            }
        }
            System.out.println("ERROR");
            return null;
    }

    protected static void updateScores(GameDescriptor gameDescriptor){
        for(Player player: gameDescriptor.getPlayers().getPlayer()){
            player.updateScore(gameDescriptor.getGame().getBoard().getNumOfDiscs(player.getId()));
        }
    }

    protected static Runnable playerMove(int row, int col, Player current, GameDescriptor gd){
        return () -> {
            if (current.getType().equals("Computer") || isMoveDone()) return;
            int value = current.getId();
            int index = gd.getGame().getBoard().getValue(row - 1, col - 1);
            if (index != 0) return;
            else {
                gd.getGame().getBoard().setValue(row - 1, col - 1, value);
                 current.addMove(row,col);
                 gd.getGame().getBoard().updateBoard(row, col,current);
            }
            updateScores(gd);
            GameManager.State state = new GameManager.State(gd.getPlayers().getPlayer(),
                    gd.getGame().getBoard(),current, gd.getGame().getVariant());
            states.add(state);
            if (gd.getGame().getBoard().isFull()) {
                printWin(gd);
            }
            moveDone = true;
        };
    }

    protected static Runnable computerMove(GameDescriptor gd, Player current){
        return ()-> {
            if (current.getType().equals("Human")) return;
            int value = current.getId();
            Random r = new Random();
            int row = -1, col = -1;
            int lowRow = 1;
            int highRow = gd.getGame().getBoard().getRows() + 1;
            int lowColumn = 1;
            int highColumn = gd.getGame().getBoard().getCols() + 1;
            boolean res = false;
            while (!res) {
                row = r.nextInt(highRow - lowRow) + lowRow;
                col = r.nextInt(highColumn - lowColumn) + lowColumn;
                int index = gd.getGame().getBoard().getValue(row - 1, col - 1);
                if (index == 0) {
                    gd.getGame().getBoard().setValue(row - 1, col - 1, value);
                    res = true;
                }
            }
            current.addMove(row,col);
            updateScores(gd);
            gd.getGame().getBoard().updateBoard(row, col, current);
            GameManager.State state = new GameManager.State(gd.getPlayers().getPlayer(),
                    gd.getGame().getBoard(),current, gd.getGame().getVariant());
            states.add(state);

            if (gd.getGame().getBoard().isFull()) {
                printWin(gd);
            }
        };
    }

    protected static boolean isMoveDone() {
        return moveDone;
    }

    protected static void setMoveDone(boolean newVal){
        moveDone = newVal;
    }

    public static Runnable createWinAlert(List<Pair<String,Integer>> list){
        return () -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("GAME OVER");
            alert.setHeaderText(list.get(0).getKey() + " won!");
            String names = "";
            for(int i = 0; i < list.size(); i++ ){
                names += list.get(i).getKey() + "  score = " + list.get(i).getValue() + "\n";
            }
            alert.setContentText(names);
            alert.showAndWait();
        };
    }


}
