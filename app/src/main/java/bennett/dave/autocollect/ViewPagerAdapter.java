package bennett.dave.autocollect;

/**
 * Created by David on 6/17/2016.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Admin on 11-12-2015.
 * The ViewPageAdapter extends the Fragment Adapter. It allows the user to switch between tabs.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    /**
     * What tab should we get?
     */
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                HomeFragment tab1 = new HomeFragment();
                return tab1;
             case 1:
                InventoryFragment tab2 = new InventoryFragment();
                 return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }// As there are only 2 Tabs...
        @Override
        public CharSequence getPageTitle ( int position){
            switch (position) {
                case 0:
                    return "Recent";
                   case 1:
                     return "Inventory";
                default:
                    return null;
            }
        }

    }
