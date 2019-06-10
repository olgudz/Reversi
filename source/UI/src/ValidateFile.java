
import javafx.application.Platform;
import javafx.scene.control.Alert;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.util.List;

public class ValidateFile {

    private boolean validXML = false;
    private boolean loaded = false;

    private final static String JAXB_XML_GAME_PACKAGE_NAME = "generated";

    protected generated.GameDescriptor unmarshallFile(File file ){
        generated.GameDescriptor gd = null;
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(JAXB_XML_GAME_PACKAGE_NAME);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                gd = (generated.GameDescriptor) unmarshaller.unmarshal(file);
            } catch (JAXBException e) {
                e.printStackTrace();
            }
            return  gd;
    }

    protected Runnable validateXML(File file){
        return() -> {
            try {
                SchemaFactory factory =
                        SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema = factory.newSchema(new StreamSource("Reversi.xsd"));
                Validator validator = schema.newValidator();
                validator.validate(new StreamSource(file.getAbsolutePath()));
                validXML = true;
            } catch (Exception ex) {
                Platform.runLater(createAlert(file.getName(), " not loaded : Unsupported Game Type"));
                validXML = false;
            }
        };

    }

    protected  Runnable detailsValidation(GameDescriptor gd){
        return()-> {
            if (validXML == false || gd == null) return;
            loaded = true;
            int rows = gd.getGame().getBoard().getRows();
            int cols = gd.getGame().getBoard().getCols();
            if (rows < 4 || rows > 50) {
                Platform.runLater(createAlert("Invalid number of rows. Number of rows must be between 4 and 50.\n",
                        "Please load another game."));
                loaded = false;
            } else if (cols < 4 || cols > 30) {
                createAlert("Invalid number of columns. Number of columns must be between 4 and 30.\n",
                        "Please load another game.");
                loaded = false;
            } else if (!isBoardSuitable(gd)) {
                Platform.runLater(createAlert("Size of the board is not suitable for all players.\n",
                        "Please load another game."));
                loaded = false;
            } else if (gd.getGame().getVariant().equals("Regular") && !positionsInOneBlock(gd)) {
                Platform.runLater(createAlert("Game type is 'Regular', but initial positions are not in one block.\n",
                        "Please load another game."));
                loaded = false;
            } else if (positionsOverlap(gd)) {
                Platform.runLater(createAlert("Initial positions overlap each other.\n",
                        "Please load another game."));
                loaded = false;
            } else if (overstepBoundaries(gd)) {
                Platform.runLater(createAlert("Initial positions overstep the boundaries of the board.\n",
                        "Please load another game."));
                loaded = false;
            }
            else if(!isValidId(gd)){
                Platform.runLater(createAlert("Two players with same id.\n",
                        "Please load another game."));
                loaded = false;
            }
            else if(!isEveryPlayerHasPosition(gd)){
                Platform.runLater(createAlert("Every player must have at least one initial position.\n",
                        "Please load another game."));
                loaded = false;
            }
            else if(!validNumberOfPlayers(gd)){
                Platform.runLater(createAlert("Number of players must be between 2 and 4.\n",
                        "Please load another game."));
                loaded = false;
            }
        };
    }

    private static boolean positionsInOneBlock(GameDescriptor gd){
        boolean res = false;
        List<Position> list = gd.getListOfInitialPositions();

        for(int i = 0; i < list.size(); i++){
            for(int j = 0; j < list.size(); j++){
                if(list.get(i).getDistance(list.get(j)) <= 1 && i != j) {
                    res = true;
                    break;
                }
            }
            if(res == false) return false;
            else res = false;
        }
        return true;
    }

    //return true if is overlap
    private static boolean positionsOverlap(GameDescriptor gd){
        List<Position> list = gd.getListOfInitialPositions();
        for(int i = 0; i < list.size(); i++){
            for(int j = 0; j < list.size();j++){
                if((list.get(i).compareTo(list.get(j)) == 0) && (i != j))
                    return true;
            }
        }
        return false;
    }

    private static boolean overstepBoundaries(GameDescriptor gd){
        List<Position> list = gd.getListOfInitialPositions();
        for(Position position: list){
            if(position.getRow() < 1 || position.getRow() > gd.getGame().getBoard().getRows())
                return true;
            if(position.getColumn() < 1 || position.getColumn() > gd.getGame().getBoard().getCols())
                return true;
        }
        return false;
    }

    private static boolean isBoardSuitable(GameDescriptor gd){
        int rows = gd.getGame().getBoard().getRows();
        int cols = gd.getGame().getBoard().getCols();
        int size = cols * rows;
        int initialPositions = gd.getGame().getInitialPositions().getNumOfAllInitialPositions();
        int numOfPlayers = gd.getGame().getInitialPositions().getParticipant().size();
        if((size - initialPositions)%numOfPlayers != 0)
            return false;
        return true;
    }

    public static Runnable createAlert(String msg1, String msg2){
        return () -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(msg1 + msg2);
            alert.setContentText("Ooops, there was an error!");
            alert.showAndWait();
        };
    }

    public boolean isFileLoaded(){return loaded;}

    public boolean isValidXML(){return validXML;}

    public static double getDistance(Position pos1, Position pos2){
        double x1 = pos1.getRow();
        double y1 = pos1.getColumn();
        double x2 = pos2.getRow();
        double y2 = pos2.getColumn();
        double distance = Math.sqrt(Math.pow(x1-x2,2) + Math.pow(y1-y2,2));
        return distance;
    }

    private boolean isValidId(GameDescriptor gd){
        boolean res = true;
        int size = gd.getPlayers().getPlayer().size();
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                if(gd.getPlayers().getPlayer().get(i).getId() == gd.getPlayers().getPlayer().get(j).getId() && i!= j){
                    res = false;
                    break;
                }
            }
            if (res == false) break;
        }
        return  res;
    }

    private boolean isEveryPlayerHasPosition(GameDescriptor gd){
        int numOfPlayers = gd.getPlayers().getPlayer().size();
        if(gd.getGame().getInitialPositions().getParticipant().size() < numOfPlayers){
            return false;
        }
        return true;
    }

    private boolean validNumberOfPlayers(GameDescriptor gd){
        int size = gd.getPlayers().getPlayer().size();
        if(size < 2 || size > 4){
            return false;
        }
        return  true;
    }
}
