package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;

import javafx.event.ActionEvent;
import org.postgresql.util.PSQLException;

import javax.persistence.NoResultException;
import java.util.List;

public class AddResultController {
    @FXML
    private TableView<Entry> runnerList;
    @FXML
    private Label errorLabel;
    @FXML
    private TextField placeField, timeField;
    @FXML
    private TableColumn<Entry, String> nameColumnRunner, emailColumnRunner, surnameColumnRunner, shirtColumnRunner,birthDateColumnRunner;
    private Race race;
    private SessionFactory sessionFactory;
    public void setRace(Race race) {
        this.race = race;
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(Entry.class).add(Restrictions.eq("race.idRace",race.getIdRace()));
        List results = null;
        try{
            results = crit.list();
            session.getTransaction().commit();
        } catch(HibernateException e) {
            session.getTransaction().rollback();
            return;
        } finally {
            if(session!=null) session.close();
        }
        ObservableList<Entry> observableList = FXCollections.observableList(results);
        runnerList.setItems(observableList);
    }

    public void initialize() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
        emailColumnRunner.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Entry, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Entry, String> entryStringCellDataFeatures) {
                return new SimpleStringProperty(entryStringCellDataFeatures.getValue().getRunner().getUserEmail());
            }
        });
        nameColumnRunner.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Entry, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Entry, String> entryStringCellDataFeatures) {
                return new SimpleStringProperty(entryStringCellDataFeatures.getValue().getRunner().getUserName());
            }
        });
        surnameColumnRunner.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Entry, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Entry, String> entryStringCellDataFeatures) {
                return new SimpleStringProperty(entryStringCellDataFeatures.getValue().getRunner().getUserSurname());
            }
        });
        birthDateColumnRunner.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Entry, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Entry, String> entryStringCellDataFeatures) {
                return new SimpleStringProperty(entryStringCellDataFeatures.getValue().getRunner().getUserBirthDate().toString());
            }
        });
        shirtColumnRunner.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Entry, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Entry, String> entryStringCellDataFeatures) {
                return new SimpleStringProperty(entryStringCellDataFeatures.getValue().getRunner().getUserShirtSize());
            }
        });
    }
    @FXML
    public void cancel(ActionEvent event) {
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.close();
    }
    @FXML
    public void addResult(ActionEvent event) {
        if(runnerList.getSelectionModel().getSelectedItem()==null) {
            errorLabel.setText("Proszę wybrać biegacza!");
            return;
        }
        Float time = null;
        try{
            time = Float.valueOf(timeField.getText());
        } catch (NumberFormatException e) {
            timeField.setStyle("-fx-border-color: red;");
            timeField.setPromptText("Liczba w formacie: mm.ss");
            return;
        }
        int place = 0;
        try{
            place = Integer.valueOf(placeField.getText());
        } catch (NumberFormatException e) {
            placeField.setStyle("-fx-border-color: red;");
            placeField.setPromptText("Liczba całkowita!");
            return;
        }

        Result result = new Result();
        result.setRaceResult(race);
        result.setRunnerResult(runnerList.getSelectionModel().getSelectedItem().getRunner());
        result.setTimeResult(time);
        result.setPlaceResult(place);
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try{
            session.save(result);
            session.getTransaction().commit();
        } catch(HibernateException e) {
            session.getTransaction().rollback();
            if(e.getCause().toString().contains("podwójna wartość klucza")) {
                errorLabel.setText("Wynik tego gracza jest już wpisany!");
            }
            return;
        } finally {
            if(session!=null) session.close();
        }
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.close();
    }
}
