package pl.michal.tim_client.coach.calendar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.michal.tim_client.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HwAdapter extends BaseAdapter {
    private Activity context;

    private java.util.Calendar month;
    private GregorianCalendar pmonth;
    /**
     * calendar instance for previous month for getting complete view
     */
    private GregorianCalendar pmonthmaxset;
    private GregorianCalendar selectedDate;
    private int firstDay;
    private int maxWeeknumber;
    private int maxP;
    private int calMaxP;
    private int mnthlength;
    private String itemvalue, curentDateString;
    private DateFormat df;

    private ArrayList<String> items;
    public static List<String> day_string;
    private ArrayList<EventsCollection>  date_collection_arr;
    private String gridvalue;
    private ListView listTeachers;
    private ArrayList<DialogPojo> alCustom= new ArrayList<>();

    public HwAdapter(Activity context, GregorianCalendar monthCalendar,ArrayList<EventsCollection> date_collection_arr) {
        this.date_collection_arr=date_collection_arr;
        HwAdapter.day_string = new ArrayList<>();
        Locale.setDefault(Locale.US);
        month = monthCalendar;
        selectedDate = (GregorianCalendar) monthCalendar.clone();
        this.context = context;
        month.set(GregorianCalendar.DAY_OF_MONTH, 1);

        this.items = new ArrayList<>();
        df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        curentDateString = df.format(selectedDate.getTime());
        refreshDays();

    }

    public int getCount() {
        return day_string.size();
    }

    public Object getItem(int position) {
        return day_string.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        TextView dayView;
        if (convertView == null) { // if it's not recycled, initialize some
            // attributes
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.cal_item, null);

        }


        dayView = v.findViewById(R.id.date);
        String[] separatedTime = day_string.get(position).split("-");


        gridvalue = separatedTime[2].replaceFirst("^0*", "");
        if ((Integer.parseInt(gridvalue) > 1) && (position < firstDay)) {
            dayView.setTextColor(Color.parseColor("#A9A9A9"));
            dayView.setClickable(false);
            dayView.setFocusable(false);
        } else if ((Integer.parseInt(gridvalue) < 7) && (position > 28)) {
            dayView.setTextColor(Color.parseColor("#A9A9A9"));
            dayView.setClickable(false);
            dayView.setFocusable(false);
        } else {
            // setting curent month's days in blue color.
            dayView.setTextColor(Color.parseColor("#696969"));
        }


        if (day_string.get(position).equals(curentDateString)) {

            v.setBackgroundColor(Color.parseColor("#ffffff"));
        } else {
            v.setBackgroundColor(Color.parseColor("#ffffff"));
        }


        dayView.setText(gridvalue);

        // create date string for comparison
        String date = day_string.get(position);

        if (date.length() == 1) {
            date = "0" + date;
        }
        String monthStr = "" + (month.get(GregorianCalendar.MONTH) + 1);
        if (monthStr.length() == 1) {
            monthStr = "0" + monthStr;
        }

        setEventView(v, position,dayView);

        return v;
    }

    public void refreshDays() {
        // clear items
        items.clear();
        day_string.clear();
        Locale.setDefault(Locale.US);
        pmonth = (GregorianCalendar) month.clone();
        // month start day. ie; sun, mon, etc
        firstDay = month.get(GregorianCalendar.DAY_OF_WEEK);
        // finding number of weeks in current month.
        maxWeeknumber = month.getActualMaximum(GregorianCalendar.WEEK_OF_MONTH);
        // allocating maximum row number for the gridview.
        mnthlength = maxWeeknumber * 7;
        maxP = getMaxP(); // previous month maximum day 31,30....
        calMaxP = maxP - (firstDay - 1);// calendar offday starting 24,25 ...
        pmonthmaxset = (GregorianCalendar) pmonth.clone();

        pmonthmaxset.set(GregorianCalendar.DAY_OF_MONTH, calMaxP + 1);


        for (int n = 0; n < mnthlength; n++) {

            itemvalue = df.format(pmonthmaxset.getTime());
            pmonthmaxset.add(GregorianCalendar.DATE, 1);
            day_string.add(itemvalue);

        }
    }

    private int getMaxP() {
        int maxP;
        if (month.get(GregorianCalendar.MONTH) == month
                .getActualMinimum(GregorianCalendar.MONTH)) {
            pmonth.set((month.get(GregorianCalendar.YEAR) - 1),
                    month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            pmonth.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) - 1);
        }
        maxP = pmonth.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

        return maxP;
    }




    private void setEventView(View v, int pos, TextView txt){

        int len= EventsCollection.date_collection_arr.size();
        for (int i = 0; i < len; i++) {
            EventsCollection cal_obj= EventsCollection.date_collection_arr.get(i);
            String date=cal_obj.date;
            int len1=day_string.size();
            if (len1>pos) {

                if (day_string.get(pos).equals(date)) {
                    if ((Integer.parseInt(gridvalue) > 1) && (pos < firstDay)) {

                    } else if ((Integer.parseInt(gridvalue) < 7) && (pos > 28)) {

                    } else {
                        v.setBackgroundColor(Color.parseColor("#343434"));
                        v.setBackgroundResource(R.drawable.rounded_calender);
                        txt.setTextColor(Color.parseColor("#696969"));
                    }

                }
            }}
    }

    public void getPositionList(String date,final Activity act){

        int len= EventsCollection.date_collection_arr.size();
        JSONArray jbarrays=new JSONArray();
        for (int j=0; j<len; j++){
            if (EventsCollection.date_collection_arr.get(j).date.equals(date)){
                HashMap<String, String> maplist = new HashMap<>();
                maplist.put("hcustomer", EventsCollection.date_collection_arr.get(j).customer);
                maplist.put("hinfo", EventsCollection.date_collection_arr.get(j).info);
                maplist.put("htimeStart", "from: " +  EventsCollection.date_collection_arr.get(j).timeStart + " to: " +
                        EventsCollection.date_collection_arr.get(j).timeEnd);
                JSONObject json1 = new JSONObject(maplist);
                jbarrays.put(json1);
            }
        }
        if (jbarrays.length()!=0) {
            final Dialog dialogs = new Dialog(context);
            dialogs.setContentView(R.layout.dialog_inform);
            listTeachers = dialogs.findViewById(R.id.list_teachers);
            ImageView imgCross = dialogs.findViewById(R.id.img_cross);
            listTeachers.setAdapter(new DialogAdaptor(context, getMatchList(jbarrays + "")));
            imgCross.setOnClickListener(view -> dialogs.dismiss());
            dialogs.show();

        }

    }

    private ArrayList<DialogPojo> getMatchList(String detail) {
        try {
            JSONArray jsonArray = new JSONArray(detail);
            alCustom = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.optJSONObject(i);

                DialogPojo pojo = new DialogPojo();

                pojo.setTitles(jsonObject.optString("hcustomer"));
                pojo.setSubjects(jsonObject.optString("hinfo"));
                pojo.setDescripts(jsonObject.optString("htimeStart"));

                alCustom.add(pojo);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return alCustom;
    }
}
