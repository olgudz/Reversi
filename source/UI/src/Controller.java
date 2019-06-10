import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


import static java.lang.Integer.MAX_VALUE;

public class Controller implements Initializable {

    @FXML private Button loadGame;
    @FXML private Button startGame;
    @FXML private Button endGame;
    @FXML private GridPane board;
    @FXML private Label GameType;
    @FXML private Label NumberOfPlayers;
    @FXML private Label CurrentPlayer;
    @FXML private Button undo;
    @FXML private Button replay;
    @FXML private ImageView image1;
    @FXML private ImageView image2;

    @FXML private TableView<Player> table;
    @FXML private TableColumn<Player, String> name;
    @FXML private TableColumn<Player, Integer> id;
    @FXML private TableColumn<Player, String> type;
    @FXML private TableColumn<Player, String> color;
    @FXML private TableColumn<Player, Integer> numberOfMoves;
    @FXML private TableColumn<Player, Integer> score;
    @FXML private TableColumn<Player, Double> flips;

    private ValidateFile valid = new ValidateFile();
    private GameDescriptor gameDescriptor = new GameDescriptor();
    private generated.GameDescriptor gd = new generated.GameDescriptor();
    private StringProperty current = new SimpleStringProperty("");
    private Player currentPlayer;
    private static File selectedFile = null;

    public void loadHandler(ActionEvent event) throws InterruptedException {
        replay.setDisable(true);
        undo.setDisable(true);

        FileChooser fc = new FileChooser();
        //fc.setInitialDirectory(new File("C:\\Users\\Olga G\\IdeaProjects\\Ex2\\UI\\src\\resources"));         /////////////////////////////todo remove!
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XML files", "*.xml"));
        selectedFile = fc.showOpenDialog(null);

        if (selectedFile != null) valid.validateXML(selectedFile).run();
        if (valid.isValidXML())GameManager.setGameDescriptor(selectedFile, valid, gameDescriptor).run();
        if (gameDescriptor != null) valid.detailsValidation(gameDescriptor).run();
        if (!valid.isFileLoaded()) {
            startGame.setDisable(true);
        }
        if (valid.isFileLoaded()) {
            startGame.setDisable(false);
            startGame.setOpacity(1);
        }
    }

    protected static void setFile(File file){
        selectedFile = file;
    }

    public void startGameHandler(ActionEvent event) {

        if (!valid.isFileLoaded()) {
            startGame.setDisable(true);
            startGame.setOpacity(0.5);
            return;
        }
        image1.setOpacity(1);
        image1.setDisable(false);
        board.setGridLinesVisible(true);
        loadGame.setDisable(true);
        loadGame.setOpacity(0);
        endGame.setDisable(false);
        startGame.setDisable(true);
        startGame.setOpacity(0);
        undo.setDisable(false);

        currentPlayer = gameDescriptor.getPlayers().getPlayer().get(0);
        current.set(currentPlayer.getName());
        fillTable();

        GameManager.State state = new GameManager.State(gameDescriptor.getPlayers().getPlayer(),
                gameDescriptor.getGame().getBoard(), currentPlayer, gameDescriptor.getGame().getVariant());
        GameManager.states.add(state);
        board.getChildren().get(0).setId("0000");
        createBoard(GameManager.states.get(0));

        GameType.setText("GameType:                " + gameDescriptor.getGame().getVariant());
        NumberOfPlayers.setText("Number Of Players:  " + gameDescriptor.getPlayers().getPlayer().size());

        drawBoard();
    }

