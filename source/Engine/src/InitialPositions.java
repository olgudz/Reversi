import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "InitialPositions")
public class InitialPositions {
    private List<Participant> participants ;

    @XmlElement(name = "Participant")
    void setParticipant(List<Participant> participants){this.participants = participants;}

    public List<Participant> getParticipant() { return participants; }

    public int getNumOfAllInitialPositions(){
        int numOfPositions = 0;
        for(Participant participant: participants){
            numOfPositions += participant.getNumOfPositions();
        }
        return  numOfPositions;
    }
}
