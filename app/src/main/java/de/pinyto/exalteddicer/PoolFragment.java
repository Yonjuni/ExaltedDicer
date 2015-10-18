package de.pinyto.exalteddicer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
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
    SharedPreferences sharedPreferences;

    private boolean shakingEnabled;
    private boolean vibrationEnabled;

    private Vibrator vib;

    // for Shaking
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeListener mShakeDetector;

    public void setShakingEnabled(boolean b) {
        this.shakingEnabled = b;
    }

    public void setVibrationEnabled(boolean b) {
        this.vibrationEnabled = b;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        loadPreferences();

        rootView = inflater.inflate(R.layout.fragment_pool, container, false);

        dicer = new Dicer();
        initNumberPicker();

        resultField = (TextView) rootView.findViewById(R.id.textViewPool);


        vib = (Vibrator) mActivity.getSystemService(Context.VIBRATOR_SERVICE);


        rollDiceButton = (Button) rootView.findViewById(R.id.buttonPool);
        rollDiceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dicer.setPoolSize(getPoolSize());
                success = dicer.evaluatePool();
                checkBotched(success);
                if (vibrationEnabled) {
                    vib.vibrate(50);
                }
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

                if (shakingEnabled) {
                    dicer.setPoolSize(getPoolSize());
                    success = dicer.evaluatePool();
                    checkBotched(success);
                    if (vibrationEnabled) {
                        vib.vibrate(50);
                    }
                }
            }
        });

        return rootView;

    }

    public void loadPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity.getBaseContext());
        shakingEnabled = sharedPreferences.getBoolean("enable_shaking", true);
        vibrationEnabled = sharedPreferences.getBoolean("enable_vibration", true);
    }

    public void initNumberPicker() {

        numberPickerRow = new NumberPicker[2];
        numberPickerRow[0] = (NumberPicker) rootView
                .findViewById(R.id.numberPickerPoolLeft);
        numberPickerRow[1] = (NumberPicker) rootView
                .findViewById(R.id.numberPickerPoolRight);

        String[] numbers = new String[10];

        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = Integer.toString(i);
        }

        Field[] pickerFields = NumberPicker.class.getDeclaredFields();

        for (int i = 0; i < 2; i++) {
            numberPickerRow[i].setMinValue(0);
            numberPickerRow[i].setMaxValue(9);
            numberPickerRow[i].setWrapSelectorWheel(true);
            numberPickerRow[i].setDisplayedValues(numbers);
            setNumberPickerTextColor(numberPickerRow[i]);
            numberPickerRow[i].setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

            for (Field field : pickerFields) {
                if (field.getName().equals("mSelectionDivider")) {
                    field.setAccessible(true);
                    try {
                        field.set(numberPickerRow[i], getResources().getDrawable(R.drawable.dm_numberpicker_selection_divider));
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    break;

                }


            }
            numberPickerRow[1].setValue(1);
            numberPickerRow[0].setValue(0);


        }

    }

    public static boolean setNumberPickerTextColor(NumberPicker numberPicker) {
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                try {
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(Color.parseColor("#ffffff"));
                    ((EditText) child).setTextColor(Color.parseColor("#ffffff"));
                    numberPicker.invalidate();
                    return true;
                } catch (NoSuchFieldException e) {
                    //Log.w("setNumberPickerTextColor", e);
                } catch (IllegalAccessException e) {
                    //Log.w("setNumberPickerTextColor", e);
                } catch (IllegalArgumentException e) {
                    //Log.w("setNumberPickerTextColor", e);
                }
            }
        }
        return false;
    }

    public int getPoolSize() {

        String poolSize = String.valueOf(numberPickerRow[0].getValue())
                + (numberPickerRow[1].getValue());
        return Integer.parseInt(poolSize);
    }

    public void checkBotched(int result) {

        if (result == -1) {
            resultField.setText("Botched");
        } else {
            resultField.setText(String.valueOf(success));
        }
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,
                SensorManager.SENSOR_DELAY_UI);
        shakingEnabled = sharedPreferences.getBoolean("enable_shaking", true);
        vibrationEnabled = sharedPreferences.getBoolean("enable_vibration", true);
    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

}
