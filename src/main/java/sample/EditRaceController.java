package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.sql.Date;
import java.time.ZoneId;
import java.util.List;

public class EditRaceController {
    private SessionFactory sessionFactory;
    private Race race;
    @FXML
    private TextField cityField, streetField, lengthField, priceField, obsField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ChoiceBox<Price> priceBox;
    @FXML
    public void updateRace(ActionEvent event) {
        boolean correctInfo = true;
        race.setDateRace(Date.valueOf(datePicker.getValue()));

        Location location = new Location();
        location.setLocationCity(cityField.getText());
        location.setLocationStreet(streetField.getText());
        race.setLocation(location);

        Route route = new Route();
        try {
            route.setRouteLength(Integer.parseInt(lengthField.getText()));
        } catch (NumberFormatException e) {
            lengthField.setStyle("-fx-border-color: red;");
            lengthField.setText("");
            lengthField.setPromptText("Liczba całkowita!");
            correctInfo = false;
        }
        try {
            route.setRouteObstacles(Integer.parseInt(obsField.getText()));
        } catch (NumberFormatException e) {
            obsField.setStyle("-fx-border-color: red;");
            obsField.setText("");
            obsField.setPromptText("Liczba całkowita!");
            correctInfo = false;
        }

        Price price = priceBox.getValue();
        if(price.getPriceValue()<0) {
            price = new Price();
            try {
                price.setPriceValue(Integer.parseInt(priceField.getText()));
            } catch (NumberFormatException e) {
                priceField.setStyle("-fx-border-color: red;");
                priceField.setText("");
                priceField.setPromptText("Liczba całkowita!");
                return;
            }
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            try{
                session.save(price);
                session.getTransaction().commit();
            } catch(HibernateException e) {
                session.getTransaction().rollback();
            } finally {
                if(session!=null) session.close();
            }
        }
        if(!correctInfo) return;
        route.setPrice(price);
        race.setRoute(route);

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try{
            session.update(race);
            session.getTransaction().commit();
        } catch(HibernateException e) {
            session.getTransaction().rollback();
        } finally {
            if(session!=null) session.close();
        }

    }

    @FXML
    public void cancel(ActionEvent event) {

    }

    @FXML
    public void initialize() {
        sessionFactory = new Configuration().configure().buildSessionFactory();

    }

    public void setRace(Race race) {
        this.race = race;
        cityField.setText(race.getLocation().getLocationCity());
        streetField.setText(race.getLocation().getLocationStreet());
        lengthField.setText(Integer.toString(race.getRoute().getRouteLength()));
        obsField.setText(Integer.toString(race.getRoute().getRouteObstacles()));
        datePicker.setValue(race.getDateRace().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        priceBox.setConverter(new PriceConverter());
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Query query = session.createQuery("from cena");
        List prices = null;
        try{
            prices = query.list();
            session.getTransaction().commit();
        } catch(HibernateException e) {
            session.getTransaction().rollback();
        } finally {
            if(session!=null) session.close();
            if(prices==null) return;
        }
        ObservableList<Price> observableList = FXCollections.observableArrayList(prices);
        priceBox.setItems(observableList);
        Price dif = new Price();
        dif.setPriceValue(-1);
        priceBox.getItems().add(dif);
        int i=0;
        for(Object p:prices) {
            if(((Price) p).getPriceValue()==race.getRoute().getPrice().getPriceValue()) {
                priceBox.getSelectionModel().select(i);
                return;
            }
            i++;
        }
    }
}
