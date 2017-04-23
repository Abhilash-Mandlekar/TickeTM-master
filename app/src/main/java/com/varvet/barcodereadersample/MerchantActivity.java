package com.varvet.barcodereadersample;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.paytm.pgsdk.PaytmClientCertificate;
import com.paytm.pgsdk.PaytmMerchant;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import static com.varvet.barcodereadersample.MainActivity.date;
import static com.varvet.barcodereadersample.MainActivity.timeStamp;
import static com.varvet.barcodereadersample.MainActivity.txnID;
import static com.varvet.barcodereadersample.MainActivity.validity;

/**
 * This is the sample app which will make use of the PG SDK. This activity will
 * show the usage of Paytm PG SDK API's.
 **/

public class MerchantActivity extends Activity {

	private AlarmManager mAlarmManager;
	private Intent mNotificationReceiverIntent, mLoggerReceiverIntent;
	private PendingIntent mNotificationReceiverPendingIntent;
	private long validityInMiliSec;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.merchantapp);
		initOrderId();
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}
	
	//This is to refresh the order id: Only for the Sample App's purpose.
	@Override
	protected void onStart(){
		super.onStart();
		initOrderId();
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}
	

	 void initOrderId() {
		Random r = new Random(System.currentTimeMillis());
		String orderId = "ORDERDD" + (1 + r.nextInt(2)) * 10000
				+ r.nextInt(10000);
		EditText orderIdEditText = (EditText) findViewById(R.id.order_id);
		orderIdEditText.setText(orderId);
		 //onStartTransaction(new View(this));
	}

	public void onStartTransaction(View view) {//VIEW PARAMETER REMOVED    CANT CHANGE
		PaytmPGService Service = PaytmPGService.getStagingService();
		Map<String, String> paramMap = new HashMap<String, String>();

		// these are mandatory parameters
		Random r = new Random(System.currentTimeMillis());
		String orderId = "ORDERDD" + (1 + r.nextInt(2)) * 10000
				+ r.nextInt(10000);


		paramMap.put("ORDER_ID", orderId);
		paramMap.put("MID", "WorldP64425807474247");
		paramMap.put("CUST_ID", "CUST23657");
		paramMap.put("CHANNEL_ID", "WAP");
		paramMap.put("INDUSTRY_TYPE_ID", "Retail");
		paramMap.put("WEBSITE", "worldpressplg");
		//paramMap.put("TXN_AMOUNT", ((EditText) findViewById(R.id.transaction_amount)).getText().toString());
		paramMap.put("TXN_AMOUNT", "10");

		paramMap.put("THEME", "merchant");
		paramMap.put("EMAIL", "abhi@gmail.com");
		paramMap.put("MOBILE_NO","123");
		PaytmOrder Order = new PaytmOrder(paramMap);

		PaytmMerchant Merchant = new PaytmMerchant(
				"https://pguat.paytm.com/paytmchecksum/paytmCheckSumGenerator.jsp",
				"https://pguat.paytm.com/paytmchecksum/paytmCheckSumVerify.jsp");

		Service.initialize(Order, Merchant, null);

		Service.startPaymentTransaction(this, true, true,
				new PaytmPaymentTransactionCallback() {
					@Override
					public void someUIErrorOccurred(String inErrorMessage) {
						// Some UI Error Occurred in Payment Gateway Activity.
						// // This may be due to initialization of views in
						// Payment Gateway Activity or may be due to //
						// initialization of webview. // Error Message details
						// the error occurred.
					}

					@Override
					public void onTransactionSuccess(Bundle inResponse) {
						// After successful transaction this method gets called.
						// // Response bundle contains the merchant response
						// parameters.
						Log.d("LOG", "Payment Transaction is successful " + inResponse);
						Toast.makeText(getApplicationContext(), "Payment Transaction is successful ", Toast.LENGTH_LONG).show();
								MainActivity.date = inResponse.getString("TXNDATE");
								MainActivity.responseCode = inResponse.getString("RESPCODE");
								MainActivity.responseMSG = inResponse.getString("RESPMSG");
								MainActivity.txnID = inResponse.getString("TXNID");

						SharedPreferences.Editor editor = getSharedPreferences("TICKETMSP", MODE_PRIVATE).edit();
						String key = MainActivity.service + "&&" + MainActivity.account_no + "&&" +
								MainActivity.amount + "&&" + MainActivity.validity + "&&" + MainActivity.txnID + "&&" + MainActivity.timeStamp;

						String value=MainActivity.service;

						editor.putString(key,value);
						editor.commit();
						//resume ticket generating activity
						//INSTEAD GO TOT FRONT PAGE , ADDING TICKET IN LIST
						Intent openMainActivity= new Intent(MerchantActivity.this, MainActivity.class);//(from , to)
						//openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						openMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
						Log.e("Servicename: ",MainActivity.service);

						addTicketToAWS();

						validityInMiliSec = Integer.parseInt(validity)*60*60*1000;
						Log.e("validityInMiliSec: ",""+validityInMiliSec+"       <---- "+validity);

						//setNextAlarm(validityInMiliSec,key);
						//setNextAlarm(60*1000,key);   //1 Minute




						startActivity(openMainActivity);

					}

					@Override
					public void onTransactionFailure(String inErrorMessage,
							Bundle inResponse) {
						// This method gets called if transaction failed. //
						// Here in this case transaction is completed, but with
						// a failure. // Error Message describes the reason for
						// failure. // Response bundle contains the merchant
						// response parameters.
						Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
						Toast.makeText(getBaseContext(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
					}

					@Override
					public void networkNotAvailable() { // If network is not
														// available, then this
														// method gets called.
					}

					@Override
					public void clientAuthenticationFailed(String inErrorMessage) {
						// This method gets called if client authentication
						// failed. // Failure may be due to following reasons //
						// 1. Server error or downtime. // 2. Server unable to
						// generate checksum or checksum response is not in
						// proper format. // 3. Server failed to authenticate
						// that client. That is value of payt_STATUS is 2. //
						// Error Message describes the reason for failure.
					}

					@Override
					public void onErrorLoadingWebPage(int iniErrorCode,
							String inErrorMessage, String inFailingUrl) {

					}

					// had to be added: NOTE
					@Override
					public void onBackPressedCancelTransaction() {
						// TODO Auto-generated method stub
					}

				});


	}
	public void addTicketToAWS()
	{
		Runnable runnable = new Runnable() {
			public void run() {
				//DynamoDB calls go here
				CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
						getApplicationContext(),
						"us-west-2:6a90e3bc-32d9-4eb3-83c1-0d19aa5906fa", // Identity Pool ID
						Regions.US_WEST_2 // Region
				);


				AmazonDynamoDBClient ddbClient = Region.getRegion(Regions.US_WEST_2) // CRUCIAL

						.createClient(
								AmazonDynamoDBClient.class,
								credentialsProvider,
								new ClientConfiguration()
						);

				DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

				TicketDetailsDb tkt = new TicketDetailsDb();
				String trans_id = txnID + "";
				tkt.setTrans_id(trans_id);
				tkt.setTime_stamp(timeStamp);
				tkt.setValidity(Integer.parseInt(validity));
				tkt.setPenalty(121212);

				mapper.save(tkt);
			}
		};


		Thread mythread = new Thread(runnable);
		mythread.start();
		Log.e("Database status: ","succesfully entered");

	}

	public void setNextAlarm(long next_alarm,String key)
	{
		mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

		// Create an Intent to broadcast to the AlarmNotificationReceiver
		mNotificationReceiverIntent = new Intent(MerchantActivity.this,
				AlarmNotificationReceiver.class);

		mNotificationReceiverIntent.putExtra("key",key);

		Random randomGenerator = new Random();
		int requestCode = randomGenerator.nextInt(100);  // 0 to 99
		// Create an PendingIntent that holds the NotificationReceiverIntent
		mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
				MerchantActivity.this, requestCode, mNotificationReceiverIntent, 0);


		// Set single alarm


		mAlarmManager.set(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + next_alarm,
				mNotificationReceiverPendingIntent);//UPGRADEABLE
		// Show Toast message
		Toast.makeText(getApplicationContext(), " Next alarm set .. ",
				Toast.LENGTH_SHORT).show();

		//-------------------------------------

	}

}
