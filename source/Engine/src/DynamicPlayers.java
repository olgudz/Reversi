import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

public class DynamicPlayers {
    private String gameTitle;
    private int totalPlayers;


    public void setGameTitle(String name){
        this.gameTitle = gameTitle;
    }

    public void setTotalPlayers(int totalPlayers){
        this.totalPlayers = totalPlayers;
    }
    public String getGameTitle(){
        return gameTitle;
    }
    public int getTotalPlayers() {
        return totalPlayers;
    }
}
