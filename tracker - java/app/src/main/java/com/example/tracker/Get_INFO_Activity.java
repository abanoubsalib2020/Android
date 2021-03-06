package com.example.tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class Get_INFO_Activity extends AppCompatActivity {
    TextView textView;
    Button button ;
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        final SharedPreferences sharedPref = this.getPreferences(this.MODE_PRIVATE);
        final String mynumber = sharedPref.getString("mynumber","");
        if (mynumber.equals(""))
        {
            editText = findViewById(R.id.get_the_number);
            button = findViewById(R.id.next);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String number = editText.getText().toString();
                    SharedPreferences.Editor edit = sharedPref.edit();
                    edit.putString("mynumber",number);
                    edit.commit();
                    Intent i = new Intent(Get_INFO_Activity.this, MainActivity.class);
                    i.putExtra("mynumber",number);
                    Get_INFO_Activity.this.startActivity(i);

                }
            });

        }else
        {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("mynumber",mynumber);
            this.startActivity(i);
        }

    }
}
