import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@XmlRootElement(name = "Game")
public class Game implements Serializable {

    private Board board = null;
    private String variant = null;
    private InitialPositions initialPositions;
    private boolean isActive = false;
    private List<Position> moves = new ArrayList<>();

//    public void addMove(int i, int j){
//        moves.add(new Position(i,j));
//    }

    public List<Position> getMoves() { return moves; }

    public void setBoard(Board board){ this.board = board; }

    public  void setVariant(String variant){ this.variant = variant; }
    public void setInitialPositions(InitialPositions positions){ this.initialPositions = positions; }

    public  void setActive(boolean isActive){this.isActive = isActive;}

    public Board getBoard () { return board; }
    public String getVariant() { return variant; }
    public boolean isActive() { return isActive; }
    public InitialPositions getInitialPositions() { return initialPositions; }

}
