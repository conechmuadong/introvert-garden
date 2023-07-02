package ie.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;

import ie.app.R;
import ie.app.adapter.StageAdapter;
import ie.app.databinding.FragmentCustomizedBinding;
import ie.app.models.Stage;

public class CustomizedFragment extends Fragment {

    private FragmentCustomizedBinding binding;
    private ArrayList<Stage> stageList;
    private StageAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCustomizedBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        String fieldname = "Cánh đồng sắn";
        TextView fieldnameView = binding.fieldName;
        fieldnameView.setText(fieldname);
        fieldnameView.setLineSpacing(10f, 1f);

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        stageList = new ArrayList<>();
        adapter = new StageAdapter();

        binding.addStageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Stage newStage = new Stage();
                stageList.add(newStage);
                //adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}