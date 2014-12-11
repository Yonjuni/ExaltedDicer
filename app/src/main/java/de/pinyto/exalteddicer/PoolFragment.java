package de.pinyto.exalteddicer;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import de.pinyto.exalteddicer.dicing.Dicer;
import de.pinyto.exalteddicer.move.ShakeListener;
import de.pinyto.exalteddicer.move.ShakeListener.OnShakeListener;

public class PoolFragment extends Fragment {

    private Activity mActivity;

    View rootView;
    NumberPicker numberPicker;
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
                int poolSize = numberPicker.getValue();
                dicer.setPoolSize(poolSize);
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

                int poolSize = numberPicker.getValue();
                dicer.setPoolSize(poolSize);
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

        numberPicker = (NumberPicker) rootView
                .findViewById(R.id.numberPickerPoolRight);
        String[] numbers = new String[100];

        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = Integer.toString(i + 1);
        }

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(100);
        numberPicker.setWrapSelectorWheel(true);
        numberPicker.setDisplayedValues(numbers);
        numberPicker.setValue(1);
    }

    public void checkBotched(int result) {

        if (result == -1) {
            resultField.setText("Botched!");
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
