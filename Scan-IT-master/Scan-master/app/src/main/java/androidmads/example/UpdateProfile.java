package androidmads.example;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

public class UpdateProfile extends AppCompatActivity {
    EditText edittextName, address1;
    Button b1,b2;
    String name, address;
    Spinner getcovid;
    DatabaseReference databaseRegistrations;
    String suffering, type;
    TextView issuffering, typeregister;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        databaseRegistrations = FirebaseDatabase.getInstance().getReference("registrations");
        b1 = (Button) findViewById(R.id.adddetails);
        progressDialog = new ProgressDialog(this);
        getcovid = (Spinner) findViewById(R.id.spinner2);
        progressDialog.setMessage("Updating Details. Please Wait");
        progressDialog.show();
        b2 =(Button) findViewById(R.id.back);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpdateProfile.this, MainActivity.class));
            }
        });
        edittextName = (EditText) findViewById(R.id.editText11);
        address1 = (EditText) findViewById(R.id.editText12);
        typeregister = (TextView) findViewById(R.id.textView6);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        String number = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        DatabaseReference userNameRef = rootRef.child("registrations").child(number);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue(String.class);
                address = dataSnapshot.child("address").getValue(String.class);
                type = dataSnapshot.child("type").getValue(String.class);
                edittextName.setText(name);
                address1.setText(address);
                typeregister.setText(type);
                progressDialog.dismiss();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", databaseError.getMessage()); //Don't ignore errors!
            }
        };
        userNameRef.addListenerForSingleValueEvent(eventListener);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRegsitration();
            }
        });
    }
    private void addRegsitration()
    {
        name = edittextName.getText().toString().trim();
        address = address1.getText().toString().trim();
        if (!TextUtils.isEmpty(name)) {
            String number = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            String suffering = getcovid.getSelectedItem().toString();
            if(suffering.equals("Yes"))
            {
                blocktheusers();
            }
            else {
                Details details = new Details(name, address, number, suffering, type);
                databaseRegistrations.child(number).setValue(details);
                Toast.makeText(this, "Your Data has been updated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UpdateProfile.this, UpdateProfile.class));
            }
            Toast.makeText(this, "Your Profile has been updated", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UpdateProfile.this, UpdateProfile.class));
        }else
        {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
        }
    }
    private void blocktheusers(){
        final HashSet<String> set=new HashSet();
        final HashSet<String> set1=new HashSet();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final String number1 = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        DatabaseReference userNameRef = rootRef.child("visit");
        ValueEventListener eventListener = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String userno = postSnapshot.child("number2").getValue(String.class);
                    if(number1.equals(userno))
                    {
                        String otherno = postSnapshot.child("number1").getValue(String.class);
                        set.add(otherno);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", databaseError.getMessage()); //Don't ignore errors!
            }
        };
        userNameRef.addValueEventListener(eventListener);
        DatabaseReference userNameRef1 = rootRef.child("visit");
        ValueEventListener eventListener1 = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<String> i=set.iterator();
                while(i.hasNext()){
                    String a = i.next();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String storeno = postSnapshot.child("number1").getValue(String.class);
                        Date dateofregistered = postSnapshot.child("time").getValue(Date.class);
                        LocalDate localDate1 = dateofregistered.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        int registereddate = localDate1.getDayOfMonth();
                        Date date = new Date();
                        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        int todaydate = localDate.getDayOfMonth();
                        int finaldate = todaydate-registereddate;
                        if(a.equals(storeno) && finaldate<=6)
                        {
                            String userno11 = postSnapshot.child("number2").getValue(String.class);
                            set1.add(userno11);
                        }

                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", databaseError.getMessage()); //Don't ignore errors!
            }
        };
        userNameRef1.addValueEventListener(eventListener1);
        DatabaseReference rootRef12 = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userNameRef12 = rootRef12.child("registrations");
        ValueEventListener eventListener12 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<String> i=set1.iterator();
                while(i.hasNext()) {
                    String a = i.next();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String number111 = postSnapshot.child("phone").getValue(String.class);
                        if(number111.equals(a))
                        {
                            Details details = new Details(name, address, number111, "Yes", type);
                            databaseRegistrations.child(number111).setValue(details);
                        }

                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", databaseError.getMessage()); //Don't ignore errors!
            }
        };
        userNameRef12.addListenerForSingleValueEvent(eventListener12);
    }

}
