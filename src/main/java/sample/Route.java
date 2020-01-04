package sample;

import javax.persistence.*;

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
