package com.sheel.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.sheel.datastructures.enums.OwnerFacebookStatus;

public class FilterPreferencesActivity extends UserSessionStateMaintainingActivity {
	
	int searchStatus;
	String selectedDate;
	int searchMethod;
	String flightNumber;
	String srcAirport;
	String desAirport;
	
	ToggleButton male; 
	ToggleButton female;
	ToggleButton friends;
	ToggleButton friends_of_friends;

	EditText numOfKgsText;
	EditText priceText;
	AutoCompleteTextView nationalityView;
	
	String availableKgs;
	String pricePerKg;
	String gender;
	String nationality;
	
	int kgs, price;
	boolean allValid;
	
	OwnerFacebookStatus facebook;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_preferences);
        
        Bundle extras = getIntent().getExtras();
        
        	if(extras !=null){
        		searchStatus = extras.getInt("searchStatus");
        		selectedDate = extras.getString("selectedDate");
        		searchMethod = extras.getInt("searchMethod");
        		
        		
        		if(searchMethod == 0)
        			flightNumber = extras.getString("flightNo");
        		
        		
        		else{
        			srcAirport = extras.getString("srcAirport");
        			desAirport = extras.getString("desAirport");
        		}
        			
        	}
        
        gender = "both";
       	nationality  = "none";
       	
    	kgs = 0;
    	price = 0;
		allValid = true;
        	
        numOfKgsText = (EditText) findViewById(R.id.autoCompleteTextView1);
       	priceText = (EditText) findViewById(R.id.autoCompleteTextView2);
        male = (ToggleButton) findViewById(R.id.male);
        female = (ToggleButton) findViewById(R.id.female);
        friends = (ToggleButton) findViewById(R.id.toggleButton1);
        friends_of_friends = (ToggleButton) findViewById(R.id.toggleButton2);
        	
        setNationalityAdaptor();
     }
	
	public void setNationalityAdaptor(){
	
        nationalityView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView3);
  
        String[] nationalities = getResources().getStringArray(R.array.nationalities_array);
        ArrayAdapter<String> nationalityAdapter = new ArrayAdapter<String>(this, R.layout.list_item, nationalities);
        
        nationalityView.setAdapter(nationalityAdapter);
        
		
	}
	
	public String removeSpaces(String str){
		
		str = str.trim();
		
		str = str.replaceAll(" ", "%20");
		
		return str;
	}
	

	public void onClick_male_gender(View v) 
	{
		female.setChecked(false);
		gender = "male";
	}
	
	public void onClick_female_gender(View v) 
	{
		male.setChecked(false);
		gender = "female";
	}
	
	public void onClick_friends_of_friends(View v) 
	{
		friends.setChecked(false);
		facebook = OwnerFacebookStatus.FRIEND_OF_FRIEND;
	}
	
	public void onClick_friends(View v) 
	{
		friends_of_friends.setChecked(false);
		facebook = OwnerFacebookStatus.FRIEND;
	}
	
	public void onClick_search_offers(View v) 
	{
		
		if(!male.isChecked() && !female.isChecked())
			gender = "both";
		
		if(!friends.isChecked() && !friends_of_friends.isChecked())
			facebook = OwnerFacebookStatus.UNRELATED;
		
		availableKgs = numOfKgsText.getText().toString();
		pricePerKg = priceText.getText().toString();
		
		nationality = nationalityView.getText().toString();
		nationality = removeSpaces(nationality);
		
		if(!availableKgs.equals("")){
			kgs = Integer.parseInt(availableKgs);
			
			if(kgs > 30){
				 Toast.makeText(FilterPreferencesActivity.this, "Available Kgs should not exceed 30 kgs", 
						 											Toast.LENGTH_LONG).show();
				 allValid = false;
			}	
		}
		
		if(!pricePerKg.equals("")){
			price = Integer.parseInt(pricePerKg);
			
			if(price > 30){
				 Toast.makeText(FilterPreferencesActivity.this, "Price per Kg should not exceed 30 euros", 
						 											Toast.LENGTH_LONG).show();
				 allValid = false;
			}
		}
		
		if(nationality.equals(""))
			nationality = "none";
		
		Log.e("mm", kgs + " ");
		Log.e("mm", price + " ");
		Log.e("mm", gender);
		Log.e("mm", nationality);
		
		if(allValid)
			startViewResultsActivity();
	}
	
	public void startViewResultsActivity(){
		
		String request = "";
		
		if(searchMethod == 0)
			request = "/filterflightnumberoffers/" + flightNumber + "/" + selectedDate + "/" + searchStatus +
									"/" + kgs + "/" + price + "/" + gender + "/" + nationality; 
		
		else
			request = "/filterairportsoffers/" + srcAirport + "/" + desAirport + "/" + selectedDate 
					+ "/" + searchStatus + "/" + kgs + "/" + price + "/" + gender + "/" + nationality;
		
		//Intent intent = new Intent(getBaseContext(), ViewSearchResultsActivity.class);
		Intent intent = setSessionInformationBetweenActivities(ViewSearchResultsActivity.class);
		intent.putExtra("request", request);
		intent.putExtra("facebook", facebook.name());
		
		Bundle extras = getIntent().getExtras();
		long userId = extras.getLong("userId");
		intent.putExtra("userId", userId);
//		Toast.makeText(getApplicationContext(),"" + userId, Toast.LENGTH_SHORT).show();
		
		startActivity(intent);
		
		
	}
	
	
	
	
	
	
	
	
	/*
	public void filterOffers(){
		
		HTTPClient sc = new HTTPClient() {
	           
			@Override
			public void doSomething() {
				final String str = this.rspStr;
				 
							 runOnUiThread(new Runnable()
                             {
                                 @Override
                                 public void run()
                                 {
                                     Toast.makeText(FilterPreferencesActivity.this, str, Toast.LENGTH_LONG).show();
                                   //  startActivity(new Intent(SearchByFlightNoActivity.this, PhoneCommunication.class));
                                     
                                     try {
                                    	 
                                    	JSONArray jsonArray = new JSONArray(builder.toString());
                                    	
                                    	JSONObject offer;
                                    	JSONObject user;
                                    	JSONObject flight;
                                    	
                                    	int kgs; 
                                    	String username = "";
                                    	
                                    	String source = "";
                                    	
                                    	 for (int i = 0; i < jsonArray.length(); i++) {
                                    		 
                                    		 offer = jsonArray.getJSONObject(i);

                                    		 kgs = offer.getInt("noOfKilograms");
                                    		 
                                    		 user = offer.getJSONObject("user");
                                    		 username = user.getString("username");
                                    		 
                                    		 flight = offer.getJSONObject("flight");
                                    		 source = flight.getString("source");
										
                                    		 Log.e("mmm", username + "");
                                    		 Log.e("mmm", source + "");
                                    		 Log.e("mmm", kgs + "");
                                    		 
                                    	 }
										
									} catch (JSONException e) {
										
										e.printStackTrace();
									}
                                 }
                             });
				}
        
			};
	        
			if(searchMethod == 0)
				sc.runHttpRequest("/filterflightnumberoffers/" + flightNumber + "/" + selectedDate + "/" + searchStatus +
															"/" + kgs + "/" + price + "/" + gender + "/" + nationality); 
			
			else
				sc.runHttpRequest("/filterairportsoffers/" + srcAirport + "/" + desAirport + "/" + selectedDate 
						+ "/" + searchStatus + "/" + kgs + "/" + price + "/" + gender + "/" + nationality);
				
		 	
	}*/
	
}
