package sample;

import javafx.fxml.FXML;

import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

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
        System.out.println(race);
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
    }
}
