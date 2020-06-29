package androidmads.example;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.firebase.ui.auth.AuthUI;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

public class MainActivity extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 1781;
    List<AuthUI.IdpConfig> providers;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    SharedPreferences detail = null;

    private EditText edtValue;
    private ImageView qrImage;
    private String inputValue;
    private String savePath = Environment.getExternalStorageDirectory().getPath() + "/QRCode/";
    private Bitmap bitmap;
    private QRGEncoder qrgEncoder;
    private AppCompatActivity activity;
    Button b1;
    Button b2,b3,b4;
    String typeofperson, suffering;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b2 = (Button) findViewById(R.id.adddetails);
        b3 = (Button) findViewById(R.id.generate_barcode);
        b4 = (Button) findViewById(R.id.button3);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting Details. Please Wait");
        progressDialog.show();
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UpdateProfile.class));
            }
        });
        if (user == null) {
            signIn();
        } else {
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            String number = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            DatabaseReference userNameRef = rootRef.child("registrations").child(number);

            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()) {
                        startActivity(new Intent(MainActivity.this, AddPersonDetails.class));
                    }
                    else
                    {
                        typeofperson = dataSnapshot.child("type").getValue(String.class);
                        suffering = dataSnapshot.child("suffering").getValue(String.class);
                        progressDialog.dismiss();
                        b2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(typeofperson.equals("Public Place")) {
                                    startActivity(new Intent(getApplicationContext(), Scanner.class));
                                }
                                else
                                {
                                    Toast.makeText(MainActivity.this, "You are Registerd as Person. So you are not allowed to Scan the code", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("Error", databaseError.getMessage()); //Don't ignore errors!
                }
            };
            userNameRef.addListenerForSingleValueEvent(eventListener);
        }
        b1=(Button) findViewById(R.id.button2);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                signIn();
            }
        });

        qrImage = findViewById(R.id.qr_image);
        activity = this;


        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(typeofperson.equals("Person") && suffering.equals("No"))
                {
                    String number = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                    String input = suffering.concat(" ").concat(number);
                    if (input.length() > 0) {
                        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                        Display display = manager.getDefaultDisplay();
                        Point point = new Point();
                        display.getSize(point);
                        int width = point.x;
                        int height = point.y;
                        int smallerDimension = width < height ? width : height;
                        smallerDimension = smallerDimension * 3 / 4;

                        qrgEncoder = new QRGEncoder(
                                input, null,
                                QRGContents.Type.TEXT,
                                smallerDimension);
                        try {
                            bitmap = qrgEncoder.getBitmap();
                            qrImage.setImageBitmap(bitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "You are not allowed to Generate the code", Toast.LENGTH_LONG).show();
                }
            }
        });

        findViewById(R.id.save_barcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        boolean save = new QRGSaver().save(savePath, edtValue.getText().toString().trim(), bitmap, QRGContents.ImageType.IMAGE_JPEG);
                        String result = save ? "Image Saved" : "Image Not Saved";
                        Toast.makeText(activity, result, Toast.LENGTH_LONG).show();
                        edtValue.setText(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
            }
        });



    }
    private void signIn() {
        //Init providers
        providers = Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().build()
        );

        showSignInOptions();
    }


    private void showSignInOptions() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.common_google_signin_btn_icon_dark)
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                        .setTheme(R.style.Theme_AppCompat_Light_DialogWhenLarge)
                        .build(), MY_REQUEST_CODE
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                //Get user
                user = FirebaseAuth.getInstance().getCurrentUser();
                //Show user email on Toast
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                String number = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                DatabaseReference userNameRef = rootRef.child("registrations").child(number);
                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()) {
                            startActivity(new Intent(MainActivity.this, AddPersonDetails.class));
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("Error", databaseError.getMessage()); //Don't ignore errors!
                    }
                };
                userNameRef.addListenerForSingleValueEvent(eventListener);
            } else {
                Toast.makeText(this, "" + response.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}


