import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ReplayStage {
    public ReplayStage() throws IOException {
        Stage subStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("Replay.fxml"));
        subStage.setTitle("Replay");
        Scene scene = new Scene(root);
        subStage.setScene(scene);
        scene.getStylesheets().add("style1.css");
        subStage.show();
    }
}