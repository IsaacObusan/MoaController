package com.example.moacontroller;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private SwitchCompat switchCompat;
    private DrawerLayout drawerLayout;
    private SwitchCompat switchHumidity;
    private boolean isHumidityOn;
    private ImageButton sidebarToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        viewPager = findViewById(R.id.viewPager);
        switchCompat = findViewById(R.id.Switch);
        setupViewPager(viewPager);


        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        sidebarToggle = findViewById(R.id.sidebar_toggle);


        sidebarToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Toggle clicked", Toast.LENGTH_SHORT).show();
                toggleSidebar();


            }
        });


        switchHumidity = navigationView.findViewById(R.id.switch_humidity);

        isHumidityOn = false;
        updateSidebarSwitches();


        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewPager.setCurrentItem(1);
            } else {
                viewPager.setCurrentItem(0);
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                switchCompat.setOnCheckedChangeListener(null);
                switchCompat.setChecked(position == 1);
                switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    viewPager.setCurrentItem(isChecked ? 1 : 0);
                });
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }


    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new Manual());   // Fragment 0
        adapter.addFragment(new Automatic()); // Fragment 1
        viewPager.setAdapter(adapter);
    }


    private void updateSidebarSwitches() {
        switchHumidity.setChecked(isHumidityOn);
    }


    private void toggleSidebar() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }
}
