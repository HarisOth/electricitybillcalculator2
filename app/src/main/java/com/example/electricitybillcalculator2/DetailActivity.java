package com.example.electricitybillcalculator2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    private TextView txtDetailMonth, txtDetailUnit, txtDetailCharges, txtDetailRebate, txtDetailFinalCost;
    private Button btnBack;

    private DatabaseHelper dbHelper;
    private int billId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initializeViews();

        dbHelper = new DatabaseHelper(this);
        billId = getIntent().getIntExtra("BILL_ID", -1);

        if (billId == -1) {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadBillDetails();
        setupButtonListeners();
    }

    private void initializeViews() {
        txtDetailMonth = findViewById(R.id.txtDetailMonth);
        txtDetailUnit = findViewById(R.id.txtDetailUnit);
        txtDetailCharges = findViewById(R.id.txtDetailCharges);
        txtDetailRebate = findViewById(R.id.txtDetailRebate);
        txtDetailFinalCost = findViewById(R.id.txtDetailFinalCost);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setText(getString(R.string.btn_back_to_history));
    }

    private void loadBillDetails() {
        BillModel bill = dbHelper.getBill(billId);

        if (bill.getMonth() == null) {
            Toast.makeText(this, "Bill not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        displayBillDetails(bill);
    }

    private void displayBillDetails(BillModel bill) {
        txtDetailMonth.setText(bill.getMonth());
        txtDetailUnit.setText(String.format("%.1f kWh", bill.getUnit()));
        txtDetailCharges.setText(String.format("RM %.2f", bill.getTotalCharges()));
        txtDetailRebate.setText(bill.getRebate() + "%");
        txtDetailFinalCost.setText(String.format("RM %.2f", bill.getFinalCost()));
    }

    private void setupButtonListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
