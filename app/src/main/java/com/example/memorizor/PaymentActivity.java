package com.example.memorizor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PaymentActivity extends AppCompatActivity {

    private Button btn_skip_pay;
    private Button btn_pay;
    private EditText et_card_number;
    private EditText et_card_date;
    private EditText et_card_cvv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        getSupportActionBar().hide();

        btn_skip_pay = findViewById(R.id.btn_skip_pay);
        btn_pay = findViewById(R.id.btn_pay);
        et_card_number = findViewById(R.id.et_card_number);
        et_card_date = findViewById(R.id.et_card_date);
        et_card_cvv = findViewById(R.id.et_card_cvv);

        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strPattern = "^\\d{2}/\\d{2}$";

                if(et_card_number.getText().toString().isEmpty() || et_card_date.getText().toString().isEmpty() || et_card_cvv.getText().toString().isEmpty()){
                    Toast.makeText(PaymentActivity.this, "Missing credentials.", Toast.LENGTH_SHORT).show();
                } else if(et_card_number.getText().toString().length() != 16 ){
                    Toast.makeText(PaymentActivity.this, "Invalid card number.", Toast.LENGTH_SHORT).show();
                } else if(!et_card_date.getText().toString().matches(strPattern)){
                    Toast.makeText(PaymentActivity.this, "Invalid date.", Toast.LENGTH_SHORT).show();
                } else if(et_card_cvv.getText().toString().length() != 3){
                    Toast.makeText(PaymentActivity.this, "Invalid CVV.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PaymentActivity.this, "Payment successful.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        btn_skip_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}