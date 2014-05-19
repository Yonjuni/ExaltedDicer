package de.pinyto.exalteddicer;

import de.pinyto.exalteddicer.dicer.Dicer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

public class DamageFragment extends Fragment {
	
View rootView;
NumberPicker numberPicker;
Button rollDiceButton;
Dicer dicer;
int success;
TextView resultField;
	
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
	
	rootView = inflater.inflate(R.layout.fragment_damage, container, false);
	
	dicer = new Dicer();
	
	initNumberpicker();
	
	resultField = (TextView) rootView.findViewById(R.id.textViewDM);
	
	rollDiceButton = (Button) rootView.findViewById(R.id.buttonDM);
	rollDiceButton.setOnClickListener(new OnClickListener()
	   {
	             public void onClick(View v)
	             {
	            	int poolSize = numberPicker.getValue();
	            	dicer.setPoolSize(poolSize);
	            	success = dicer.evaluateDamage();
	            	checkBotched(success);
	             } 
	   }); 
	
	return rootView;
		
	}

public void initNumberpicker(){
		
	numberPicker = (NumberPicker) rootView.findViewById(R.id.numberPickerDM);
	String[] numbers = new String[42];
	for (int i=0; i<numbers.length; i++){
		numbers[i] = Integer.toString(i+1);
	}
	
	numberPicker.setMinValue(1);
	numberPicker.setMaxValue(42);
	numberPicker.setWrapSelectorWheel(true);
	numberPicker.setDisplayedValues(numbers);
	numberPicker.setValue(1);
	}

public void checkBotched(int result){
	
	if (result == -1){
		resultField.setText("Botched!");
	}else{
		resultField.setText(String.valueOf(success));
	}
	
}

}


