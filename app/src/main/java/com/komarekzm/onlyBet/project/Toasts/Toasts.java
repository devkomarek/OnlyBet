package com.komarekzm.onlyBet.project.Toasts;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by komarekzm on 2/23/2017.
 * Tutorial
 * *
 */

public class Toasts {
    private static final String NO_CONNECTIONS = "No Connections";
    private static final String NEW_TIPS_1 = "Welcome. You have new Tips!";
    private static final String NEW_TIPS_2 = "You have new Tips!";
    private static final String THE_SAME_TIPS = "You have actual Tips";
    private static final String NEW_VERSION = "We Released the new version";

    public enum State {
        noConnections, newTips, actualTips, newVersion
    }

    static public void displayWhileStart(Context context, State state) {
        Toast toast;

        switch (state) {
            case noConnections:
                toast = Toast.makeText(context, NO_CONNECTIONS, Toast.LENGTH_LONG);
                break;

            case newTips:
                toast = Toast.makeText(context, NEW_TIPS_1, Toast.LENGTH_LONG);
                break;

            case newVersion:
                toast = Toast.makeText(context, NEW_VERSION, Toast.LENGTH_LONG);
                break;

            default:
                toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
        }
        toast.show();
    }

    static public void displayWhileRefresh(Context context, State state) {
        Toast toast;
        switch (state) {
            case noConnections:
                toast = Toast.makeText(context, NO_CONNECTIONS, Toast.LENGTH_LONG);
                break;

            case newTips:
                toast = Toast.makeText(context, NEW_TIPS_2, Toast.LENGTH_LONG);
                break;

            case actualTips:
                toast = Toast.makeText(context, THE_SAME_TIPS, Toast.LENGTH_LONG);
                break;

            default:
                toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
        }
        toast.show();
    }
}
