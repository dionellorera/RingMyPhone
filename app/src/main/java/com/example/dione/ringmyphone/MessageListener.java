package com.example.dione.ringmyphone;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by dione on 7 Sep 2016.
 */
public class MessageListener extends Service {
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            String message = "";
            if (bundle != null)
            {
                //---retrieve the SMS message received---
                Object[] pdus = (Object[]) bundle.get("pdus");
                    if (Build.VERSION.SDK_INT >= 19){
                        String format = bundle.getString("format");
                        msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                        for (int i=0; i<msgs.length; i++){
                            msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i], format);
                            message += msgs[i].getMessageBody().toString();
                        }
                    }else{
                        msgs = new SmsMessage[pdus.length];
                        for (int i=0; i<msgs.length; i++){
                            msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                            message += msgs[i].getMessageBody().toString();
                        }
                    }
                if (message.toLowerCase().contains("whereu")){
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    AudioManager am =
                            (AudioManager) getSystemService(Context.AUDIO_SERVICE);

                    am.setStreamVolume(
                            AudioManager.STREAM_MUSIC,
                            am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                            0);
                    MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                    mp.start();
                }
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(broadcastReceiver, filter);
    }
}
