package sample;

public enum ShirtSize {
    BRAK(""),
    S("S"),
    M("M"),
    L("L"),
    XL("XL"),
    XXL("XXL");

    public String getSize() {
        return size;
    }

    private final String size;
    ShirtSize(String s) {
        size = s;
    }

    public static ShirtSize getShirt(String s) {
        for(ShirtSize value:ShirtSize.values()){
            if(value.getSize().equals(s)) return value;
        }
        return null;
    }
}
