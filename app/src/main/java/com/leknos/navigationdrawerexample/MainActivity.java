package com.leknos.navigationdrawerexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.leknos.navigationdrawerexample.services.LocationService;

public class MainActivity extends AppCompatActivity{

    public static final String APP_PREFERENCES = "my_settings";
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private FloatingActionButton fab;
    private NavigationView navigationView;
    private AppBarConfiguration mAppBarConfiguration;
    public int x;
    private Intent serviceIntent;

    public int getX() {
        return x;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.activity_main__toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.activity_main__nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_draw_open,
                R.string.navigation_draw_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.drawer_menu__nav_map, R.id.drawer_menu__nav_list, R.id.drawer_menu__nav_location_info)
                .setDrawerLayout(drawerLayout)
                .build();

        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.drawer_menu__share :
                        Toast.makeText(MainActivity.this, "Share", Toast.LENGTH_SHORT).show();
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,
                                "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                        break;
                    case R.id.drawer_menu__send :
                        Toast.makeText(MainActivity.this, "Send", Toast.LENGTH_SHORT).show();
                        break;
                }
                NavigationUI.onNavDestinationSelected(menuItem, navController);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        serviceIntent = new Intent(this, LocationService.class);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu__service_start :
                Toast.makeText(this, "Service has been started", Toast.LENGTH_SHORT).show();
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    MainActivity.this.startForegroundService(serviceIntent);
                }else{
                    startService(serviceIntent);
                }
                // TODO: 31.07.2020 Add a behavior for STARTING_SERVICE instead of stub
                return true;
            case R.id.menu__service_stop:
                stopService(serviceIntent);
                // TODO: 31.07.2020 Add a behavior for STOPPING SERVICE instead of stub
                Toast.makeText(this, "Service has been stopped", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu__center_map_picture:
                ImageView imageView = findViewById(R.id.fragment_maps__location_marker);
                if(item.getTitle().equals(getString(R.string.hide_location_marker))){
                    item.setTitle(getString(R.string.show_location_marker));
                    imageView.setVisibility(View.INVISIBLE);
                }else{
                    item.setTitle(getString(R.string.hide_location_marker));
                    imageView.setVisibility(View.VISIBLE);
                }
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }
}