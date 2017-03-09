package bennett.dave.autocollect;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class Home extends AppCompatActivity {
    private static final int PERMISSION_ACCESS_FINE_LOCATION = 123;
    private Button serviceButton;
    private Vibrator v;
    private AdView mAdView;
    private TabLayout tab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("77066512AA5B4546A94BF9ECA6D2E0B2")
                .build();
        mAdView.loadAd(adRequest);
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        tab = (TabLayout) findViewById(R.id.tabs);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tab.setupWithViewPager(pager);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION },
                    PERMISSION_ACCESS_FINE_LOCATION);
        }
        if(Scanner.running){
     //       serviceButton.setText("Stop Service");
        }
        else{
//            serviceButton.setText("Start Service");
        }
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {
                    new Alert("Location Permission","Bruh! I can't do anything without your location.").buildAlert(this);
                }

                break;
        }
    }

    //called in button XML
    public void startService() {

            Intent i = new Intent(Home.this, Scanner.class);
            Home.this.startService(i);

    }

    public void stopService() {
        stopService(new Intent(Home.this, Scanner.class));

    }

    private class ServiceButtonListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            v.vibrate(100);
            if(!Scanner.running) {
                startService();
                serviceButton.setText("Stop Service");

            }
            else{
                stopService();
                serviceButton.setText("Start Service");
            }

        }
    }
}
