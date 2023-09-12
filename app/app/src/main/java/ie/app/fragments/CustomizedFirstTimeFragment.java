package ie.app.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import ie.app.R;
import ie.app.api.FirebaseAPI;
import ie.app.databinding.FragmentCustomizedFirstTimeBinding;

public class CustomizedFirstTimeFragment extends BaseFragment {

    private FragmentCustomizedFirstTimeBinding binding;
    private String fieldName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCustomizedFirstTimeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Bundle bundle = getArguments();
        if (bundle != null) {
            fieldName = bundle.getString("newField");
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Double> customized = new ArrayList<>();
                if (binding.areaEditText.getText().toString().equals("") || binding.numberHolesEditText.getText().toString().equals("") ||
                        binding.distanceHoleEditText.getText().toString().equals("") || binding.distanceRowEditText.getText().toString().equals("") ||
                        binding.dripRateEditText.getText().toString().equals("") || binding.scaleRainEditText.getText().toString().equals("") ||
                        binding.ferLevelEditText.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Không được để trông!", Toast.LENGTH_SHORT).show();
                } else {
                    customized.add(Double.valueOf(binding.areaEditText.getText().toString()));
                    customized.add(Double.valueOf(binding.numberHolesEditText.getText().toString()));
                    customized.add(Double.valueOf(binding.distanceHoleEditText.getText().toString()));
                    customized.add(Double.valueOf(binding.distanceRowEditText.getText().toString()));
                    customized.add(Double.valueOf(binding.dripRateEditText.getText().toString()));
                    customized.add(Double.valueOf(binding.scaleRainEditText.getText().toString()));
                    customized.add(Double.valueOf(binding.ferLevelEditText.getText().toString()));
                    FirebaseAPI.addField("users", fieldName, customized);
                    NavHostFragment.findNavController(CustomizedFirstTimeFragment.this).navigateUp();
                    NavHostFragment.findNavController(CustomizedFirstTimeFragment.this).navigateUp();
                }
            }
        });
    }
}