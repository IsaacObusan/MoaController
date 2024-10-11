package com.example.moacontroller;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private DatabaseReference currentPageRef;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private SwitchCompat switchCompat;
    private DrawerLayout drawerLayout;
    private SwitchCompat switchHumidity;
    private SwitchCompat switchVoltage;
    private SwitchCompat switchDaylight;
    private SwitchCompat switchAudience;
    private ImageButton sidebarToggle;
    private TextView headerTitle; // Declare TextView for header title

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("switchStates");
        currentPageRef = FirebaseDatabase.getInstance().getReference("currentPage"); // Separate reference for current page

        viewPager = findViewById(R.id.viewPager);
        switchCompat = findViewById(R.id.Switch);
        headerTitle = findViewById(R.id.header_title); // Initialize the TextView for the header title
        setupViewPager(viewPager);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        sidebarToggle = findViewById(R.id.sidebar_toggle);

        sidebarToggle.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Toggle clicked", Toast.LENGTH_SHORT).show();
            toggleSidebar();
        });

        // Initialize switches
        switchHumidity = navigationView.findViewById(R.id.switch_humidity);
        switchVoltage = navigationView.findViewById(R.id.switch_voltage);
        switchDaylight = navigationView.findViewById(R.id.switch_Daylight);
        switchAudience = navigationView.findViewById(R.id.switch_Audience);

        // Load saved switch states
        loadSwitchStates();

        // Set listeners for each switch
        setupSwitchListener(switchHumidity, "Humidity");
        setupSwitchListener(switchVoltage, "Voltage Detection");
        setupSwitchListener(switchDaylight, "Daylight Cycle");
        setupSwitchListener(switchAudience, "Audience");

        // Load current page
        loadCurrentPage();

        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewPager.setCurrentItem(isChecked ? 1 : 0);
            currentPageRef.setValue(isChecked ? "Automatic" : "Manual"); // Save current page to Firebase
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
                    currentPageRef.setValue(isChecked ? "Automatic" : "Manual"); // Update current page in Firebase
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

    private void toggleSidebar() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    private void setupSwitchListener(SwitchCompat switchCompat, String switchName) {
        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String message = switchName + " is " + (isChecked ? "ON" : "OFF");
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            databaseReference.child(switchName).setValue(isChecked);  // Save state to Firebase
        });
    }

    private void loadSwitchStates() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String switchName = snapshot.getKey();
                    Boolean isChecked = snapshot.getValue(Boolean.class);
                    if (switchName != null && isChecked != null) {
                        // Restore switch states based on the data from Firebase
                        switch (switchName) {
                            case "Humidity":
                                switchHumidity.setChecked(isChecked);
                                break;
                            case "Voltage Detection":
                                switchVoltage.setChecked(isChecked);
                                break;
                            case "Daylight Cycle":
                                switchDaylight.setChecked(isChecked);
                                break;
                            case "Audience":
                                switchAudience.setChecked(isChecked);
                                break;
                            case "Switch": // Adjust this case if you have a specific switch for the main switch
                                switchCompat.setChecked(isChecked);
                                break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to load switch states.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCurrentPage() {
        currentPageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String currentPage = dataSnapshot.getValue(String.class);
                if (currentPage != null) {
                    // Update header title based on the current page
                    if ("Automatic".equals(currentPage)) {
                        headerTitle.setText(R.string.automatic_mode); // Update this string in strings.xml
                        viewPager.setCurrentItem(1); // Switch to Automatic fragment
                    } else {
                        headerTitle.setText(R.string.manual_mode); // Update this string in strings.xml
                        viewPager.setCurrentItem(0); // Switch to Manual fragment
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to load current page.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
