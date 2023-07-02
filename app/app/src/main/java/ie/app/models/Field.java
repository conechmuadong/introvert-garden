package ie.app.models;

public class Field {

    public String name;
    public MeasuredData measuredData = new MeasuredData();

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
