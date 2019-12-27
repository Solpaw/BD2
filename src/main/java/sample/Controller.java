package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.postgresql.util.PSQLException;

import javafx.event.ActionEvent;

import javax.persistence.NoResultException;
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
    private SessionFactory sessionFactory = null;

    @FXML
    public void initialize() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    @FXML
    public void register(ActionEvent event) throws Exception{
        Parent parent = FXMLLoader.load(getClass().getClassLoader().getResource("registerFxml.fxml"));
        Scene registerScene = new Scene(parent);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(registerScene);
        window.show();
    }

    @FXML
    public void logIn(ActionEvent event) throws Exception{
        Runner user = null;
        Admin admin = null;
        String email = emailField.getText();
        String password = passField.getText();

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Query query= null;
        try {
            query = session.createQuery("from biegacz where userEmail = :email");
            query.setParameter("email",email);
            user = (Runner)query.getSingleResult();
            session.getTransaction().commit();
        } catch(NoResultException e) {
            session.getTransaction().rollback();
        } finally {
            if(session!=null)session.close();
        }
        if(user!=null) {
            if(!user.getUserPassword().toString().equals(password)) return;
            effectLabel.setTextFill(Color.GREEN);
            effectLabel.setText("Logowanie...");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("userScene.fxml"));
            Parent parent = loader.load();
            UserController controller = loader.getController();
            controller.setUser(user);
            Scene userScene = new Scene(parent);
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(userScene);
            window.show();
        } else {
            session = sessionFactory.openSession();
            session.beginTransaction();
            try {
                query = session.createQuery("from admin where adminEmail = :email");
                query.setParameter("email",email);
                admin = (Admin) query.getSingleResult();
                session.getTransaction().commit();
            } catch(NoResultException e) {
                session.getTransaction().rollback();
                effectLabel.setTextFill(Color.RED);
                effectLabel.setText("Niprawidłowy email/hasło");
                return;
            } finally {
                if(session!=null)session.close();
            }
            effectLabel.setTextFill(Color.GREEN);
            effectLabel.setText("Logowanie admina...");
        }
    }
}
