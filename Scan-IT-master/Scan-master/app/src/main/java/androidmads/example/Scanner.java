package androidmads.example;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.Calendar;
import java.util.Date;

public class Scanner extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    AlertDialog.Builder builder;
    String number, value;
    ProgressDialog progressDialog;
    DatabaseReference databaseRegistrations;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait");
        number = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        databaseRegistrations = FirebaseDatabase.getInstance().getReference("visit");

        builder = new AlertDialog.Builder(this);
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        value = result.getText();
                        String[] splited = value.split("\\s+");
                        progressDialog.show();
                        addRegistrations();
                        progressDialog.dismiss();
                        if(splited[0].equals("No")) {
                            builder.setMessage("Safe");
                        }
                        else if(splited[0].equals("Yes"))
                        {
                            builder.setMessage("Not Safe");
                        }
                        AlertDialog alert = builder.create();
                        alert.setTitle("Status");
                        alert.show();
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }
    private void addRegistrations()
    {
        String id = databaseRegistrations.push().getKey();
        String[] splited = value.split("\\s+");
        Date currentTime = Calendar.getInstance().getTime();
        Reports reports = new Reports(id, number, splited[1], splited[0], currentTime);
        databaseRegistrations.child(id).setValue(reports);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestforcamera();

    }

    private void requestforcamera() {
        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                mCodeScanner.startPreview();

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(Scanner.this, "Camera Permission is required", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();

            }
        }).check();

    }
    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}