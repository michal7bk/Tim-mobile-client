package pl.michal.tim_client.coach.calendar;

import java.util.ArrayList;

public class EventsCollection {
    public String date="";
    public String customer ="";
    public String info="";
    public String timeStart;
    public String timeEnd;
    public boolean accepted;


    public static ArrayList<EventsCollection> date_collection_arr;
    public EventsCollection(String date, String customer, String info, String timeStart, String timeEnd, boolean accepted){

        this.date=date;
        this.customer = customer;
        this.info=info;
        this.timeEnd = timeEnd;
        this.timeStart = timeStart;
        this.accepted = accepted;
    }
}
