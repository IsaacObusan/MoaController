package com.example.moacontroller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class Manual extends Fragment {

    private SeekBar seekBar;
    private ImageView thumbImage, lights_button, rotate_button;
    private DatabaseReference databaseReference;
    private boolean isLightOn = false;  // Track light state
    private boolean isRotateOn = false; // Track rotate state

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_manual, container, false);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("control");

        seekBar = view.findViewById(R.id.seekBar);
        thumbImage = view.findViewById(R.id.floatingThumb);
        lights_button = view.findViewById(R.id.light);
        rotate_button = view.findViewById(R.id.rotate);

        // Load initial SeekBar value from Firebase
        loadInitialSeekBarValue();

        // Listener for SeekBar changes
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the position of the floating thumb
                int width = seekBar.getWidth();
                float thumbOffset = (width / (float) seekBar.getMax()) * progress;
                thumbImage.setTranslationX(thumbOffset - (thumbImage.getWidth() / 2));

                String message = "SeekBar is " + (progress + 1); // +1 to match your labels (1-4)
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                // Store the SeekBar value in Firebase under "speeDometer"
                databaseReference.child("speeDometer").setValue(progress + 1); // +1 to match your labels
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Toggle light state
        lights_button.setOnClickListener(v -> {
            isLightOn = !isLightOn;
            String message = "Light is " + (isLightOn ? "ON" : "OFF");
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            // Store light state in Firebase
            databaseReference.child("light").setValue(isLightOn);
        });

        // Toggle rotate state
        rotate_button.setOnClickListener(v -> {
            isRotateOn = !isRotateOn;
            String message = "Rotate is " + (isRotateOn ? "ON" : "OFF");
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            // Store rotate state in Firebase
            databaseReference.child("rotate").setValue(isRotateOn);
        });

        return view;
    }

    private void loadInitialSeekBarValue() {
        databaseReference.child("speeDometer").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer value = dataSnapshot.getValue(Integer.class);
                if (value != null) {
                    // Set the SeekBar value and update the thumb position
                    seekBar.setProgress(value - 1); // -1 to match the label range (1-4)
                    thumbImage.setTranslationX((seekBar.getWidth() / (float) seekBar.getMax()) * (value - 1) - (thumbImage.getWidth() / 2));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load SeekBar value.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
