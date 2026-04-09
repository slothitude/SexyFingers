package com.example.sexyfingers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.UsedByGodot;

import java.util.Arrays;
import java.util.List;

public class SexyFingersPlugin extends GodotPlugin {

    // ===== Guard Numbers =====
    private final List<String> guardNumbers = Arrays.asList(
        "61412345678",
        "61498765432",
        "61455511223"
    );

    public SexyFingersPlugin(Godot godot) {
        super(godot);
    }

    @Override
    public String getPluginName() {
        return "SexyFingers";
    }

    private Activity getActivitySafe() {
        return getActivity();
    }

    // ===== Public API =====
    @UsedByGodot
    public void send(String type, String number, String message) {
        send(type, number, message, false);
    }

    @UsedByGodot
    public void send(String type, String number, String message, boolean direct) {
        Activity activity = getActivitySafe();

        if(!guardNumbers.contains(number)) {
            // Unauthorized number, block action
            System.out.println("Number not authorized: " + number);
            return;
        }

        try {
            switch(type.toLowerCase()) {
                case "whatsapp":
                    openWhatsApp(activity, number, message);
                    break;
                case "sms":
                    if(direct && hasSmsPermission(activity)) {
                        sendDirectSMS(activity, number, message);
                    } else {
                        openSMS(activity, number, message);
                    }
                    break;
                case "share":
                    shareText(activity, message);
                    break;
                case "dialer":
                    openDialer(activity, number);
                    break;
                case "call":
                    if(direct && hasCallPermission(activity)) {
                        makeDirectCall(activity, number);
                    } else {
                        openDialer(activity, number);
                    }
                    break;
                default:
                    // Fallback
                    shareText(activity, message);
                    break;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // ===== Helper Methods =====
    private void shareText(Activity activity, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        Intent chooser = Intent.createChooser(intent, "Send via");
        if(intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(chooser);
        }
    }

    private void openSMS(Activity activity, String number, String message) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + number));
        intent.putExtra("sms_body", message);
        if(intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
        }
    }

    private void sendDirectSMS(Activity activity, String number, String message) {
        android.telephony.SmsManager sms = android.telephony.SmsManager.getDefault();
        sms.sendTextMessage(number, null, message, null, null);
    }

    private void openWhatsApp(Activity activity, String number, String message) {
        String url = "https://wa.me/" + number + "?text=" + Uri.encode(message);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        if(intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
        }
    }

    private void openDialer(Activity activity, String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        if(intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
        }
    }

    private void makeDirectCall(Activity activity, String number) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        if(intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
        }
    }

    // ===== Permissions =====
    private boolean hasCallPermission(Activity activity) {
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CALL_PHONE}, 1);
            return false;
        }
        return true;
    }

    private boolean hasSmsPermission(Activity activity) {
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.SEND_SMS}, 1);
            return false;
        }
        return true;
    }
}
