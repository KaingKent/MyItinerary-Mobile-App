package com.example.myitinerary;

import android.app.AlertDialog;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;

public class Itinerary extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);
        String itinID = getIntent().getStringExtra("id");
        String itinCol = getIntent().getStringExtra("collection");

        TextView itinNameEdit = findViewById(R.id.editItinName);
        TextView itinDescEdit = findViewById(R.id.editItinDesc);
        TextView itinStartEdit = findViewById(R.id.startTimeEditText);
        TextView itinEndEdit = findViewById(R.id.endTimeEditText);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        assert user != null;
        DocumentReference itinerary =
                db.collection("itineraries").document("users")
                        .collection(itinCol)
                        .document(itinID);

        itinerary.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> itineraryData = document.getData();
                    assert itineraryData != null;
                    String itineraryName = document.getString("name");
                    String itineraryDesc = document.getString("description");
                    itinNameEdit.setText(itineraryName);
                    itinDescEdit.setText(itineraryDesc);
                    itinStartEdit.setText(itinStartEdit.getText() + " " + document.getString("timeStart"));
                    itinEndEdit.setText(itinEndEdit.getText() + " " + document.getString("timeEnd"));

                } else {
                    Toast.makeText(this, "Opening Shared Itinerary", LENGTH_LONG).show();

                }
            } else {
                Toast.makeText(this, "Error", LENGTH_LONG).show();
            }
        });

        itinerary.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Toast.makeText(Itinerary.this,
                        e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            if (snapshot == null) {
                Toast.makeText(Itinerary.this,
                        "Deleted itinerary", Toast.LENGTH_SHORT).show();
            }
        });

        // create new event
        Button newEventBttn = findViewById(R.id.newEventBttn);
        newEventBttn.setOnClickListener(v ->
            setFragment(new NewEventFragment(), itinID));

        //back to home
        ImageButton backItineraries = findViewById(R.id.backItineraries);
        backItineraries.setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class)));

        //delete itinerary
        ImageButton delete = findViewById(R.id.deleteItin);
        delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog);
            builder.setCancelable(true);
            builder.setTitle("Delete Itinerary");
            builder.setMessage("Are you sure you want to delete this?");
            builder.setPositiveButton("Confirm",
                    (dialog, which) -> {
                        deleteItinerary(itinID, user.getUid());
                        Intent intent = new Intent(Itinerary.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Itinerary.this.startActivity(intent);
                    });
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });
        //save button
        ImageButton saveBtn = findViewById(R.id.save);
        saveBtn.setOnClickListener(v -> {
            String itinName = itinNameEdit.getText().toString().trim();
            String itinDesc = itinDescEdit.getText().toString().trim();

            itinerary.update("name", itinName);
            itinerary.update("description", itinDesc);

            itinDescEdit.setText(itinDesc);
            itinNameEdit.setText(itinName);
            startActivity(new Intent(Itinerary.this, MainActivity.class));
        });

        //edit start time
        ImageButton editStartTime = findViewById(R.id.startTimeEdit);
        editStartTime.setOnClickListener(v -> {
            final View dialogView = View.inflate(Itinerary.this, R.layout.date_time_picker, null);
            final AlertDialog alertDialog = new AlertDialog.Builder(Itinerary.this).create();

            dialogView.findViewById(R.id.date_time_set).setOnClickListener((View.OnClickListener) view -> {

                DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

                Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getHour(),
                        timePicker.getMinute());
                itinStartEdit.setText("Start Time: " + calendar.getTime().toString());
                itinerary.update("timeStart", calendar.getTime().toString());

                alertDialog.dismiss();
            });
            alertDialog.setView(dialogView);
            alertDialog.show();
        });

        //edit end time
        ImageButton editEndTime = findViewById(R.id.endTimeEdit);
        editEndTime.setOnClickListener(v -> {
            final View dialogView = View.inflate(Itinerary.this, R.layout.date_time_picker, null);
            final AlertDialog alertDialog = new AlertDialog.Builder(Itinerary.this).create();

            dialogView.findViewById(R.id.date_time_set).setOnClickListener((View.OnClickListener) view -> {

                DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

                Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getHour(),
                        timePicker.getMinute());
                itinEndEdit.setText("End Time: " + calendar.getTime().toString());
                itinerary.update("timeEnd", calendar.getTime().toString());

                alertDialog.dismiss();
            });
            alertDialog.setView(dialogView);
            alertDialog.show();
        });

        Button share = findViewById(R.id.share);
        share.setOnClickListener(v -> {
           // db.collection("itineraries").document("users").collection("shared").add(itinerary.get());

            itinerary.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        DocumentReference itineraries = db.collection("itineraries").document("users");

                        //update number of itineraries
                        itineraries.update("numItins", FieldValue.increment(1));

                        CollectionReference userItineraries = itineraries.collection("shared");

                        Map<String, Object> itin1 = new HashMap<>();
                        //needs automatically calculated and put into INFO document of itinerary event collection
                        itin1.put("timeStart", document.getString("timeStart"));
                        itin1.put("timeEnd", document.getString("timeEnd"));
                        itin1.put("name", document.getString("name"));
                        itin1.put("description", document.getString("description"));

                        // set itinerary document id as random, this is identifier always in db, but field is edited
                        userItineraries.document().set(itin1);

                    }
                }
            });


        });

        //display events
        CollectionReference userItineraryEvents =
                db.collection("itineraries").document("users")
                        .collection(user.getUid())
                        .document(itinID)
                        .collection("events");

        userItineraryEvents.get().addOnSuccessListener(documents -> {
            if(!documents.isEmpty())
            {
                int i = 1;
                int id = 100;
                List<DocumentSnapshot> firebaseEvents = documents.getDocuments();
                for (DocumentSnapshot d : firebaseEvents) {

                    // add xml view for event listing
                    ConstraintLayout parentCl = findViewById(R.id.events_itinerary);

                    //Travel Event
                    if (d.contains("startingAirport")) {
                        ConstraintLayout eventListing = (ConstraintLayout) this.getLayoutInflater().inflate(R.layout.travel_event_listing, parentCl, false);

                        eventListing.setId(id);
                        TextView eventStartingAirport = eventListing.findViewById(R.id.airport1);
                        eventStartingAirport.setText((String) d.get("startingAirport"));
                        TextView eventEndingAirport = eventListing.findViewById(R.id.airport2);
                        eventEndingAirport.setText((String) d.get("endingAirport"));
                        TextView eventDate = eventListing.findViewById(R.id.travel_event_date);

                        String[] startToken = d.get("timeStart").toString().split(" ");
                        String[] endToken = d.get("timeEnd").toString().split(" ");
                        String date;
                        //print times since some times could be empty
                        if (startToken.length == 1 && endToken.length == 1) {
                            date = "";
                        } else if (endToken.length == 1) {
                            date = startToken[1] + " " + startToken[2] + " " + startToken[3] + " -";
                        } else if (startToken.length == 1) {
                            date = "- " + endToken[1] + " " + endToken[2] + " " + endToken[3];
                        } else {
                            date = startToken[1] + " " + startToken[2] + " " + startToken[3] + " - \n" +
                                    endToken[1] + " " + endToken[2] + " " + endToken[3];
                        }

                        eventDate.setText(date);
                        parentCl.addView(eventListing);

                        ConstraintSet set = new ConstraintSet();
                        set.clone(parentCl);

                        //set constraints of generated layout
                        // TODO: itinListing height and padding is hardcoded as 100 for all
                        //  below first itinerary, values from layout file
                        if (i == 1) {
                            set.connect(eventListing.getId(), ConstraintSet.TOP, parentCl.getId(),
                                    ConstraintSet.TOP, dpToPx(15));
                        } else {
                            set.connect(eventListing.getId(), ConstraintSet.TOP, parentCl.getId(),
                                    ConstraintSet.TOP, dpToPx(130 * (i - 1)) + dpToPx(15));
                        }
                        set.applyTo(parentCl);

                    }
                    i++;
                    id++;
                }
            }
        });
        CollectionReference userItineraryActivities =
                db.collection("itineraries").document("users")
                        .collection(user.getUid())
                        .document(itinID)
                        .collection("activities");

        userItineraryActivities.get().addOnSuccessListener(documents -> {
            if(!documents.isEmpty())
            {
                int i = 1;
                int id = 100;
                List<DocumentSnapshot> firebaseEvents = documents.getDocuments();
                for (DocumentSnapshot d : firebaseEvents) {

                    // add xml view for event listing
                    ConstraintLayout parentCl = findViewById(R.id.events_itinerary);

                        ConstraintLayout activityListing = (ConstraintLayout) this.getLayoutInflater().inflate(R.layout.itinerary_listing, parentCl, false);

                        activityListing.setId(id);
                        TextView itinName = activityListing.findViewById(R.id.itin_name);
                        itinName.setText((String) d.get("name"));
                        TextView itinDate = activityListing.findViewById(R.id.travel_event_date);

                        String[] startToken = d.get("timeStart").toString().split(" ");
                        String[] endToken = d.get("timeEnd").toString().split(" ");
                        String date;
                        //print times since some times could be empty
                        if (startToken.length == 1 && endToken.length == 1) {
                            date = "";
                        } else if (endToken.length == 1) {
                            date = startToken[1] + " " + startToken[2] + " " + startToken[3] + " -";
                        } else if (startToken.length == 1) {
                            date = "- " + endToken[1] + " " + endToken[2] + " " + endToken[3];
                        } else {
                            date = startToken[1] + " " + startToken[2] + " " + startToken[3] + " - \n" +
                                    endToken[1] + " " + endToken[2] + " " + endToken[3];
                        }

                    itinDate.setText(date);
                        parentCl.addView(activityListing);

                        ConstraintSet set = new ConstraintSet();
                        set.clone(parentCl);

                        //set constraints of generated layout
                        // TODO: itinListing height and padding is hardcoded as 100 for all
                        //  below first itinerary, values from layout file
                        if (i == 1) {
                            set.connect(activityListing.getId(), ConstraintSet.TOP, parentCl.getId(),
                                    ConstraintSet.TOP, dpToPx(15));
                        } else {
                            set.connect(activityListing.getId(), ConstraintSet.TOP, parentCl.getId(),
                                    ConstraintSet.TOP, dpToPx(130 * (i - 1)) + dpToPx(15));
                        }
                        set.applyTo(parentCl);
                    i++;
                    id++;
                }
            }
        });
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private void setFragment(Fragment frag, String itinID) {
        Bundle arguments = new Bundle();
        arguments.putString("itinId" , itinID);
        frag.setArguments(arguments);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out
        );
        ConstraintLayout itin = findViewById(R.id.itinerary_layout);
        itin.removeAllViews();
        ft.replace(R.id.itinerary_layout, frag);
        ft.commit();
    }

    public static void deleteItinerary(String itinName, String uid){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference itineraries = db.collection("itineraries").document("users");
        CollectionReference userItineraries = itineraries.collection(uid);

        itineraries.update("numItins", FieldValue.increment(-1));
        userItineraries.document(itinName).delete();
    }

}