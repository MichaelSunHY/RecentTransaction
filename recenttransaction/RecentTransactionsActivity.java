package com.example.recenttransaction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.example.recenttransaction.TransactionAdapter;
import com.example.recenttransaction.Transaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecentTransactionsActivity extends AppCompatActivity {

    private Button buttonHome, buttonApplyFilters;
    private EditText editTextNameFilter, editTextFromDate, editTextToDate;
    private Spinner spinnerCategory;
    private RecyclerView recyclerViewTransactions;

    private TransactionAdapter adapter;
    private List<Transaction> allTransactions; // Typically fetched from DB
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_transactions);

        // 1) Link UI elements
        buttonHome = findViewById(R.id.button_home);
        buttonApplyFilters = findViewById(R.id.button_applyFilters);
        editTextNameFilter = findViewById(R.id.editText_nameFilter);
        editTextFromDate = findViewById(R.id.editText_fromDate);
        editTextToDate = findViewById(R.id.editText_toDate);
        spinnerCategory = findViewById(R.id.spinner_category);
        recyclerViewTransactions = findViewById(R.id.recyclerView_transactions);

        // 2) Setup RecyclerView
        recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(this));
        // For demo, let's load a static list. In a real app, fetch from DB.
        allTransactions = getDummyTransactions();
        adapter = new TransactionAdapter(allTransactions);
        recyclerViewTransactions.setAdapter(adapter);

        // 3) Setup Category Spinner (demo categories)
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                new String[]{"All", "Food", "Transport", "Salary", "Shopping"});
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // 4) DatePicker on fromDate / toDate
        editTextFromDate.setOnClickListener(v -> showDatePickerDialog(editTextFromDate));
        editTextToDate.setOnClickListener(v -> showDatePickerDialog(editTextToDate));

        // 5) Apply Filters button
        buttonApplyFilters.setOnClickListener(v -> applyFilters());

        // 6) Home button
        buttonHome.setOnClickListener(v -> {
            // Return to main dashboard (MainActivity or another)
            Intent homeIntent = new Intent(RecentTransactionsActivity.this, MainActivity.class);
            startActivity(homeIntent);
            finish();
        });
    }

    private void showDatePickerDialog(EditText targetEditText) {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, day) -> {
            String dateStr = String.format("%04d-%02d-%02d", year, (month + 1), day);
            targetEditText.setText(dateStr);
        };

        DatePickerDialog dialog = new DatePickerDialog(
                this, dateSetListener, 2023, 0, 1);
        dialog.show();
    }

    private void applyFilters() {
        String nameFilter = editTextNameFilter.getText().toString().trim();
        String fromDateStr = editTextFromDate.getText().toString().trim();
        String toDateStr = editTextToDate.getText().toString().trim();
        String categoryFilter = spinnerCategory.getSelectedItem().toString();

        // Convert date strings to long (millis) for comparison
        long fromMillis = 0, toMillis = Long.MAX_VALUE;
        try {
            if (!fromDateStr.isEmpty()) {
                fromMillis = dateFormat.parse(fromDateStr).getTime();
            }
            if (!toDateStr.isEmpty()) {
                toMillis = dateFormat.parse(toDateStr).getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Filter the allTransactions list
        List<Transaction> filteredList = new ArrayList<>();
        for (Transaction tx : allTransactions) {
            // 1) Name filter
            if (!nameFilter.isEmpty() && !tx.getName().toLowerCase().contains(nameFilter.toLowerCase())) {
                continue;
            }
            // 2) Category filter
            if (!categoryFilter.equals("All") && !tx.getCategory().equals(categoryFilter)) {
                continue;
            }
            // 3) Date range filter
            long txDate = tx.getDate();
            if (txDate < fromMillis || txDate > toMillis) {
                continue;
            }

            filteredList.add(tx);
        }

        // Update the adapter with the filtered list
        adapter.setTransactions(filteredList);
    }

    // Demo data. Replace with actual DB query.
    private List<Transaction> getDummyTransactions() {
        List<Transaction> dummy = new ArrayList<>();
        try {
            dummy.add(new Transaction(1, "Groceries", "Food", dateFormat.parse("2023-01-02").getTime(), -50.0));
            dummy.add(new Transaction(2, "Bus Ticket", "Transport", dateFormat.parse("2023-01-03").getTime(), -2.75));
            dummy.add(new Transaction(3, "Salary", "Salary", dateFormat.parse("2023-01-05").getTime(), 2000.0));
            dummy.add(new Transaction(4, "Shopping", "Shopping", dateFormat.parse("2023-02-01").getTime(), -120.0));
            dummy.add(new Transaction(5, "Lunch", "Food", dateFormat.parse("2023-02-03").getTime(), -15.0));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dummy;
    }
}
