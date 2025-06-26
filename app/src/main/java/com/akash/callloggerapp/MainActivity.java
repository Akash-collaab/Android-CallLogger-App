package com.akash.callloggerapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<CallLogModel> callLogs = new ArrayList<>();
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.callLogRecyclerView);

        // Request permission if not granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_CODE);
        } else {
            fetchCallLogs();
        }

    }

    private void fetchCallLogs() {
        Cursor cursor = getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                null, null, null,
                CallLog.Calls.DATE + " DESC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String number = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER));
                int type = cursor.getInt(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE));
                long dateMillis = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE));
                long duration = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION));

                String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date(dateMillis));

                String callType;
                String callStatus;

                if (type == CallLog.Calls.OUTGOING_TYPE) {
                    callType = "Outgoing";
                    callStatus = duration > 0 ? "Received" : "Rejected/Missed";
                } else if (type == CallLog.Calls.INCOMING_TYPE) {
                    callType = "Incoming";
                    callStatus = duration > 0 ? "Received" : "Rejected/Missed";
                } else if (type == CallLog.Calls.MISSED_TYPE) {
                    callType = "Incoming";
                    callStatus = "Rejected/Missed";
                }else if (duration == 0) {
                    callType = "Incoming";
                    callStatus = "Rejected/Missed";
                } else {
                    callType = "Unknown";
                    callStatus = "N/A";
                }

                // Optional: replace "Unknown" with getContactName(this, number)
                String contactName = getContactName(this, number);
                callLogs.add(new CallLogModel(number, contactName, callType, formattedDate, duration + " sec", callStatus));

            } while (cursor.moveToNext());

            cursor.close();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CallLogAdapter(callLogs));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean callLogGranted = false;
            boolean contactsGranted = false;

            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.READ_CALL_LOG)) {
                    callLogGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                }
                if (permissions[i].equals(Manifest.permission.READ_CONTACTS)) {
                    contactsGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                }
            }

            if (callLogGranted && contactsGranted) {
                fetchCallLogs();
            } else {
                Toast.makeText(this, "Permissions Denied!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public static String getContactName(Context context, String phoneNumber) {
        if (context == null || phoneNumber == null) return "Unknown";

        Cursor cursor = null;
        String name = "Unknown";

        try {
            Uri uri = Uri.withAppendedPath(
                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode(phoneNumber)
            );

            cursor = context.getContentResolver().query(
                    uri,
                    new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME},
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndexOrThrow(
                        ContactsContract.PhoneLookup.DISPLAY_NAME));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }

        return name;
    }



}
