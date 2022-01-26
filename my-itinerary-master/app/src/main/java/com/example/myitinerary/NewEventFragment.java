package com.example.myitinerary;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewEventFragment//#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewEventFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_event, container, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Bundle arguments = getArguments();
        assert arguments != null;
        String itinId = arguments.getString("itinId");

        view.findViewById(R.id.backItinerary).setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), Itinerary.class);
                    intent.putExtra("id", itinId);
                    intent.putExtra("collection", user.getUid());
                    startActivity(intent);
                });

        view.findViewById(R.id.new_activity_event).setOnClickListener(v ->
                setFragment(new ActivityEventFragment(), itinId));

        view.findViewById(R.id.new_travel_event).setOnClickListener(v ->
                setFragment(new TravelEventFragment(), itinId));
        return view;
    }

    private void setFragment(Fragment frag, String itinID)
    {
        Bundle arguments = new Bundle();
        arguments.putString("itinId" , itinID);
        frag.setArguments(arguments);
        requireActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out
        ).replace(((ViewGroup) requireView().getParent()).getId(), frag)
                .commit();
    }
}