package com.example.electricitybillcalculator2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class EditActivity extends AppCompatActivity {

    private Spinner spinnerMonth;
    private SeekBar seekBarRebate;
    private TextView txtRebateValue;
    private EditText inputUnit;
    private Button btnSave, btnCancel;
    private CardView cardEditForm;

    private DatabaseHelper dbHelper;
    private BillModel currentBill;
    private int billId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        initializeViews();

        dbHelper = new DatabaseHelper(this);
        billId = getIntent().getIntExtra("BILL_ID", -1);

        if (billId == -1) {
            Toast.makeText(this, "Invalid bill ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupSpinner();
        setupSeekBar();
        loadBillData();
        setupButtonListeners();
    }

    private void initializeViews() {
        spinnerMonth = findViewById(R.id.spinnerMonth);
        seekBarRebate = findViewById(R.id.seekBarRebate);
        txtRebateValue = findViewById(R.id.txtRebateValue);
        inputUnit = findViewById(R.id.inputUnit);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        cardEditForm = findViewById(R.id.cardEditForm);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.months,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);
    }

    private void setupSeekBar() {
        txtRebateValue.setText(getString(R.string.label_selected_rebate, 0));

        seekBarRebate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtRebateValue.setText(getString(R.string.label_selected_rebate, progress));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void loadBillData() {
        currentBill = dbHelper.getBill(billId);

        if (currentBill != null) {
            // Set month in spinner
            String month = currentBill.getMonth();
            if (month != null && !month.isEmpty()) {
                String monthName = extractMonthName(month);
                ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerMonth.getAdapter();
                int position = adapter.getPosition(monthName);
                if (position >= 0) {
                    spinnerMonth.setSelection(position);
                }
            }

            // Set unit
            inputUnit.setText(String.valueOf(currentBill.getUnit()));

            // Set rebate (convert double to int for seekbar)
            int rebateInt = (int) Math.round(currentBill.getRebate());
            seekBarRebate.setProgress(rebateInt);
            txtRebateValue.setText(getString(R.string.label_selected_rebate, rebateInt));
        }
    }

    private String extractMonthName(String monthString) {
        if (monthString.contains(" ")) {
            return monthString.split(" ")[0];
        }
        return monthString;
    }

    private void setupButtonListeners() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBill();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void updateBill() {
        String selectedMonth = spinnerMonth.getSelectedItem().toString();
        String unitInputStr = inputUnit.getText().toString().trim();

        if (unitInputStr.isEmpty()) {
            inputUnit.setError(getString(R.string.error_enter_units));
            showToast(getString(R.string.error_enter_units));
            return;
        }

        try {
            double totalUnit = Double.parseDouble(unitInputStr);
            if (totalUnit <= 0) {
                inputUnit.setError(getString(R.string.error_units_positive));
                showToast(getString(R.string.error_units_positive));
                return;
            }

            int rebatePercentage = seekBarRebate.getProgress();
            double totalCharges = calculateChargesBasedOnTariff(totalUnit);
            double finalCost = totalCharges - (totalCharges * rebatePercentage / 100.0);

            BillModel updatedBill = new BillModel();
            updatedBill.setId(billId);
            updatedBill.setMonth(selectedMonth);
            updatedBill.setUnit(totalUnit);
            updatedBill.setRebate(rebatePercentage);
            updatedBill.setTotalCharges(totalCharges);
            updatedBill.setFinalCost(finalCost);

            boolean isUpdated = dbHelper.updateBill(updatedBill);

            if (isUpdated) {
                Toast.makeText(this, "Bill updated successfully", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to update bill", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            inputUnit.setError(getString(R.string.error_invalid_number));
            showToast(getString(R.string.error_invalid_number));
        }
    }

    private double calculateChargesBasedOnTariff(double unit) {
        if (unit <= 200) return unit * 0.218;
        else if (unit <= 300) return (200 * 0.218) + ((unit - 200) * 0.334);
        else if (unit <= 600) return (200 * 0.218) + (100 * 0.334) + ((unit - 300) * 0.516);
        else if (unit <= 900) return (200 * 0.218) + (100 * 0.334) + (300 * 0.516) + ((unit - 600) * 0.546);
        else return (200 * 0.218) + (100 * 0.334) + (300 * 0.516) + (300 * 0.546) + ((unit - 900) * 0.546);
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}