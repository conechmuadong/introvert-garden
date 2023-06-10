package ie.app.models;

public class MeasuredData {
    public float air_humidity = 30;
    public float radiation = 30;
    public float soil_humidity_30 = 30;
    public float soil_humidity_60 = 30;
    public float temperature = 30;

    class Time {
        int day, month, year;
        int hour, min, second;

        @Override
        public String toString() {
            return day + ":" + month + ":" + year + "  "
                    + hour + ":" + min + ":" + second;
        }
    }

    Time time;

    @Override
    public String toString() {
        return "Measured data at: " + time.toString() + "\n";
    }
}
