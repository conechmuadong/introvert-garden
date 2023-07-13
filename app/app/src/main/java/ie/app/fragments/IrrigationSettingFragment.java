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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ie.app.R;
import ie.app.api.FirebaseAPI;
import ie.app.databinding.FragmentIrrigationSettingBinding;
import ie.app.main.MainActivity;
import ie.app.models.IrrigationInformation;

enum Mode {
    MANUAL,
    AUTO
}
public class IrrigationSettingFragment extends BaseFragment {

    private FragmentIrrigationSettingBinding binding;
    private Mode mode = Mode.AUTO;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        Log.v("Irrigation Setting", field.irrigationInformation.toString());
        binding = FragmentIrrigationSettingBinding.inflate(inflater, container, false);
        mode = field.irrigationInformation.isIrrigationCheck() == true ? Mode.AUTO : Mode.MANUAL;
        Log.v("Irrigation Setting", mode.toString());

        if(mode == Mode.AUTO) {
            binding.manualButton.setBackgroundColor(binding.manualButton.getContext().
                    getResources().getColor(R.color.disable));
            binding.autoButton.setBackgroundColor(binding.autoButton.getContext().
                    getResources().getColor(R.color.colorPrimary));
        } else {
            binding.manualButton.setBackgroundColor(binding.manualButton.getContext().
                    getResources().getColor(R.color.colorPrimary));
            binding.autoButton.setBackgroundColor(binding.autoButton.getContext().
                    getResources().getColor(R.color.disable));
        }

        try {
            binding.dateEditText.setHint(field.irrigationInformation.getStartDate());
            binding.timeEditText.setHint(field.irrigationInformation.getStartTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.manualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                field.irrigationInformation.setIrrigationCheck(false);
                mode = Mode.MANUAL;
                // set background
                binding.manualButton.setBackgroundColor(binding.manualButton.getContext().
                        getResources().getColor(R.color.colorPrimary));
                binding.autoButton.setBackgroundColor(binding.autoButton.getContext().
                        getResources().getColor(R.color.disable));
            }
        });

        binding.autoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                field.irrigationInformation.setIrrigationCheck(true);
                mode = Mode.AUTO;
                // set background
                binding.manualButton.setBackgroundColor(binding.manualButton.getContext().
                        getResources().getColor(R.color.disable));
                binding.autoButton.setBackgroundColor(binding.autoButton.getContext().
                        getResources().getColor(R.color.colorPrimary));
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

                        // UPD field hiện tại

                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        String strDate = formatter.format(selectedDate);
                        field.irrigationInformation.setNewStartDate(strDate, field.name);

                        binding.dateEditText.setText(strDate);

                        // UPD database

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

                        // UPD field hiện tại

                        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");
                        String strDate = formatter.format(selectedDate);
                        field.irrigationInformation.setNewStartTime(strDate, field.name);

                        binding.timeEditText.setText(strDate);

                        // UPD database

                        Log.v("Irrigation Setting", field.irrigationInformation.toString());
                    }
                }, hour, min, true);
                timePickerDialog.show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}