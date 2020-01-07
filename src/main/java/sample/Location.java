package sample;

import javax.persistence.*;
import java.util.Objects;

@Entity (name = "lokalizacja")
public class Location {

    @Id @Column(name = "lokalizacja_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int locationId;
    @Column(name = "miejscowosc")
    private String locationCity;
    @Column(name = "ulica")
    private String locationStreet;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(locationCity, location.locationCity) &&
                Objects.equals(locationStreet, location.locationStreet);
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getLocationCity() {
        return locationCity;
    }

    public void setLocationCity(String locationCity) {
        this.locationCity = locationCity;
    }

    public String getLocationStreet() {
        return locationStreet;
    }

    public void setLocationStreet(String locationStreet) {
        this.locationStreet = locationStreet;
    }

}
