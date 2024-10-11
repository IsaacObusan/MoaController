package com.example.moacontroller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import androidx.appcompat.widget.SwitchCompat;
import java.util.ArrayList;
import java.util.Locale;

public class Automatic extends Fragment {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private ImageView needle;
    private Button voiceCommandButton;
    private SwitchCompat switchLights; // Switch for lights
    private SeekBar verticalSeekBar; // SeekBar for rotate

    private DatabaseReference databaseReference; // Firebase database reference

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_automatic, container, false);

        needle = view.findViewById(R.id.needle);
        voiceCommandButton = view.findViewById(R.id.btn_voice_command);
        switchLights = view.findViewById(R.id.switch_lights); // Initialize the SwitchCompat
        verticalSeekBar = view.findViewById(R.id.verticalSeekBar); // Initialize SeekBar

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("control");

        // Set up the switch state based on the database
        setupSwitchListener();

        // Set up the SeekBar based on the database value
        setupSeekBarListener();

        // Fetch initial values from Firebase
        fetchInitialValues();

        voiceCommandButton.setOnClickListener(v -> {
            if (checkAudioPermission()) {
                startVoiceRecognition();
            } else {
                requestAudioPermission();
            }
        });

        return view;
    }

    private void setupSwitchListener() {
        // Read the current state from Firebase and update the switch
        databaseReference.child("light").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean isLightOn = dataSnapshot.getValue(Boolean.class);
                if (isLightOn != null) {
                    switchLights.setChecked(isLightOn); // Update the switch state
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load light state.", Toast.LENGTH_SHORT).show();
            }
        });

        // Update Firebase when the switch is toggled
        switchLights.setOnCheckedChangeListener((buttonView, isChecked) -> {
            databaseReference.child("light").setValue(isChecked);
            Toast.makeText(getContext(), "Lights turned " + (isChecked ? "ON" : "OFF"), Toast.LENGTH_SHORT).show();
        });
    }

    private void setupSeekBarListener() {
        verticalSeekBar.setMax(1); // Set max to 1 for start/stop functionality
        verticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                databaseReference.child("rotate").setValue(progress == 1); // Update the rotate value in Firebase
                Toast.makeText(getContext(), progress == 1 ? "Start" : "Stop", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optional: Do something when user starts interacting with SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Optional: Do something when user stops interacting with SeekBar
            }
        });
    }

    private void fetchInitialValues() {
        // Fetch the initial state for both the light and rotate
        databaseReference.child("light").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean isLightOn = dataSnapshot.getValue(Boolean.class);
                if (isLightOn != null) {
                    switchLights.setChecked(isLightOn); // Set Switch based on light state
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load initial light state.", Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference.child("rotate").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean isRotateOn = dataSnapshot.getValue(Boolean.class);
                if (isRotateOn != null) {
                    verticalSeekBar.setProgress(isRotateOn ? 1 : 0); // Set SeekBar based on rotate state
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load initial rotate state.", Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch the speed value from Firebase
        databaseReference.child("speeDometer").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Float speed = dataSnapshot.getValue(Float.class);
                if (speed != null) {
                    rotateNeedle(speed); // Set the needle based on speed value
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load speed value.", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up listener for real-time updates on rotate
        databaseReference.child("rotate").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean isRotateOn = dataSnapshot.getValue(Boolean.class);
                if (isRotateOn != null) {
                    verticalSeekBar.setProgress(isRotateOn ? 1 : 0); // Update SeekBar based on rotate state
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load rotate state.", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up listener for real-time updates on speed
        databaseReference.child("speeDometer").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Float speed = dataSnapshot.getValue(Float.class);
                if (speed != null) {
                    rotateNeedle(speed); // Update needle based on speed value
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load speed value.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void rotateNeedle(float speed) {
        float rotationAngle = getRotationAngleForSpeed(speed);
        needle.setRotation(rotationAngle);
    }

    private float getRotationAngleForSpeed(float speed) {
        switch ((int) speed) {
            case 1: return -5f;
            case 2: return 23f;
            case 3: return 122f;
            case 4: return 150f;
            default: return 0f;
        }
    }

    private boolean checkAudioPermission() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestAudioPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
    }

    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");

        try {
            startActivityForResult(intent, 100);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Speech recognition not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startVoiceRecognition();
            } else {
                Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == getActivity().RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String spokenText = result.get(0);
                Toast.makeText(getContext(), "You said: " + spokenText, Toast.LENGTH_SHORT).show();
                handleVoiceCommand(spokenText);
            }
        }
    }

    private void handleVoiceCommand(String command) {
        command = command.toLowerCase();

        if (command.contains("turn on the lights")) {
            databaseReference.child("light").setValue(true);
            Toast.makeText(getContext(), "Lights turned ON", Toast.LENGTH_SHORT).show();
        } else if (command.contains("turn off the lights")) {
            databaseReference.child("light").setValue(false);
            Toast.makeText(getContext(), "Lights turned OFF", Toast.LENGTH_SHORT).show();
        } else if (command.contains("start the rotation")) {
            databaseReference.child("rotate").setValue(true);
            Toast.makeText(getContext(), "Rotation started", Toast.LENGTH_SHORT).show();
        } else if (command.contains("stop the rotation")) {
            databaseReference.child("rotate").setValue(false);
            Toast.makeText(getContext(), "Rotation stopped", Toast.LENGTH_SHORT).show();
        } else if (command.contains("set the speed")) {
            String[] parts = command.split(" ");
            if (parts.length > 4) {
                String speedLevelStr = parts[4];
                try {
                    int speedLevel = Integer.parseInt(speedLevelStr);
                    if (speedLevel >= 1 && speedLevel <= 4) {
                        databaseReference.child("speeDometer").setValue((float) speedLevel);
                        Toast.makeText(getContext(), "Speed set to level " + speedLevel, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Speed level must be between 1 and 4", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Invalid speed level. Please say a number between 1 and 4.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Please specify a speed level between 1 and 4.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Command not recognized", Toast.LENGTH_SHORT).show();
        }
    }
}
