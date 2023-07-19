package ie.app.models;

public class Field {

    public String name;
    public MeasuredData measuredData = new MeasuredData();
    public CustomizedParameter customizedParameter = new CustomizedParameter();
    public IrrigationInformation irrigationInformation = new IrrigationInformation();

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + measuredData.toString() + "\n" + customizedParameter.toString();
    }
}
