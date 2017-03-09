package bennett.dave.autocollect;

import java.util.HashMap;

/**
 * Created by David on 7/24/2016.
 */
public interface ServerCallback {
    void onSuccess(boolean success, HashMap<String, Integer> stuff);
}
