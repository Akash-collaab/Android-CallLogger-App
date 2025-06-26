package com.akash.callloggerapp;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.provider.CallLog;

public class CallLogUtils {

    public static String getCallDuration(Context context, String number) {
        String duration = "0";

        try {
            Cursor cursor = context.getContentResolver().query(
                    CallLog.Calls.CONTENT_URI,
                    null,
                    CallLog.Calls.NUMBER + " = ?",
                    new String[]{number},
                    CallLog.Calls.DATE + " DESC"
            );

            if (cursor != null && cursor.moveToFirst()) {
                int durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION);
                duration = cursor.getString(durationIndex);
                cursor.close();
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return duration; // in seconds
    }

    public static String getCurrentFormattedDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault());
        return sdf.format(new Date());
    }

    public static String getContactName(Context context, String phoneNumber) {
        String contactName = "Unknown";

        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return contactName;
        }

        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber)
        );

        Cursor cursor = contentResolver.query(
                uri,
                new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME},
                null,
                null,
                null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(
                        cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME)
                );
            }
            cursor.close();
        }

        return contactName;
    }
}
