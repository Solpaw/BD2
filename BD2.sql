DROP TABLE BIEG CASCADE;
DROP TABLE BIEGACZ CASCADE;
DROP TABLE WYNIKI CASCADE;
DROP TABLE TRASA CASCADE;
DROP TABLE LOKALIZACJA CASCADE;
DROP TABLE ADMIN CASCADE;
DROP TABLE CENA CASCADE;
DROP TABLE Zapisy CASCADE;
DROP TABLE biegaczLog CASCADE;

CREATE TABLE Lokalizacja(
    lokalizacja_id SERIAL Primary KEY,
    miejscowosc VARCHAR(30) NOT NULL,
    ulica VARCHAR(30) NOT NULL,
    unique (miejscowosc,ulica)
);

CREATE TABLE Biegacz(
    biegacz_id SERIAL PRIMARY KEY,
    imie varchar(30) NOT NULL,
    nazwisko varchar(30) NOT NULL,
    adres_email varchar(30) NOT NULL UNIQUE,
    data_urodzenia DATE NOT NULL,
    Rozmiar_tshirt varchar(3),
    haslo varchar(255) NOT NULL
);

CREATE TABLE Cena(
    id SERIAL PRIMARY KEY,
    cena INTEGER NOT NULL,
    UNIQUE (cena)
);

CREATE TABLE Trasa(
    trasa_id SERIAL PRIMARY KEY,
    dlugosc INTEGER NOT NULL,
    ilosc_przeszkod INTEGER,
    cena_id INTEGER not null,
    UNIQUE (dlugosc,ilosc_przeszkod,cena_id),
    FOREIGN KEY (cena_id) REFERENCES Cena(id) ON DELETE CASCADE ON UPDATE CASCADE
);

Create table ADMIN(
    admin_id SERIAL PRIMARY KEY,
    login varchar(15) NOT NULL,
    adres_email varchar(30) NOT NULL,
    haslo  varchar(255) NOT NULL
);

