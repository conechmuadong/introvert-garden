package ie.app.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.List;
import java.util.concurrent.ExecutionException;

import ie.app.R;
import ie.app.adapter.FieldListAdapter;
import ie.app.adapter.PhaseListAdapter;
import ie.app.api.FirebaseAPI;
import ie.app.databinding.FragmentListFieldBinding;
import ie.app.databinding.FragmentPhaseListBinding;
import ie.app.models.CustomizedParameter;
import ie.app.models.Field;
import ie.app.models.OnFieldSelectedListener;
import ie.app.models.Phase;
import ie.app.models.User;

public class PhaseListFragment extends BaseFragment {

    ListView listView;
    private FragmentPhaseListBinding binding;
    private PhaseListAdapter adapter;
    private Button addPhaseBtn;
    private Button updateBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPhaseListBinding.inflate(inflater, container, false);
        listView = (ListView) binding.listPhase;
        addPhaseBtn = binding.addPhaseButton;
        updateBtn = binding.updatePhaseButton;

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        update();
        adapter = new PhaseListAdapter(getContext(), field.customizedParameter.getFieldCapacity());
        listView.setAdapter(adapter);

        addPhaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(PhaseListFragment.this)
                        .navigate(R.id.action_listPhase_to_addNewPhaseFragment);
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < field.customizedParameter.getFieldCapacity().size(); i++) {
                    EditText x = listView.getChildAt(i).findViewById(R.id.stageEditHumid);
                    EditText y = listView.getChildAt(i).findViewById(R.id.stageStartDate);
                    EditText z = listView.getChildAt(i).findViewById(R.id.stageEndDate);
                    FirebaseAPI.addPhase(x.getText().toString(),
                            y.getText().toString(),
                            z.getText().toString(),
                            "user", field.getName(),
                            i + 1);
                }
                Toast.makeText(getContext(), "Các thay đổi đã được cập nhật", Toast.LENGTH_SHORT).show();
                update();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class GetTask extends AsyncTask<String, Void, CustomizedParameter> {
        protected ProgressDialog dialog;
        protected Context context;

        public GetTask(Context context) {
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
        protected CustomizedParameter doInBackground(String... params) {
            try {
                Task<CustomizedParameter> task = FirebaseAPI.getCustomizedParameter((String) params[0], (String) params[1]);
                field.customizedParameter = Tasks.await(task);
                Log.v("PhaseListFragment", "Got update");
                return field.customizedParameter;
            } catch (Exception e) {
                Log.v("ASYNC", "ERROR : " + e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(CustomizedParameter result) {
            super.onPostExecute(result);
            field.customizedParameter = result;
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

    private void update() {
        GetTask task = new GetTask(getContext());
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "/user", "/" + field.getName());
        new AsyncTask<Void, Void, CustomizedParameter>() {
            @Override
            protected CustomizedParameter doInBackground(Void... voids) {
                try {
                    return task.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CustomizedParameter customizedParameter) {
                field.customizedParameter = customizedParameter;
                adapter = new PhaseListAdapter(getContext(), field.customizedParameter.getFieldCapacity());
                listView.setAdapter(adapter);
            }
        }.execute();
    }
}