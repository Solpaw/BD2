package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.postgresql.util.PSQLException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passField;
    @FXML
    private Label effectLabel;
    SessionFactory sessionFactory = null;

    @FXML
    public void initialize() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    @FXML
    public void logIn() throws Exception{
        Runner user = null;
        String email = emailField.getText();
        String password = passField.getText();
//        Date date = new Date(System.currentTimeMillis());
//        Runner runner = new Runner("Paweł","Kuriata",email,date,password,"M");
//        Session session = sessionFactory.openSession();
//        session.beginTransaction();
//        session.save(runner);
//        session.getTransaction().commit();
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Query query = session.createQuery("from biegacz where userEmail = :email");
        query.setParameter("email",email);
        try{
            user = (Runner)query.getSingleResult();
            session.getTransaction().commit();
            session.close();
        } catch (Exception e) {
            effectLabel.setTextFill(Color.RED);
            effectLabel.setText("Niprawidłowy email/hasło");
            return;
        }
        if(!user.getUserPassword().toString().equals(password)) {
            effectLabel.setTextFill(Color.RED);
            effectLabel.setText("Niprawidłowy email/hasło");
            return;
        }
        effectLabel.setTextFill(Color.GREEN);
        effectLabel.setText("Logowanie...");
    }
}
