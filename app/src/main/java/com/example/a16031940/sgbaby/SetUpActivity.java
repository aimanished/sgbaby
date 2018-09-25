package com.example.a16031940.sgbaby;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SetUpActivity extends AppCompatActivity {

    ImageView profileImage;
    Button setUpBtn;
    EditText user_name;
    TextView EDD;
    DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        EDD = findViewById(R.id.EDD);

        EDD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int Year = cal.get(Calendar.YEAR);
                int Month = cal.get(Calendar.MONTH);
                int Day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(SetUpActivity.this, android.R.style.Theme_DeviceDefault_Dialog_Alert, mDateSetListener, Year, Month, Day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day + "/" + month + "/" + year;

                Calendar cal = Calendar.getInstance();
                int Year = cal.get(Calendar.YEAR);
                int Month = cal.get(Calendar.MONTH);
                int Day = cal.get(Calendar.DAY_OF_MONTH);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
                ParsePosition pp1 = new ParsePosition(0);
                Date dateChosen = sdf.parse("01/12/2015",pp1);
                Date dateCurrent = sdf.parse(Day + "/" + Month + "/" + Year,pp1);

//                if(dateChosen.compareTo(dateCurrent) < 1){
//                    EDD.setText(date);
//                }else{
//                    Toast.makeText(getBaseContext(),"Please enter a valid EDD",Toast.LENGTH_SHORT).show();
//                }
            }
        };
    }
}
