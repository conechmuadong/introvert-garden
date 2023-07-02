package ie.app.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

import ie.app.databinding.FragmentIrrigationSettingBinding;
import ie.app.main.MainActivity;

enum Mode {
    MANUAL,
    AUTO
}
public class IrrigationSettingFragment extends Fragment {

    private FragmentIrrigationSettingBinding binding;
    private Mode mode = Mode.AUTO;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentIrrigationSettingBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.manualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode = Mode.MANUAL;
                // set background
            }
        });

        binding.autoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode = Mode.AUTO;
                // set background
            }
        });

        binding.dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy giá trị ngày giờ hiện tại để hiển thị trên DatePicker
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                // Hiển thị DatePickerDialog để cho phép người dùng chọn giá trị ngày giờ
                DatePickerDialog datePickerDialog = new DatePickerDialog(IrrigationSettingFragment.this.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Cập nhật giá trị ngày giờ của EditText khi người dùng chọn giá trị trên DatePicker
                        binding.dateEditText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
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

                // Hiển thị DatePickerDialog để cho phép người dùng chọn giá trị ngày giờ
                TimePickerDialog timePickerDialog = new TimePickerDialog(IrrigationSettingFragment.this.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Cập nhật giá trị giờ của EditText khi người dùng chọn giá trị trên TimePicker
                        binding.timeEditText.setText(hourOfDay + " : " + minute + " : 00");
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