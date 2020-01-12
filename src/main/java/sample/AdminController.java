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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import javafx.util.Callback;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;


import javax.persistence.NoResultException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class AdminController {
    private SessionFactory sessionFactory;
    private Admin admin;
    @FXML
    private Label welcomeLabel;
    @FXML
    private TableView<Race> raceList, pastRaceList;
    @FXML
    private TableView<Runner> runnerList;
    @FXML
    private TableColumn<Race, Integer> idColumnRace, lengthColumnRace, objColumnRace, priceColumnRace, idColummnPastRace, lengthColumnPastRace;
    @FXML
    private TableColumn<Race, Date> dateColumnRace, dateColumnPastRace;
    @FXML
    private TableColumn<Race, String> cityColumnRace, streetColumnRace, cityColumnPastRace;
    @FXML
    private TableColumn<Runner, String> nameColumnRunner, emailColumnRunner, surnameColumnRunner, shirtColumnRunner;
    @FXML
    private TableColumn<Runner, Date> birthDateColumnRunner;
    @FXML
    private TableView<Result> resultsList;
    @FXML
    private TableColumn<Result,Integer> placeResults;
    @FXML
    private TableColumn<Result,Float> timeResults;
    @FXML
    private TableColumn<Result,String> nameResults,surnameResults;

    @FXML
    public void seeRunners() throws IOException {
        if(raceList.getSelectionModel().getSelectedItem()==null) return;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("raceParticipantsScene.fxml"));
        Parent parent = loader.load();
        RaceParticipantsController controller = loader.getController();
        controller.setRace(raceList.getSelectionModel().getSelectedItem());
        Scene userScene = new Scene(parent);
        Stage window = new Stage();
        window.setScene(userScene);
        window.setTitle("Lista uczestników");
        window.show();
    }

    public void setAdmin(Admin a) {
        admin = a;
        welcomeLabel.setText("Witaj, "+admin.getAdminLogin());
    }

    private void updateRunners() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Query query = session.createQuery("from biegacz");
        List runners = null;
        try{
            runners = query.list();
            session.getTransaction().commit();
        } catch(HibernateException e) {
            session.getTransaction().rollback();
            return;
        } finally {
            if(session!=null) session.close();
        }
        ObservableList<Runner> observableList = FXCollections.observableArrayList(runners);
        runnerList.setItems(observableList);
    }

    private void updateRaces() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Query query = session.createQuery("from bieg where data_biegu > :date");
        query.setParameter("date", LocalDate.now());
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
        ObservableList<Race> observableList = FXCollections.observableArrayList(races);
        raceList.setItems(observableList);
    }

    private void updateResults(Race race) {
        Session ses = sessionFactory.openSession();
        ses.beginTransaction();
        Query q = ses.createQuery("from wyniki where bieg_id = :id");
        q.setParameter("id",race.getIdRace());
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
    }

    @FXML
    public void initialize() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
        //zakładka biegi
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
            updateResults(newSelection);
        });
        //zakładka użytkownicy
        emailColumnRunner.setCellValueFactory(new PropertyValueFactory<>("userEmail"));
        nameColumnRunner.setCellValueFactory(new PropertyValueFactory<>("userName"));
        surnameColumnRunner.setCellValueFactory(new PropertyValueFactory<>("userSurname"));
        birthDateColumnRunner.setCellValueFactory(new PropertyValueFactory<>("userBirthDate"));
        shirtColumnRunner.setCellValueFactory(new PropertyValueFactory<>("userShirtSize"));
        updateRunners();
        //zakładka biegi
        idColumnRace.setCellValueFactory(new PropertyValueFactory<>("idRace"));
        dateColumnRace.setCellValueFactory(new PropertyValueFactory<>("dateRace"));
        cityColumnRace.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Race, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Race, String> raceStringCellDataFeatures) {
                return new SimpleStringProperty(raceStringCellDataFeatures.getValue().getLocation().getLocationCity());
            }
        });
        streetColumnRace.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Race, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Race, String> raceStringCellDataFeatures) {
                return new SimpleStringProperty(raceStringCellDataFeatures.getValue().getLocation().getLocationStreet());
            }
        });
        lengthColumnRace.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Race, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Race, Integer> raceIntegerCellDataFeatures) {
                return new SimpleIntegerProperty(raceIntegerCellDataFeatures.getValue().getRoute().getRouteLength()).asObject();
            }
        });
        objColumnRace.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Race, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Race, Integer> raceIntegerCellDataFeatures) {
                return new SimpleIntegerProperty(raceIntegerCellDataFeatures.getValue().getRoute().getRouteObstacles()).asObject();
            }
        });
        priceColumnRace.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Race, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Race, Integer> raceIntegerCellDataFeatures) {
                return new SimpleIntegerProperty(raceIntegerCellDataFeatures.getValue().getRoute().getPrice().getPriceValue()).asObject();
            }
        });
        updateRaces();
    }

    @FXML
    public void removeRunner(ActionEvent event) {
        Runner runner = runnerList.getSelectionModel().getSelectedItem();
        if(runner==null) return;
        if(!ConfirmationAlert.userConfirmation("Usunięcie użytkownika","Czy usunąć użytkownika?")) return;
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try{
            session.delete(runner);
            session.getTransaction().commit();
        } catch(HibernateException e) {
            session.getTransaction().rollback();
            return;
        } finally {
            if(session!=null) session.close();
        }
        updateRunners();
    }

    @FXML
    public void logOut(ActionEvent event) throws IOException {
        this.admin = null;
        Parent parent = FXMLLoader.load(getClass().getClassLoader().getResource("sample.fxml"));
        Scene registerScene = new Scene(parent);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(registerScene);
        window.show();
    }

    @FXML
    public void addRace(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("addRaceScene.fxml"));
        Parent parent = loader.load();
        AddRaceController controller = loader.getController();
        Scene userScene = new Scene(parent);
        Stage window = new Stage();
        window.setScene(userScene);
        window.setTitle("Dodaj bieg");
        window.initModality(Modality.APPLICATION_MODAL);
        window.showAndWait();
        updateRaces();
    }

    @FXML
    public void removeRace() {
        Race race = raceList.getSelectionModel().getSelectedItem();
        if(race==null) return;
        if(!ConfirmationAlert.userConfirmation("Usuwanie biegu","Czy usunąć bieg?")) return;
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try{
            session.delete(race);
            session.getTransaction().commit();
        } catch(HibernateException e) {
            session.getTransaction().rollback();
            return;
        } finally {
            if(session!=null) session.close();
        }
        updateRaces();
    }

    @FXML
    public void editRace() throws IOException {
        if(raceList.getSelectionModel().getSelectedItem()==null) return;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("editRaceScene.fxml"));
        Parent parent = loader.load();
        EditRaceController controller = loader.getController();
        controller.setRace(raceList.getSelectionModel().getSelectedItem());
        Scene userScene = new Scene(parent);
        Stage window = new Stage();
        window.setScene(userScene);
        window.setTitle("Edytuj bieg");
        window.initModality(Modality.APPLICATION_MODAL);
        window.showAndWait();
        updateRaces();
    }

    @FXML
    public void addResult() throws IOException {
        if(pastRaceList.getSelectionModel().getSelectedItem()==null) return;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("addResultScene.fxml"));
        Parent parent = loader.load();
        AddResultController controller = loader.getController();
        controller.setRace(pastRaceList.getSelectionModel().getSelectedItem());
        Scene userScene = new Scene(parent);
        Stage window = new Stage();
        window.setScene(userScene);
        window.setTitle("Dodaj wynik biegu o id: "+ Integer.toString(pastRaceList.getSelectionModel().getSelectedItem().getIdRace()));
        window.initModality(Modality.APPLICATION_MODAL);
        window.showAndWait();
        updateResults(pastRaceList.getSelectionModel().getSelectedItem());
    }

    @FXML
    public void removeResult() {
        if(!ConfirmationAlert.userConfirmation("Usunięcie wyniku","Czy na pewno usunąć wynik?")) return;
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try{
            session.delete(resultsList.getSelectionModel().getSelectedItem());
            session.getTransaction().commit();
        } catch(NoResultException e) {
            session.getTransaction().rollback();
        } finally {
            if(session!=null) session.close();
        }
        updateResults(pastRaceList.getSelectionModel().getSelectedItem());
    }
}
