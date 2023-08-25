package ie.app.fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ie.app.R;
import ie.app.adapter.PhaseListAdapter;
import ie.app.api.FirebaseAPI;
import ie.app.databinding.FragmentIrrigationSettingBinding;
import ie.app.main.MainActivity;
import ie.app.models.CustomizedParameter;
import ie.app.models.IrrigationInformation;
import ie.app.models.TreeData;

enum Mode {
    MANUAL,
    AUTO
}
public class IrrigationSettingFragment extends BaseFragment {

    private FragmentIrrigationSettingBinding binding;
    private Mode mode = Mode.AUTO;
    private String selectedStartDate, selectedStartTime, selectedAmount;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        Log.v("Irrigation Setting", field.irrigationInformation.toString());
        binding = FragmentIrrigationSettingBinding.inflate(inflater, container, false);
        update();
        mode = field.irrigationInformation.isAutoIrrigation() ? Mode.AUTO : Mode.MANUAL;
        Log.v("Irrigation Setting", mode.toString());

        if(mode == Mode.AUTO) {
            binding.manualButton.setBackgroundColor(binding.manualButton.getContext().
                    getResources().getColor(R.color.disable));
            binding.autoButton.setBackgroundColor(binding.autoButton.getContext().
                    getResources().getColor(R.color.colorPrimary));
            binding.amountEditText.setEnabled(false);
            binding.dateEditText.setEnabled(false);
            binding.timeEditText.setEnabled(false);
        } else {
            binding.manualButton.setBackgroundColor(binding.manualButton.getContext().
                    getResources().getColor(R.color.colorPrimary));
            binding.autoButton.setBackgroundColor(binding.autoButton.getContext().
                    getResources().getColor(R.color.disable));
            binding.amountEditText.setEnabled(true);
            binding.dateEditText.setEnabled(true);
            binding.timeEditText.setEnabled(true);
        }

