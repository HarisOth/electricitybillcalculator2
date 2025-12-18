package com.example.electricitybillcalculator2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private ListView listViewHistory;
    private Button btnBackToMain;
    private DatabaseHelper dbHelper;
    private List<BillModel> billList;
    private TextView txtEmptyHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listViewHistory = findViewById(R.id.listViewHistory);
        btnBackToMain = findViewById(R.id.btnBackToMain);
        txtEmptyHistory = findViewById(R.id.txtEmptyHistory);
        dbHelper = new DatabaseHelper(this);

        loadHistoryFromDatabase();
        setupButtonListeners();
    }

    private void loadHistoryFromDatabase() {
        billList = dbHelper.getAllBills();

        if (billList.isEmpty()) {
            // Show empty state message
            listViewHistory.setVisibility(View.GONE);
            if (txtEmptyHistory != null) {
                txtEmptyHistory.setVisibility(View.VISIBLE);
                txtEmptyHistory.setText(getString(R.string.no_history));
            }
            return;
        }

        // Hide empty state message
        listViewHistory.setVisibility(View.VISIBLE);
        if (txtEmptyHistory != null) {
            txtEmptyHistory.setVisibility(View.GONE);
        }

        // Create array of strings to display
        String[] displayItems = new String[billList.size()];
        for (int i = 0; i < billList.size(); i++) {
            BillModel bill = billList.get(i);
            displayItems[i] = String.format("%s - RM %.2f",
                    bill.getMonth(),
                    bill.getFinalCost());
        }

        // Use simple ArrayAdapter with default layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                displayItems
        );

        listViewHistory.setAdapter(adapter);

        listViewHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BillModel selectedBill = billList.get(position);
                openDetailPage(selectedBill.getId());
            }
        });
    }

    private void openDetailPage(int billId) {
        Intent i = new Intent(HistoryActivity.this, DetailActivity.class);
        i.putExtra("BILL_ID", billId);
        startActivityForResult(i, 100);
    }

    private void setupButtonListeners() {
        btnBackToMain.setOnClickListener(v -> finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Refresh the list when returning from DetailActivity after edit/delete
            loadHistoryFromDatabase();
            Toast.makeText(this, "List refreshed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when activity resumes
        loadHistoryFromDatabase();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}