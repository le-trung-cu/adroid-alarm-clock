package funix.prm.prm391x_alarmclock_cultfx02223funixeduvn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, AlarmMusicService.class);
        context.startService(intent1);
    }
}
