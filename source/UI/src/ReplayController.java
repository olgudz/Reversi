import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

import static java.lang.Integer.MAX_VALUE;

public class ReplayController implements Initializable {

    @FXML private Button next;
    @FXML private Button prev;
    @FXML private Button exit;
    @FXML private GridPane board;
    @FXML private Label GameType;
    @FXML private Label NumberOfPlayers;
    @FXML private Label CurrentPlayer;
    @FXML private TableView<Player> table;
    @FXML private TableColumn<Player, String> name;
    @FXML private TableColumn<Player, Integer> id;
    @FXML private TableColumn<Player, String> type;
    @FXML private TableColumn<Player, String> color;
    @FXML private TableColumn<Player, Integer> numberOfMoves;
    @FXML private TableColumn<Player, Integer> score;
    @FXML private TableColumn<Player, Double> flips;

    private List<GameManager.State> states = new ArrayList<>(GameManager.getStates());
    ObservableList<Player> players = FXCollections.observableArrayList();

    int position = 0;

    private boolean incPos() {
        if(position == 0){
            prev.setDisable(false);
        }
        if(position < states.size() - 1){
            position++;
        }
        else{
            next.setDisable(true);
            return false;
        }
        return true;
    }

    private boolean decPos(){
        if(position == states.size() - 1){
            next.setDisable(false);
        }
        if(position > 0){
            position--;
        }
        else{
            prev.setDisable(true);
            return false;
        }
        return true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prev.setDisable(true);
        fillTable();
        createBoard(states.get(0));
        CurrentPlayer.setText("Current Player:          " + states.get(position).getCurrent().getName());
        NumberOfPlayers.setText("Number Of Players:  " + states.get(position).getPlayers().size());
        GameType.setText("GameType:                " + states.get(position).getType());
        drawBoard();
    }

