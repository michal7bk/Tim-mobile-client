package pl.michal.tim_client.coach.calendar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import pl.michal.tim_client.R;

import java.util.ArrayList;
public class DialogAdaptor  extends BaseAdapter{
    Activity activity;

    private Activity context;
    private ArrayList<DialogPojo> alCustom;
    private String sturl;

    public DialogAdaptor(Activity context, ArrayList<DialogPojo> alCustom) {
        this.context = context;
        this.alCustom = alCustom;
    }
    @Override
    public int getCount() {
        return alCustom.size();

    }

    @Override
    public Object getItem(int i) {
        return alCustom.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.row_addapt, null, true);

        TextView tvTitle= listViewItem.findViewById(R.id.tv_name);
        TextView tvSubject= listViewItem.findViewById(R.id.tv_type);
        TextView tvDuedate= listViewItem.findViewById(R.id.tv_desc);
        TextView tvDescription= listViewItem.findViewById(R.id.tv_class);


        tvTitle.setText("Customer : "+alCustom.get(position).getTitles());
        tvSubject.setText("Info : "+alCustom.get(position).getSubjects());
        tvDuedate.setText("Due Date : "+alCustom.get(position).getDuedates());
        tvDescription.setText("Date : "+alCustom.get(position).getDescripts());

        return  listViewItem;
    }

}
