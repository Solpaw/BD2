package sample;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "trasa")
public class Route {
    @Id
    @Column(name = "trasa_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int routeId;
    @Column(name = "dlugosc")
    private int routeLength;
    @Column(name = "ilosc_przeszkod")
    private int routeObstacles;
    @ManyToOne @JoinColumn(name = "cena_id", nullable = false)
    private Price price;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return  routeLength == route.routeLength &&
                routeObstacles == route.routeObstacles &&
                Objects.equals(price, route.price);
    }


    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public int getRouteLength() {
        return routeLength;
    }

    public void setRouteLength(int routeLength) {
        this.routeLength = routeLength;
    }

    public int getRouteObstacles() {
        return routeObstacles;
    }

    public void setRouteObstacles(int routeObstacles) {
        this.routeObstacles = routeObstacles;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }
}
