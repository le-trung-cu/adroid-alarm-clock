package funix.prm.prm391x_alarmclock_cultfx02223funixeduvn;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements  TimePickerDialogFragment.OnFragmentInteractionListener,
        AlarmAdapter.OnAdapterInteractionListener
{

    private ArrayList<AlarmModel> mAlarms;
    private AlarmAdapter mAlarmAdapter;

    private AlarmDbHelper mDbContext;

    private int REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbContext = AlarmDbHelper.getInstance(this);
        mAlarms = mDbContext.getAlarms();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        mAlarmAdapter = new AlarmAdapter(mAlarms, this);
        ft.replace(R.id.frameAlarms, AlarmsFragment.newInstance(mAlarmAdapter));
        ft.commit();
    }

    // delete an alarm
    @Override
    public void onDeleteAlarmInteraction(AlarmModel alarmModel) {
        if(alarmModel.isOn()){
            onOffAlarmBroadcast(alarmModel, false);
        }
        mDbContext.deleteAlarm(alarmModel.getId());
        mAlarms.remove(alarmModel);
        mAlarmAdapter.notifyDataSetChanged();
    }

    // on of an alarm
    @Override
    public void onToggleAlarmInteraction(AlarmModel alarmModel, boolean isChecked) {
        alarmModel.setOn(isChecked);
        mDbContext.updateAlarm(alarmModel);
        if(!isChecked){
            stopService(new Intent(getApplicationContext(), AlarmMusicService.class));
        }
        onOffAlarmBroadcast(alarmModel, alarmModel.isOn());
    }

    // add icon action bar click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_add:
                addAlarm();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // open a Fragment to create an alarm
    public void addAlarm() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameAlarms, TimePickerDialogFragment.newInstance());
        ft.addToBackStack("xx");
        ft.commit();
    }

    // open a Fragment to Edit an alarm
    @Override
    public void onEditAlarmInteraction(AlarmModel alarmModel) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameAlarms, TimePickerDialogFragment.newInstance(alarmModel));
        ft.addToBackStack("xx");
        ft.commit();
    }

    // handle to Create or edit an alarm
    @Override
    public void onCreateOrEditAlarmFragmentInteraction(AlarmModel alarmModel) {
        if(alarmModel.getId() > 0){
            // for updating alram

            AlarmModel previousAlarm = mDbContext.getAlarm(alarmModel.getId());
            if(previousAlarm.isOn()){
                //unregister previous Alarm
                onOffAlarmBroadcast(previousAlarm, false);

                // register Alarm updated
                onOffAlarmBroadcast(alarmModel, alarmModel.isOn());
            }

            // update database
            mDbContext.updateAlarm(alarmModel);

            // update list view
            for (AlarmModel alarm: mAlarms){
                if(alarm.getId() == alarmModel.getId()){
                    alarm.setHour(alarmModel.getHour());
                    alarm.setMinute(alarmModel.getMinute());
                    break;
                }
            }

        }else {
            // for add new alarm
            int id = mDbContext.addAlarm(alarmModel);

            alarmModel.setId(id);
            mAlarms.add(alarmModel);

            onOffAlarmBroadcast(alarmModel, alarmModel.isOn());
        }

        // update list view
        mAlarmAdapter.notifyDataSetChanged();

        // pop fragment TimePickerDialog
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }
    }

    private void onOffAlarmBroadcast(AlarmModel alarmModel, boolean turnOn){
        Intent intent1 = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                REQUEST_CODE, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        long currentTimeInMillis = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, alarmModel.getHour());
        calendar.set(Calendar.MINUTE, alarmModel.getMinute());

        long triggerAtMillis = calendar.getTimeInMillis();

        // if time of alarm < current time, setup alarm for tomorrow
        if(triggerAtMillis < currentTimeInMillis){
            triggerAtMillis += 24*60*60*1000;
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);

        if(!turnOn){
            alarmManager.cancel(pendingIntent);
        }
    }
}



























