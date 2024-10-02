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

        // Initialize ViewPager and SwitchCompat in main content
        viewPager = findViewById(R.id.viewPager);
        switchCompat = findViewById(R.id.Switch);
        setupViewPager(viewPager);

        // Initialize DrawerLayout and NavigationView for the sidebar
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Initialize sidebar toggle button
        sidebarToggle = findViewById(R.id.sidebar_toggle);

        // Set up click listener for the sidebar toggle button
        sidebarToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Toggle clicked", Toast.LENGTH_SHORT).show();
                toggleSidebar(); // Call the method to open/close the sidebar
                // Show a toast message indicating the toggle has been clicked

            }
        });

        // Initialize sidebar switches as indicators
        switchHumidity = navigationView.findViewById(R.id.switch_humidity);

        // Default states for sidebar indicators
        isHumidityOn = false;

        // Update the sidebar switches initially
        updateSidebarSwitches();

        // Set up the main SwitchCompat listener for controlling fragments
        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewPager.setCurrentItem(1);  // Switch to Automatic fragment
            } else {
                viewPager.setCurrentItem(0);  // Switch to Manual fragment
            }
        });

        // Synchronize the SwitchCompat with the ViewPager's fragment changes
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

    // Method to set up the ViewPager with fragments
    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new Manual());   // Fragment 0
        adapter.addFragment(new Automatic()); // Fragment 1
        viewPager.setAdapter(adapter);
    }

    // Method to update sidebar switches based on current states (indicators only)
    private void updateSidebarSwitches() {
        switchHumidity.setChecked(isHumidityOn);
    }

    // Method to toggle the sidebar manually
    private void toggleSidebar() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }
}
