package sample;

import javafx.fxml.FXML;

public class UserController {
    private Runner user;

    public void setUser(Runner runner) {
        System.out.println("Lol");
        this.user = runner;
        System.out.println(user.getUserName());
    }

    @FXML
    public void initialize() {

    }
}