        try {
            selectedStartDate = field.irrigationInformation.getStartDate();
            selectedStartTime = field.irrigationInformation.getStartTime();
            binding.dateEditText.setText(selectedStartDate);
            binding.timeEditText.setText(selectedStartTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        update();

        binding.manualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode = Mode.MANUAL;
                // set background
                binding.manualButton.setBackgroundColor(binding.manualButton.getContext().
                        getResources().getColor(R.color.colorPrimary));
                binding.autoButton.setBackgroundColor(binding.autoButton.getContext().
                        getResources().getColor(R.color.disable));

                binding.amountEditText.setEnabled(true);
                binding.dateEditText.setEnabled(true);
                binding.timeEditText.setEnabled(true);
            }
        });

        binding.autoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode = Mode.AUTO;
                // set background
                binding.manualButton.setBackgroundColor(binding.manualButton.getContext().
                        getResources().getColor(R.color.disable));
                binding.autoButton.setBackgroundColor(binding.autoButton.getContext().
                        getResources().getColor(R.color.colorPrimary));
                binding.amountEditText.setEnabled(false);
                binding.dateEditText.setEnabled(false);
                binding.timeEditText.setEnabled(false);
            }
        });

        binding.dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy giá trị ngày giờ từ database để hiển thị trên DatePicker
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                // Hiển thị DatePickerDialog để cho phép người dùng chọn giá trị ngày giờ
                DatePickerDialog datePickerDialog = new DatePickerDialog(IrrigationSettingFragment.this.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Cập nhật giá trị ngày giờ của EditText khi người dùng chọn giá trị trên DatePicker
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(Calendar.YEAR, year);
                        selectedCalendar.set(Calendar.MONTH, monthOfYear);
                        selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        Date selectedDate = selectedCalendar.getTime(); // Lấy ra đối tượng Date tương ứng với giá trị được chọn


                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        String strDate = formatter.format(selectedDate);
                        selectedStartDate = strDate;

                        binding.dateEditText.setText(strDate);


                    }
                }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        binding.timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy giá trị giờ hiện tại để hiển thị trên DatePicker
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR);
                int min = calendar.get(Calendar.MINUTE);

                // Hiển thị DatePickerDialog để cho phép người dùng chọn giá trị giờ phút
                TimePickerDialog timePickerDialog = new TimePickerDialog(IrrigationSettingFragment.this.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Cập nhật giá trị giờ của EditText khi người dùng chọn giá trị trên TimePicker

                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(Calendar.HOUR, hourOfDay);
                        selectedCalendar.set(Calendar.MINUTE, minute);
                        selectedCalendar.set(Calendar.SECOND, 0);
                        Date selectedDate = selectedCalendar.getTime(); // Lấy ra đối tượng Date tương ứng với giá trị được chọn


                        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");
                        String strTime = formatter.format(selectedDate);
                        selectedStartTime = strTime;

                        binding.timeEditText.setText(strTime);

                        Log.v("Irrigation Setting", field.irrigationInformation.toString());
                    }
                }, hour, min, true);
                timePickerDialog.show();
            }
        });
        binding.updateButton.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = binding.amountEditText.getText().toString();
                float number = Float.parseFloat(input);
                field.irrigationInformation.setNewAmount(number);


                Log.v("API", "before call API");
                field.irrigationInformation.setNewStartDate(selectedStartDate, field.name);
                Log.v("API", "after change date");
                field.irrigationInformation.setNewStartTime(selectedStartTime, field.name);
                Log.v("API", "after change time");
                field.irrigationInformation.setAutoIrrigation((mode == Mode.AUTO), field.name);
                Log.v("API", "after change mode");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class GetTreeData extends AsyncTask<String, Void, TreeData> {
        protected ProgressDialog dialog;
        protected Context context;

        public GetTreeData(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog = new ProgressDialog(context, 1);
            this.dialog.setMessage("Đang lấy dữ liệu");
            this.dialog.show();
        }

        @Override
        protected TreeData doInBackground(String... params) {
            try {
                Task<TreeData> task = FirebaseAPI.getTreeData((String) params[0], (String) params[1]);
                field.treeData = Tasks.await(task);
                Log.v("TreeData", "Got update");
                return field.treeData;
            } catch (Exception e) {
                Log.v("ASYNC", "ERROR : " + e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(TreeData result) {
            super.onPostExecute(result);
            field.treeData = result;
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

    private class GetAllMeasureData extends AsyncTask<String, Void, List<List<Double>>> {
        protected ProgressDialog dialog;
        protected Context context;

        public GetAllMeasureData(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog = new ProgressDialog(context, 1);
            this.dialog.setMessage("Đang lấy dữ liệu");
            this.dialog.show();
        }

        @Override
        protected List<List<Double>> doInBackground(String... params) {
            try {
                Task<List<List<Double>>> task = FirebaseAPI.getAllMeasuredData((String) params[0], (String) params[1]);
                field.allMeasuredDate = Tasks.await(task);
                Log.v("TreeData", "Got update");
                return field.allMeasuredDate;
            } catch (Exception e) {
                Log.v("ASYNC", "ERROR : " + e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<List<Double>> result) {
            super.onPostExecute(result);
            field.allMeasuredDate = result;
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

    private void update() {
        GetTreeData taskTreeData = new GetTreeData(getContext());
        taskTreeData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "/user", "/" + field.getName());
        new AsyncTask<Void, Void, TreeData>() {
            @Override
            protected TreeData doInBackground(Void... voids) {
                try {
                    return taskTreeData.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(TreeData treeData) {
                field.treeData = treeData;
            }
        }.execute();

        GetAllMeasureData taskAll = new GetAllMeasureData(getContext());
        taskAll.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "/user", "/" + field.getName());
        new AsyncTask<Void, Void, List<List<Double>>>() {
            @Override
            protected List<List<Double>> doInBackground(Void... voids) {
                try {
                    return taskAll.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<List<Double>> data) {
                field.allMeasuredDate = data;
            }
        }.execute();
    }
}
