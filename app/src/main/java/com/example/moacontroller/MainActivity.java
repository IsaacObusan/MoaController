package com.example.moacontroller;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private SwitchCompat switchCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        switchCompat = findViewById(R.id.Switch);


        setupViewPager(viewPager);


        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                viewPager.setCurrentItem(1);
            } else {

                viewPager.setCurrentItem(0);
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                switchCompat.setOnCheckedChangeListener(null);
                if (position == 1) {
                    switchCompat.setChecked(true);  // Manual fragment
                } else {
                    switchCompat.setChecked(false); // Automatic fragment
                }
                switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        viewPager.setCurrentItem(1);
                    } else {
                        viewPager.setCurrentItem(0);
                    }
                });
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // No action needed here
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new Manual());
        adapter.addFragment(new Automatic());
        viewPager.setAdapter(adapter);
    }
}
