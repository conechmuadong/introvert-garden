package ie.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import ie.app.databinding.FragmentCustomizedBinding;
import ie.app.models.Stage;

public class CustomizedFragment extends Fragment {

    private FragmentCustomizedBinding binding;
    private ArrayList<Stage> stageList;


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
// Adapter cho danh sách giai đoạn
// Lấy thông tin người dùng nhập và update database
// Sử dụng thông tin để tính toán cho mô hình
