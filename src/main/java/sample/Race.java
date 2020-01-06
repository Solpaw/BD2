package sample;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "bieg")
public class Race {
    @Id @Column(name = "bieg_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idRace;
    @Column (name = "data_biegu")
    private Date dateRace;
    @ManyToOne @JoinColumn(name = "lokalizacja_id", nullable = false)
    private Location location;
    @ManyToOne @JoinColumn(name = "trasa_id",nullable = false)
    private Route route;

    @Override
    public String toString() {
        return "" +
                idRace +
                ", " + dateRace +
                ", " + location.getLocationCity() +
                ", " + route.getRouteLength() +
                ", " + route.getPrice().getPriceValue();
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public int getIdRace() {
        return idRace;
    }

    public void setIdRace(int idRace) {
        this.idRace = idRace;
    }

    public Date getDateRace() {
        return dateRace;
    }

    public void setDateRace(Date dateRace) {
        this.dateRace = dateRace;
    }
}
