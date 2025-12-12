package com.example.electricitybillcalculator2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerMonth;
    private SeekBar seekBarRebate;
    private TextView txtRebateValue, txtMonthResult, txtChargesResult, txtRebateResult, txtFinalResult;
    private EditText inputUnit;
    private Button btnCalculate, btnHistory, btnAbout;
    private LinearLayout layoutResult;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        initializeViews();
        setupSpinner();
        setupSeekBar();
        setupButtonListeners();
    }

    private void initializeViews() {
        spinnerMonth = findViewById(R.id.spinnerMonth);
        seekBarRebate = findViewById(R.id.seekBarRebate);
        txtRebateValue = findViewById(R.id.txtRebateValue);
        inputUnit = findViewById(R.id.inputUnit);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnHistory = findViewById(R.id.btnHistory);
        btnAbout = findViewById(R.id.btnAbout);
        layoutResult = findViewById(R.id.layoutResult);
        txtMonthResult = findViewById(R.id.txtMonthResult);
        txtChargesResult = findViewById(R.id.txtChargesResult);
        txtRebateResult = findViewById(R.id.txtRebateResult);
        txtFinalResult = findViewById(R.id.txtFinalResult);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.months,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);
        spinnerMonth.setSelection(0);
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

    private void setupButtonListeners() {
        btnCalculate.setOnClickListener(v -> calculateBill());
        btnHistory.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));
        btnAbout.setOnClickListener(v -> startActivity(new Intent(this, AboutActivity.class)));
    }

    private void calculateBill() {
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

            displayResults(selectedMonth, totalCharges, rebatePercentage, finalCost);
            saveToDatabase(selectedMonth, totalUnit, totalCharges, rebatePercentage, finalCost);

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

    private void displayResults(String month, double totalCharges, int rebate, double finalCost) {
        txtMonthResult.setText(getString(R.string.label_month) + " " + month);
        txtChargesResult.setText(String.format(getString(R.string.label_total_charges), totalCharges));
        txtRebateResult.setText(String.format(getString(R.string.label_rebate), rebate));
        txtFinalResult.setText(String.format(getString(R.string.label_final_cost), finalCost));
        layoutResult.setVisibility(View.VISIBLE);
    }

    private void saveToDatabase(String month, double unit, double totalCharges, int rebate, double finalCost) {
        BillModel bill = new BillModel(month, unit, totalCharges, rebate, finalCost);
        long id = dbHelper.addBill(bill);

        if (id != -1) {
            showToast(getString(R.string.success_saved));
        } else {
            showToast(getString(R.string.error_save_failed, "Database error"));
        }
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
