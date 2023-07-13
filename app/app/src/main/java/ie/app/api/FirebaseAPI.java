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
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.concurrent.ExecutionException;

import ie.app.fragments.MeasuredDataFragment;
import ie.app.models.Field;
import ie.app.models.IrrigationInformation;
import ie.app.models.MeasuredData;
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

    public static Task<IrrigationInformation> getIrrigationInformation(String userID, String fieldID) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(userID).child(fieldID).child("irrigation_information");

        Log.v("API", userID + " " + fieldID);

        TaskCompletionSource<IrrigationInformation> taskCompletionSource = new TaskCompletionSource<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
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

    public static  String insert(String userID, String fieldID, String startTime) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(userID).child(fieldID).child("irrigation_information");

        String donationKey = "startTime";
        ref.child(donationKey).setValue(startTime);
        return "added to clould";
    }

    /*
    public static  String insert(String call, Donation donation) {
        DatabaseReference donationsRef = FirebaseDatabase.getInstance(instance).getReference(call);
        String donationKey = donation._id;
        Log.v("Firebase", "donationKey:" + donationKey );
        donationsRef.child(donationKey).setValue(donation);
        return "added to clould";
    }

    public static String delete(String call, String donationId) {
        DatabaseReference ref = FirebaseDatabase.getInstance(instance).getReference(call).child(donationId);
        final String[] result = new String[1];
        ref.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    // Xóa đối tượng Donation thành công
                    result[0] = "Delete successful";
                    Log.d("TAG", "Donation deleted successfully");
                } else {
                    // Xóa đối tượng Donation thất bại
                    result[0] = "Delete failed";
                    Log.w("TAG", "Failed to delete donation", databaseError.toException());
                }
            }
        });
        return result[0];
    }

    public static String deleteAll(String call) {
        DatabaseReference ref = FirebaseDatabase.getInstance(instance).getReference(call);
        final String[] result = new String[1];

        ref.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    // Xóa toàn bộ cơ sở dữ liệu thành công
                    result[0] = "Delete successful";
                    Log.d("TAG", "All data deleted successfully");
                } else {
                    result[0] = "Delete failed";
                    // Xóa toàn bộ cơ sở dữ liệu thất bại
                    Log.w("TAG", "Failed to delete all data", databaseError.toException());
                }
            }
        });

        return result[0];
    } */
}