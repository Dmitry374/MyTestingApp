package com.example.dima.mytestingapp.Activitys;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dima.mytestingapp.R;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

public class RatesCalcActivity extends AppCompatActivity {

    TextView tvTitleRates, tvSubTitle, rateFromName, rateToName, tvRateToRes;
    EditText etInput;
    Button btnClear;

    String rateNameFrom, rateNameTo;
    double rateCourse;

    double getCount, result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rates_calc);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);    //   Стрелка
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(RatesCalcActivity.this, UserActivity.class);
//                intent.putExtra("name_fragment", "frates");
//                startActivity(intent);
//
//                finish();
//            }
//        });

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        tvTitleRates = (TextView) findViewById(R.id.tvTitleRates);
        tvSubTitle = (TextView) findViewById(R.id.tvSubTitle);
        rateFromName = (TextView) findViewById(R.id.rateFromName);
        rateToName = (TextView) findViewById(R.id.rateToName);
        tvRateToRes = (TextView) findViewById(R.id.tvRateToRes);

        etInput = (EditText) findViewById(R.id.etInput);

        btnClear = (Button) findViewById(R.id.btnClear);

        Intent intent = getIntent();
        rateNameFrom = intent.getStringExtra("rate_name_from");
        rateNameTo = intent.getStringExtra("rate_name_to");
        rateCourse = intent.getDoubleExtra("rate_course", 0);

        tvTitleRates.setText(rateNameFrom + " в " + rateNameTo);
        tvSubTitle.setText("Курс 1  :  " + Double.toString(rateCourse));
        rateFromName.setText(rateNameFrom);
        rateToName.setText(rateNameTo);


        //        ------------------ Расчеты в реальном времени без кнопок --------------------------------
        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                try{
                    if (etInput.getText().toString().equals("")){
                        tvRateToRes.setText("0.0");
                    } else {
                        getCount = Double.parseDouble(etInput.getText().toString());
                        result = Math.rint(10000.0 * (getCount * rateCourse))/10000.0;
                        DecimalFormat decimalFormat = new DecimalFormat("#,###.####");
                        tvRateToRes.setText(decimalFormat.format(result));
                    }

                } catch (NumberFormatException e){
                    Log.d("myLogs", "Перевод строки в число");
                }
            }
        });


        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etInput.setText("");
                tvRateToRes.setText("0.0");
            }
        });

    }

}
