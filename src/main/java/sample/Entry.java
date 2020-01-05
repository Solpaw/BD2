package sample;

import javax.persistence.*;

@Entity(name = "zapisy")
public class Entry {
    @Id @Column(name = "zapisy_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int entryId;
    @Column(name = "biegacz_id")
    private int runnerId;
    @ManyToOne @JoinColumn(name = "bieg_id",nullable = false)
    private Race race;

    public Entry(){};

    public Entry(int runnerId, Race race) {
        this.runnerId = runnerId;
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

    public int getRunnerId() {
        return runnerId;
    }

    public void setRunnerId(int runnerId) {
        this.runnerId = runnerId;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }
}
