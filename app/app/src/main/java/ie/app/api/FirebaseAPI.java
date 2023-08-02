package ie.app.api;

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

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
import ie.app.models.User;


public class FirebaseAPI {

    static FirebaseDatabase database;
    static final String instance = "https://sample-c5146-default-rtdb.firebaseio.com/";

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

    // successfull
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
        return "added to clould";
    }

    public static  String changeAutoIrrigation(String userID, String fieldID, boolean auto) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(userID).child(fieldID).child("irrigation_information");

        String donationKey = "autoIrrigation";
        Log.v("API", ""+auto);
        ref.child(donationKey).setValue(auto);
        return "added to clould";
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
        return "added to clould";
    }

    public static String addField(String userID, String name) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(userID).child(name);

        DatabaseReference cus = ref.child("customized_parameter");
        cus.child("acreage").setValue(0);
        cus.child("autoIrrigation").setValue(true);
        cus.child("distanceBetweenHole").setValue(0);
        cus.child("distanceBetweenRow").setValue(0);
        cus.child("dripRate").setValue(0);
        cus.child("fertilizationLevel").setValue(0);
        cus.child("numberOfHoles").setValue(0);
        cus.child("scaleRain").setValue(0);

        DatabaseReference irr = ref.child("irrigation_information");
        irr.child("duration").setValue("0");
        irr.child("endTime").setValue("0");
        irr.child("irrigationCheck").setValue(false);
        irr.child("startTime").setValue("0");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DatabaseReference mea = ref.child("measured_data").child(LocalDate.now().toString()).child("00:00:00");
            mea.child("air_humidity").setValue(30);
            mea.child("radiation").setValue(30);
            mea.child("soil_humidity_30").setValue(30);
            mea.child("soil_humidity_60").setValue(30);
            mea.child("temperature").setValue(30);
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
        ref.child("threshHold").setValue(Math.floor(Float.parseFloat(humid)*100)/100);

        return "added to cloud";
    }

    public static String deleteField(String userID, String name) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(userID).child(name);
        ref.removeValue();
        return "delete from cloud";
    }

    public static String deletePhase(String userID, String name, int position, int size) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(userID).child(name).child("customized_parameter")
                .child("fieldCapacity");
        ArrayList<Double> humid = new ArrayList<>();
        ArrayList<String> stTm = new ArrayList<>();
        ArrayList<String> enTm = new ArrayList<>();

        for(int i = position + 1; i < size; i++) {
            int finalI = i;
            ref.child("phase" + (i + 1)).child("threshHold").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    humid.clear();
                    humid.add(snapshot.getValue(Double.class));
                    ref.child("phase" + (finalI)).child("threshHold").setValue(humid.get(0));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            ref.child("phase" + (i + 1)).child("startTime").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    stTm.clear();
                    stTm.add(snapshot.getValue(String.class));
                    ref.child("phase" + (finalI)).child("startTime").setValue(stTm.get(0));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            ref.child("phase" + (i + 1)).child("endTime").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    enTm.clear();
                    enTm.add(snapshot.getValue(String.class));
                    ref.child("phase" + (finalI)).child("endTime").setValue(enTm.get(0));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        ref.child("phase" + (size)).removeValue();

        return "delete from cloud";
    }
}