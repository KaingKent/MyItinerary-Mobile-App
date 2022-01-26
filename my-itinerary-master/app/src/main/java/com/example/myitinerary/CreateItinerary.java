package com.example.myitinerary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateItinerary extends AppCompatActivity {
    private Calendar calendarS;
    private Calendar calendarE;
    // used in registration and createItinerary
    public static void createItineraryEntry(String itinName, String itinDescription, String uid, String start, String end) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference itineraries = db.collection("itineraries").document("users");

        //update number of itineraries
        itineraries.update("numItins", FieldValue.increment(1));

        CollectionReference userItineraries = itineraries.collection(uid);

        Map<String, Object> itin1 = new HashMap<>();
        //needs automatically calculated and put into INFO document of itinerary event collection
        itin1.put("timeStart", start);
        itin1.put("timeEnd", end);
        itin1.put("name", itinName);
        itin1.put("description", itinDescription);
        // set itinerary document id as random, this is identifier always in db, but field is edited
        userItineraries.document().set(itin1);

        // create collection for events in that itinerary
        //userItineraries.document(itinName).collection("events");

        // first event
        /* itin1 = new HashMap<>();
        itin1.put("location", location);
        itin1.put("endTime", endTimeEvent);
        itin1.put("startTime", startTimeEvent);
        events.document(location).set(itin1); */
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_itinerary);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        EditText itinNameEditText = findViewById(R.id.editNameItinerary);
        EditText itinDescEditText = findViewById(R.id.editDescription);

        // go back to main activity
        ImageButton backItinerary = findViewById(R.id.backItineraries);
        backItinerary.setOnClickListener(v ->
                startActivity(new Intent(CreateItinerary.this, MainActivity.class)));

        Button start = findViewById(R.id.startTime);
        //start time
        start.setOnClickListener(v -> {
            final View dialogView = View.inflate(CreateItinerary.this, R.layout.date_time_picker, null);
            //View view = View.inflate(this, R.layout.LAYOUT, null);
            final AlertDialog alertDialog = new AlertDialog.Builder(CreateItinerary.this).create();

            dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                    TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

                    calendarS = new GregorianCalendar(datePicker.getYear(),
                            datePicker.getMonth(),
                            datePicker.getDayOfMonth(),
                            timePicker.getHour(),
                            timePicker.getMinute());

                    TextView startTimeText = findViewById(R.id.startTimeText);
                    startTimeText.setText(startTimeText.getText() + calendarS.getTime().toString());

                    alertDialog.dismiss();
                }});
            alertDialog.setView(dialogView);
            alertDialog.show();
        });

        Button end = findViewById(R.id.endTime);
        //end time
        end.setOnClickListener(v -> {
            final View dialogView = View.inflate(CreateItinerary.this, R.layout.date_time_picker, null);
            final AlertDialog alertDialog = new AlertDialog.Builder(CreateItinerary.this).create();

            dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                    TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

                    calendarE = new GregorianCalendar(datePicker.getYear(),
                            datePicker.getMonth(),
                            datePicker.getDayOfMonth(),
                            timePicker.getHour(),
                            timePicker.getMinute());

                    TextView endTimeText = findViewById(R.id.endTimeText);
                    endTimeText.setText(endTimeText.getText() + calendarE.getTime().toString());

                    alertDialog.dismiss();
                }});
            alertDialog.setView(dialogView);
            alertDialog.show();
        });

        Button createItinerary = findViewById(R.id.createItineraryBtn);
        createItinerary.setOnClickListener(v -> {
            // validate input
            String itinName = itinNameEditText.getText().toString().trim();
            if(itinName.isEmpty())
            {
                itinNameEditText.setError("Please enter an itinerary name");
                itinNameEditText.requestFocus();
                return;
            }
            // optional entry
            String itinDescription = itinDescEditText.getText().toString().trim();
            if(itinDescription.isEmpty())
            {
                itinDescription = "";
            }
            assert user != null;

            //no times might be selected
            if(calendarS == null && calendarE == null){
                CreateItinerary.createItineraryEntry(itinName, itinDescription, user.getUid(), "", "");
            }else if(calendarS == null ){
                CreateItinerary.createItineraryEntry(itinName, itinDescription, user.getUid(), "", calendarE.getTime().toString());
            }else if(calendarE == null){
                CreateItinerary.createItineraryEntry(itinName, itinDescription, user.getUid(), calendarS.getTime().toString(), "");
            }else{
                CreateItinerary.createItineraryEntry(itinName, itinDescription, user.getUid(), calendarS.getTime().toString(), calendarE.getTime().toString());
            }

            Toast.makeText(CreateItinerary.this,
                    "Itinerary created", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(CreateItinerary.this, MainActivity.class));
        });
    }
}