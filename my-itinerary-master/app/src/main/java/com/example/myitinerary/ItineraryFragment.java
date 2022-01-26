package com.example.myitinerary;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

// Fragment shows all itinerary listings, when one clicked, go to Itinerary class activity w/
// add event and display event functions as fragments

public class ItineraryFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_itineraries, container, false);

        // add create itinerary on fab
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> startActivity(new Intent(getActivity(),
                CreateItinerary.class)));


        //display itineraries
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        assert user != null;
        CollectionReference userItineraries =
                db.collection("itineraries").document("users")
                        .collection(user.getUid());

        userItineraries.get().addOnSuccessListener(documents -> {
            if(!documents.isEmpty())
            {
                int i = 1;
                int id = 100;
                List<DocumentSnapshot> firebaseItineraries = documents.getDocuments();
                for (DocumentSnapshot d : firebaseItineraries) {

                    // add xml view for itinerary listing
                    ConstraintLayout parentCl = view.findViewById(R.id.layout);
                    ConstraintLayout itinListing = (ConstraintLayout) inflater.inflate(R.layout.itinerary_listing, parentCl, false);
                    itinListing.setId(id);
                    TextView itinName = itinListing.findViewById(R.id.itin_name);
                    itinName.setText((String) d.get("name"));
                    TextView itinDate = itinListing.findViewById(R.id.travel_event_date);

                    String[] startToken = d.get("timeStart").toString().split(" ");
                    String[] endToken = d.get("timeEnd").toString().split(" ");
                    String date;
                    //print times since some times could be empty
                    if(startToken.length == 1 && endToken.length == 1){
                        date = "";
                    }else if(endToken.length == 1){
                        date = startToken[1] + " " + startToken[2] + " " + startToken[3] + " -";
                    }else if(startToken.length == 1){
                        date = "- " + endToken[1] + " " + endToken[2] + " " + endToken[3];
                    }else{
                        date = startToken[1] + " " + startToken[2] + " " + startToken[3] + " - \n" +
                                endToken[1] + " " + endToken[2] + " " + endToken[3];
                    }


                            //String date = d.get("timeStart") + " - " + d.get("timeEnd");
                    itinDate.setText(date);
                    parentCl.addView(itinListing);

                    ConstraintSet set = new ConstraintSet();
                    set.clone(parentCl);

                    //set constraints of generated layout
                    // TODO: itinListing height and padding is hardcoded as 100 for all
                    //  below first itinerary, values from layout file
                    if(i == 1) {
                        set.connect(itinListing.getId(), ConstraintSet.TOP, parentCl.getId(),
                                ConstraintSet.TOP, dpToPx(15));
                    }
                    else {
                        set.connect(itinListing.getId(), ConstraintSet.TOP, parentCl.getId(),
                                ConstraintSet.TOP, dpToPx(130*(i-1))+dpToPx(15));
                    }
                    set.applyTo(parentCl);

                    itinListing.setOnClickListener(v -> {
                        Intent intent = new Intent(getContext(), Itinerary.class);
                        intent.putExtra("id", d.getId());
                        intent.putExtra("itinName", (String) d.get("name"));
                        intent.putExtra("collection", user.getUid());
                        startActivity(intent);
                    });

                    i++;
                    id++;
                }
            }
        });
        return view;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = requireContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}