package com.pembelajar.locationapps;

import android.Manifest;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.pembelajar.locationapps.databinding.ActivityMainBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;
    private FusedLocationProviderClient fusedClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        fusedClient = LocationServices.getFusedLocationProviderClient(this);
        TedPermission.with(this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        finish();
                    }
                })
                .setRationaleTitle(R.string.txt_permission_required)
                .setRationaleMessage(R.string.txt_rational_msg)
                .setDeniedTitle(R.string.txt_permission_denied)
                .setDeniedMessage(R.string.txt_denied_msg)
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();
        binding.btnGetCurLoc.setOnClickListener(this);
        binding.btnGetRadius.setOnClickListener(this);
    }

    private void getCurentLocation(){
        fusedClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    Log.d(TAG, "Lat = "+location.getLatitude());
                    Log.d(TAG, "Longlat = "+location.getLongitude());
                    binding.tvLocation.setVisibility(View.VISIBLE);
                    binding.tvLonglatide.setVisibility(View.VISIBLE);
                    binding.tvLocation.setText("Latitude "+location.getLatitude());
                    binding.tvLonglatide.setText("Longitude "+location.getLongitude());
                }
            }
        });
    }

    private void getRadius(){
        final double latitudeRefrence = -6.230390;      //todo latitude & longitude tlt
        final double longitudeRefrence = 106.817949;
        final float[] distance = new float[1];
        fusedClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    Location.distanceBetween(latitudeRefrence, longitudeRefrence,
                            location.getLatitude(), location.getLongitude(), distance);
                    binding.tvLocation.setText(distance[0]+"Meters");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_get_cur_loc:
                getCurentLocation();
                break;
            case R.id.btn_get_radius:
                inAbsen();
                break;
        }
    }


    //Todo fungsi untuk absen masuk
    private void inAbsen(){
        String checkIn = "08:00:00";
        String checkLimit = "03:00:00";
        final double latitudeRefrence = -6.230390;       //todo ketrangan latitude & longitude telkomsel smart office
        final double longitudeRefrence = 106.817949;     //todo tambahkan latitude & longitude tempat kerja anda, dapat di copas dr maps
        final float[] distance = new float[1];
        final float[] mainRadius = {0, 50};

        try {
            Date mainTime = new SimpleDateFormat("HH:mm:ss").parse(checkIn);
            final Calendar calendarCheckIn = Calendar.getInstance();
            calendarCheckIn.setTime(mainTime);
            calendarCheckIn.add(Calendar.DATE, 1);

            Date limitTime = new SimpleDateFormat("HH:mm:ss").parse(checkLimit);
            final Calendar limitCalender = Calendar.getInstance();
            limitCalender.setTime(limitTime);
            limitCalender.add(Calendar.DATE, 1);

            Calendar currentCalendar = Calendar.getInstance();
            final Date x = currentCalendar.getTime();

            fusedClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null){
                        Location.distanceBetween(latitudeRefrence, longitudeRefrence,
                                location.getLatitude(), location.getLongitude(), distance);

                        if (distance[0] <= mainRadius[1] && (x.before(calendarCheckIn.getTime()) && x.after(limitCalender.getTime()))){
                            binding.tvLonglatide.setVisibility(View.VISIBLE);
                            binding.tvLocation.setText("Anda Masuk");
                            System.out.println("Anda Masukk");
                        }else{
                            Toast.makeText(getApplicationContext(), "Maaf Anda Tidak Dapat Absen, Karena Anda Telat", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    //Todo fungsi untuk absen keluar
    private void outAbsen(){
        String checkout = "17:00:00";
        String checkLimit = "1:00:00";
        final double latitudeRefrence = -6.230390;      //todo ketrangan latitude & longitude telkomsel smart office
        final double longitudeRefrence = 106.817949;       //todo tambahkan latitude & longitude tempat kerja anda, dapat di copas dr maps
        final float[] distance = new float[1];
        final float[] mainRadius = {0, 50};

        try {
            Date mainTime = new SimpleDateFormat("HH:mm:ss").parse(checkout);
            final Calendar calendarCheckOut = Calendar.getInstance();
            calendarCheckOut.setTime(mainTime);
            calendarCheckOut.add(Calendar.DATE, 1);

            Date limitTime = new SimpleDateFormat("HH:mm:ss").parse(checkLimit);
            final Calendar limitCalender = Calendar.getInstance();
            limitCalender.setTime(limitTime);
            limitCalender.add(Calendar.DATE, 1);

            Calendar currentCalendar = Calendar.getInstance();
            final Date x = currentCalendar.getTime();

            fusedClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null){
                        Location.distanceBetween(latitudeRefrence, longitudeRefrence,
                                location.getLatitude(), location.getLongitude(), distance);

                        if (distance[0] <= mainRadius[1] && x.after(calendarCheckOut.getTime())){
                            binding.tvLonglatide.setVisibility(View.VISIBLE);
                            binding.tvLocation.setText("Anda Keluar");
                        }else{
                            Toast.makeText(getApplicationContext(), "Maaf Anda Tidak Dapat Keluar", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
