package sample;

import javafx.fxml.FXML;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class ConfirmWithPassController {
    @FXML
    private PasswordField passField;
    @FXML
    private Label questionLabel;
    private Boolean answer = false;
    private String password;

    public void setPassword(String pass) {
        password = pass;
    }

    public void setQuestion(String question) {
        questionLabel.setText(question);
    }

    public void decline(ActionEvent event) {
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.close();
    }

    public void accept(ActionEvent event) {
        if(!password.equals(passField.getText())) {
            passField.setStyle("-fx-border-color: red;");
            return;
        }
        answer = true;
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.close();
    }

    public Boolean getAnswer() {
        return answer;
    }
}
