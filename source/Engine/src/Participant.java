import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@XmlRootElement(name = "Participant")
public class Participant implements Serializable {
    private int number;
    private List<Position> positions;

    @XmlAttribute(name = "number")
    public void setNumber(int number) {
        this.number = number;
    }
    @XmlElement(name = "Position")
    public void setPosition(List<Position> positions ){ this.positions = positions; }

    public int getNumber() { return number; }
    public List<Position> getPositions() { return positions; }

    public int getNumOfPositions(){
        return positions.size();
    }
}
