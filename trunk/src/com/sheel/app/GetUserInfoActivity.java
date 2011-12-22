package com.sheel.app;

import java.util.Arrays;
import java.util.Calendar;

import com.sheel.datastructures.enums.OwnerFacebookStatus;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AutoCompleteTextView.Validator;

public class GetUserInfoActivity extends UserSessionStateMaintainingActivity {
    /** Called when the activity is first created. */
	
	int searchStatus;
	String selectedDate;
	int searchMethod;
	
	RadioButton lessWeight;
	RadioButton extraWeight;
	ToggleButton srcDes;
	ToggleButton flightNo;
	
	TextView dateDisplay;
    Button pickDate;
    int year;
    int month;
    int day;
    
    String flightNumber;
    String source;
    String destination;
    String request;
    
    String[] airportsList;

    static final int DATE_DIALOG_ID = 0;	
    static Calendar datePicked = Calendar.getInstance();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_user_info);
        
        lessWeight = (RadioButton) findViewById(R.id.lessWeight);
        extraWeight = (RadioButton) findViewById(R.id.extraWeight);
        srcDes = (ToggleButton) findViewById(R.id.srcDes);
        flightNo = (ToggleButton) findViewById(R.id.flightNo);
        
        dateDisplay = (TextView) findViewById(R.id.dateDisplay);
        pickDate = (Button) findViewById(R.id.changeDate);

        pickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

        year = datePicked.get(Calendar.YEAR);
        month = datePicked.get(Calendar.MONTH);
        day = datePicked.get(Calendar.DAY_OF_MONTH);

        datePicked.set(year, month, day, 23, 59, 59);
       
        updateDisplay();
        
        airportsList = getResources().getStringArray(R.array.airports_array);
    }

	private void updateDisplay() {
    	
        dateDisplay.setText(
            new StringBuilder()
                    // Month is 0 based so add 1
                    .append(month + 1).append("-")
                    .append(day).append("-")
                    .append(year));
        
        selectedDate = (String) dateDisplay.getText();
    }
    
    private DatePickerDialog.OnDateSetListener mDateSetListener =
        new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int selectedYear, 
                                  int monthOfYear, int dayOfMonth) {
            	
            	GetUserInfoActivity.datePicked.set(selectedYear, monthOfYear, dayOfMonth, 23, 59, 59);
                year = selectedYear;
                month = monthOfYear;
                day = dayOfMonth;
                updateDisplay();
            }
        };
        
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                            mDateSetListener,
                            year, month, day);
            }
            return null;
   }

	public void onClick_lessWeight(View v) 
	{
		extraWeight.setChecked(false);
	}
      
	public void onClick_extraWeight(View v) 
	{
		lessWeight.setChecked(false);
	}
	
	public void onClick_srcDes(View v) {
		flightNo.setChecked(false);
		if(!isValidInput())
			return;
		
		searchMethod = 1;
		enterSourceAndDestination();
	}
	
	public void onClick_flightNo(View v) {
		srcDes.setChecked(false);
		if(!isValidInput())
			return;
		
		searchMethod = 0;
		enterFlightNumber();			
	}
	
	 public void showErrors(String title, String message){
		 srcDes.setChecked(false);
		 flightNo.setChecked(false);
		 
		 AlertDialog alertDialog;
		 alertDialog = new AlertDialog.Builder(this).create();
		 alertDialog.setTitle(title);
		 alertDialog.setMessage(message);
		 alertDialog.show();
	 }
	 
	 public boolean isValidInput(){
		 
		 if(!lessWeight.isChecked() && !extraWeight.isChecked()){
				showErrors(getResources().getString(R.string.missing_input), 
						getResources().getString(R.string.status_valid));
				return false;
		}
		 
		//if(datePicked.before(Calendar.getInstance())){
	    	//showErrors(getResources().getString(R.string.invalid_input), getResources().getString(R.string.date_valid));
	    	//return false;
	   // }
		 
		if(lessWeight.isChecked()){
			searchStatus = 0;
		}
			
		else if(extraWeight.isChecked()){
			searchStatus = 1;
		}
		 
		 return true;
	 }
	 
	 public String removeSpaces(String str){
			str = str.trim();			
			str = str.replaceAll(" ", "%20");
			return str;
	}
	 
	 public void enterFlightNumber(){
		 
		 AlertDialog.Builder alert = new AlertDialog.Builder(this);                 
		 alert.setTitle(getResources().getString(R.string.flight_details));  
		 alert.setMessage(getResources().getString(R.string.enter_flight));                
  
		 final EditText flight = new EditText(this); 
		 flight.setSingleLine(true);
		 alert.setView(flight);
		  
		  alert.setPositiveButton(getResources().getString(R.string.search), new DialogInterface.OnClickListener() {  
			    public void onClick(DialogInterface dialog, int whichButton) {  
			         flightNumber = removeSpaces(flight.getText().toString());
			         
			         if(flightNumber.equals(""))
			        	 showErrors(getResources().getString(R.string.missing_input), 
			        			 getResources().getString(R.string.flight_valid));
			         
			         else
			        	 startViewResultsActivity();
			         
			         Log.e("maged", flightNumber);
			       	 return;	          
			      }  
		  }); 
		  
		  alert.setNeutralButton(getResources().getString(R.string.filter), new DialogInterface.OnClickListener() {  
			    public void onClick(DialogInterface dialog, int whichButton) {  
			    	flightNumber = removeSpaces(flight.getText().toString());
			    	
			    	if(flightNumber.equals(""))
			        	 showErrors(getResources().getString(R.string.missing_input), 
			        			 getResources().getString(R.string.flight_valid));
			    	else
			    		startFilterOffersActivity();
			    	
			         Log.e("maged", flightNumber);	          
			       	 return;  	          
			    }  
		  });  
		  
		  alert.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener(){
		         public void onClick(DialogInterface dialog, int which) {
		        	 srcDes.setChecked(false);
		    		 flightNo.setChecked(false);
		             return;   
		         }
		  });
		  
		  alert.show();
	 }
	 
	
	 public void enterSourceAndDestination(){
		 
		 AlertDialog.Builder alert = new AlertDialog.Builder(this);                 
		 alert.setTitle(getResources().getString(R.string.flight_details));  
		 alert.setMessage(getResources().getString(R.string.enter_airports));                
		 
		 final String[] airports = getResources().getStringArray(R.array.airports_array);
		 ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, airports);

		 ScrollView view = new ScrollView(this);
		 LinearLayout layout = new LinearLayout(this);
		 layout.setOrientation(1);
		 
		 final TextView enterSource = new TextView(this);
		 enterSource.setText(getResources().getString(R.string.source));
		 layout.addView(enterSource);
		 
		 final AutoCompleteTextView sourceAirport = new AutoCompleteTextView(this);
		 sourceAirport.setSingleLine(true);
		 layout.addView(sourceAirport);
		 
		 final TextView enterDestination = new TextView(this);
		 enterDestination.setText(getResources().getString(R.string.destination));
		 layout.addView(enterDestination);
		 
		 final AutoCompleteTextView desAirport = new AutoCompleteTextView(this);
		 desAirport.setSingleLine(true);
		 layout.addView(desAirport);
		  
		 Validator AirportValidator = new Validator() {
				@Override
				public boolean isValid(CharSequence text) {

					String stringText = text.toString();
					stringText = stringText.trim();
					Arrays.sort(airports);
					if ((Arrays.binarySearch(airports, stringText) > 0)) {
						return true;
					}
					return false;
				}
				@Override
				public CharSequence fixText(CharSequence invalidText) {

					return invalidText;
				}

			};
		sourceAirport.setAdapter(adapter);	
		desAirport.setAdapter(adapter);
		
		sourceAirport.setValidator(AirportValidator);
		desAirport.setValidator(AirportValidator);

		view.addView(layout);
		alert.setView(view);

		 alert.setPositiveButton(getResources().getString(R.string.search), new DialogInterface.OnClickListener() {  
			    public void onClick(DialogInterface dialog, int whichButton) {  
			         source = removeSpaces(sourceAirport.getText().toString());
			         destination = removeSpaces(desAirport.getText().toString());
			         
			         if(source.equals(""))
			        	 showErrors(getResources().getString(R.string.missing_input), 
			        			 getResources().getString(R.string.source_valid_missing));
			         else if(destination.equals(""))
			        	 showErrors(getResources().getString(R.string.missing_input), 
			        			 getResources().getString(R.string.des_valid_missing));
			         else if(!sourceAirport.getValidator().isValid(source))
			        	 showErrors(getResources().getString(R.string.invalid_input), 
			        			 getResources().getString(R.string.source_valid));
			         else if(!desAirport.getValidator().isValid(destination))
			        	 showErrors(getResources().getString(R.string.invalid_input), 
			        			 getResources().getString(R.string.des_valid));
			         else if(source.equalsIgnoreCase(destination))
			        	 showErrors(getResources().getString(R.string.invalid_input), 
			        			 getResources().getString(R.string.src_des_valid));
			         else
			        	 startViewResultsActivity();
			         
			         Log.e("maged", source);
			         Log.e("maged", destination);
			       	 return;	          
			      }  
		  }); 
		  
		  alert.setNeutralButton(getResources().getString(R.string.filter), new DialogInterface.OnClickListener() {  
			    public void onClick(DialogInterface dialog, int whichButton) {  
			    	source = removeSpaces(sourceAirport.getText().toString());
			         destination = removeSpaces(desAirport.getText().toString());
			         
			         if(source.equals(""))
			        	 showErrors(getResources().getString(R.string.missing_input), 
			        			 getResources().getString(R.string.source_valid_missing));
			         else if(destination.equals(""))
			        	 showErrors(getResources().getString(R.string.missing_input), 
			        			 getResources().getString(R.string.des_valid_missing));
			         else if(!sourceAirport.getValidator().isValid(source))
			        	 showErrors(getResources().getString(R.string.invalid_input), 
			        			 getResources().getString(R.string.source_valid));
			         else if(!desAirport.getValidator().isValid(destination))
			        	 showErrors(getResources().getString(R.string.invalid_input), 
			        			 getResources().getString(R.string.des_valid));
			         else if(source.equalsIgnoreCase(destination))
			        	 showErrors(getResources().getString(R.string.invalid_input), 
			        			 getResources().getString(R.string.src_des_valid));
			         else
			        	 startFilterOffersActivity();

			         Log.e("maged", source);
			         Log.e("maged", destination);
			       	 return;   	          
			    }  
		  });  
		  
		  alert.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener(){
		         public void onClick(DialogInterface dialog, int which) {
		        	 srcDes.setChecked(false);
		    		 flightNo.setChecked(false);
		             return;   
		         }
		  });
		  
		  alert.show();
	 }
	 
	 public void startViewResultsActivity(){
		 
		 if(searchMethod == 0)
			 request = "/filterflightnumberoffers/" + flightNumber + "/" + selectedDate + "/" + searchStatus +
				 	"/0/0/both/none";
		 
		 else{
			 
			 source = getAirportIndex(source);
			 destination = getAirportIndex(destination);
			 
			 Log.e("mm", source);
			 
			 request =  "/filterairportsoffers/" + source + "/" + destination + "/" + selectedDate 
				+ "/" + searchStatus + "/0/0/both/none";
			}
			 
		Intent intent = new Intent(getBaseContext(), ViewSearchResultsActivity.class);
		intent.putExtra("request", request);
		intent.putExtra("facebook", OwnerFacebookStatus.UNRELATED.name());

		startActivity(intent);
		 
	 }
	 

	 public void startFilterOffersActivity(){
		 
		Intent intent = new Intent(getBaseContext(), FilterPreferencesActivity.class);
		intent.putExtra("searchStatus", searchStatus);
		intent.putExtra("selectedDate", selectedDate);
		intent.putExtra("searchMethod", searchMethod);
		
		if(searchMethod == 0)
			intent.putExtra("flightNo", flightNumber);
		
		else{
			
			source = getAirportIndex(source);
			destination = getAirportIndex(destination);
			
			intent.putExtra("srcAirport", source);
			intent.putExtra("desAirport", destination);
		}
		
		startActivity(intent);
	 }
	 
	 public String getAirportIndex(String airport){
		 
		 for(int i = 0 ; i < airportsList.length ; i++){
			 if(airport.equals(airportsList[i]))
				 return i+"";	 
		 }
		 
		 return -1+"";
	 }

	@Override
	protected void onResume() {
			
		super.onResume();
			
		lessWeight.setChecked(false);
		extraWeight.setChecked(false);
		srcDes.setChecked(false);
    	flightNo.setChecked(false);		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig){
	    super.onConfigurationChanged(newConfig);
	}

}