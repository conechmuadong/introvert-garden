package ie.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import ie.app.R;
import ie.app.databinding.FragmentAddNewFieldBinding;
import ie.app.databinding.FragmentListFieldBinding;

public class AddNewFieldFragment extends Fragment {

    private FragmentAddNewFieldBinding binding;

    private Button doneBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddNewFieldBinding.inflate(inflater, container, false);

        doneBtn = binding.doneBtn;

        return binding.getRoot();
    }
}