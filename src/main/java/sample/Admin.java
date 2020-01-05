package sample;

import javax.persistence.*;

@Entity (name = "admin")
public class Admin {
    @Id
    @Column (name = "admin_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int adminId;
    @Column (name="login")
    private String adminLogin;
    @Column (name = "adres_email")
    private String adminEmail;
    @Column (name = "haslo")
    private String adminPassword;

    public String getAdminLogin() {
        return adminLogin;
    }

    public void setAdminLogin(String adminLogin) {
        this.adminLogin = adminLogin;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }
}
