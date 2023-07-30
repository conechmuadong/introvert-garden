package ie.app.models;

import java.util.ArrayList;
import java.util.List;

public class CustomizedParameter {

    public float distanceBetweenHole = 5.0f;
    public float acreage;
    public boolean autoIrrigation;
    public float distanceBetweenRow;
    public float dripRate;
    public float fertilizationLevel;
    public int numberOfHoles;
    public float scaleRain;
    public List<Phase> fieldCapacity = new ArrayList<Phase>();

    public void setFieldCapacity(List<Phase> fieldCapacity) {
        this.fieldCapacity = fieldCapacity;
    }


    @Override
    public String toString() {
        String ret = "CustomizedParameter: " + distanceBetweenHole + "\n";
        for(Phase p : fieldCapacity) ret += p.toString() + "\n";
        return ret;
    }

    public List<Phase> getFieldCapacity() {
        return fieldCapacity;
    }
}