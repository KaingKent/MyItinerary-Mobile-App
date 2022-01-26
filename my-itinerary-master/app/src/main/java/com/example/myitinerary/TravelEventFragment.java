package com.example.myitinerary;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class TravelEventFragment extends Fragment {
    private Calendar calendarS;
    private Calendar calendarE;

    private static void createEvent(String airport1, String airport2, String startTime, String endTime, String uid, String itinId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference itinerary =
                db.collection("itineraries").document("users")
                        .collection(uid)
                        .document(itinId);
        CollectionReference events = itinerary.collection("events");

        Map<String, Object> event = new HashMap<>();

        event.put("timeStart", startTime);
        event.put("timeEnd", endTime);
        event.put("startingAirport", airport1);
        event.put("endingAirport", airport2);
        events.document().set(event);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_travel_event, container, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Bundle bundle = this.getArguments();
        DocumentReference itinerary =
                db.collection("itineraries").document("users")
                        .collection(user.getUid())
                        .document(bundle.getString("itinId"));
        final String[] name = {""};
        itinerary.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                name[0] = (String) document.get("name");
            } else {
                Log.e("Uhoh", "get failed with ", task.getException());
            }
        });


        view.findViewById(R.id.cancelTravel).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Itinerary.class);
            assert bundle != null;
            intent.putExtra("id", bundle.getString("itinId"));
            intent.putExtra("collection", user.getUid());
            //intent.putExtra("itinName", name[0]);
            startActivity(intent);
        });



        EditText startingAirport = view.findViewById(R.id.editStartingAirport);
        EditText endingAirport = view.findViewById(R.id.editEndingAirport);

        Button start = view.findViewById(R.id.startTime);
        //start time
        start.setOnClickListener(v -> {
            final View dialogView = View.inflate(this.getContext(), R.layout.date_time_picker, null);
            final AlertDialog alertDialog = new AlertDialog.Builder(this.getContext()).create();

            dialogView.findViewById(R.id.date_time_set).setOnClickListener(view1 -> {

                DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

                calendarS = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getHour(),
                        timePicker.getMinute());

                TextView startTimeText = view.findViewById(R.id.startTimeText);
                startTimeText.setText(startTimeText.getText() + calendarS.getTime().toString());

                alertDialog.dismiss();
            });
            alertDialog.setView(dialogView);
            alertDialog.show();
        });

        Button end = view.findViewById(R.id.endTime);
        //end time
        end.setOnClickListener(v -> {
            final View dialogView = View.inflate(this.getContext(), R.layout.date_time_picker, null);
            final AlertDialog alertDialog = new AlertDialog.Builder(this.getContext()).create();

            dialogView.findViewById(R.id.date_time_set).setOnClickListener(view1 -> {

                DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

                calendarE = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getHour(),
                        timePicker.getMinute());

                TextView endTimeText = view.findViewById(R.id.endTimeText);
                endTimeText.setText(endTimeText.getText() + calendarE.getTime().toString());

                alertDialog.dismiss();
            });
            alertDialog.setView(dialogView);
            alertDialog.show();
        });
        Button createEvent = view.findViewById(R.id.createTravelBtn);
        createEvent.setOnClickListener(v -> {
            //Input must be validated
            String airport1 = startingAirport.getText().toString().trim();
            String airport2 = endingAirport.getText().toString().trim();
            if(airport1.isEmpty()) {
                startingAirport.setError("Please enter a starting airport.");
                startingAirport.requestFocus();
                return;
            } else if (airport2.isEmpty()){
                endingAirport.setError("Please enter an ending airport.");
                endingAirport.requestFocus();
                return;
            } else if (calendarE == null){
                end.setError("Please enter an ending time.");
                end.requestFocus();
                return;
            } else if (calendarS == null){
                start.setError("Please enter a starting time.");
                start.requestFocus();
                return;
            }

            TravelEventFragment.createEvent(airport1, airport2, calendarS.getTime().toString(), calendarE.getTime().toString(), user.getUid(), bundle.getString("itinId"));

            Intent intent = new Intent(getContext(), Itinerary.class);
            intent.putExtra("id", bundle.getString("itinId"));
            //intent.putExtra("itinName", name[0]);
            intent.putExtra("collection", user.getUid());
            startActivity(intent);
        });


        return view;
    }
}