package ie.app.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.List;

import ie.app.R;
import ie.app.adapter.FieldlistAdapter;
import ie.app.api.FirebaseAPI;
import ie.app.databinding.FragmentListFieldBinding;
import ie.app.main.MainActivity;
import ie.app.models.Field;
import ie.app.models.OnFieldSelectedListener;
import ie.app.models.User;

public class FieldlistFragment extends BaseFragment implements AdapterView.OnItemClickListener, OnFieldSelectedListener {

    ListView listView;
    private FragmentListFieldBinding binding;
    private FieldlistAdapter adapter;
    private OnFieldSelectedListener listener;

    private Button addBtn;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentListFieldBinding.inflate(inflater, container, false);

        listView = (ListView) binding.listField;
        new GetAllTask(getContext()).execute("/user");

        addBtn = binding.addButton;

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listener = new OnFieldSelectedListener() {
            @Override
            public void onFieldSelected(Field field, String type) {
                Log.v("onFieldSelected", field.getName() + " onViewCreated");
                Bundle bundle = new Bundle();
                bundle.putString("selectedFieldName", field.getName());
                if(type == "status") {
                    NavHostFragment.findNavController(FieldlistFragment.this)
                            .navigate(R.id.action_FieldlistFragment_to_MeasuredDataFragment, bundle);
                } else if(type == "adjust") {
                    NavHostFragment.findNavController(FieldlistFragment.this)
                            .navigate(R.id.action_FieldlistFragment_to_CustomizedFragment, bundle);
                }
            }
        };

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(FieldlistFragment.this)
                        .navigate(R.id.action_FieldlistFragment_to_AddNewFieldFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Field field = (Field) adapterView.getItemAtPosition(i);
    }



    @Override
    public void onFieldSelected(Field field, String type) {
        Bundle bundle = new Bundle();
        bundle.putString("selectedField", field.getName());
        Log.v("onFieldSelected", field.getName() + " in fieldlist Fragment");
        if(type == "status") {
            NavHostFragment.findNavController(FieldlistFragment.this)
                    .navigate(R.id.action_FieldlistFragment_to_MeasuredDataFragment, bundle);
        } else if(type == "adjust") {
            NavHostFragment.findNavController(FieldlistFragment.this)
                    .navigate(R.id.action_FieldlistFragment_to_CustomizedFragment, bundle);
        }
    }

    //---------------------------ACT CLASS---------------------------
    private class GetAllTask extends AsyncTask<String, Void, List<Field>> {
        protected ProgressDialog dialog;
        protected Context context;
        public GetAllTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog = new ProgressDialog(context, 1);
            this.dialog.setMessage("Retrieving Fields List");
            this.dialog.show();
        }

        @Override
        protected List<Field> doInBackground(String... params) {
            try {
                Task<User> task = FirebaseAPI.getUser((String) params[0]);
                user = Tasks.await(task);
                Log.v("GetAllTask", user.toString());
                return user.getFields();
            } catch (Exception e) {
                Log.v("ASYNC", "ERROR : " + e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Field> result) {
            super.onPostExecute(result);
            adapter = new FieldlistAdapter(context, user.getFields(), FieldlistFragment.this);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(FieldlistFragment.this);
            // Đăng ký OnFieldSelectedListener cho adapter
            adapter.setOnFieldSelectedListener(listener);
            if (dialog.isShowing())
                dialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            dialog.dismiss();
            Toast.makeText(getContext(), "An error occurred while getting data", Toast.LENGTH_SHORT).show();
            Log.e("AsyncTask", "An error occurred while getting data");
        }
    }

}

/*TODO
* Xử lý menu điều chỉnh
* Xử lý khi nhấn delete - Xóa một cánh đồng -> ghi lại cho database -> Get data mới được ghi
* Đổi màu nền các field khi một field được nhấn vào tùy chọn
*/