import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.*;

public class Players {
    private ObservableList<Player> player;


    public ObservableList<Player> getPlayer() {
        if (player == null) {
            player = FXCollections.observableArrayList();
        }
        return this.player;
    }

//    public static List<Player> getPlayersClone(List<Player> players){
//        List<Player> newList = new ArrayList<>() ;
//        for(Player p: players){
//            Player newP = Player.clonePlayer(p);
//            System.out.println("in clonePlayers : " + newP.getName());
//        }
//        return newList;
//    }

}
