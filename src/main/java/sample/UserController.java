package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.event.ActionEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserController {
    private Runner user;
    private boolean selected = false;
    @FXML
    private Button logOutButton;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Tab accountTab;
    @FXML
    private TextField nameTextField, surnameTextField, emailTextField, shirtTextField;
    @FXML
    private DatePicker datePicker;
    private SessionFactory sessionFactory;

    public void setUser(Runner runner) {
        this.user = runner;
        welcomeLabel.setText("Witaj, " + this.user.getUserName() + "!");
        nameTextField.setText(user.getUserName());
        surnameTextField.setText(user.getUserSurname());
        emailTextField.setText(user.getUserEmail());
        LocalDate localDate = LocalDate.parse(user.getUserBirthDate().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        datePicker.setValue(localDate);
        shirtTextField.setText(user.getUserShirtSize());
    }

    @FXML
    public void initialize() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
        accountTab.setOnSelectionChanged((event)->{
            if(selected = true && userDetailsChanged()) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getClassLoader().getResource("confirmWithPassScene.fxml"));
                Parent parent = null;
                try {
                    parent = loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ConfirmWithPassController controller = loader.getController();
                controller.setPassword(user.getUserPassword());
                controller.setQuestion("Czy wprowadzić zmiany?");
                Scene userScene = new Scene(parent);
                Stage window = new Stage();
                window.setScene(userScene);
                window.setTitle("Edycja danych konta");
                window.initModality(Modality.APPLICATION_MODAL);
                window.showAndWait();
                Boolean answer = controller.getAnswer();
                if(answer) {
                    updateUser();
                }
            }
            selected = !selected;
            nameTextField.setText(user.getUserName());
            surnameTextField.setText(user.getUserSurname());
            emailTextField.setText(user.getUserEmail());
            LocalDate localDate = LocalDate.parse(user.getUserBirthDate().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            datePicker.setValue(localDate);
            shirtTextField.setText(user.getUserShirtSize());
        });
    }

    @FXML
    public void logOut(ActionEvent event) throws IOException {
        this.user = null;
        Parent parent = FXMLLoader.load(getClass().getClassLoader().getResource("sample.fxml"));
        Scene registerScene = new Scene(parent);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(registerScene);
        window.show();
    }

    private boolean userDetailsChanged() {
        if(!user.getUserName().equals(nameTextField.getText())) return true;
        else if(!user.getUserSurname().equals(surnameTextField.getText())) return true;
        else if(!user.getUserShirtSize().equals(shirtTextField.getText())) return true;
        else if(!user.getUserBirthDate().toString().equals(datePicker.getValue().toString())) return true;
        else return false;
    }

    @FXML
    public void update() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("confirmWithPassScene.fxml"));
        Parent parent = null;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ConfirmWithPassController controller = loader.getController();
        controller.setPassword(user.getUserPassword());
        controller.setQuestion("Czy wprowadzić zmiany?");
        Scene userScene = new Scene(parent);
        Stage window = new Stage();
        window.setScene(userScene);
        window.setTitle("Edycja danych konta");
        window.initModality(Modality.APPLICATION_MODAL);
        window.showAndWait();
        Boolean answer = controller.getAnswer();
        if(answer) {
            updateUser();
        }
    }


    public void updateUser() {
        if(!userDetailsChanged()) return;
        user.setUserName(nameTextField.getText());
        user.setUserSurname(surnameTextField.getText());
        user.setUserShirtSize(shirtTextField.getText());
        user.setUserBirthDate(java.sql.Date.valueOf(datePicker.getValue()));

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try{
            session.update(user);
            session.getTransaction().commit();
        } catch(HibernateException e) {
            session.getTransaction().rollback();
        } finally {
            if(session!=null) session.close();
        }
    }

    @FXML
    public void changePassword(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("changePasswordScene.fxml"));
        Parent parent = loader.load();
        ChangePasswordController controller = loader.getController();
        controller.setOldPassword(user.getUserPassword());
        Scene userScene = new Scene(parent);
        Stage window = new Stage();
        window.setScene(userScene);
        window.setTitle("Zmiana hasła");
        window.initModality(Modality.APPLICATION_MODAL);
        window.showAndWait();
        String password = controller.getPassword();
        user.setUserPassword(password);
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try{
            session.update(user);
            session.getTransaction().commit();
        } catch(HibernateException e) {
            session.getTransaction().rollback();
        } finally {
            if(session!=null) session.close();
        }
    }

    @FXML
    public void deleteUser(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("confirmWithPassScene.fxml"));
        Parent parent = loader.load();
        ConfirmWithPassController controller = loader.getController();
        controller.setPassword(user.getUserPassword());
        controller.setQuestion("Czy na pewno usunąć konto?");
        Scene userScene = new Scene(parent);
        Stage window = new Stage();
        window.setScene(userScene);
        window.setTitle("Usunięcie konta");
        window.initModality(Modality.APPLICATION_MODAL);
        window.showAndWait();
        Boolean answer = controller.getAnswer();
        System.out.println(answer);
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try{
            session.delete(user);
            session.getTransaction().commit();
        } catch(HibernateException e) {
            session.getTransaction().rollback();
        } finally {
            if(session!=null) session.close();
        }
    }
}
