package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;

import java.time.LocalDate;

public class RegisterController {

    private SessionFactory sessionFactory;
    @FXML
    private TextField nameUser, surnameUser, emailUser;
    @FXML
    private PasswordField passwordUser, conPasswordUser;
    @FXML
    private DatePicker dateUser;
    @FXML
    private Label dateLabel, errorLabel;
    @FXML
    private ChoiceBox<ShirtSize> shirtSizeChoiceBox;


    @FXML
    public void initialize() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
        shirtSizeChoiceBox.getItems().addAll(ShirtSize.values());
        shirtSizeChoiceBox.setValue(ShirtSize.BRAK);
    }

    @FXML
    public void cancel(ActionEvent event) throws Exception{
        Parent parent = FXMLLoader.load(getClass().getClassLoader().getResource("sample.fxml"));
        Scene registerScene = new Scene(parent);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(registerScene);
        window.show();
    }

    public void register(ActionEvent event) throws Exception{
        String name = nameUser.getText();
        String surname = surnameUser.getText();
        String email = emailUser.getText();
        String password = passwordUser.getText();
        String conPassword = conPasswordUser.getText();
        LocalDate date = dateUser.getValue();
        String shirtSize;

        boolean goodData = true;
        if(name.trim().isEmpty()) {
            nameUser.setPromptText("Imię nie może być puste!");
            nameUser.setStyle("-fx-border-color: red;");
            goodData = false;
        } else nameUser.setStyle("-fx-border-color: transparent;");
        if(surname.trim().isEmpty()) {
            surnameUser.setPromptText("Nazwisko nie może być puste!");
            surnameUser.setStyle("-fx-border-color: red;");
            goodData = false;
        } else surnameUser.setStyle("-fx-border-color: transparent;");
        if(email.trim().isEmpty()) {
            emailUser.setPromptText("Email nie może być pusty!");
            emailUser.setStyle("-fx-border-color: red;");
            goodData = false;
        } else emailUser.setStyle("-fx-border-color: transparent;");
        if(password.trim().isEmpty()) {
            passwordUser.setPromptText("Hasło nie może być puste!");
            passwordUser.setStyle("-fx-border-color: red;");
            goodData = false;
        } else passwordUser.setStyle("-fx-border-color: transparent;");
        if(conPassword.trim().isEmpty()) {
            conPasswordUser.setPromptText("Hasło nie może być puste!");
            conPasswordUser.setStyle("-fx-border-color: red;");
            goodData = false;
        } else conPasswordUser.setStyle("-fx-border-color: transparent;");
        if(date==null) {
            dateLabel.setText("Data nie może być pusta!");
            dateLabel.setTextFill(Color.RED);
            goodData = false;
        } else {
            dateLabel.setText("Data urodzenia:");
            dateLabel.setTextFill(Color.BLACK);
        }

        ShirtSize shirt = shirtSizeChoiceBox.getValue();
        if(shirt==null) {
            shirtSize = "";
        } else shirtSize = shirt.getSize();

        if(!password.equals(conPassword)) {
            errorLabel.setText("Hasła muszą być identyczne!");
            conPasswordUser.setStyle("-fx-border-color: red;");
            goodData = false;
        } else {
            conPasswordUser.setStyle("-fx-border-color: transparent;");
            errorLabel.setText("");
        }
        if(!goodData) return;

        Runner runner = new Runner(name,surname,email,java.sql.Date.valueOf(date),password,shirtSize);
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try{
            session.save(runner);
            session.getTransaction().commit();
            session.close();
            cancel(event);
        } catch (HibernateException e){
            session.getTransaction().rollback();
            if(e.getCause().toString().contains("adres_email")) {
                errorLabel.setText("Email już zajęty!");
                emailUser.setStyle("-fx-border-color: red;");
            }
        } finally {
            if(session!=null) session.close();
        }
    }
}
