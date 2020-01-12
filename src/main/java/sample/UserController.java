package sample;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.event.ActionEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

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
    private TableView<Race> raceList, pastRaceList;
    @FXML
    private TableView<Entry> entryList;
    @FXML
    private TableColumn<Race, Integer> idColumnRace, lengthColumnRace, objColumnRace, priceColumnRace, idColummnPastRace, lengthColumnPastRace;
    @FXML
    private TableColumn<Race, Date> dateColumnRace, dateColumnPastRace;
    @FXML
    private TableColumn<Race, String> cityColumnRace, streetColumnRace, cityColumnPastRace;
    @FXML
    private TableColumn<Entry,Integer>  idColumnEntry, lengthColumnEntry, objColumnEntry, priceColumnEntry;
    @FXML
    private TableColumn<Entry,String> cityColumnEntry, streetColumnEntry,dateColumnEntry;
    @FXML
    private TableView<Result> resultsList;
    @FXML
    private TableColumn<Result,Integer> placeResults;
    @FXML
    private TableColumn<Result,Float> timeResults;
    @FXML
    private TableColumn<Result,String> nameResults,surnameResults;

    @FXML
    public void enrollInRace() {
        Race race = raceList.getSelectionModel().getSelectedItem();
        if(race==null) return;
        Entry entry = new Entry(user,race);
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
        if(entry==null) return;
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
        ObservableList<Entry> lol = FXCollections.observableArrayList(races);
        entryList.setItems(lol);
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
        //zakładka wyniki
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Query query = session.createQuery("from bieg where data_biegu < :date");
        query.setParameter("date",LocalDate.now());
        List races = null;
        try{
            races = query.list();
            session.getTransaction().commit();
        } catch(HibernateException e) {
            session.getTransaction().rollback();
            return;
        } finally {
            if(session!=null) session.close();
        }
        idColummnPastRace.setCellValueFactory(new PropertyValueFactory<>("idRace"));
        dateColumnPastRace.setCellValueFactory(new PropertyValueFactory<>("dateRace"));
        cityColumnPastRace.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Race, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Race, String> raceStringCellDataFeatures) {
                return new SimpleStringProperty(raceStringCellDataFeatures.getValue().getLocation().getLocationCity());
            }
        });
        lengthColumnPastRace.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Race, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Race, Integer> raceIntegerCellDataFeatures) {
                return new SimpleIntegerProperty(raceIntegerCellDataFeatures.getValue().getRoute().getRouteLength()).asObject();
            }
        });
        ObservableList<Race> observableList = FXCollections.observableArrayList(races);
        pastRaceList.setItems(observableList);

        placeResults.setCellValueFactory(new PropertyValueFactory<>("placeResult"));
        timeResults.setCellValueFactory(new PropertyValueFactory<>("timeResult"));
        nameResults.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Result, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Result, String> resultsStringCellDataFeatures) {
                return new SimpleStringProperty(resultsStringCellDataFeatures.getValue().getRunnerResult().getUserName());
            }
        });
        surnameResults.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Result, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Result, String> resultsStringCellDataFeatures) {
                return new SimpleStringProperty(resultsStringCellDataFeatures.getValue().getRunnerResult().getUserSurname());
            }
        });
        pastRaceList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            Session ses = sessionFactory.openSession();
            ses.beginTransaction();
            Query q = ses.createQuery("from wyniki where bieg_id = :id");
            q.setParameter("id",newSelection.getIdRace());
            List r = null;
            try{
                r = q.list();
                ses.getTransaction().commit();
            } catch(HibernateException e) {
                ses.getTransaction().rollback();
                return;
            } finally {
                if(ses!=null) ses.close();
            }
            ObservableList<Result> resultObservableList = FXCollections.observableArrayList(r);
            resultsList.setItems(resultObservableList);
        });

        //zakładka biegi
        session = sessionFactory.openSession();
        session.beginTransaction();
        query = session.createQuery("from bieg where data_biegu > :date");
        query.setParameter("date", LocalDate.now());
        races = null;
        try{
            races = query.list();
            session.getTransaction().commit();
        } catch(HibernateException e) {
            session.getTransaction().rollback();
            return;
        } finally {
            if(session!=null) session.close();
        }
        idColumnRace.setCellValueFactory(new PropertyValueFactory<>("idRace"));
        idColumnEntry.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Entry, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Entry, Integer> entryIntegerCellDataFeatures) {
                return new SimpleIntegerProperty(entryIntegerCellDataFeatures.getValue().getRace().getIdRace()).asObject();
            }
        });
        dateColumnRace.setCellValueFactory(new PropertyValueFactory<>("dateRace"));
        dateColumnEntry.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Entry, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Entry, String> entryStringCellDataFeatures) {
                return new SimpleStringProperty(entryStringCellDataFeatures.getValue().getRace().getDateRace().toString());
            }
        });
        cityColumnRace.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Race, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Race, String> raceStringCellDataFeatures) {
                return new SimpleStringProperty(raceStringCellDataFeatures.getValue().getLocation().getLocationCity());
            }
        });
        cityColumnEntry.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Entry, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Entry, String> entryStringCellDataFeatures) {
                return new SimpleStringProperty(entryStringCellDataFeatures.getValue().getRace().getLocation().getLocationCity());
            }
        });
        streetColumnRace.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Race, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Race, String> raceStringCellDataFeatures) {
                return new SimpleStringProperty(raceStringCellDataFeatures.getValue().getLocation().getLocationStreet());
            }
        });
        streetColumnEntry.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Entry, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Entry, String> entryStringCellDataFeatures) {
                return new SimpleStringProperty(entryStringCellDataFeatures.getValue().getRace().getLocation().getLocationStreet());
            }
        });
        lengthColumnRace.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Race, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Race, Integer> raceIntegerCellDataFeatures) {
                return new SimpleIntegerProperty(raceIntegerCellDataFeatures.getValue().getRoute().getRouteLength()).asObject();
            }
        });
        lengthColumnEntry.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Entry, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Entry, Integer> entryIntegerCellDataFeatures) {
                return new SimpleIntegerProperty(entryIntegerCellDataFeatures.getValue().getRace().getRoute().getRouteLength()).asObject();
            }
        });
        objColumnRace.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Race, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Race, Integer> raceIntegerCellDataFeatures) {
                return new SimpleIntegerProperty(raceIntegerCellDataFeatures.getValue().getRoute().getRouteObstacles()).asObject();
            }
        });
        objColumnEntry.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Entry, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Entry, Integer> entryIntegerCellDataFeatures) {
                return new SimpleIntegerProperty(entryIntegerCellDataFeatures.getValue().getRace().getRoute().getRouteObstacles()).asObject();
            }
        });
        priceColumnRace.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Race, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Race, Integer> raceIntegerCellDataFeatures) {
                return new SimpleIntegerProperty(raceIntegerCellDataFeatures.getValue().getRoute().getPrice().getPriceValue()).asObject();
            }
        });
        priceColumnEntry.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Entry, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Entry, Integer> entryIntegerCellDataFeatures) {
                return new SimpleIntegerProperty(entryIntegerCellDataFeatures.getValue().getRace().getRoute().getPrice().getPriceValue()).asObject();
            }
        });
        observableList = FXCollections.observableArrayList(races);
        raceList.setItems(observableList);

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
        if(ConfirmationAlert.userConfirmation("Zmiana danych konta","Czy zmienić dane?")) updateUser();
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
