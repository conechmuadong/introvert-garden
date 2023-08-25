package ie.app.api;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.utilities.Tree;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.concurrent.ExecutionException;

import ie.app.fragments.MeasuredDataFragment;
import ie.app.models.CustomizedParameter;
import ie.app.models.Field;
import ie.app.models.IrrigationInformation;
import ie.app.models.MeasuredData;
import ie.app.models.Phase;
import ie.app.models.TreeData;
import ie.app.models.User;


public class FirebaseAPI {

    static FirebaseDatabase database;
    static final String instance = "https://sample-c5146-default-rtdb.firebaseio.com/";
    final static private double cuttingDryMass = 75.4; // (g)
    final static private double numberOfSoilLayer = 5;

    public static Task<User> getUser(String userID) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(userID);

        TaskCompletionSource<User> taskCompletionSource = new TaskCompletionSource<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = new User();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Field data = new Field();
                    data.setName(childSnapshot.getKey());
                    user.addField(data);
                }
                taskCompletionSource.setResult(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                taskCompletionSource.setException(databaseError.toException());
            }
        });

        return taskCompletionSource.getTask();
    }

    public static Task<Field> getField(String userID, String fieldID) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(userID).child(fieldID);

        TaskCompletionSource<Field> taskCompletionSource = new TaskCompletionSource<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Field field = new Field();

                taskCompletionSource.setResult(field);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                taskCompletionSource.setException(databaseError.toException());
            }
        });

        return taskCompletionSource.getTask();
    }

    public static Task<MeasuredData> getMeasuredData(String userID, String fieldID) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(userID).child(fieldID).child("measured_data");

        Log.v("MeasuredDataFragment", userID + " " + fieldID);

        TaskCompletionSource<MeasuredData> taskCompletionSource = new TaskCompletionSource<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Tìm ngày gần nhất có dữ liệu
                Date lastDate = new Date(0, 0, 0);
                String lastDateString = "00-00-00";
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String dateString = childSnapshot.getKey();
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = null;
                    try {
                        date = dateFormat.parse(dateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(date.after(lastDate)) {
                        lastDate = date;
                        lastDateString = dateString;
                    }
                }
                Log.v("MeasuredData API", lastDateString);
                // Tìm giờ gần nhất có dữ liệu
                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference()
                        .child(userID).child(fieldID).child("measured_data").child(lastDateString);
                ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Time lastTime = new Time(0, 0, 0);
                        String lastTimeString = "00:00:00";
                        MeasuredData data = new MeasuredData();
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            String timeString = childSnapshot.getKey();
                            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                            Date date = null;
                            try {
                                date = dateFormat.parse(timeString);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Time time = new Time(date.getTime());
                            if(time.after(lastTime)) {
                                lastTime = time;
                                lastTimeString = timeString;
                                Log.v("MeasuredData API", lastTimeString);
                                data = childSnapshot.getValue(MeasuredData.class);
                                Log.v("MeasuredData API", data.toString());
                            }
                        }
                        taskCompletionSource.setResult(data);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("Error: " + databaseError.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                taskCompletionSource.setException(databaseError.toException());
            }
        });

        return taskCompletionSource.getTask();
    }

    public void deleteMeasuredData(String userID, String fieldID) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(userID).child(fieldID).child("measured_data");

        Log.v("MeasuredDataFragment", userID + " " + fieldID);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    Log.v("API", "data is empty");
                    return;
                }
                // Tìm ngày gần nhất có dữ liệu
                Date lastDate = new Date(0, 0, 0);
                String lastDateString = "00-00-00";
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String dateString = childSnapshot.getKey();
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = null;
                    try {
                        date = dateFormat.parse(dateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(date.after(lastDate)) {
                        lastDate = date;
                        lastDateString = dateString;
                    }
                }
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String dateString = childSnapshot.getKey();
                    if(!dateString.equals(lastDateString)) {
                        childSnapshot.getRef().removeValue();
                    }
                }
                Log.v("MeasuredData API", lastDateString);
                // Tìm giờ gần nhất có dữ liệu
                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference()
                        .child(userID).child(fieldID).child("measured_data").child(lastDateString);
                ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Time lastTime = new Time(0, 0, 0);
                        String lastTimeString = "00:00:00";
                        MeasuredData data = new MeasuredData();
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            String timeString = childSnapshot.getKey();
                            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                            Date date = null;
                            try {
                                date = dateFormat.parse(timeString);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Time time = new Time(date.getTime());
                            if(time.after(lastTime)) {
                                lastTime = time;
                                lastTimeString = timeString;
                            }
                        }
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            String timeString = childSnapshot.getKey();
                            if(!timeString.equals(lastTimeString))
                                childSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("Error: " + databaseError.getMessage());
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static Task<List<List<Double>>> getAllMeasuredData(String userID, String fieldID) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(userID).child(fieldID).child("measured_data");

        Log.v("AllMeasuredData", userID + " " + fieldID);

        TaskCompletionSource<List<List<Double>>> taskCompletionSource = new TaskCompletionSource<>();
        List<List<Double>> data = new ArrayList<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String dateString = childSnapshot.getKey();
                    DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child(userID)
                            .child(fieldID).child("measured_data").child(dateString);
                    ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                String timeString = childSnapshot.getKey();
                                List<Double> num = new ArrayList<>();
                                num.add(snapshot.child(timeString).child("air_humidity").getValue(Double.class));
                                num.add(snapshot.child(timeString).child("radiation").getValue(Double.class));
                                num.add(snapshot.child(timeString).child("temperature").getValue(Double.class));
                                data.add(num);
                            }
                            taskCompletionSource.setResult(data);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println("Error: " + databaseError.getMessage());
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                taskCompletionSource.setException(databaseError.toException());
            }
        });

        return taskCompletionSource.getTask();
    }

    public static Task<IrrigationInformation> getIrrigationInformation(String userID, String fieldID) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(userID).child(fieldID).child("irrigation_information");

        Log.v("API", userID + " " + fieldID);

        TaskCompletionSource<IrrigationInformation> taskCompletionSource = new TaskCompletionSource<>();
        ref.addListenerForSingleValueEvent (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                IrrigationInformation data = new IrrigationInformation();
                data = snapshot.getValue(IrrigationInformation.class);
                Log.v("API", data.toString());
                taskCompletionSource.setResult(data);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                taskCompletionSource.setException(databaseError.toException());
            }
        });

        return taskCompletionSource.getTask();
    }

    // unsuccessfull
    public static Task<CustomizedParameter> getCustomizedParameter(String userID, String fieldID) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(userID).child(fieldID).child("customized_parameter");

        Log.v("API", userID + " " + fieldID);

        TaskCompletionSource<CustomizedParameter> taskCompletionSource = new TaskCompletionSource<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CustomizedParameter customizedParameter = new CustomizedParameter();
                customizedParameter.acreage = dataSnapshot.child("acreage").getValue(Float.class);
                customizedParameter.distanceBetweenHole = dataSnapshot.child("distanceBetweenHole").getValue(Float.class);
                customizedParameter.distanceBetweenRow = dataSnapshot.child("distanceBetweenRow").getValue(Float.class);
                customizedParameter.dripRate = dataSnapshot.child("dripRate").getValue(Float.class);
                customizedParameter.fertilizationLevel = dataSnapshot.child("fertilizationLevel").getValue(Float.class);
                customizedParameter.numberOfHoles = dataSnapshot.child("numberOfHoles").getValue(Integer.class);
                customizedParameter.scaleRain = dataSnapshot.child("scaleRain").getValue(Float.class);

                for (DataSnapshot phaseSnapshot : dataSnapshot.child("fieldCapacity").getChildren()) {
                    Phase phase = phaseSnapshot.getValue(Phase.class);
                    phase.setName(phaseSnapshot.getKey());
                    customizedParameter.fieldCapacity.add(phase);
                }

                Log.v("API", customizedParameter.toString());
                taskCompletionSource.setResult(customizedParameter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                taskCompletionSource.setException(databaseError.toException());
            }
        });

        return taskCompletionSource.getTask();
    }

    public static  String changeIrrigationTime(String userID, String fieldID, String startTime) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(userID).child(fieldID).child("irrigation_information");

        String donationKey = "startTime";
        ref.child(donationKey).setValue(startTime);
        return "added to cloud";
    }

    public static  String changeAutoIrrigation(String userID, String fieldID, boolean auto) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(userID).child(fieldID).child("irrigation_information");

        String donationKey = "autoIrrigation";
        Log.v("API", ""+auto);
        ref.child(donationKey).setValue(auto);
        return "added to cloud";
    }

    public static  String changeCustomizedParameter(String userID, String fieldID, CustomizedParameter parameter) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(userID).child(fieldID).child("customized_parameter");

        ref.child("acreage").setValue(parameter.acreage);
        ref.child("distanceBetweenHole").setValue(parameter.distanceBetweenHole);
        ref.child("distanceBetweenRow").setValue(parameter.distanceBetweenRow);
        ref.child("dripRate").setValue(parameter.dripRate);
        ref.child("fertilizationLevel").setValue(parameter.fertilizationLevel);
        ref.child("numberOfHoles").setValue(parameter.numberOfHoles);
        ref.child("scaleRain").setValue(parameter.scaleRain);

        List<Double> icontL = List.of(39.830, 10.105, 16.050, 8.0, 8.0);

        DatabaseReference data = FirebaseDatabase.getInstance().getReference()
                .child(userID).child(fieldID).child("treeData");
        data.child("contL").child("first").setValue(parameter.fertilizationLevel *
                icontL.get(0) / 100);
        data.child("contL").child("second").setValue(parameter.fertilizationLevel *
                icontL.get(1) / 100);
        data.child("contL").child("third").setValue(parameter.fertilizationLevel *
                icontL.get(2) / 100);
        data.child("contL").child("forth").setValue(parameter.fertilizationLevel *
                icontL.get(3) / 100);
        data.child("contL").child("fifth").setValue(parameter.fertilizationLevel *
                icontL.get(4) / 100);
        return "change the cloud";
    }

    public static String addField(String userID, String name) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(userID).child(name);

        DatabaseReference cus = ref.child("customized_parameter");
        cus.child("acreage").setValue(0);
        cus.child("distanceBetweenHole").setValue(0);
        cus.child("distanceBetweenRow").setValue(0);
        cus.child("dripRate").setValue(0);
        cus.child("fertilizationLevel").setValue(0);
        cus.child("numberOfHoles").setValue(0);
        cus.child("scaleRain").setValue(0);

        DatabaseReference irr = ref.child("irrigation_information");
        irr.child("autoIrrigation").setValue(false);
        irr.child("duration").setValue("0");
        irr.child("endTime").setValue("0");
        irr.child("irrigationCheck").setValue(false);
        irr.child("startTime").setValue("0");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DatabaseReference mea = ref.child("measured_data").child(LocalDate.now().toString()).child(LocalTime.now().toString());
            mea.child("air_humidity").setValue(0);
            mea.child("radiation").setValue(0);
            mea.child("soil_humidity_30").setValue(0);
            mea.child("soil_humidity_60").setValue(0);
            mea.child("temperature").setValue(0);
        }

        DatabaseReference data = ref.child("tree_data");
        data.child("LDM").setValue(0);      // Leaf Dry Mass (g)
        data.child("SDM").setValue(cuttingDryMass);     // Stem Dry Mass (g)
        data.child("RDM").setValue(0);      // Root Dry Mass (g)
        data.child("SRDM").setValue(0);     // Storage Root Dry Mass (g)
        data.child("LA").setValue(0);      // Leaf Area (m2)

        data.child("mDMl").setValue(0);
        data.child("Clab").setValue(0);     // 8

        data.child("rootLength").child("first").setValue(0);
        data.child("rootLength").child("second").setValue(0);
        data.child("rootLength").child("third").setValue(0);
        data.child("rootLength").child("forth").setValue(0);
        data.child("rootLength").child("fifth").setValue(0);

        data.child("rootTips").child("first").setValue(6.0);
        data.child("rootTips").child("second").setValue(0);
        data.child("rootTips").child("third").setValue(0);
        data.child("rootTips").child("forth").setValue(0);
        data.child("rootTips").child("fifth").setValue(0);

        data.child("soilWaterCapacity").child("first").setValue(0.2);
        data.child("soilWaterCapacity").child("second").setValue(0.22);
        data.child("soilWaterCapacity").child("third").setValue(0.24);
        data.child("soilWaterCapacity").child("forth").setValue(0.26);
        data.child("soilWaterCapacity").child("fifth").setValue(0.28);

        data.child("contL").child("first").setValue(0);
        data.child("contL").child("second").setValue(0);
        data.child("contL").child("third").setValue(0);
        data.child("contL").child("forth").setValue(0);
        data.child("contL").child("fifth").setValue(0);

        data.child("nuptL").child("first").setValue(cuttingDryMass * 30.0 / numberOfSoilLayer);
        data.child("nuptL").child("second").setValue(cuttingDryMass * 30.0 / numberOfSoilLayer);
        data.child("nuptL").child("third").setValue(cuttingDryMass * 30.0 / numberOfSoilLayer);
        data.child("nuptL").child("forth").setValue(cuttingDryMass * 30.0 / numberOfSoilLayer);
        data.child("nuptL").child("fifth").setValue(cuttingDryMass * 30.0 / numberOfSoilLayer);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            data.child("timeFromLastUpdate").child("day")
                    .setValue(LocalDate.now().toString());
            data.child("timeFromLastUpdate").child("time")
                    .setValue(LocalTime.now().toString());
        }

        return "added to cloud";
    }

    public static String addPhase(String humid, String startDate, String endDate, String userID, String name, Integer newPhaseNum) {
        String newPhaseName = "phase" + newPhaseNum;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(userID).child(name).child("customized_parameter")
                .child("fieldCapacity").child(newPhaseName);
        ref.child("endTime").setValue(endDate);
        ref.child("startTime").setValue(startDate);
        ref.child("threshHold").setValue(Float.parseFloat(humid));

        return "added to cloud";
    }

    public static String deleteField(String userID, String name) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(userID).child(name);
        ref.removeValue();
        return "delete from cloud";
    }

    public static Task<TreeData> getTreeData(String userID, String fieldID) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(userID).child(fieldID).child("tree_data");

        TaskCompletionSource<TreeData> taskCompletionSource = new TaskCompletionSource<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                TreeData treeData = new TreeData();
                treeData.LDM = dataSnapshot.child("LDM").getValue(Double.class);
                treeData.SDM = dataSnapshot.child("SDM").getValue(Double.class);
                treeData.RDM = dataSnapshot.child("RDM").getValue(Double.class);
                treeData.SRDM = dataSnapshot.child("SRDM").getValue(Double.class);
                treeData.LA = dataSnapshot.child("LA").getValue(Double.class);

                treeData.mDMl = dataSnapshot.child("mDMl").getValue(Double.class);
                treeData.Clab = dataSnapshot.child("Clab").getValue(Double.class);

                treeData.rootLength.add(dataSnapshot.child("rootLength")
                        .child("first").getValue(Double.class));
                treeData.rootLength.add(dataSnapshot.child("rootLength")
                        .child("second").getValue(Double.class));
                treeData.rootLength.add(dataSnapshot.child("rootLength")
                        .child("third").getValue(Double.class));
                treeData.rootLength.add(dataSnapshot.child("rootLength")
                        .child("forth").getValue(Double.class));
                treeData.rootLength.add(dataSnapshot.child("rootLength")
                        .child("fifth").getValue(Double.class));

                treeData.rootTips.add(dataSnapshot.child("rootTips")
                        .child("first").getValue(Double.class));
                treeData.rootTips.add(dataSnapshot.child("rootTips")
                        .child("second").getValue(Double.class));
                treeData.rootTips.add(dataSnapshot.child("rootTips")
                        .child("third").getValue(Double.class));
                treeData.rootTips.add(dataSnapshot.child("rootTips")
                        .child("forth").getValue(Double.class));
                treeData.rootTips.add(dataSnapshot.child("rootTips")
                        .child("fifth").getValue(Double.class));

                treeData.soilWaterCapacity.add(dataSnapshot.child("soilWaterCapacity")
                        .child("first").getValue(Double.class));
                treeData.soilWaterCapacity.add(dataSnapshot.child("soilWaterCapacity")
                        .child("second").getValue(Double.class));
                treeData.soilWaterCapacity.add(dataSnapshot.child("soilWaterCapacity")
                        .child("third").getValue(Double.class));
                treeData.soilWaterCapacity.add(dataSnapshot.child("soilWaterCapacity")
                        .child("forth").getValue(Double.class));
                treeData.soilWaterCapacity.add(dataSnapshot.child("soilWaterCapacity")
                        .child("fifth").getValue(Double.class));

                treeData.contL.add(dataSnapshot.child("contL")
                        .child("first").getValue(Double.class));
                treeData.contL.add(dataSnapshot.child("contL")
                        .child("second").getValue(Double.class));
                treeData.contL.add(dataSnapshot.child("contL")
                        .child("third").getValue(Double.class));
                treeData.contL.add(dataSnapshot.child("contL")
                        .child("forth").getValue(Double.class));
                treeData.contL.add(dataSnapshot.child("contL")
                        .child("fifth").getValue(Double.class));

                treeData.nuptL.add(dataSnapshot.child("nuptL")
                        .child("first").getValue(Double.class));
                treeData.nuptL.add(dataSnapshot.child("nuptL")
                        .child("second").getValue(Double.class));
                treeData.nuptL.add(dataSnapshot.child("nuptL")
                        .child("third").getValue(Double.class));
                treeData.nuptL.add(dataSnapshot.child("nuptL")
                        .child("forth").getValue(Double.class));
                treeData.nuptL.add(dataSnapshot.child("nuptL")
                        .child("fifth").getValue(Double.class));

                treeData.dayFromLastUpdate = dataSnapshot.child("timeFromLastUpdate")
                        .child("day").getValue(String.class);
                treeData.timeFromLastUpdate = dataSnapshot.child("timeFromLastUpdate")
                        .child("time").getValue(String.class);

                taskCompletionSource.setResult(treeData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                taskCompletionSource.setException(databaseError.toException());
            }
        });
        return taskCompletionSource.getTask();
    }

}