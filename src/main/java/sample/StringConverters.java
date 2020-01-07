package sample;

import javafx.util.StringConverter;

class PriceConverter extends StringConverter<Price> {
    @Override
    public String toString(Price price) {
        if(price.getPriceValue()<0) return "Inna";
        return Integer.toString(price.getPriceValue());
    }

    @Override
    public Price fromString(String s) {
        return null;
    }
}

class CityConverter extends StringConverter<Location> {
    @Override
    public String toString(Location price) {
        return price.getLocationCity();
    }

    @Override
    public Location fromString(String s) {
        return null;
    }
}

class StreetConverter extends StringConverter<Location> {
    @Override
    public String toString(Location price) {
        return price.getLocationStreet();
    }

    @Override
    public Location fromString(String s) {
        return null;
    }
}


