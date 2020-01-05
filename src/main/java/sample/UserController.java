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
import org.hibernate.query.Query;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class UserController {
    private SessionFactory sessionFactory;
    private Runner user;
    private Boolean accountInfoChanged = false;
    private boolean accountTabSelected = false;
    @FXML
    private Label welcomeLabel, raceListErrorLabel;
    @FXML
    private Tab accountTab;
    @FXML
    private TextField nameTextField, surnameTextField, emailTextField, shirtTextField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ListView<Race> raceList;
    @FXML
    private ListView<Entry> entryList;

    @FXML
    public void enrollInRace() {
        Race race = raceList.getSelectionModel().getSelectedItem();
        Entry entry = new Entry(user.getUserId(),race);
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try{
            session.save(entry);
            session.getTransaction().commit();
            raceListErrorLabel.setText("");
        } catch(HibernateException e) {
            session.getTransaction().rollback();
            raceListErrorLabel.setText("Bieg został już wybrany!");
            return;
        } finally {
            if(session!=null) session.close();
        }
        updateEntries();
    }

    @FXML
    public void resignFromRace() {
        raceListErrorLabel.setText("");
        Entry entry = entryList.getSelectionModel().getSelectedItem();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try{
            session.delete(entry);
            session.getTransaction().commit();
        } catch(HibernateException e) {
            session.getTransaction().rollback();
            return;
        } finally {
            if(session!=null) session.close();
        }
        updateEntries();
    }

    private void updateEntries() {
        entryList.getItems().clear();
        List races = null;
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Query query = session.createQuery("from zapisy where biegacz_id= :id");
        query.setParameter("id",user.getUserId());
        try{
            races = query.list();
            session.getTransaction().commit();
        } catch(HibernateException e) {
            session.getTransaction().rollback();
            return;
        } finally {
            if(session!=null) session.close();
        }
        entryList.getItems().addAll(races);
    }

    public void setUser(Runner runner) {
        this.user = runner;
        welcomeLabel.setText("Witaj, " + this.user.getUserName() + "!");
        nameTextField.setText(user.getUserName());
        surnameTextField.setText(user.getUserSurname());
        emailTextField.setText(user.getUserEmail());
        LocalDate localDate = LocalDate.parse(user.getUserBirthDate().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        datePicker.setValue(localDate);
        shirtTextField.setText(user.getUserShirtSize());
        updateEntries();
    }

    @FXML
    public void initialize() {
        sessionFactory = new Configuration().configure().buildSessionFactory();

        //zakładka biegi
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Query query = session.createQuery("from bieg where data_biegu > :date");
        query.setParameter("date", LocalDate.now());
        List races = query.list();
        session.getTransaction().commit();
        session.close();
        raceList.getItems().addAll(races);

        //Zakładka konto
        accountTab.setOnSelectionChanged((event)->{
            if(accountTabSelected = true) {
               update();
            }
            accountTabSelected = !accountTabSelected;
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
        if(!user.getUserName().equals(nameTextField.getText())) accountInfoChanged = true;
        else if(!user.getUserSurname().equals(surnameTextField.getText())) accountInfoChanged = true;
        else if(!user.getUserShirtSize().equals(shirtTextField.getText())) accountInfoChanged = true;
        else if(!user.getUserBirthDate().toString().equals(datePicker.getValue().toString())) accountInfoChanged = true;
        return false;
    }

    @FXML
    public void update() {
        userDetailsChanged();
        if(!accountInfoChanged) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        ButtonType buttonTypeOne = new ButtonType("Zmień");
        ButtonType buttonTypeCancel = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeCancel,buttonTypeOne);
        alert.setTitle("Potwierdzenie");
        alert.setHeaderText("Zmiana danych konta");
        alert.setContentText("Czy zmienić dane?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get()==buttonTypeOne) updateUser();
        accountInfoChanged = false;
    }

    public void updateUser() {
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
        if(!controller.getResponse()) return;
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
        if(!controller.getAnswer()) return;
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
