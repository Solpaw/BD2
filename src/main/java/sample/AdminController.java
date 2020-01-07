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
    private TableView<Race> raceList;
    @FXML
    private TableColumn<Race, Integer> idColumnRace, lengthColumnRace, objColumnRace, priceColumnRace, idColummnPastRace, lengthColumnPastRace;
    @FXML
    private TableColumn<Race, Date> dateColumnRace;
    @FXML
    private TableColumn<Race, String> cityColumnRace, streetColumnRace;

    public void setAdmin(Admin a) {
        admin = a;
        welcomeLabel.setText("Witaj, "+admin.getAdminLogin());
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

    @FXML
    public void initialize() {
        sessionFactory = new Configuration().configure().buildSessionFactory();

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
}
