package bennett.dave.autocollect;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.inventory.Item;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.yalantis.phoenix.PullToRefreshView;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by David on 8/5/2016.
 */
public class InventoryFragment extends Fragment {
    private ListView inventory;
    private PullToRefreshView mPullToRefreshView;
    private OkHttpClient httpClient;
    private String refresh_Token;
    private SharedPreferences pf;
    private String inventoryString="";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.inventory_fragment, container, false);
        inventory = (ListView) view.findViewById(R.id.inventory);
        httpClient = new OkHttpClient();
        mPullToRefreshView = (PullToRefreshView) view.findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new inventoryRefresh());
        pf = PreferenceManager.getDefaultSharedPreferences(getContext());
        refresh_Token = pf.getString("token",null);
        return view;
    }




    private class inventoryRefresh implements PullToRefreshView.OnRefreshListener {

        @Override
        public void onRefresh() {
            mPullToRefreshView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPullToRefreshView.setRefreshing(true);
                    getInventory(new ServerCallback() {
                        @Override
                        public void onSuccess(boolean success, final HashMap<String, Integer> stuff) {
                            if(success){
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        inventory.setAdapter(new HashMapAdapter(stuff, getContext()));
                                        mPullToRefreshView.setRefreshing(false);
                                    }
                                });


                            }
                            else{
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mPullToRefreshView.setRefreshing(false);
                                    }
                                });

                            }
                        }
                    });
                }
            }, 0);
        }
    }




    public void getInventory(final ServerCallback callback){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                    try {
                        HashMap<String,Integer> stuff = new HashMap<String,Integer>();
                        PokemonGo go = PokeController.getGo();
                        Collection<Item> items = go.getInventories().getItemBag().getItems();
                        for(Item i: items){
                            String name = i.getItemId().toString().replace("ITEM_","").replace("_"," ");
                            int count = i.getCount();
                            stuff.put(name,count);
                        }
                        callback.onSuccess(true,stuff);
                    }
                    catch (LoginFailedException e) {
                        e.printStackTrace();
                        callback.onSuccess(false,null);

                    }
                    catch (RemoteServerException e) {
                        Log.d("bacoonia",  e.toString());
                        e.printStackTrace();
                        callback.onSuccess(false,null);
                    }

                }
        });
        t.start();

    }


    /**
     * The ListAdapter is the Adapter used for the Listview to display the Pokestops
     */
    public class HashMapAdapter extends BaseAdapter {

        private HashMap<String, Integer> mData = new HashMap<String, Integer>();
        private String[] mKeys;
        private final Context context;
        public HashMapAdapter(HashMap<String, Integer> data,Context context){
            mData  = data;
            this.context = context;
            mKeys = mData.keySet().toArray(new String[data.size()]);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(mKeys[position]);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            String key = mKeys[pos];
            String value = getItem(pos).toString();
            View rowView;
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                rowView = new View(context);
                rowView = inflater.inflate(R.layout.list_item, parent, false);
                TextView item = (TextView) rowView.findViewById(R.id.inventoryItem);
                item.setText(value + " " + key);


            } else {
                rowView = (View) convertView;
            }

            return rowView;
        }
        }
    }


