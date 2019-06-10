import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final SimpleStringProperty name;
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty type;
    private final SimpleStringProperty color;
    private SimpleDoubleProperty flips;
    private SimpleIntegerProperty score;
    private SimpleIntegerProperty numOfMoves;
    private double reversal = 0;

    private List<Position> moves;
    private List<Double> reverseList;

    public Player(int id, String name, String type, String color) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.type = new SimpleStringProperty(type);
        flips = new SimpleDoubleProperty(0);
        score = new SimpleIntegerProperty(0);
        this.color = new SimpleStringProperty(color);
        numOfMoves = new SimpleIntegerProperty(0);
        moves =  FXCollections.observableArrayList();
        reverseList = new ArrayList<>();
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() { return name; }

    public int getId(){return id.get();}

    public IntegerProperty idProperty() { return id; }

    public String getType(){return type.get();}

    public StringProperty typeProperty() { return type; }

    public String getColor(){ return color.get(); }

    public double getFlips(){return flips.get();}

    private void setFlips(double _flips){flips.set(_flips);}

    public DoubleProperty flipsProperty() { return flips; }

    public int getScore(){ return score.get(); }

    public IntegerProperty scoreProperty() { return score; }

    public IntegerProperty numberOfMovesProperty() { return numOfMoves;}

    protected void updateScore(int newScore){ score.set(newScore); }

    private void setReversal(double _reversal){reversal = _reversal;}

    protected void updateReversal(int add){
        reversal += add;
        reverseList.add(reversal);
        updateAverageFlips();
    }

    protected double getReversal(){return reversal;}

    protected void updateAverageFlips() {
        if(reversal == 0 || reverseList.size() == 0) return;
        double avg = reversal/numOfMoves.get();
        flips.set(avg);
    }

    protected int compareTo(Player other){
        if(other.getId() == id.get() && other.getName().equals(name) )
            return 0;
        else return  -1;
    }

    protected void undo(){
        int n = numOfMoves.get();
        if(n == 0){
            System.out.println("ERROR!!!!!!!!");
            return;
        }
        numOfMoves.set(n - 1);
        moves.remove(n - 1);
        if(n == 1){
            reversal = 0;
        }
        else {
            reversal = reverseList.get(n - 2);
        }
        reverseList.remove(n - 1);
        updateAverageFlips();
    }

    protected void addMove(int i, int j){
        moves.add(new Position(i,j));
        numOfMoves.set(numOfMoves.get() + 1);
    }

    protected Position getLastMove(){
        if(moves.size() > 0) {
            return moves.get(numOfMoves.get() - 1);
        }
        return null;
    }

    protected static Player clonePlayer(Player prev){
        Player newPlayer = new Player(prev.getId(),prev.getName(),prev.getType(),prev.getColor());
        newPlayer.reverseList = new ArrayList<>();
        newPlayer.moves = new ArrayList<>();

        newPlayer.setFlips(prev.getFlips());

        for(Position pos: prev.moves){
            newPlayer.addMove(pos.getRow(),pos.getColumn());
        }

        newPlayer.setReversal(prev.getReversal());
        for(Double rev : prev.reverseList){
            newPlayer.reverseList.add(rev);
        }
        newPlayer.score.set(prev.getScore());
        return newPlayer;
    }
}
