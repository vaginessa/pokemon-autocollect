package bennett.dave.autocollect;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.fort.Pokestop;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import java.util.Collection;

import okhttp3.OkHttpClient;

/**
 * Created by David on 8/11/2016.
 */
public class PokeController {
    private static OkHttpClient okHttpClient = new OkHttpClient();
    private static PokemonGo go;
    private static SharedPreferences pf;
    private static Handler handler;
    private static Handler pokeStopHandler;
    private static boolean expired = false;
    private static Collection<Pokestop> pokestops;

    public static void init(Context c){
        pf = PreferenceManager.getDefaultSharedPreferences(c);
        okHttpClient = new OkHttpClient();
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initGo();
                handler.postDelayed(this,540000); //9 minutes
            }
        }, 1000);



    }

    public static PokemonGo getGo(){
        return go;
    }

    public static void initService(){
        Log.d("bacoonia", "Locations init called");
        pokeStopHandler = new Handler();
        pokeStopHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initStops();
                pokeStopHandler.postDelayed(this,15000);  //15 seconds
            }
        }, 2000);

    }

    public static void stopStopService(){
        pokeStopHandler.removeCallbacksAndMessages(null);
    }

    public static Collection<Pokestop> getStops(){
        return pokestops;
    }

    public static void setLocation(Location l){
        go.setLocation(l.getLatitude(), l.getLongitude(), l.getAltitude());
        Log.d("bacoonia", "Setting new Location");
    }

    private static void initStops(){
        if(go != null){
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d("bacoonia", "Refreshing Stops");
                    try{
                        pokestops = go.getMap().getMapObjects().getPokestops();
                        Log.d("bacoonia", "Got Stops");
                    }
                    catch (LoginFailedException e) {

                    }
                    catch (RemoteServerException e) {
                        Log.d("bacoonia",  e.toString());

                    }

                }
            });

            t.start();
        }
    }


    private static void initGo(){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("bacoonia", "Refreshing Go object");
                try {
                    go = new PokemonGo(new GoogleUserCredentialProvider(okHttpClient, pf.getString("token", null)), okHttpClient);
                }
                catch (LoginFailedException e) {

                }
                catch (RemoteServerException e) {
                    Log.d("bacoonia",  e.toString());

                }

            }
        });

     t.start();
    }

}
