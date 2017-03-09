package bennett.dave.autocollect;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Date;
import java.util.LinkedList;

/**
 * Created by David on 8/5/2016.
 */
public class HomeFragment extends Fragment {
    private FloatingActionButton actionButton;
    private Vibrator vibrator;
    private Snackbar snackbar;
    private CoordinatorLayout cLayout;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView = (RecyclerView ) view.findViewById(R.id.recycler_view);
        cLayout = (CoordinatorLayout) view.findViewById(R.id.main_content);
        actionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        actionButton.setOnClickListener(new fabButtonListener());
        vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        recyclerView.setLayoutManager(mLayoutManager);

        return view;
    }

    /**
     * Resume the Google Api client
     */
    public void onResume() {
        super.onResume();
        if(Scanner.running) {
            actionButton.setImageResource(R.drawable.ic_stop_white_48dp);
        }
        else{
            actionButton.setImageResource(R.drawable.ic_play_white_48dp);
        }
        list();


    }
    @Override
    /**
     * Pause the Google Api Client
     */
    public void onPause() {
        super.onPause();
    }


    public void list(){
        if(Controller.itemLinkedList !=null && !Controller.itemLinkedList.isEmpty()){
            recyclerView.setAdapter(new RecyclerAdpater(Controller.itemLinkedList));
        }
    }



    private class fabButtonListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            vibrator.vibrate(100);
            if(!Scanner.running) {
                actionButton.setImageResource(R.drawable.ic_stop_white_48dp);
                makeSnack("Service Started");
                startService();
            }
            else{
                actionButton.setImageResource(R.drawable.ic_play_white_48dp);
                makeSnack("Service Stopped");
                stopService();

            }

        }
    }

    public void makeSnack(String snackMessage){
        snackbar = Snackbar.make(cLayout, snackMessage, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void startService() {

        getActivity().startService(new Intent(getActivity(),Scanner.class));
        PokeController.initService();

    }

    public void stopService() {
        getActivity().stopService(new Intent(getActivity(),Scanner.class));
        PokeController.stopStopService();

    }





}
