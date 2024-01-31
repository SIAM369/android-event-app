package edu.ewubd.cse489_2023_2_2020_1_60_080;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateEventActivity extends Activity {

    private EditText etName, etPlace, etDateTime, etCapacity, etBudget, etEmail, etPhone, etDescription;

    private TextView errorMsg;

    private RadioButton rdIndoor, rdOutdoor, rdOnline;

    private Button btnCancel, btnShare, btnSave;

    private String eventID = "";

    private EventDB eventDB;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        etName = findViewById(R.id.etName);
        etPlace = findViewById(R.id.etPlace);
        etDateTime = findViewById(R.id.etDateTime);
        etCapacity = findViewById(R.id.etCapacity);
        etBudget = findViewById(R.id.etBudget);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etDescription = findViewById(R.id.etDescription);

        rdIndoor = findViewById(R.id.rdIndoor);
        rdOutdoor = findViewById(R.id.rdOutdoor);
        rdOnline = findViewById(R.id.rdOnline);

        btnCancel = findViewById(R.id.btnCancel);
        btnShare = findViewById(R.id.btnShare);
        btnSave = findViewById(R.id.btnSave);

        errorMsg = findViewById(R.id.tvErrorMsg);

        eventDB = new EventDB(this);

        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String name =  etName.getText().toString();
                String place = etPlace.getText().toString();
                String dateTime =  etDateTime.getText().toString();
                String capacity = etCapacity.getText().toString();
                String budget = etBudget.getText().toString();
                String email = etEmail.getText().toString();
                String phone = etPhone.getText().toString();
                String description = etDescription.getText().toString();
                String eventType = "";
                String errMsg = "";

                if(!name.isEmpty() && !place.isEmpty() && !dateTime.isEmpty() && !capacity.isEmpty() && !budget.isEmpty() && !email.isEmpty() && !phone.isEmpty() && !description.isEmpty() && !eventType.isEmpty()){
                    String regex = "^[A-Za-z\\s'-]+$";
                    if(name.length()< 4 || name.length() > 12 || !name.matches(regex)){
                        errMsg += "Invalid Name\n";
                    }


                    if(place.length()<6 || place.length() > 64){
                        errMsg += "Invalid Place\n";
                    }

                    boolean isIndoorChecked = rdIndoor.isChecked();
                    boolean isOutdoorChecked = rdOutdoor.isChecked();
                    boolean isOnlineChecked = rdOnline.isChecked();

                    if (!(isIndoorChecked || isOutdoorChecked || isOnlineChecked)){
                        errMsg += "Select an event type\n";
                    }


                    if (!dateTime.isEmpty()) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()); // Use HH:mm for 24-hour format
                        try {
                            Date eventDate = format.parse(dateTime);
                            Date now = new Date();
                            if (eventDate.before(now)) {
                                errMsg += "Invalid Date And Time (It must be in the future)\n";
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            errMsg += "Invalid Date And Time Format (Use YYYY-MM-dd HH:mm:ss)\n";
                        }
                    }
                    else {
                        errMsg += "Invalid Date And Time\n";
                    }


                    int capacityInt = 0;
                    if(!capacity.isEmpty()){
                        capacityInt = Integer.parseInt(capacity);
                    }
                    if(capacityInt <= 0){
                        errMsg += "Invalid Capacity (Field is empty)\n";
                    }


                    double budgetDouble = 0.0;
                    if(!budget.isEmpty()){
                        budgetDouble = Double.parseDouble(budget);
                    }
                    if(budgetDouble<1000.0){
                        errMsg += "Invalid Budget (Must be greater than 1000)\n";
                    }


                    String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
                    if(!email.matches(emailRegex)){
                        errMsg += "Invalid Email\n";
                    }

                    if(phone.length()<8 && phone.length()>=16){
                        errMsg += "Invalid Phone Number (8 > Phone Number > 17)\n";
                    }
                    else if(phone.startsWith("+") && phone.length()!=14){
                        errMsg += "Invalid Phone Number (Add '+88' beginning of the Phone Number\n";
                    }
                    else if(phone.startsWith("0") && phone.length()!=11){
                        errMsg += "Invalid Phone (Try again)\n";
                    }

                    if(description.length()<10 && description.length()>1000){
                        errMsg += "Invalid Description (Length is bigger than 10 and less than 1000\n";
                    }
                    errorMsg.setText(errMsg);
                }
                else {
                    errMsg += "Fill all the fields\n";
                }

                long isDateTimeOkay = 0;
                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    Date eventDate = format.parse(dateTime);
                    isDateTimeOkay = eventDate.getTime();
                }catch (ParseException e){
                    e.printStackTrace();
                }

                int capacityInt = Integer.parseInt(capacity);
                double budgetDouble = Double.parseDouble(budget);

                //code to store data on local database
                if(eventID.isEmpty()){
                    eventID = name + System.currentTimeMillis();
                    eventDB.insertEvent(eventID, name, place, isDateTimeOkay, capacityInt, budgetDouble, email, phone, description, eventType);
                }
                else{
                    eventDB.updateEvent(eventID, name, place, isDateTimeOkay, capacityInt, budgetDouble, email, phone, description, eventType);
                }


                /*//Store event information to remote server
                String keys[] = {"action", "sid", "semester", "id", "title", "place", "type", "date_time", "capacity", "budget", "email", "phone", "des"};
                String values[] = {"backup", "2020-1-60-080", "2023-2", eventID, name, place, eventType, isDateTimeOkay, capacityInt, budgetDouble, email, phone, description};
                //httpRequest(keys, values);
                 */
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("cancel btn");
                finish();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Share Button");
            }
        });

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
                    //updateEventListByServerData(data);
                    Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }*/
}