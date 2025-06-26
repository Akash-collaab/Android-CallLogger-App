package com.akash.callloggerapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class IncomingCallReceiver extends BroadcastReceiver {

    private static boolean isListenerRegistered = false; // Prevent multiple listeners

    @Override
    public void onReceive(Context context, Intent intent) {

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            // Show popup toast
            Toast.makeText(context, "Incoming Call: " + incomingNumber, Toast.LENGTH_LONG).show();

            // OPTIONAL: You can immediately send a partial API call here (only number)
            String contactName = CallLogUtils.getContactName(context, incomingNumber);
            String date = CallLogUtils.getCurrentFormattedDateTime();
            String duration = "0";  // Optional if call hasn't ended yet
            String type = "Incoming";
            String status = "Ringing";

            CallLogModel model = new CallLogModel(incomingNumber, contactName, type, date, duration, status);
            ApiHelper.sendIncomingCall(context, model);

        }

        if (!isListenerRegistered) {
            TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            CallStateListener listener = new CallStateListener(context);
            telephony.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
            isListenerRegistered = true;
        }
    }
}
