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
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;

import javafx.event.ActionEvent;

import javax.persistence.NoResultException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class AddRaceController {
    private SessionFactory sessionFactory;

    @FXML
    private TextField cityField, streetField, lengthField, priceField, obsField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ChoiceBox<Price> priceBox;
    @FXML
    private ChoiceBox<String> cityBox;
    @FXML
    private ChoiceBox<String> lenBox, obsBox, streetBox;


    @FXML
    public void cancel(ActionEvent event) {
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.close();
    }

    @FXML
    public void addRace(ActionEvent event) {
        String cityName = "",streetName = "";
        if(cityField.isDisabled()){
            cityName = cityBox.getSelectionModel().getSelectedItem();
        } else cityName = cityField.getText();
        if(streetField.isDisabled()){
            streetName = streetBox.getSelectionModel().getSelectedItem();
        } else streetName = streetField.getText();

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Query query = session.createQuery("from lokalizacja where locationCity=:city and locationStreet=:street");
        query.setParameter("city",cityName);
        query.setParameter("street",streetName);
        Location location = null;
        try{
            location = (Location) query.getSingleResult();
            session.getTransaction().commit();
        } catch(NoResultException e) {
            session.getTransaction().rollback();
        } finally {
            if(session!=null) session.close();
        }
        if(location==null) {
            location = new Location();
            location.setLocationCity(cityName);
            location.setLocationStreet(streetName);
            session = sessionFactory.openSession();
            session.beginTransaction();
            try{
                session.save(location);
                session.getTransaction().commit();
            } catch(HibernateException e) {
                session.getTransaction().rollback();
            } finally {
                if(session!=null) session.close();
            }
        }

        Price price = null;
        if(priceField.isDisabled()){
            price = priceBox.getValue();
        } else {
            price = new Price();
            try{
                price.setPriceValue(Integer.parseInt(priceField.getText()));
            } catch (NumberFormatException e) {
                priceField.setStyle("-fx-border-color: red;");
                priceField.setText("");
                priceField.setPromptText("Liczba całkowita!");
                return;
            }

            session = sessionFactory.openSession();
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

        int routeLen = 0,routeObs=0;
        if(lengthField.isDisabled()){
            routeLen = Integer.parseInt(lenBox.getSelectionModel().getSelectedItem());
        } else {
            try{
                routeLen = Integer.parseInt(lengthField.getText());
            } catch (NumberFormatException e) {
                lengthField.setStyle("-fx-border-color: red;");
                lengthField.setText("");
                lengthField.setPromptText("Liczba całkowita!");
                return;
            }
        }
        if(obsField.isDisabled()){
            routeObs = Integer.parseInt(obsBox.getSelectionModel().getSelectedItem());
        } else {
            try{
                routeObs = Integer.parseInt(obsField.getText());
            } catch (NumberFormatException e) {
                obsField.setStyle("-fx-border-color: red;");
                obsField.setText("");
                obsField.setPromptText("Liczba całkowita!");
                return;
            }
        }
        session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(Route.class).add(Restrictions.eq("price.priceId",price.getPriceId()))
                .add(Restrictions.eq("routeLength",routeLen)).add(Restrictions.eq("routeObstacles",routeObs));
        Route route = null;
        try{
            route = (Route) crit.uniqueResult();
            session.getTransaction().commit();
        } catch(HibernateException e) {
            session.getTransaction().rollback();
        } finally {
            if(session!=null) session.close();
        }
        if(route==null) {
            route = new Route();
            route.setRouteObstacles(routeObs);
            route.setRouteLength(routeLen);
            route.setPrice(price);
            session = sessionFactory.openSession();
            session.beginTransaction();
            try{
                session.save(route);
                session.getTransaction().commit();
            } catch(HibernateException e) {
                session.getTransaction().rollback();
            } finally {
                if(session!=null) session.close();
            }
        }

        Race race = new Race();
        race.setDateRace(Date.valueOf(datePicker.getValue()));
        race.setLocation(location);
        race.setRoute(route);
        session = sessionFactory.openSession();
        session.beginTransaction();
        try{
            session.save(race);
            session.getTransaction().commit();
        } catch(HibernateException e) {
            session.getTransaction().rollback();
        } finally {
            if(session!=null) session.close();
        }
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.close();
    }

    @FXML
    public void initialize() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
        datePicker.setValue(LocalDate.now().plusDays(1));

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Query query = session.createQuery("select distinct routeLength from trasa");
        List routes = null;
        try{
            routes = query.list();
            session.getTransaction().commit();
        } catch(HibernateException e) {
            session.getTransaction().rollback();
        } finally {
            if(session!=null) session.close();
            if(routes==null) return;
        }
        for(Object s:routes){
            lenBox.getItems().add(Integer.toString((Integer)s));
        }
        lenBox.getItems().add("Inne");

        lenBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if(!t1.equals("Inne")){
                    lengthField.setDisable(true);
                    obsBox.setDisable(false);
                    obsField.setDisable(true);
                    Session session1 = sessionFactory.openSession();
                    session1.beginTransaction();
                    Query query1 = session1.createQuery("select distinct routeObstacles from trasa where dlugosc= :len");
                    query1.setParameter("len",Integer.parseInt(t1));
                    List loc = null;
                    try{
                        loc = query1.list();
                        session1.getTransaction().commit();
                    } catch(HibernateException e) {
                        session1.getTransaction().rollback();
                    } finally {
                        if(session1!=null) session1.close();
                        if(loc==null) return;
                    }
                    obsBox.getItems().clear();
                    for(Object k:loc){
                        obsBox.getItems().add(Integer.toString((Integer)k));
                    }
                    obsBox.getItems().add("Inne");
                    obsBox.getSelectionModel().selectFirst();
                } else {
                    lengthField.setDisable(false);
                    obsBox.setDisable(true);
                    obsField.setDisable(false);
                }
            }
        });
        obsBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if(t1!=null && !t1.equals("Inne")) {
                    obsField.setDisable(true);
                } else {
                    obsField.setDisable(false);
                }
            }
        });
        lenBox.getSelectionModel().selectFirst();

        session = sessionFactory.openSession();
        session.beginTransaction();
        query = session.createQuery("select distinct locationCity from lokalizacja");
        List locations = null;
        try{
            locations = query.list();
            session.getTransaction().commit();
        } catch(HibernateException e) {
            session.getTransaction().rollback();
        } finally {
            if(session!=null) session.close();
            if(locations==null) return;
        }
        ObservableList<String> locationObservableList = FXCollections.observableArrayList(locations);
        cityBox.setItems(locationObservableList);
        cityBox.getItems().add("Inne");

        cityBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if(!t1.equals("Inne")) {
                    cityField.setDisable(true);
                    streetBox.setDisable(false);
                    streetField.setDisable(true);
                    Session session1 = sessionFactory.openSession();
                    session1.beginTransaction();
                    Query query1 = session1.createQuery("select distinct locationStreet from lokalizacja where miejscowosc= :city");
                    query1.setParameter("city",t1);
                    List loc = null;
                    try{
                        loc = query1.list();
                        session1.getTransaction().commit();
                    } catch(HibernateException e) {
                        session1.getTransaction().rollback();
                    } finally {
                        if(session1!=null) session1.close();
                        if(loc==null) return;
                    }
                    ObservableList<String> locationObservableList = FXCollections.observableArrayList(loc);
                    streetBox.setItems(locationObservableList);
                    streetBox.getItems().add("Inne");
                    streetBox.getSelectionModel().selectFirst();
                } else {
                    cityField.setDisable(false);
                    streetBox.setDisable(true);
                    streetField.setDisable(false);
                }
            }
        });
        cityBox.getSelectionModel().selectFirst();

        streetBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if(t1!=null&&!t1.equals("Inne")){
                    streetField.setDisable(true);
                } else streetField.setDisable(false);
            }
        });
        streetBox.getSelectionModel().selectFirst();

        session = sessionFactory.openSession();
        session.beginTransaction();
        query = session.createQuery("from cena");
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
        ObservableList<Price> priceObservableList = FXCollections.observableArrayList(prices);
        priceBox.setConverter(new PriceConverter());
        Price price = new Price();
        price.setPriceValue(-1);
        priceBox.setItems(priceObservableList);
        priceBox.getItems().add(price);
        priceBox.getSelectionModel().selectFirst();

        priceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Price>() {
            @Override
            public void changed(ObservableValue<? extends Price> observableValue, Price price, Price t1) {
                if(t1.getPriceValue()<0){
                    priceField.setDisable(false);
                } else priceField.setDisable(true);
            }
        });
    }
}