    public void fillTable() {
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
                        } else if (color.equals("BLUE")) {
                            setStyle("-fx-background-color: blue");
                        } else if (color.equals("RED")) {
                            setStyle("-fx-background-color: red");
                        } else if (color.equals("YELLOW")) {
                            setStyle("-fx-background-color: yellow");
                        } else setStyle("-fx-background-color: white");
                    }
                }
            };
        });
        table.setItems(gameDescriptor.getPlayers().getPlayer());
    }

    public void replayHandler(ActionEvent event) throws IOException {
        ReplayStage replay = new ReplayStage();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        current.addListener((observable) -> {
            CurrentPlayer.setText("Current Player:          " + current.getValue());
        });
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

        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= cols; j++) {
                setBox(i, j);
            }
        }
    }

    public void endGameHandler(ActionEvent e) {
        List<Node> children = board.getChildren();
        for (Node n : children) {
            if (n.getId().substring(0, 3).equals("box")) {
                n.setOnMouseClicked(null);
            }
        }
        replay.setDisable(false);
        undo.setDisable(true);
        endGame.setDisable(true);
    }

    public void setStyle1(ActionEvent e) {
        this.board.getScene().getStylesheets().clear();
        this.board.getScene().getStylesheets().add(getClass().getResource("style1.css").toExternalForm());

        //board.getScene().getStylesheets().add("style1.css");
    }

    public void setStyle2(ActionEvent e) {
        this.board.getScene().getStylesheets().clear();
        this.board.getScene().getStylesheets().add(getClass().getResource("style2.css").toExternalForm());
    }

    public void setStyle3(ActionEvent e) {
        this.board.getScene().getStylesheets().clear();
        this.board.getScene().getStylesheets().add(getClass().getResource("style3.css").toExternalForm());
    }

    public void undoGameHandler(ActionEvent e) {
        int size = GameManager.states.size() - 1;
        if (size > 0) {
            currentPlayer = GameManager.prevPlayer(currentPlayer, gameDescriptor);
            current.set(currentPlayer.getName());
            Board prevBoard = GameManager.states.get(size - 1).getBoard();

            gameDescriptor.getGame().getBoard().copyBoard(prevBoard);
            currentPlayer.undo();
            GameManager.updateScores(gameDescriptor);
            if (currentPlayer.getType().equals("Computer")) {
                computerMove();
            }

            GameManager.states.remove(size);
            GameManager.setMoveDone(false);
            drawBoard();
        }
    }

    protected void drawBoard() {
        int value;
        int radius;
        int pos = GameManager.states.size() - 1;
        if (GameManager.states.get(pos).getBoard().getRows() < 10 &&
                GameManager.states.get(pos).getBoard().getCols() < 10) radius = 25;
        else if (GameManager.states.get(pos).getBoard().getRows() < 20 &&
                GameManager.states.get(pos).getBoard().getCols() < 20) radius = 20;
        else radius = 15;
        for (int i = 0; i < GameManager.states.get(pos).getBoard().getRows(); i++) {
            for (int j = 0; j < GameManager.states.get(pos).getBoard().getCols(); j++) {
                int numOfPlayers = GameManager.states.get(pos).getPlayers().size();
                value = GameManager.states.get(pos).getBoard().getValue(i, j);
                Circle node = (Circle) getNode(i, j, board);
                Paint p = null;
                if (node != null) {
                    p = node.getFill();
                }
                Circle c = null;
                if ((value == -1 || value == 0) && node != null) {
                    board.getChildren().remove(node);
                    continue;
                } else if (value == -1 || value == 0) continue;
                else if (value == GameManager.states.get(pos).getPlayers().get(0).getId()) {
                    c = createCircle(0, p, radius, pos, node);
                    if (c == null) continue;
                } else if (value == GameManager.states.get(pos).getPlayers().get(1).getId()) {
                    c = createCircle(1, p, radius, pos, node);
                    if (c == null) continue;
                } else if (numOfPlayers > 2 && value == GameManager.states.get(pos).getPlayers().get(2).getId()) {
                    c = createCircle(2, p, radius, pos, node);
                    if (c == null) continue;
                } else if (numOfPlayers > 3 && value == GameManager.states.get(pos).getPlayers().get(3).getId()) {
                    c = createCircle(3, p, radius, pos, node);
                    if (c == null) continue;
                }
                c.setId(i * 100 + j + "0000");
                board.add(c, j + 1, i + 1);
            }
        }
    }

    protected static Node getNode(int row, int col, GridPane gridPane) {
        List<Node> nodes = gridPane.getChildren();
        String id = row * 100 + col + "0000";
        for (Node n : nodes) {
            if (id.equals(n.getId())) {
                return n;
            }
        }
        return null;
    }

    protected Color getColorFromString(String s) {
        Color color = null;
        if (s.equals("RED"))
            color = Color.RED;
        else if (s.equals("BLACK"))
            color = Color.BLACK;
        else if (s.equals("BLUE"))
            color = Color.BLUE;
        else color = Color.YELLOW;
        return color;
    }

    protected Circle createCircle(int i, Paint p, int radius, int pos, Node node) {
        Circle c = null;
        String colorS = GameManager.states.get(pos).getPlayers().get(i).getColor();
        Color color = getColorFromString(colorS);
        if (p == null) {
            c = new Circle(0, 0, radius);
            c.setFill(Color.web(colorS));
        } else if (!p.toString().equals(color)) {
            c = new Circle(0, 0, radius);
            board.getChildren().remove(node);
            c.setFill(Color.web(colorS));
        }
        return c;
    }

    private void computerMove() {
        GameManager.setMoveDone(false);
        drawBoard();
        while (currentPlayer.getType().equals("Computer")) {
            GameManager.computerMove(gameDescriptor, currentPlayer).run();
            if (GameManager.isGameOver())
                endGameHandler(new ActionEvent());
            drawBoard();
            currentPlayer = GameManager.nextPlayer(currentPlayer, gameDescriptor);
            current.setValue(currentPlayer.getName());
        }
    }

    private void setLabels(int rows, int cols, int size) {
        for (int i = 0; i <= rows; i++) {
            RowConstraints row = new RowConstraints();
            row.setPrefHeight(size);
            row.setMinHeight(size);
            row.setMaxHeight(MAX_VALUE);
            board.getRowConstraints().add(i, row);
            Label lbl;
            if (i != 0) {
                if (rows < 15) {
                    lbl = new Label("   " + i);
                    lbl.setFont(Font.font("Cambria", FontWeight.BOLD, 30));

                } else {
                    lbl = new Label(" " + i);
                    lbl.setFont(Font.font("Cambria", FontWeight.BOLD, 20));
                }
            } else lbl = new Label("");
            lbl.setId("1lbl" + i);
            lbl.setAlignment(Pos.CENTER);

            board.add(lbl, 0, i);
        }
        for (int j = 0; j <= cols; j++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPrefWidth(size);
            column.setMinWidth(size);
            column.setMaxWidth(MAX_VALUE);
            board.getColumnConstraints().add(j, column);
            Label lbl;
            if (j != 0) {
                if (cols < 15) {
                    lbl = new Label("   " + j);
                    lbl.setFont(Font.font("Cambria", FontWeight.BOLD, 30));
                } else {
                    lbl = new Label(" " + j);
                    lbl.setFont(Font.font("Cambria", FontWeight.BOLD, 20));
                }
            } else lbl = new Label("");
            lbl.setId("2lbl" + j);
            lbl.setAlignment(Pos.CENTER);
            board.add(lbl, j, 0);
        }
    }

    private void setBox(int i, int j) {
        Pane box = new Pane();
        box.setId("box" + i * 10 + j);
        box.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Node source = (Node) event.getSource();
                int colIndex = board.getColumnIndex(source);
                int rowIndex = board.getRowIndex(source);
                GameManager.playerMove(rowIndex, colIndex, currentPlayer, gameDescriptor).run();
                if (GameManager.isGameOver()) {
                    endGameHandler(new ActionEvent());
                }
                if (GameManager.isMoveDone()) {
                    currentPlayer = GameManager.nextPlayer(currentPlayer, gameDescriptor);
                    current.set(currentPlayer.getName());
                    computerMove();
                }
            }
        });
        board.add(box, j, i);
    }

}
