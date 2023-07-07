package ie.app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import ie.app.R;
import ie.app.databinding.FragmentMeasuredDataBinding;
import ie.app.models.Field;

public class MeasuredDataFragment extends Fragment {
    private FragmentMeasuredDataBinding binding;
    private Field field;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMeasuredDataBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Bundle bundle = getArguments();
        Log.v("MeasuredDataFragment", "onCreateView ");
        if (bundle != null) {
            Log.v("MeasuredDataFragment", "bundle not null");
            String selectedFieldName = bundle.getString("selectedFieldName");
            field = getFieldByName(selectedFieldName);
        }

        if(field != null) {

            String fieldname = "Cánh đồng";
            TextView fieldnameView = binding.fieldName;
            fieldnameView.setText(fieldname);
            fieldnameView.setLineSpacing(10f, 1f);

            String airHumidityText = "Độ ẩm không khí\n" + field.measuredData.air_humidity;
            TextView airHumidityView = binding.airHumidityView;
            airHumidityView.setText(airHumidityText);
            airHumidityView.setLineSpacing(10f, 1f);

            String radiationText = "Bức xạ\n" + field.measuredData.radiation;
            TextView radiationView = binding.radiationView;
            radiationView.setText(radiationText);
            radiationView.setLineSpacing(10f, 1f);

            String soilHumidity30Text = "Độ ẩm đất ở\nđộ sâu 30cm\n" + field.measuredData.soil_humidity_30;
            TextView soilHumidity30View = binding.soilHumidity30View;
            soilHumidity30View.setText(soilHumidity30Text);
            soilHumidity30View.setLineSpacing(10f, 1f);

            String soilHumidity60Text = "Độ ẩm đất ở\nđộ sâu 60cm\n" + field.measuredData.soil_humidity_60;
            TextView soilHumidity60View = binding.soilHumidity60View;
            soilHumidity60View.setText(soilHumidity60Text);
            soilHumidity60View.setLineSpacing(10f, 1f);

            String temperatureText = "Nhiệt độ\n" + field.measuredData.temperature;
            TextView temperatureView = binding.temperatureView;
            temperatureView.setText(temperatureText);
            temperatureView.setLineSpacing(10f, 1f);
        }

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.waterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(MeasuredDataFragment.this)
                        .navigate(R.id.action_MeasuredDataFragment_to_IrrigationFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private Field getFieldByName(String name) {
        // Lấy thông tin chi tiết về Field từ cơ sở dữ liệu hoặc các nguồn khác
        Field field = new Field();
        Log.v("MeasuredDataFragment", "FieldByName " + name);
        return field;
    }
}