package bennett.dave.autocollect;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by David on 7/28/2016.
 * The Alert class builds an Alert Dialog that can be used at any point in the app
 */
public class Alert {
    private String title;
    private String message;

    public Alert(String t, String m)
    {
        title = t;
        message = m;
    }
    public void buildAlert(Context c)
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(c);
        builder1.setMessage(message);
        builder1.setIcon(android.R.drawable.ic_dialog_alert);
        builder1.setPositiveButton(
                        "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                            }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
            }



        }


