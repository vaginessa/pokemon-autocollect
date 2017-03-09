package bennett.dave.autocollect;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.fort.Pokestop;
import com.pokegoapi.api.map.fort.PokestopLootResult;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import android.os.Handler;

import POGOProtos.Inventory.Item.ItemAwardOuterClass;
import okhttp3.OkHttpClient;

/**
 * Created by David on 7/31/2016.
 */
public class Controller {
    private Thread background_Thread;
    private Context appContext;
    private boolean wait;
    private Queue<Location> locationQueue;
    private Handler handler;
    public static LinkedList<Item> itemLinkedList = new LinkedList<Item>();


    public Controller(Context c){
        appContext = c;
        wait = false;
        locationQueue = new LinkedList<Location>();
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loop();
                handler.postDelayed(this,2000);
            }
        }, 2000);
    }

    private void loop(){
        if(!locationQueue.isEmpty() && wait== false){
            tryLoot(locationQueue.poll());
        }

    }

    public void stop(){
        handler.removeCallbacksAndMessages(null);
    }

    public void addLocationToQueue(Location l){
        locationQueue.add(l);
    }


    private void tryLoot(final Location l){
        background_Thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if(l != null){
                    wait = true;
                    Log.d("bacoonia",  "Background Thread Started");
                    PokestopLootResult result;
                    List<ItemAwardOuterClass.ItemAward> items;
                    try {
                        Collection<Pokestop> p = PokeController.getStops();
                        if(p != null) {
                            Log.d("bacoonia", "Stop size is " + p.size());
                            for (Pokestop stop : p) {
                                if (stop.canLoot()) {
                                    Log.d("bacoonia", "Found Stop to Loot");
                                    result = stop.loot();
                                    items = result.getItemsAwarded();
                                    if (result.wasSuccessful()) {
                                        createNotication(itemToMap(items));
                                        wait = false;
                                    }
                                }
                            }
                        }

                    }
                    catch (LoginFailedException e) {
                        wait = false;

                    }
                    catch (RemoteServerException e) {
                        Log.d("bacoonia",  e.toString());
                        wait = false;
                    }
                    wait = false;
                }

            }
        });
        if(wait == false) {
            background_Thread.start();
        }
    }

    private void createNotication(HashMap<String,Integer> items)
    {
        String notificationText="";
        Iterator it = items.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            notificationText += pair.getValue().toString() + " " +  pair.getKey().toString() + "\r\n";
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(appContext);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(appContext.getResources(), R.mipmap.ic_launcher));
        builder.setContentTitle(appContext.getResources().getText(R.string.app_name));
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(notificationText));
        builder.setContentText(notificationText);
        builder.setVibrate(new long[] { 2000, 2000,});
        int NOTIFICATION_ID = 12345;
        NotificationManager nManager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(NOTIFICATION_ID, builder.build());
    }

    private HashMap<String,Integer> itemToMap(List<ItemAwardOuterClass.ItemAward> items){
        HashMap<String,Integer> receivedItems = new HashMap<String,Integer>();
        for(ItemAwardOuterClass.ItemAward i: items){
            String name = i.getItemId().toString().replace("ITEM_","").replace("_"," ");
            itemLinkedList.add(new Item(name,new Date()));
            if(receivedItems.containsKey(name)){
                receivedItems.put(name,receivedItems.get(name) + 1);
            }
            else{
                receivedItems.put(name,1);
            }
        }return  receivedItems;
    }

}