CREATE TABLE Bieg(
    bieg_id SERIAL PRIMARY KEY,
    trasa_id INTEGER not null,
    lokalizacja_id INTEGER,
    data_biegu DATE not null,
    FOREIGN KEY (trasa_id) REFERENCES TRASA (trasa_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (lokalizacja_id) REFERENCES Lokalizacja(lokalizacja_id) ON DELETE CASCADE ON UPDATE CASCADE
 );

CREATE TABLE Zapisy(
    zapisy_id SERIAL PRIMARY KEY,
    biegacz_id INTEGER not null,
    bieg_id INTEGER not null,
    UNIQUE (biegacz_id,bieg_id),
    FOREIGN KEY(biegacz_id) REFERENCES Biegacz(biegacz_id) ON DELETE CASCADE,
    FOREIGN KEY (bieg_id) REFERENCES Bieg(bieg_id) ON DELETE CASCADE
);

CREATE TABLE  Wyniki(
    id SERIAL PRIMARY KEY,
    bieg_id INTEGER not null,
    biegacz_id INTEGER not null,
    miejsce INTEGER,
    czas NUMERIC(6,2),
    unique (bieg_id,biegacz_id),
    FOREIGN KEY (biegacz_id) REFERENCES Biegacz(biegacz_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (bieg_id) REFERENCES Bieg(bieg_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE biegaczLog(
    id INTEGER not null,
    akcja varchar(9) not null,
    czas timestamp not null
);

CREATE OR REPLACE FUNCTION logDodaniaBiegacza() RETURNS TRIGGER AS $$
    BEGIN
        INSERT INTO biegaczLog (id,akcja,czas) values (new.biegacz_id,'Dodanie',current_timestamp);
        return new;
    end;
$$ language plpgsql;

CREATE OR REPLACE FUNCTION logUsunieciaBiegacza() RETURNS TRIGGER AS $$
    BEGIN
        INSERT INTO biegaczLog (id,akcja,czas) values (old.biegacz_id,'Usuwanie',current_timestamp);
        return new;
    end;
$$ language plpgsql;

CREATE TRIGGER biegaczLogAdd after insert on biegacz for each row execute procedure logDodaniaBiegacza();
CREATE TRIGGER biegaczLogRemove after delete on biegacz for each row execute procedure logUsunieciaBiegacza();

CREATE OR REPLACE FUNCTION dodajBiegacza(imie VARCHAR, nazwisko varchar,adres_email varchar, data_urodzenia date,rozmiar_tshirt VARCHAR,haslo varchar) RETURNS VOID AS $$
    BEGIN
        INSERT INTO Biegacz (imie, nazwisko,adres_email, data_urodzenia,rozmiar_tshirt,haslo) VALUES (imie, nazwisko,adres_email,data_urodzenia,rozmiar_tshirt,haslo);
    END$$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION usunBiegacza(nr integer) returns void as $$
    begin
        delete from biegacz where biegacz_id = nr;
    end$$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION dodajBieg(trasa_id integer,data_biegu date,lokalizacja_id integer) returns void as $$
    begin
        INSERT INTO Bieg (trasa_id,data_biegu,lokalizacja_id) VALUES (trasa_id,data_biegu,lokalizacja_id);
    end$$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION usunBieg(nr integer) returns void as $$
    begin
        delete from bieg where bieg_id=nr;
    end$$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION dodajZapis(biegacz_id integer,bieg_id integer) returns void as $$
    begin
        INSERT INTO Zapisy(biegacz_id, bieg_id) VALUES (biegacz_id,bieg_id);
    end$$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION usunZapis(nr integer) returns void as $$
    begin
        delete from zapisy where zapisy_id=nr;
    end$$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION dodajWynik(biegacz_id integer,bieg_id integer,miejsce integer,czas numeric(6,2)) returns void as $$
    begin
        INSERT INTO WYNIKI (BIEGACZ_ID,bieg_id, miejsce, CZAS) VALUES (biegacz_id,bieg_id,miejsce,czas);
    end$$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION usunWynik(nr integer) returns void as $$
    begin
        delete from wyniki where id=nr;
    end$$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION dodajTrase(dlugosc integer, cena_id integer,ilosc_przeszkod integer) returns void as $$
    begin
        INSERT INTO TRASA (dlugosc, cena_id,ilosc_przeszkod) VALUES (dlugosc, cena_id,ilosc_przeszkod);
    end$$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION usunTrase(nr integer) returns void as $$
    begin
        delete from Trasa where trasa_id=nr;
    end$$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION dodajCene(cena integer) returns void as $$
    begin
        INSERT INTO Cena(cena) VALUES (cena);
    end$$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION usunCene(nr integer) returns void as $$
    begin
        delete from Cena where id=nr;
    end$$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION dodajLokalizajce(ulica varchar,miejscowosc varchar) returns void as $$
    begin
        INSERT INTO Lokalizacja (ulica,miejscowosc) VALUES (ulica,miejscowosc);
    end$$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION usunLokalizajce(nr integer) returns void as $$
    begin
        delete from Lokalizacja where lokalizacja_id=nr;
    end$$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION dodajAdmina(login varchar,adres_email varchar,haslo varchar) returns void as $$
    begin
        INSERT INTO Admin (login, adres_email,haslo) VALUES (login,adres_email,haslo);
    end$$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION usunAdmina(nr integer) returns void as $$
    begin
        delete from ADMIN where admin_id=nr;
    end$$
LANGUAGE PLPGSQL;


select dodajadmina('Solpaw','sio@gmail.com','1234');



select dodajLokalizajce('Augustowska','Lubin');
select dodajLokalizajce('Szkolna','Wrocław');
select dodajLokalizajce('Teczowa','Wrocław');
select dodajLokalizajce('Kochanowskiego','Wrocław');
select dodajLokalizajce('Obozowa','Lubin');

select dodajcene(10);
select dodajcene(20);
select dodajcene(30);

select dodajTrase(100,1,5);
select dodajTrase(100,1,10);
select dodajTrase(1000,3,20);
select dodajTrase(500,2,15);

select dodajBiegacza('Piotr', 'Strachoń','dio@gmail.com','1998-09-21','M','1234');
select dodajBiegacza('Karol','Tomaszewski', 'mister8@gmail.com','1968-06-06','XXL','2345');
select dodajBiegacza('Jan','Poniatowski', 'tyu@gmail.com','1966-06-06','S','3456');
select dodajBiegacza('Maciej','Godlewski', 'biegacz@gmail.com','1999-11-12','L','4311');
select dodajBiegacza('Maja','Kowalska', 'kowalska@gmail.com','1988-04-01','S','865432');
select dodajBiegacza('Marta','Dziurman', 'dziurman@gmail.com','1999-08-15','S','12357');
select dodajBiegacza('Kamila','Rakowska', 'radowska@gmail.com','1993-02-08','S','6423');
select dodajBiegacza('Luiza','Cichoń', 'cich@gmail.com','1996-09-24','M','098776');
select dodajBiegacza('Emilia','Stachowiak', 'stach@gmail.com','1998-06-16','S','2342');
select dodajBiegacza('Szymon','Piechowski', 'piechu@gmail.com','1989-03-28','M','54647');

select dodajBieg(1,'2019-04-01',1);
select dodajBieg(4,'2019-06-01',3);
select dodajBieg(3,'2020-02-01',4);
select dodajBieg(2,'2020-02-05',5);

select dodajZapis(1,1);
select dodajZapis(2,1);
select dodajZapis(3,1);
select dodajZapis(4,1);
select dodajZapis(5,1);
select dodajZapis(6,1);

select dodajZapis(5,2);
select dodajZapis(4,2);
select dodajZapis(8,2);
select dodajZapis(9,2);

select dodajZapis(6,3);
select dodajZapis(2,3);
select dodajZapis(7,3);
select dodajZapis(10,3);
select dodajZapis(9,4);
select dodajZapis(8,4);

select dodajWynik(1, 1,1, 18.54);
select dodajWynik(2, 1,2, 19.74);
select dodajWynik(3, 1,3, 19.84);
select dodajWynik(4, 1,4, 20.21);
select dodajWynik(5, 1,5, 21.54);
select dodajWynik(6, 1,6, 22.78);

select dodajWynik(5, 2,1, 30.56);
select dodajWynik(4, 2,2, 32.76);
select dodajWynik(9, 2,4, 35.98);
select dodajWynik(8, 2,3, 34.06);
