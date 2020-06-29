package androidmads.example;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddPersonDetails extends AppCompatActivity {

    EditText edittextName, address1;
    Button b1;
    Spinner getcovid, getuses;
    DatabaseReference databaseRegistrations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseRegistrations = FirebaseDatabase.getInstance().getReference("registrations");
        setContentView(R.layout.activity_add_person_details);
        edittextName = (EditText) findViewById(R.id.editText11);
        address1 = (EditText) findViewById(R.id.editText12);
        getcovid = (Spinner) findViewById(R.id.spinner);
        getuses = (Spinner) findViewById(R.id.spinner1);
        b1 = (Button) findViewById(R.id.adddetails);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRegsitration();
            }
        });
    }

    private void addRegsitration()
    {
        String name = edittextName.getText().toString().trim();
        String address = address1.getText().toString().trim();
        String suffering = getcovid.getSelectedItem().toString();
        String usesinfo = getuses.getSelectedItem().toString();
        if (!TextUtils.isEmpty(name)) {
            String number = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            Details details = new Details(name, address, number, suffering, usesinfo);
            databaseRegistrations.child(number).setValue(details);
            startActivity(new Intent(AddPersonDetails.this, MainActivity.class));

        }else
        {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
        }
    }
}