    public void fillTable(){
        id.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        name.setCellValueFactory(new PropertyValueFactory<Player, String>("Name"));
        type.setCellValueFactory(new PropertyValueFactory<Player, String>("Type"));
        color.setCellValueFactory(new PropertyValueFactory<Player, String>("Color"));
        numberOfMoves.setCellValueFactory(cellData -> cellData.getValue().numberOfMovesProperty().asObject());
        score.setCellValueFactory(cellData -> cellData.getValue().scoreProperty().asObject());
        flips.setCellValueFactory(cellData -> cellData.getValue().flipsProperty().asObject());
        color.setCellFactory(column -> {
            return new TableCell<Player, String>() {
                @Override
                protected void updateItem(String color, boolean empty) {
                    super.updateItem(color, empty);
                    if (color == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {

                        setText("");
                        if (color.equals("BLACK")) {
                            setStyle("-fx-background-color: black");
                        }else if (color.equals("BLUE")) {
                            setStyle("-fx-background-color: blue");
                        }else if (color.equals("RED")) {
                            setStyle("-fx-background-color: red");
                        }else if (color.equals("YELLOW")){
                            setStyle("-fx-background-color: yellow");
                        }
                        else setStyle("-fx-background-color: white");
                    }
                }
            };
        });
        table.setItems(players);
    }

    private void updateTableAndBoard(){
        drawBoard();
        players.clear();
        for(Player p: states.get(position).getPlayers()){
            players.add(p);
        }
        table.refresh();
        CurrentPlayer.setText("Current Player:          " + states.get(position).getCurrent().getName());
    }

    private void createBoard(GameManager.State state) {
        board.gridLinesVisibleProperty().setValue(true);
        board.alignmentProperty().setValue(Pos.CENTER);
        int rows = state.getBoard().getRows();
        int cols = state.getBoard().getCols();
        int size;
        if (state.getBoard().getRows() < 10 && state.getBoard().getCols() < 10) size = 50;
        else if (state.getBoard().getRows() < 20 && state.getBoard().getCols() < 20) size = 40;
        else size = 30;
        setLabels(rows, cols, size);
    }

    private void setLabels(int rows, int cols, int size){
        for (int i = 0; i <= rows; i++) {
            RowConstraints row = new RowConstraints();
            row.setPrefHeight(size);
            row.setMinHeight(size);
            row.setMaxHeight(MAX_VALUE);
            board.getRowConstraints().add(i, row);
            Label lbl;
            if (i != 0) lbl = new Label("" + i);
            else lbl = new Label("");
            lbl.setId("1lbl" + i);
            lbl.setAlignment(Pos.CENTER);
            lbl.setFont(Font.font("Cambria", 20));
            board.add(lbl, 0, i);
        }
        for (int j = 0; j <= cols; j++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPrefWidth(size);
            column.setMinWidth(size);
            column.setMaxWidth(MAX_VALUE);
            board.getColumnConstraints().add(j, column);
            Label lbl;
            if (j != 0) lbl = new Label("" + j);
            else lbl = new Label("");
            lbl.setId("2lbl" + j);
            lbl.setAlignment(Pos.CENTER);
            lbl.setFont(Font.font("Cambria", 20));
            board.add(lbl, j, 0);
        }
    }

    protected void drawBoard() {
        int value;
        int radius;
        if (states.get(position).getBoard().getRows() < 10 &&
                states.get(position).getBoard().getCols() < 10) radius = 25;
        else if (states.get(position).getBoard().getRows() < 20 &&
                states.get(position).getBoard().getCols() < 20) radius = 20;
        else radius = 15;
        for (int i = 0; i < states.get(position).getBoard().getRows(); i++) {
            for (int j = 0; j < states.get(position).getBoard().getCols(); j++) {
                int numOfPlayers = states.get(position).getPlayers().size();
                value = states.get(position).getBoard().getValue(i, j);
                Circle node = (Circle)getNode(i,j,board);
                Paint p = null;
                if(node != null) {
                    p = node.getFill();
                }
                Circle c = null;
                if ((value == -1 || value == 0) && node != null) {
                    board.getChildren().remove(node);
                    continue;
                }
                else if(value == -1 || value == 0)continue;
                else if (value == states.get(position).getPlayers().get(0).getId()) {
                    c = createCircle(0,p,radius,position,node);
                    if (c == null) continue;
                } else if (value == states.get(position).getPlayers().get(1).getId()) {
                    c = createCircle(1,p,radius,position,node);
                    if (c == null) continue;
                } else if (numOfPlayers > 2 && value == states.get(position).getPlayers().get(2).getId()) {
                    c = createCircle(2,p,radius,position,node);
                    if (c == null) continue;
                } else if (numOfPlayers > 3 && value == states.get(position).getPlayers().get(3).getId()) {
                    c = createCircle(3,p,radius,position,node);
                    if (c == null) continue;
                }
                c.setId(i*100 + j+ "0000");
                board.add(c, j + 1, i + 1);
            }
        }
    }

    public void nextHandler(){
        boolean res = incPos();
        if(res)
            updateTableAndBoard();
    }

    public void prevHandler(){
        boolean res = decPos();
        if(res)
            updateTableAndBoard();
    }

    public void exitHandler(){
        Stage stage = (Stage)exit.getScene().getWindow();
        stage.close();
    }

    protected static Node getNode(int row, int col, GridPane gridPane){
        List<Node> nodes = gridPane.getChildren();
        String id = row*100 + col + "0000";
        for(Node n: nodes){
            if(id.equals(n.getId())) {
                return n;
            }
        }
        return null;
    }

    protected Circle createCircle(int i, Paint p, int radius, int pos, Node node){
        Circle c  = null;
        String colorS = GameManager.states.get(pos).getPlayers().get(i).getColor();
        Color color = getColorFromString(colorS);
        if(p == null) {
            c = new Circle(0, 0, radius);
            c.setFill(Color.web(colorS));
        }
        else if( !p.toString().equals(color) ){
            c = new Circle(0, 0, radius);
            board.getChildren().remove(node);
            c.setFill(Color.web(colorS));
        }
        return c;
    }

    protected Color getColorFromString(String s){
        Color color = null;
        if(s.equals("RED"))
            color = Color.RED;
        else if (s.equals("BLACK"))
            color = Color.BLACK;
        else if (s.equals("BLUE"))
            color = Color.BLUE;
        else color = Color.YELLOW;
        return color;
    }

}
