package funix.prm.prm391x_alarmclock_cultfx02223funixeduvn;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TimePickerDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TimePickerDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimePickerDialogFragment extends Fragment {
    private static final String ARG_IS_UPDATING = "param1";
    private static final String ARG_ALARM_ID = "param2";
    private static final String ARG_ALARM_ON_OFF = "param3";
    private static final String ARG_ALARM_HOUR = "param4";
    private static final String ARG_ALARM_MINUTE = "param5";

    private int mAlarmHour;
    private int mAlarmMinute;

    private boolean mIsUpdating;
    private int mAlarmId;
    private boolean mAlarmIsOn;

    private OnFragmentInteractionListener mListener;

    public TimePickerDialogFragment() {
        // Required empty public constructor
    }

    public static TimePickerDialogFragment newInstance() {
        TimePickerDialogFragment fragment = new TimePickerDialogFragment();

        return fragment;
    }

    public static TimePickerDialogFragment newInstance(AlarmModel alarm) {
        TimePickerDialogFragment fragment = new TimePickerDialogFragment();
        Bundle args = new Bundle();

        args.putInt(ARG_ALARM_ID, alarm.getId());
        args.putBoolean(ARG_ALARM_ON_OFF, alarm.isOn());
        args.putBoolean(ARG_IS_UPDATING, true);
        args.putInt(ARG_ALARM_HOUR, alarm.getHour());
        args.putInt(ARG_ALARM_MINUTE, alarm.getMinute());

        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mIsUpdating = arguments.getBoolean(ARG_IS_UPDATING);
            mAlarmId = arguments.getInt(ARG_ALARM_ID);
            mAlarmIsOn = arguments.getBoolean(ARG_ALARM_ON_OFF);

            mAlarmHour = arguments.getInt(ARG_ALARM_HOUR);
            mAlarmMinute = arguments.getInt(ARG_ALARM_MINUTE);
        }else {
            Calendar calendar = Calendar.getInstance();
            mAlarmHour = calendar.get(Calendar.HOUR_OF_DAY);
            mAlarmMinute = calendar.get(Calendar.MINUTE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_time_picker_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TimePicker timePicker = view.findViewById(R.id.timPicker);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                mAlarmHour = hourOfDay;
                mAlarmMinute = minute;
            }
        });

        timePicker.setHour(mAlarmHour);
        timePicker.setMinute(mAlarmMinute);


        Button btnOk = view.findViewById(R.id.btnOk_fragment_time_picker);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    AlarmModel alarmModel = null;
                    if(mIsUpdating){
                        alarmModel = new AlarmModel(mAlarmId, mAlarmHour, mAlarmMinute, mAlarmIsOn);
                    }else {
                        alarmModel = new AlarmModel(mAlarmHour, mAlarmMinute, true);
                    }
                    mListener.onCreateOrEditAlarmFragmentInteraction(alarmModel);
                }
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onCreateOrEditAlarmFragmentInteraction(AlarmModel alarmModel);
    }
}
