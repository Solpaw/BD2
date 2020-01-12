package sample;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class ConfirmationAlert {
    public static boolean userConfirmation (String header,String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        ButtonType buttonTypeOne = new ButtonType("Potwierdź");
        ButtonType buttonTypeCancel = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeCancel,buttonTypeOne);
        alert.setTitle("Potwierdzenie");
        alert.setHeaderText(header);
        alert.setContentText(content);
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get()==buttonTypeOne) return true;
        return false;
    }
}
