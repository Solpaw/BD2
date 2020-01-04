package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.io.IOException;

public class ChangePasswordController {
    private String password = null, oldPassword = null;
    @FXML
    private PasswordField oldPass, nPass, cNPass;
    @FXML
    private Button accept, decline;
    @FXML
    private Label errorLabel;
    private Boolean response=false;

    public void declineChanges(ActionEvent event) {
        response = false;
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.close();
    }

    public Boolean getResponse() {
        return response;
    }

    public void acceptChanges(ActionEvent event) {
        if(!oldPassword.equals(oldPass.getText())) {
            oldPass.setStyle("-fx-border-color: red;");
            errorLabel.setText("Złe hasło!");
            return;
        } else {
            oldPass.setStyle("-fx-border-color: transparent;");
            errorLabel.setText("");
        }
        if(!nPass.getText().equals(cNPass.getText())) {
            nPass.setStyle("-fx-border-color: red;");
            cNPass.setStyle("-fx-border-color: red;");
            errorLabel.setText("Hasła muszą być takie same!");
            return;
        } else {
            nPass.setStyle("-fx-border-color: transparent;");
            cNPass.setStyle("-fx-border-color: transparent;");
            errorLabel.setText("");
        }
        if(nPass.getText().trim().isEmpty()){
            nPass.setStyle("-fx-border-color: red;");
            errorLabel.setText("Hasło nie może być puste!");
            return;
        } else {
            nPass.setStyle("-fx-border-color: transparent;");
            errorLabel.setText("");
        }

        password = nPass.getText();
        response = true;
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.close();
    }

    public void setOldPassword(String pass) {
        this.oldPassword = pass;
    }

    public String getPassword(){
        return password;
    }
}
