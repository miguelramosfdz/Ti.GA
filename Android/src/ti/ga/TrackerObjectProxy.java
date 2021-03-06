/**
 * Ti.GA - Google Analytics for Titanium
 * Copyright (c) 2013 by Ben Bahrenburg. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 * 
 * Available at https://github.com/benbahrenburg/Ti.GA
 */
package ti.ga;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.util.TiConvert;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

@Kroll.proxy(creatableInModule=TigaModule.class)
public class TrackerObjectProxy extends KrollProxy {

	private Tracker _Tracker; //Local access for our tracker object
	
	//Local defaults
	private String _TrackerId = ""; 
	private boolean _SessionStarted = false;
	
	@SuppressWarnings("rawtypes") 
	public TrackerObjectProxy(Tracker tracker, String trackerId, HashMap hm)
	{
		super();
		_Tracker = tracker;
		_TrackerId = trackerId;
		applyDefaults();
		applyProperties(hm);
	}
	
	private void applyDefaults(){
		Util.LogDebug("Applying default application information");
		createValue(Fields.APP_ID,Util.getApplicationPackageName(TiApplication.getInstance().getApplicationContext()));
		createValue(Fields.APP_NAME,Util.getApplicationName(TiApplication.getInstance().getApplicationContext()));
		createValue(Fields.APP_VERSION,Util.getApplicationVersion(TiApplication.getInstance().getApplicationContext()));
	}
	@SuppressWarnings("rawtypes") 
	private void applyProperties(HashMap hm){	
		Util.LogDebug("Applying Tracker Parameters provided");
		Iterator entries = hm.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry) entries.next();
			createValue(entry.getKey().toString(),entry.getValue().toString());
		}
	}
	
	@Kroll.getProperty
	public String trackerId()
	{
		return _TrackerId;
	}
	
	@Kroll.method
	public void sendView(String view)
	{
		Util.LogDebug("sendView view =" + view);
		// Set the screen name on the tracker so that it is used in all hits sent from this screen.
		_Tracker.set(Fields.SCREEN_NAME, view);

		// Send a screenview.
		_Tracker.send(MapBuilder
		  .createAppView()
		  .build()
		);	
		
	}

	@Kroll.method
	public void sendException(String description)
	{
		Util.LogDebug("sendException description =" + description);
		_Tracker.send(MapBuilder
	    	      .createException(description,false)                                              
	    	      .build()
	    	      );
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Kroll.method
	public void sendSocial(HashMap hm)
	{
		Util.LogDebug("sendSocial called");
		KrollDict args = new KrollDict(hm);		
		_Tracker.send(MapBuilder
			    .createSocial(TiConvert.toString(args, "network"),// Social network (required)
			    		TiConvert.toString(args, "action"),// Social action (required)
			    		TiConvert.toString(args, "target"))// Social target
			    .build()
			);		
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Kroll.method
	public void sendEvent(HashMap hm)
	{
		Util.LogDebug("sendEvent called");
		KrollDict args = new KrollDict(hm);
		long value = TiConvert.toInt(args, "value");
		_Tracker.send(MapBuilder
			      .createEvent(TiConvert.toString(args, "category"),     // Event category (required)
			    		  		TiConvert.toString(args, "action"),  // Event action (required)
			                   TiConvert.toString(args, "label"),  // Event label
			                   value)            // Event value
			      .build()
			  );		
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Kroll.method
	public void addCustomDimension(HashMap hm)
	{
		Util.LogDebug("sendTiming called");
		KrollDict args = new KrollDict(hm);
		_Tracker.set(Fields.customDimension(TiConvert.toInt(args,"index")),TiConvert.toString(args, "dimesion"));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Kroll.method
	public void addCustomMetric(HashMap hm)
	{
		Util.LogDebug("sendTiming called");
		KrollDict args = new KrollDict(hm);
		_Tracker.set(Fields.customMetric(TiConvert.toInt(args,"index")),TiConvert.toString(args, "metric"));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Kroll.method
	public void sendTiming(HashMap hm)
	{
		Util.LogDebug("sendTiming called");
		KrollDict args = new KrollDict(hm);
		double interval = TiConvert.toDouble(args, "value");
		//_Tracker.sendTiming(category, ((long) interval), name, label);
		_Tracker.send(MapBuilder
			      .createTiming(TiConvert.toString(args, "category"),    // Timing category (required)
			    		  		(long) interval,       // Timing interval in milliseconds (required)
			    		  		TiConvert.toString(args, "name"),  // Timing name
			    		  		TiConvert.toString(args, "label")) // Timing label
			      .build()
			  );
		  
	}
	

	@Kroll.method
	public String findValue(String key) {
		Util.LogDebug("getValue: key =" + key);
		return _Tracker.get(key);
	}
	
	@Kroll.method
	public void createValue(String key,String value) {
		Util.LogDebug("setValue: key =" + key + " value=" + value);
		_Tracker.set(key, value);
	}
	
	@Kroll.method
	public boolean isSessionStarted() {
		return _SessionStarted;
	}

	@Kroll.method
	public void startSession() {
		Util.LogDebug("startSession");
		_SessionStarted = true;
		_Tracker.set(Fields.SESSION_CONTROL, "start");
	}
	
	@Kroll.method
	public void endSession() {
		Util.LogDebug("endSession");
		_SessionStarted = false;
		_Tracker.set(Fields.SESSION_CONTROL, "end");
	}

	@Kroll.method
	public void sendSession() {
		Util.LogDebug("sendSession");
		MapBuilder paramMap = MapBuilder.createAppView();
		_Tracker.send(paramMap.build());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Kroll.method
	public void sendCampaign(HashMap hm){
		KrollDict args = new KrollDict(hm);
		Util.LogDebug("sendCampaign method called with parameters");
		HashMap<String, String> campaignData = new HashMap<String, String>();
		campaignData.put(Fields.CAMPAIGN_SOURCE, TiConvert.toString(args, "source"));
		campaignData.put(Fields.CAMPAIGN_MEDIUM, TiConvert.toString(args, "medium"));
		campaignData.put(Fields.CAMPAIGN_NAME, TiConvert.toString(args, "name"));
		campaignData.put(Fields.CAMPAIGN_CONTENT, TiConvert.toString(args, "content"));
		
		MapBuilder paramMap = MapBuilder.createAppView();
		// Campaign data sent with this hit.
		// Note that the campaign data is set on the Map, not the tracker.
		_Tracker.send(paramMap
		    .setAll(campaignData).build()
		);		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Kroll.method
	public void send(@Kroll.argument(optional=true) HashMap hm){
		
		if(hm == null){
			Util.LogDebug("send method called without parameters");
			
			MapBuilder paramMap = MapBuilder.createAppView();
			_Tracker.send(paramMap.build());				
		}else{
			Util.LogDebug("send method called with parameters");
			
			MapBuilder paramMap = MapBuilder.createAppView();
			_Tracker.send(paramMap
			    .setAll(hm).build()
			);				
		}
	
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Kroll.method
	public void createTransactionWithId(HashMap hm){
		KrollDict args = new KrollDict(hm);
		Util.LogDebug("createTransactionWithId method called with parameters");
		_Tracker.send(MapBuilder
			      .createTransaction(TiConvert.toString(args, "transID"), // (String) Transaction ID
			    		  			TiConvert.toString(args, "affiliation"),// (String) Affiliation
			    		  			TiConvert.toDouble(args,"revenue"), // (Double) Order revenue
			    		  			TiConvert.toDouble(args,"tax"),// (Double) Tax
			                        TiConvert.toDouble(args,"shipping"),// (Double) Shipping
			                        TiConvert.toString(args, "currencyCode")) // (String) Currency code
			      .build()
			  );	
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Kroll.method
	public void createItemWithTransactionId(HashMap hm){
		KrollDict args = new KrollDict(hm);
		Util.LogDebug("createItemWithTransactionId method called with parameters");
		_Tracker.send(MapBuilder
			      .createItem(TiConvert.toString(args, "transID"),// (String) Transaction ID
			    		  TiConvert.toString(args, "name"), // (String) Product name
			    		  TiConvert.toString(args, "sku"), // (String) Product SKU
			    		  TiConvert.toString(args, "category"),// (String) Product category
			    		  TiConvert.toDouble(args,"price"),// (Double) Product price
			    		  (long) TiConvert.toDouble(args,"quantity"),// (Long) Product quantity
			              TiConvert.toString(args, "currencyCode"))// (String) Currency code
			      .build()
			  );	
	}
}
