package com.example.electricitybillcalculator2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class DetailActivity extends AppCompatActivity {

    private TextView txtDetailMonth, txtDetailUnit, txtDetailCharges, txtDetailRebate, txtDetailFinalCost;
    private Button btnBack, btnEdit, btnDelete;
    private CardView cardDetails;

    private DatabaseHelper dbHelper;
    private int billId;
    private BillModel currentBill;

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
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        cardDetails = findViewById(R.id.cardDetails);
    }

    private void loadBillDetails() {
        currentBill = dbHelper.getBill(billId);

        if (currentBill == null || currentBill.getMonth() == null) {
            Toast.makeText(this, "Bill not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        displayBillDetails(currentBill);
    }

    private void displayBillDetails(BillModel bill) {
        txtDetailMonth.setText(bill.getMonth());
        txtDetailUnit.setText(String.format("%.1f kWh", bill.getUnit()));
        txtDetailCharges.setText(String.format("RM %.2f", bill.getTotalCharges()));
        txtDetailRebate.setText(String.format("%.0f%%", bill.getRebate()));
        txtDetailFinalCost.setText(String.format("RM %.2f", bill.getFinalCost()));
    }

    private void setupButtonListeners() {
        btnBack.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity.this, EditActivity.class);
            intent.putExtra("BILL_ID", billId);
            startActivityForResult(intent, 1);
        });

        btnDelete.setOnClickListener(v -> {
            showDeleteConfirmationDialog();
        });
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Bill")
                .setMessage("Are you sure you want to delete this bill for " + currentBill.getMonth() + "?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteBill();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteBill() {
        boolean isDeleted = dbHelper.deleteBill(billId);

        if (isDeleted) {
            Toast.makeText(this, "Bill deleted successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Failed to delete bill", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadBillDetails();
            setResult(RESULT_OK);
            Toast.makeText(this, "Bill updated successfully", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}