package com.example.myitinerary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.List;

// import com.google.android.gms.tasks.OnCompleteListener;
// import com.google.android.gms.tasks.Task;
// import java.util.Arrays;

public class ProfileFragment extends Fragment {


    private EditText editTextEmail, editPasswordProfile, editPasswordConfirmProfile, editName,
            editHomeCity, editBio;
    private FirebaseUser user;
    private DatabaseReference userRef;
    //private String currentEmail;
    private String currentName, currentBio, currentLocation;
    User userProfile;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        editTextEmail = view.findViewById(R.id.editEmailProfile);
        Button updateProfileBtn = view.findViewById(R.id.updateProfileBtn);
        Button updateProfileBtn2 = view.findViewById(R.id.updateProfileBtn2);
        editPasswordProfile = view.findViewById(R.id.editPasswordProfile);
        editPasswordConfirmProfile = view.findViewById(R.id.editPasswordConfirmProfile);
        editName = view.findViewById(R.id.editName);
        editHomeCity = view.findViewById(R.id.editHomeCity);
        editBio = view.findViewById(R.id.editBio);
        currentLocation = "";
        currentBio = "";
        String placesApiKey = "AIzaSyBpG3NwV_XSq5PaRbrCL7UobwKRUrCE-ZM";
        Places.initialize(requireActivity().getApplicationContext(),
                placesApiKey);

        //fill current user name, email, location, and bio
        userRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userProfile = snapshot.getValue(User.class);
                assert userProfile != null;
                editTextEmail.setText(userProfile.email);
                //currentEmail = userProfile.email;
                currentName = userProfile.name;
                editName.setText(currentName);
                // feature added later, might not be a field
                if(userProfile.location != null)
                {
                    currentLocation = userProfile.location;
                    editHomeCity.setText(currentLocation);
                }
                if(userProfile.bio != null)
                {
                    currentBio = userProfile.bio;
                    editBio.setText(currentBio);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // update password
        updateProfileBtn.setOnClickListener(v -> {
            boolean changePassword = false;

            if(!editPasswordProfile.getText().toString().trim().isEmpty())
            {
                if(editPasswordProfile.getText().toString().trim().length() < 8)
                {
                    editPasswordProfile.setError("Password must be at least 8 characters");
                    editPasswordProfile.requestFocus();
                    return;
                } else if(!editPasswordProfile.getText().toString().trim().equals(
                        editPasswordConfirmProfile.getText().toString().trim()))
                {
                    editPasswordConfirmProfile.setError("New passwords must match");
                    editPasswordConfirmProfile.requestFocus();
                    return;
                } else changePassword = true;
            }
            //check what needs updated
            if(changePassword)
            {
                updatePassword(editPasswordProfile.getText().toString().trim());
            }
        });

        //places API activity
        editHomeCity.setFocusable(false);
        editHomeCity.setOnClickListener(v -> {
            List<Place.Field> fields = Collections.singletonList(Place.Field.ADDRESS);
            Intent i = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,
                    fields).setTypeFilter(TypeFilter.CITIES)
                    .build(requireActivity());
            startActivityForResult(i,100);
        });

        // update profile location and bio
        updateProfileBtn2.setOnClickListener(v -> {
            boolean profileUpdated = false;

            if(!editName.getText().toString().equals(currentName))
            {
                if(!editName.getText().toString().contains(" "))
                {
                    editName.setError("Please enter a first and last name");
                    editName.requestFocus();
                    return;
                }
                currentName = editName.getText().toString();
                profileUpdated = true;
                userRef.child(user.getUid()).child("name").setValue(currentName);
            }
            if(!editHomeCity.getText().toString().equals(currentLocation))
            {
                currentLocation = editHomeCity.getText().toString();
                profileUpdated = true;
                userRef.child(user.getUid()).child("location").setValue(currentLocation);
            }

            if(!editBio.getText().toString().equals(currentBio))
            {
                currentBio = editBio.getText().toString();
                profileUpdated = true;
                userRef.child(user.getUid()).child("bio").setValue(currentBio);
            }
            if(profileUpdated)
                Toast.makeText(getActivity(),"Profile updated", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    //places API, get result back from activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == Activity.RESULT_OK)
        {
            Place place = Autocomplete.getPlaceFromIntent(data);
            editHomeCity.setText(place.getAddress());
        }
        else if(resultCode == AutocompleteActivity.RESULT_ERROR)
        {
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(requireActivity().getApplicationContext(), status.getStatusMessage(),
                    Toast.LENGTH_SHORT).show();
            //editHomeCity.setText(status.getStatusMessage());
        }
    }

    private void updatePassword(String password)
    {
        user.updatePassword(password).addOnCompleteListener(
                task -> {
                    if(task.isSuccessful()) {
                        Toast.makeText(getActivity(),"Password update success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(),"Password update failed", Toast.LENGTH_SHORT).show();
                    }
                });
        editPasswordProfile.setText("");
        editPasswordConfirmProfile.setText("");
    }

}