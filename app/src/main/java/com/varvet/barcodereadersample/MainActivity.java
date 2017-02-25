package com.varvet.barcodereadersample;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.varvet.barcodereadersample.barcode.BarcodeCaptureActivity;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int BARCODE_READER_REQUEST_CODE = 1;
    private String service;
    private String account_no;
    private String amount;
    private String validity;
    private TextView mResultTextView;
    private int vcs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResultTextView = (TextView) findViewById(R.id.result_textview);

        Button scanBarcodeButton = (Button) findViewById(R.id.scan_barcode_button);
        scanBarcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
                startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        setContentView(R.layout.ticket_desc_pay);
        TextView tv_service = (TextView) findViewById(R.id.tv_service);
        TextView tv_accno = (TextView) findViewById(R.id.tv_accno);
        TextView tv_amt = (TextView) findViewById(R.id.tv_amt);
        TextView tv_validity = (TextView) findViewById(R.id.tv_validity);
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;
                    String qr_code_data = barcode.displayValue;
                    String[] qr_code_data_parameters =  qr_code_data.split("&&");
                    service = qr_code_data_parameters[0];
                    account_no = qr_code_data_parameters[1];
                    amount = qr_code_data_parameters[2];
                    validity = qr_code_data_parameters[3];
                    tv_service.setText(service);
                    tv_accno.setText(account_no);
                    tv_amt.setText(amount);
                    tv_validity.setText(validity);
                   // mResultTextView.setText(barcode.displayValue);

                } else mResultTextView.setText(R.string.no_barcode_captured);
            } else Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format),
                    CommonStatusCodes.getStatusCodeString(resultCode)));
        } else super.onActivityResult(requestCode, resultCode, data);
    }
}
