package sample;

import javax.persistence.*;

@Entity (name = "wyniki")
public class Results {
    @Id
    @Column(name = "id")
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int idResult;
    @Column (name = "miejsce")
    private int placeResult;
    @Column (name = "czas")
    private float timeResult;
    @ManyToOne @JoinColumn(name = "bieg_id",nullable = false)
    private Race raceResult;
    @ManyToOne @JoinColumn(name = "biegacz_id",nullable = false)
    private Runner runnerResult;

    public int getIdResult() {
        return idResult;
    }

    public void setIdResult(int idResult) {
        this.idResult = idResult;
    }

    public int getPlaceResult() {
        return placeResult;
    }

    public void setPlaceResult(int placeResult) {
        this.placeResult = placeResult;
    }

    public float getTimeResult() {
        return timeResult;
    }

    public void setTimeResult(float timeResult) {
        this.timeResult = timeResult;
    }

    public Race getRaceResult() {
        return raceResult;
    }

    public void setRaceResult(Race raceResult) {
        this.raceResult = raceResult;
    }

    public Runner getRunnerResult() {
        return runnerResult;
    }

    public void setRunnerResult(Runner runnerResult) {
        this.runnerResult = runnerResult;
    }
}
