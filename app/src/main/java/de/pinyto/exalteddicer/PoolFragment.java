package de.pinyto.exalteddicer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.lang.reflect.Field;

import de.pinyto.exalteddicer.dicing.Dicer;
import de.pinyto.exalteddicer.move.ShakeListener;
import de.pinyto.exalteddicer.move.ShakeListener.OnShakeListener;

public class PoolFragment extends Fragment {

    private Activity mActivity;

    View rootView;
    NumberPicker[] numberPickerRow;
    Button rollDiceButton;
    Dicer dicer;
    int success;
    TextView resultField;

    // for Shaking
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeListener mShakeDetector;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_pool, container, false);

        dicer = new Dicer();
        initNumberpicker();

        resultField = (TextView) rootView.findViewById(R.id.textViewPool);

        rollDiceButton = (Button) rootView.findViewById(R.id.buttonPool);
        rollDiceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dicer.setPoolSize(getPoolSize());
                success = dicer.evaluatePool();
                checkBotched(success);
            }
        });

        // ShakeDetector initialization
        mSensorManager = (SensorManager) mActivity
                .getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeListener();
        mShakeDetector.setOnShakeListener(new OnShakeListener() {

            public void onShake(int count) {
                dicer.setPoolSize(getPoolSize());
                success = dicer.evaluatePool();
                checkBotched(success);
            }
        });

        return rootView;

    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    public void initNumberpicker() {

        numberPickerRow = new NumberPicker[2];
        numberPickerRow[0] = (NumberPicker) rootView
                .findViewById(R.id.numberPickerPoolLeft);
        numberPickerRow[1] = (NumberPicker) rootView
                .findViewById(R.id.numberPickerPoolRight);

        String[] numbers = new String[10];

        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = Integer.toString(i);
        }
        for (int i = 0; i < 2; i++) {
            numberPickerRow[i].setMinValue(0);
            numberPickerRow[i].setMaxValue(9);
            numberPickerRow[i].setWrapSelectorWheel(true);
            numberPickerRow[i].setDisplayedValues(numbers);
            setNumberPickerTextColor(numberPickerRow[i]);
        }
        numberPickerRow[1].setValue(1);
        numberPickerRow[0].setValue(0);
    }

    public static boolean setNumberPickerTextColor(NumberPicker numberPicker)
    {
        final int count = numberPicker.getChildCount();
        for(int i = 0; i < count; i++){
            View child = numberPicker.getChildAt(i);
            if(child instanceof EditText){
                try{
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint)selectorWheelPaintField.get(numberPicker)).setColor(Color.parseColor("#ffffff"));
                    ((EditText)child).setTextColor(Color.parseColor("#ffffff"));
                    numberPicker.invalidate();
                    return true;
                }
                catch(NoSuchFieldException e){
                    Log.w("setNumberPickerTextColor", e);
                }
                catch(IllegalAccessException e){
                    Log.w("setNumberPickerTextColor", e);
                }
                catch(IllegalArgumentException e){
                    Log.w("setNumberPickerTextColor", e);
                }
            }
        }
        return false;
    }

    public int getPoolSize() {

        String poolsize = String.valueOf(numberPickerRow[0].getValue())
                + (numberPickerRow[1].getValue());
        return Integer.parseInt(poolsize);
    }

    public void checkBotched(int result) {

        if (result == -1) {
            resultField.setText("Botched");
        } else {
            resultField.setText(String.valueOf(success));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener
        // onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

}