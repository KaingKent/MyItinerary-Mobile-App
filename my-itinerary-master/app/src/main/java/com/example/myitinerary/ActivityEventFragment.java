package com.example.myitinerary;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActivityEventFragment#//newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivityEventFragment extends Fragment {
    private Calendar calendarS;
    private Calendar calendarE;

    private static void createEvent(String name1, String description, String startTime, String endTime, String uid, String itinID) { //Maybe dont need itinId
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference itineraries = db.collection("itineraries").document("users").collection(uid).document(itinID);

        CollectionReference activities = itineraries.collection("activities");

        Map<String,Object> activity = new HashMap<>();

        activity.put("timeStart",startTime);
        activity.put("timeEnd",endTime);
        activity.put("name",name1);
        activity.put("description", description);

        activities.document().set(activity);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_activity_event, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Bundle bundle = this.getArguments();
        //TextView test = view.findViewById(R.id.testItinId);
        //assert bundle != null;
       // test.setText(bundle.getString("itinId"));
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

        view.findViewById(R.id.cancelActivities).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Itinerary.class);
            assert bundle != null;
            intent.putExtra("id", bundle.getString("itinId"));
            intent.putExtra("itinName", name[0]);
            startActivity(intent);
        });

        EditText nameAct = view.findViewById(R.id.editNameAct);
        EditText descriptionAct = view.findViewById(R.id.editDescriptionAct);

        Button start = view.findViewById(R.id.startTimeAct);

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

        Button end = view.findViewById(R.id.endTimeAct);

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

        Button createEvent = view.findViewById(R.id.createActivityBtn);
        createEvent.setOnClickListener(v -> {
            //Input must be validated
            String name2 = nameAct.getText().toString().trim();
            String description2 = descriptionAct.getText().toString().trim();
            if (name2.isEmpty()) {
                nameAct.setError("Please enter a starting airport.");
                nameAct.requestFocus();
                return;
            } else if (description2.isEmpty()) {
                descriptionAct.setError("Please enter an ending airport.");
                descriptionAct.requestFocus();
                return;
            } else if (calendarE == null) {
                end.setError("Please enter an ending time.");
                end.requestFocus();
                return;
            } else if (calendarS == null) {
                start.setError("Please enter a starting time.");
                start.requestFocus();
                return;
            }


            ActivityEventFragment.createEvent(name2, description2, calendarS.getTime().toString(), calendarE.getTime().toString(), user.getUid(), bundle.getString("itinId"));
            Intent intent = new Intent(getContext(), Itinerary.class);
            intent.putExtra("id", bundle.getString("itinId"));
            intent.putExtra("itinName", name[0]);
            intent.putExtra("collection", user.getUid());
            startActivity(intent);
        });
        return view;
    }
}