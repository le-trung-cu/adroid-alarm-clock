package funix.prm.prm391x_alarmclock_cultfx02223funixeduvn;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;

public class AlarmAdapter extends BaseAdapter {

    // alarms list
    private final ArrayList<AlarmModel> mAlarms;
    // event to edit, delete, on_off an alarm
    private final OnAdapterInteractionListener mListener;

    public AlarmAdapter(ArrayList alarms, OnAdapterInteractionListener listener){
        mAlarms = alarms;
        mListener = listener;
    }

    @Override
    public int getCount() {
        return mAlarms.size();
    }

    @Override
    public Object getItem(int position) {
        return mAlarms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mAlarms.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        final AlarmModel alarmModel = mAlarms.get(position);

        convertView = View.inflate(parent.getContext(), R.layout.list_item_alarm, null);

        // text for time
        final TextView txtTimeHeading = convertView.findViewById(R.id.txtTimeHeading);
        // text for day (today or tomorrow)
        final TextView txtTimeHumanRead = convertView.findViewById(R.id.txtTimeHumanRed);
        final ToggleButton toggleOnOffAlarm = convertView.findViewById(R.id.toggleOnOff);

        // HH:MM AM/PM format time
        int x = alarmModel.getHour() > 12? alarmModel.getHour() - 12 : alarmModel.getHour();
        String tStr = x < 10? "0"+x+":": x+":";
        tStr += alarmModel.getMinute() < 10? "0"+alarmModel.getMinute(): String.valueOf(alarmModel.getMinute());

        tStr += " " + (alarmModel.getHour()>12?
                parent.getContext().getString(R.string.pm)
                : parent.getContext().getString(R.string.am));

        txtTimeHeading.setText(tStr);

        createTimeHumanRead(txtTimeHumanRead, alarmModel);

        if(alarmModel.isOn()){
            toggleOnOffAlarm.setChecked(true);
        }

        toggleOnOffAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == alarmModel.isOn()){
                    return;
                }
                mListener.onToggleAlarmInteraction(alarmModel, isChecked);
                createTimeHumanRead(txtTimeHumanRead, alarmModel);
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.alarm_list_item_popupmenu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.delete:
                                mListener.onDeleteAlarmInteraction(alarmModel);
                                break;
                            case R.id.edit:
                                mListener.onEditAlarmInteraction(alarmModel);
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        return convertView;

    }

    // if time of alarm < current time, setup alarm for tomorrow
    private void createTimeHumanRead(TextView txt, AlarmModel alarmModel){

        Calendar calendar = Calendar.getInstance();
        long currentMills = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, alarmModel.getHour());
        calendar.set(Calendar.MINUTE, alarmModel.getMinute());
        long alarmInMillis = calendar.getTimeInMillis();

        if(alarmInMillis < currentMills){
            txt.setText(R.string.tomorrow);
        }else {
            txt.setText(R.string.today);
        }
    }

    public interface OnAdapterInteractionListener {
        void onEditAlarmInteraction(AlarmModel alarmModel);
        void onDeleteAlarmInteraction(AlarmModel alarmModel);
        void onToggleAlarmInteraction(AlarmModel alarmModel, boolean isChecked);
    }
}
