package ie.app.models;

import java.util.ArrayList;
import java.util.List;

public class TreeData {
    public double LDM, SDM, RDM, SRDM, LA;
    public double mDMl, Clab;
    public double growTime;
    public ArrayList<Double> rootLength, rootTips, soilWaterCapacity, contL, nuptL;

    public void copy(TreeData another) {
        this.LDM = another.LDM;
        this.LDM = another.LDM;
        this.LDM = another.LDM;
        this.LDM = another.LDM;
        this.LDM = another.LDM;

        this.LDM = another.LDM;
        this.LDM = another.LDM;

        this.rootLength.clear();
        this.rootTips.clear();
        this.soilWaterCapacity.clear();
        this.contL.clear();
        this.nuptL.clear();

        for (int i = 0; i < 5; i++) {
            this.rootLength.add(another.rootLength.get(i));
            this.rootTips.add(another.rootTips.get(i));
            this.soilWaterCapacity.add(another.soilWaterCapacity.get(i));
            this.contL.add(another.contL.get(i));
            this.nuptL.add(another.nuptL.get(i));
        }
    }

}
