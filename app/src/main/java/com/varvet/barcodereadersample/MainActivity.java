package com.varvet.barcodereadersample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.regions.Region;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.varvet.barcodereadersample.barcode.BarcodeCaptureActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private ListView mainListView ;
    private ArrayAdapter<String> listAdapter ;

    ListView lview;
    ListViewAdapter lviewAdapter;

    private final static String month[] = {"January","February","March","April","May",
            "June","July","August","September","October","November","December"};

    private final static String number[] = {"Month - 1", "Month - 2","Month - 3",
            "Month - 4","Month - 5","Month - 6",
            "Month - 7","Month - 8","Month - 9",
            "Month - 10","Month - 11","Month - 12"};

    private final static ArrayList<String> lTitle = new ArrayList<String>();
    private final static ArrayList<String> lDescription = new ArrayList<String>();


    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int BARCODE_READER_REQUEST_CODE = 1;


    //BARCODE DATA
    public static String service;
    public static String account_no;
    public static String amount;
    public static String validity;
    public  static String txnID;
    public  static String timeStamp;


    private TextView mResultTextView;
    public static String date;
    public static String responseCode;
    public static String responseMSG;

    Bitmap bitmap;

    public final static int QRcodeWidth = 500;
    private boolean paymentSuccessful = false;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //------------------------ LIST VIEW--------------------------------------------------


//        // Find the ListView resource.
//        mainListView = (ListView) findViewById( R.id.mainListView );
//
//        // Create and populate a List of planet names.
//        String[] planets = new String[] { "Mercury", "Venus", "Earth", "Mars",
//                "Jupiter", "Saturn", "Uranus", "Neptune"};
//        ArrayList<String> planetList = new ArrayList<String>();
//        planetList.addAll( Arrays.asList(planets) );
//
//        // Create ArrayAdapter using the planet list.
//        listAdapter = new ArrayAdapter<String>(this, R.layout.row_content, planetList);
//
//        // Add more planets. If you passed a String[] instead of a List<String>
//        // into the ArrayAdapter constructor, you must not add more items.
//        // Otherwise an exception will occur.
//        listAdapter.add( "Ceres" );
//        listAdapter.add( "Pluto" );
//        listAdapter.add( "Haumea" );
//        listAdapter.add( "Makemake" );
//        listAdapter.add( "Eris" );
//
//        // Set the ArrayAdapter as the ListView's adapter.
//        mainListView.setAdapter( listAdapter );
        //------------------------ LIST VIEW--------------------------------------------------

        //------------------------ LIST VIEW2--------------------------------------------------

        Log.e("onCreate"," --------------------------------->>>>>>>>>>>>>>>>>>>>    called");
//        for(String s : month)
//        {
//            lTitle.add(s);
//        }
//        for(String s : number)
//        {
//            lDescription.add(s);
//        }

        SharedPreferences prefs = getSharedPreferences("TICKETMSP", MODE_PRIVATE);
        HashMap<String,String> h = (HashMap<String, String>) prefs.getAll();


        lview = (ListView) findViewById(R.id.mainListView);
        //lviewAdapter = new ListViewAdapter(this, month, number);
        lviewAdapter = new ListViewAdapter(this, lTitle, lDescription);

        System.out.println("adapter => "+lviewAdapter.getCount());

        lviewAdapter.clear();           //to avoid repeated insert in list
        for(String key : h.keySet())
        {
            String title = h.get(key);
            lviewAdapter.add(title,key);
        }
        lview.setAdapter(lviewAdapter);

        lview.setOnItemClickListener(this);


//------------------------ LIST VIEW 2--------------------------------------------------




        mResultTextView = (TextView) findViewById(R.id.result_textview);

        Button scanBarcodeButton = (Button) findViewById(R.id.scan_barcode_button);
        scanBarcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
                startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        setContentView(R.layout.ticket_desc_pay);
        TextView tv_service = (TextView) findViewById(R.id.tv_service);
        TextView tv_accno = (TextView) findViewById(R.id.tv_accno);
        TextView tv_amt = (TextView) findViewById(R.id.tv_amt);
        TextView tv_validity = (TextView) findViewById(R.id.tv_validity);
        Button btn_pay = (Button) findViewById(R.id.btn_pay);
        int min = 100000;
        int max = 999999;

        final int transaction_id = new Random().nextInt(max - min + 1) + min;
        final String ts = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        timeStamp=ts;

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
                String trans_id = transaction_id + "";
                tkt.setTrans_id(trans_id);
                tkt.setTime_stamp("yess");
                tkt.setValidity(100);
                tkt.setPenalty(121212);

                mapper.save(tkt);
            }
        };


        Thread mythread = new Thread(runnable);
        mythread.start();

        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {


                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;
                    String qr_code_data = barcode.displayValue;
                    String[] qr_code_data_parameters = qr_code_data.split("&&");

                    service = qr_code_data_parameters[0];
                    account_no = qr_code_data_parameters[1];
                    amount = qr_code_data_parameters[2];
                    validity = qr_code_data_parameters[3];

                    tv_service.setText(" Service Name                   : " + service);
                    tv_accno.setText(" Account Number                 : " + account_no);
                    tv_amt.setText(" Amount of Ticket (in Rs)       : " + amount);
                    tv_validity.setText(" Validity of Ticket (in hrs)    : " + validity);

                    //button onclick
                    btn_pay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

//                            MerchantActivity mMerchantActivity = new MerchantActivity();
//                            mMerchantActivity.onStart();
//                            onStartTransaction(amount);

                            Intent pay_intent = new Intent(MainActivity.this, MerchantActivity.class);
                            MainActivity.this.startActivity(pay_intent);

                        //--------------------------------dead code------------------------------
//                            if(responseCode.equals("01")&&responseMSG.equals("Txn Successful."))
//                                paymentSuccessful = true;
//                            Log.e("responseCode: ",responseCode);
//                            Log.e("responseMSG: ",responseMSG);

                            // to be done in front list

                            if (paymentSuccessful) {
                                String encode_ticket = service + "&&" + account_no + "&&" + amount + "&&" + validity + "&&" + txnID + "&&" + timeStamp;

                                try {
                                    bitmap = TextToImageEncode(encode_ticket);
                                    ImageView imageView = (ImageView) findViewById(R.id.imageView);
                                    imageView.setImageBitmap(bitmap);

                                } catch (WriterException e) {
                                    e.printStackTrace();
                                }

                            }

                            //--------------------------------dead code------------------------------

                        }
                    });
                    // mResultTextView.setText(barcode.displayValue);

                } else setContentView(R.layout.activity_main);
            } else Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format),
                    CommonStatusCodes.getStatusCodeString(resultCode)));
        } else super.onActivityResult(requestCode, resultCode, data);
    }


    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        ContextCompat.getColor(MainActivity.this, android.R.color.black) : ContextCompat.getColor(MainActivity.this, android.R.color.white);

            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
       // Toast.makeText(this,"restarted",Toast.LENGTH_SHORT).show();
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
        // TODO Auto-generated method stub
        TextView tv  =(TextView)arg1.findViewById(R.id.tv2);    // qrcode reciept data
        Log.e("tv2"," ---------> "+tv.getText());


        String encode_ticket = tv.getText().toString();

        try {
            bitmap = TextToImageEncode(encode_ticket);
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }Toast.makeText(this,tv.getText(),Toast.LENGTH_LONG);


    }
}
