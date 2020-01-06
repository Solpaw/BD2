package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import javafx.event.ActionEvent;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class AddRaceController {

    class PriceConverter extends StringConverter<Price> {
        @Override
        public String toString(Price price) {
            if(price.getPriceValue()<0) return "Inna";
            return Integer.toString(price.getPriceValue());
        }

        @Override
        public Price fromString(String s) {
            return null;
        }
    }

    private SessionFactory sessionFactory;

    @FXML
    private TextField cityField, streetField, lengthField, priceField, obsField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ChoiceBox<Price> priceBox;

    @FXML
    public void cancel(ActionEvent event) {
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.close();
    }

    @FXML
    public void addRace(ActionEvent event) {
        boolean correctInfo = true;
        Race race = new Race();
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
            session.save(price);
            session.getTransaction().commit();
            session.close();
        }
        if(!correctInfo) return;
        route.setPrice(price);
        race.setRoute(route);

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(route);
        session.save(location);
        session.save(race);
        session.getTransaction().commit();
        session.close();
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.close();
    }

    @FXML
    public void initialize() {
        sessionFactory = new Configuration().configure().buildSessionFactory();

        datePicker.setValue(LocalDate.now().plusDays(1));
        priceBox.setConverter(new PriceConverter());
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Query query = session.createQuery("from cena");
        List prices = query.list();
        ObservableList<Price> observableList = FXCollections.observableArrayList(prices);
        priceBox.setItems(observableList);
        Price dif = new Price();
        dif.setPriceValue(-1);
        priceBox.getItems().add(dif);
        priceBox.getSelectionModel().selectFirst();

        priceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Price>() {
            @Override
            public void changed(ObservableValue<? extends Price> observableValue, Price price, Price t1) {
                if(t1.getPriceValue()<0) priceField.setDisable(false);
                else priceField.setDisable(true);
            }
        });
    }
}
