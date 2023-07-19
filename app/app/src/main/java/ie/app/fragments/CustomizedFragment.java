package ie.app.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.concurrent.ExecutionException;

import ie.app.api.FirebaseAPI;
import ie.app.databinding.FragmentCustomizedBinding;
import ie.app.models.CustomizedParameter;
import ie.app.models.IrrigationInformation;
import ie.app.models.MeasuredData;
import ie.app.models.Phase;

public class CustomizedFragment extends BaseFragment {

    private FragmentCustomizedBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCustomizedBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        Bundle bundle = getArguments();
        Log.v("CustomizedFragment", "onCreateView ");
        if (bundle != null) {
            Log.v("CustomizedFragment", "bundle not null");
            String selectedFieldName = bundle.getString("selectedFieldName");
            getFieldByName(selectedFieldName);
        }

        updateUI();

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.addStageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Phase phase = new Phase();
                //field.customizedParameter.fieldCapacity.add(phase);
                //adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void getFieldByName(String name) {
        field.name = name;
        CustomizedFragment.GetTask task = new CustomizedFragment.GetTask(getContext());
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "/user", "/" + name);
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
                Log.v("CustomizedFragment", "FieldByName " + name + " " + customizedParameter.toString());
                field.customizedParameter = customizedParameter;
                updateUI();
            }
        }.execute();
    }

    private void updateUI() {

    }

    //---------------------------ACT CLASS---------------------------

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
            this.dialog.setMessage("Retrieving Data");
            this.dialog.show();
        }

        @Override
        protected CustomizedParameter doInBackground(String... params) {
            try {
                Task<CustomizedParameter> task = FirebaseAPI.getCustomizedParameter((String) params[0], (String) params[1]);
                field.customizedParameter = Tasks.await(task);
                Log.v("CustomizedFragment", "Got data: " + field.customizedParameter.toString());
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


}
// Adapter cho danh sách giai đoạn
// Lấy thông tin người dùng nhập và update database
// Sử dụng thông tin để tính toán cho mô hình
