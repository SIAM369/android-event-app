package edu.ewubd.cse489_2023_2_2020_1_60_080;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    private ListView listEvents;
    private ArrayList<Event> events;
    private CustomEventAdapter adapter;
    Button btnCreateNew, btnHistory, btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCreateNew = findViewById(R.id.btnCreateNew);
        btnHistory = findViewById(R.id.btnHistory);
        btnExit = findViewById(R.id.btnExit);


        listEvents = findViewById(R.id.listEvents);
        events = new ArrayList<>();


        btnCreateNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to CreateEventActivity
                Intent intent = new Intent(MainActivity.this, CreateEventActivity.class);
                startActivity(intent);
            }
        });

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show previous events
                showHistory();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the login page
                navigateToLoginPage();
            }
        });


    }


    private void showHistory() {
        StringBuilder eventNames = new StringBuilder();

        // Get the current date and time
        long currentTimeMillis = System.currentTimeMillis();

        for (Event event : events) {
            if (event.datetime < currentTimeMillis) {
                // Event date-time is in the past, include it in the dialog
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String eventDateTime = dateFormat.format(new Date(event.datetime));

                eventNames.append(event.name).append(" - ").append(eventDateTime).append("\n");
            }
        }

        if (eventNames.length() > 0) {
            // There are previous date events, display them in the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Previous Date Events");
            builder.setMessage(eventNames.toString());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            // No previous date events found
            Toast.makeText(this, "No previous date events found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToLoginPage() {
        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(intent);
    }


    public void onStart(){
        super.onStart();
        loadData();
        String keys[] = {"action", "sid", "semester"};
        String values[] = {"restore", "2020-1-60-080", "2023-2"};
        //httpRequest(keys, values);
    }

    private void loadData(){
        events.clear();
        EventDB db = new EventDB(this);
        Cursor rows = db.selectEvents("SELECT * FROM events");
        if (rows.getCount() > 0) {

            while (rows.moveToNext()) {
                String id = rows.getString(0);
                String name = rows.getString(1);
                String place = rows.getString(2);
                long dateTime = rows.getLong(3);
                int capacity = rows.getInt(4);
                double budget = rows.getDouble(5);
                String email = rows.getString(6);
                String phone = rows.getString(7);
                String des = rows.getString(8);
                String eventType = rows.getString(9);
                Event e = new Event(id, name, place, dateTime, capacity, budget, email, phone, des, eventType);
                events.add(e);
            }// end-while
        } // end-if
        db.close();
        adapter = new CustomEventAdapter(this, events);
        listEvents.setAdapter(adapter);


        // handle the click on an event-list item
        listEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, CreateEventActivity.class);
                //String item = (String) parent.getItemAtPosition(position);

                i.putExtra("EventID", events.get(position).id);
                i.putExtra("name", events.get(position).name);
                i.putExtra("place", events.get(position).place);
                i.putExtra("datetime", events.get(position).datetime);
                i.putExtra("capacity", events.get(position).capacity);
                i.putExtra("budget", events.get(position).budget);
                i.putExtra("email", events.get(position).email);
                i.putExtra("phone", events.get(position).phone);
                i.putExtra("des", events.get(position).description);
                i.putExtra("type", events.get(position).eventType);

                startActivity(i);
            }
        });


        // handle the long-click on an event-list item
        listEvents.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //String message = "Do you want to delete event - "+events[position].name +" ?";
                //String message = "Do you want to delete event - "+events.get(position).name +" ?";
                //System.out.println(message);
                showDeleteDialog(events.get(position).name, position);
                return true;
            }
        });
    }
    private void showDeleteDialog(String eventName, int position){ //showing delete dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Event Details");
        builder.setMessage("Do you want to delete this event?\n'"+eventName+"'");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                deleteEvent(position);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteEvent(int position){
        EventDB db = new EventDB(this);
        db.deleteEvent(events.get(position).id);
        db.close();

        events.remove(position);
        adapter.notifyDataSetChanged();
    }
    /*private void httpRequest(final String keys[],final String values[]){
        new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... voids) {
                List<NameValuePair> params=new ArrayList<NameValuePair>();
                for (int i=0; i<keys.length; i++){
                    params.add(new BasicNameValuePair(keys[i],values[i]));
                }
                String url= "https://www.muthosoft.com/univ/cse489/index.php";
                String data="";
                try {
                    data=JSONParser.getInstance().makeHttpRequest(url,"POST",params);
                    System.out.println(data);
                    return data;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            protected void onPostExecute(String data){
                if(data!=null){
                    System.out.println(data);
                    System.out.println("Ok2");
                    updateEventListByServerData(data);
                    Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
    private void updateEventListByServerData(String data){
        System.out.println("found");
        try{
            JSONObject jo = new JSONObject(data);
            if(jo.has("events")){
                events.clear();
                JSONArray ja = jo.getJSONArray("events");
                for(int i=0; i<ja.length(); i++){
                    JSONObject event = ja.getJSONObject(i);
                    String id = event.getString("id");
                    String name = event.getString("title");
                    String place = event.getString("place");
                    long dateTime = event.getLong("datetime");
                    int capacity = event.getInt("capacity");
                    double budget = event.getDouble("budget");
                    String email = event.getString("email");
                    String phone = event.getString("phone");
                    String description = event.getString("description");
                    String eventType = event.getString("type");

                    Event e = new Event(id, name, place, dateTime, capacity, budget, email, phone, description, eventType);
                    events.add(e);
                }
                listEvents.deferNotifyDataSetChanged();
            }
        }catch(Exception e){}
    }*/
}