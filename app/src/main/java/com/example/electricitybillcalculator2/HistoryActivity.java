package com.example.electricitybillcalculator2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private ListView listViewHistory;
    private Button btnBackToMain;
    private DatabaseHelper dbHelper;
    private List<BillModel> billList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listViewHistory = findViewById(R.id.listViewHistory);
        btnBackToMain = findViewById(R.id.btnBackToMain);
        dbHelper = new DatabaseHelper(this);

        loadHistoryFromDatabase();
        setupButtonListeners();
    }

    private void loadHistoryFromDatabase() {
        billList = dbHelper.getAllBills();

        if (billList.isEmpty()) {
            String[] emptyList = {getString(R.string.no_history)};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_list_item_1, emptyList
            );
            listViewHistory.setAdapter(adapter);
            return;
        }

        String[] displayItems = new String[billList.size()];
        for (int i = 0; i < billList.size(); i++) {
            BillModel bill = billList.get(i);
            displayItems[i] = String.format(getString(R.string.history_item_format),
                    bill.getMonth(), bill.getFinalCost());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, displayItems
        );
        listViewHistory.setAdapter(adapter);

        listViewHistory.setOnItemClickListener((parent, view, position, id) -> {
            if (!billList.isEmpty()) {
                BillModel selectedBill = billList.get(position);
                openDetailPage(selectedBill.getId());
            }
        });
    }

    private void openDetailPage(int billId) {
        Intent i = new Intent(HistoryActivity.this, DetailActivity.class);
        i.putExtra("BILL_ID", billId);
        startActivity(i);
    }

    private void setupButtonListeners() {
        btnBackToMain.setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
