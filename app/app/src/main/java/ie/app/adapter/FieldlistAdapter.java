package ie.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import ie.app.R;
import ie.app.models.Field;

public class FieldlistAdapter extends ArrayAdapter<Field> {
    private Context context;
    public List<Field> fields;

    public FieldlistAdapter(Context context, List<Field> fields) {
        super(context, R.layout.fragment_list_field, fields);
        Log.v("Adapter", "Constructor: " + fields.size());
        this.context = context;
        this.fields = fields;
    }

    public FieldlistAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.field_name, parent, false);
        Field field = fields.get(position);
        TextView fieldName = (TextView) view.findViewById(R.id.field_name);
        fieldName.setText(field.getName());
        return view;
    }

}