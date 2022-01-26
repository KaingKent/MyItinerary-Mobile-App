package com.example.myitinerary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;
public class ActivityEvent extends AppCompatActivity{
    private Calendar calendarS;
    private Calendar calendarE;
    public static void createActivityEntry(String itinName, String itinDescription, String uid, String start, String end) {
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
        setContentView(R.layout.activity_activity_event);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String itinID = getIntent().getStringExtra("id");

        EditText itinNameEditText = findViewById(R.id.editNameActivity);
        EditText itinDescEditText = findViewById(R.id.editDescriptionAct);

        ImageButton backItinerary = findViewById(R.id.cancelActivities);
        backItinerary.setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class)));
        //Firebase user = FirebaseAuth.getInstance().getCurrentUser();

        Button start = findViewById(R.id.startTime);
        //
//        start.setOnClickListener(v -> {
//            final View dialogView = View.inflate(ActivityEvent.this, R.layout.date_time_picker, null);
//            final AlertDialog alertDialog = new AlertDialog.Builder(ActivityEvent.this).create();
//
//            dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
//                    TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);
//
//                    calendarS = new GregorianCalendar(datePicker.getYear(),
//                            datePicker.getMonth(),
//                            datePicker.getDayOfMonth(),
//                            timePicker.getHour(),
//                            timePicker.getMinute());
//
//                    TextView startTimeText = findViewById(R.id.startTimeText);
//                    startTimeText.setText(startTimeText.getText() + calendarS.getTime().toString());
//
//                    alertDialog.dismiss();
//                }});
//            alertDialog.setView(dialogView);
//            alertDialog.show();
//        });
//
//        Button end = findViewById(R.id.endTime);
//        //end time
//        end.setOnClickListener(v -> {
//            final View dialogView = View.inflate(ActivityEvent.this, R.layout.date_time_picker, null);
//            final AlertDialog alertDialog = new AlertDialog.Builder(ActivityEvent.this).create();
//
//            dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
//                    TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);
//
//                    calendarE = new GregorianCalendar(datePicker.getYear(),
//                            datePicker.getMonth(),
//                            datePicker.getDayOfMonth(),
//                            timePicker.getHour(),
//                            timePicker.getMinute());
//
//                    TextView endTimeText = findViewById(R.id.endTimeText);
//                    endTimeText.setText(endTimeText.getText() + calendarE.getTime().toString());
//
//                    alertDialog.dismiss();
//                }});
//            alertDialog.setView(dialogView);
//            alertDialog.show();
//        });
//        Button createActivity = findViewById(R.id.createActivityBtn);
//        createActivity.setOnClickListener(v -> {
//            String itinName = itinNameEditText.getText().toString().trim();
//            if (itinName.isEmpty()) {
//                itinNameEditText.setError("Enter Activity name");
//                itinNameEditText.requestFocus();
//                return;
//            }
//
//            String itinDescription = itinDescEditText.getText().toString().trim();
//            if(itinDescription.isEmpty()) {
//                itinDescription = "";
//            }
//            assert user != null;
//            //ActivityEvent.createActivityEntry();
//            if(calendarS == null && calendarE == null){
//                ActivityEvent.createActivityEntry(itinName, itinDescription, user.getUid(), "", "");
//            }else if(calendarS == null ){
//                ActivityEvent.createActivityEntry(itinName, itinDescription, user.getUid(), "", calendarE.getTime().toString());
//            }else if(calendarE == null){
//                ActivityEvent.createActivityEntry(itinName, itinDescription, user.getUid(), calendarS.getTime().toString(), "");
//            }else{
//                ActivityEvent.createActivityEntry(itinName, itinDescription, user.getUid(), calendarS.getTime().toString(), calendarE.getTime().toString());
//            }
//
//            Toast.makeText(ActivityEvent.this,
//                    "Itinerary created", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(ActivityEvent.this, MainActivity.class));
//        });


    }
}
