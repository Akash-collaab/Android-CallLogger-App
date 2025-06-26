package com.akash.callloggerapp;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallStateListener extends PhoneStateListener {

    private boolean isIncoming = false;
    private boolean wasRinging = false;
    private boolean callAnswered = false;
    private String incomingNumber = "";
    private Context context;

    public CallStateListener(Context context) {
        this.context = context;
    }

    @Override
    public void onCallStateChanged(int state, String number) {
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                wasRinging = true;
                callAnswered = false;
                incomingNumber = number;
                break;

            case TelephonyManager.CALL_STATE_OFFHOOK:
                if (wasRinging && isIncoming) {
                    callAnswered = true;
                }
                break;

            case TelephonyManager.CALL_STATE_IDLE:
                if (wasRinging && isIncoming) {
                    String status;
                    if (callAnswered) {
                        status = "Received";
                    } else {
                        status = "Missed";  // or Rejected – hard to distinguish without API level > 28
                    }
                    saveCallLog(context, incomingNumber, "Incoming", status);
                }

                // Reset all
                isIncoming = false;
                wasRinging = false;
                callAnswered = false;
                incomingNumber = "";
                break;
        }
    }

    private void saveCallLog(Context context, String number, String type, String status) {
        String contactName = CallLogUtils.getContactName(context, number);
        String date = CallLogUtils.getCurrentFormattedDateTime();
        String duration = CallLogUtils.getCallDuration(context, number);  // Optional logic

        CallLogModel model = new CallLogModel(number, contactName, type, date, duration, status);
        ApiHelper.sendIncomingCall(context, model);
    }
}
