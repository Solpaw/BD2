package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;

import javafx.event.ActionEvent;
import java.util.List;

public class RaceParticipantsController {
    @FXML
    private TableView<Entry> runnerList;
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
    @FXML
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
    public void exit(ActionEvent event) {
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.close();
    }
}
