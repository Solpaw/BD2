package sample;

import javax.persistence.*;

@Entity(name = "zapisy")
public class Entry {
    @Id @Column(name = "zapisy_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int entryId;
    @ManyToOne @JoinColumn(name = "biegacz_id",nullable = false)
    private Runner runner;
    @ManyToOne @JoinColumn(name = "bieg_id",nullable = false)
    private Race race;

    public Entry(){};

    public Entry(Runner runner, Race race) {
        this.runner = runner;
        this.race = race;
    }

    @Override
    public String toString() {
        return race.toString();
    }

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public Runner getRunner() {
        return runner;
    }

    public void setRunner(Runner runner) {
        this.runner = runner;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }
}
