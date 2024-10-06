package com.example.moacontroller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;

public class Automatic extends Fragment {

    private ImageView needle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_automatic, container, false);
        needle = view.findViewById(R.id.needle);
        float speed = 1;
        rotateNeedle(speed);

        return view;
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
}
