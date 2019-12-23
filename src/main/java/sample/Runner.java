package sample;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Entity (name = "biegacz")
public class Runner {
    @Id @Column (name = "biegacz_id")
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int userId;
    @Column (name = "imie")
    private String userName;
    @Column (name = "nazwisko")
    private String userSurname;
    @Column (name = "adres_email")
    private String userEmail;
    @Type(type="date")
    @Column (name = "data_urodzenia")
    private Date userBirthDate;
    @Column (name = "rozmiar_tshirt")
    private String userShirtSize;
    @Column (name = "haslo")
    private String userPassword;

    public Runner() {}
    public Runner(String name, String surname, String email, Date userBirthDate, String password, String userShirtSize) {
        this.userName = name;
        this.userEmail = email;
        this.userBirthDate = userBirthDate;
        this.userPassword = password;
        this.userShirtSize = userShirtSize;
        this.userSurname = surname;
    }

    public void setUserId(int id) {
        this.userId = id;
    }

    public void setUserName(String name) {
        this.userName = name;
    }

    public int getUserId() {
        return this.userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public Date getUserBirthDate() {
        return userBirthDate;
    }

    public void setUserBirthDate(Date userBirthDate) {
        this.userBirthDate = userBirthDate;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserShirtSize() {
        return userShirtSize;
    }

    public void setUserShirtSize(String userShirtSize) {
        this.userShirtSize = userShirtSize;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }
}
