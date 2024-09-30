package com.example.moacontroller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Manual extends Fragment {

    private SeekBar seekBar;
    private ImageView thumbImage;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manual, container, false);

        // Initialize SeekBar and ImageView
        seekBar = view.findViewById(R.id.seekBar);
        thumbImage = view.findViewById(R.id.floatingThumb);

        // Set SeekBar listener to update thumb position
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the position of the floating thumb
                int width = seekBar.getWidth();
                float thumbOffset = (width / (float) seekBar.getMax()) * progress; // Calculate offset based on progress
                thumbImage.setTranslationX(thumbOffset - (thumbImage.getWidth() / 2)); // Center the thumb
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        return view;
    }
}
