package com.example.accountingsystem;

import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class Transaction {
    String date;
    String debitAccount;
    double debitAmount;
    String creditAccount;
    double creditAmount;
    String description;

    public Transaction(String date, String drAcc, double drAmt, String crAcc, double crAmt, String desc) {
        this.date = date;
        this.debitAccount = drAcc;
        this.debitAmount = drAmt;
        this.creditAccount = crAcc;
        this.creditAmount = crAmt;
        this.description = desc;
    }
}

public class MainActivity extends AppCompatActivity {

    private List<Transaction> transactions = new ArrayList<>();
    private Map<String, String> accountTypes = new LinkedHashMap<>();

    private EditText etDate, etDescription, etAmount;
    private Spinner spDebitAccount, spCreditAccount, spLedgerAccount;
    
    private TableLayout tableTransactions, tableAccounts, tableJournal, tableLedger, tableAssets, tableLiabilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeAccounts();
        initializeDefaultTransactions();

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 24, 24, 24);
        root.setBackgroundColor(0xFF121826);

        // Header Title
        TextView tvTitle = new TextView(this);
        tvTitle.setText("Financial Dashboard");
        tvTitle.setTextSize(22);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setTextColor(0xFFF0F4F8);

        TextView tvSubtitle = new TextView(this);
        tvSubtitle.setText("Ledger & Financial Reporting Workspace");
        tvSubtitle.setTextSize(13);
        tvSubtitle.setTextColor(0xFF94A3B8);
        tvSubtitle.setPadding(0, 0, 0, 24);

        root.addView(tvTitle);
        root.addView(tvSubtitle);

        ScrollView scrollView = new ScrollView(this);
        LinearLayout mainContent = new LinearLayout(this);
        mainContent.setOrientation(LinearLayout.VERTICAL);

        mainContent.addView(createSectionTitle("New Entry"));
        mainContent.addView(createNewTransactionView());

        mainContent.addView(createSectionTitle("Transactions"));
        tableTransactions = createStyledTable();
        mainContent.addView(tableTransactions);

        mainContent.addView(createSectionTitle("Chart of Accounts"));
        tableAccounts = createStyledTable();
        mainContent.addView(tableAccounts);

        mainContent.addView(createSectionTitle("General Journal"));
        tableJournal = createStyledTable();
        mainContent.addView(tableJournal);

        mainContent.addView(createSectionTitle("General Ledger"));
        mainContent.addView(createLedgerView());

        mainContent.addView(createSectionTitle("Balance Sheet - Assets"));
        tableAssets = createStyledTable();
        mainContent.addView(tableAssets);

        mainContent.addView(createSectionTitle("Balance Sheet - Liabilities & Equity"));
        tableLiabilities = createStyledTable();
        mainContent.addView(tableLiabilities);

        scrollView.addView(mainContent);
        root.addView(scrollView);

        setContentView(root);

        refreshAllData();
    }

    private TextView createSectionTitle(String title) {
        TextView tv = new TextView(this);
        tv.setText(title);
        tv.setTextSize(16);
        tv.setTypeface(null, android.graphics.Typeface.BOLD);
        tv.setTextColor(0xFF06B6D4);
        tv.setPadding(0, 32, 0, 12);
        return tv;
    }

    private LinearLayout createNewTransactionView() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundColor(0xFF222C44);
        card.setPadding(24, 24, 24, 24);

        String[] accountNames = accountTypes.keySet().toArray(new String[0]);
        Arrays.sort(accountNames);

        etDate = createStyledEditText("2025-10-08");
        etDescription = createStyledEditText("");
        spDebitAccount = createStyledSpinner(accountNames);
        spCreditAccount = createStyledSpinner(accountNames);
        etAmount = createStyledEditText("");
        etAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        card.addView(createFormRow("Date (YYYY-MM-DD)", etDate));
        card.addView(createFormRow("Description", etDescription));
        card.addView(createFormRow("Debit Account", spDebitAccount));
        card.addView(createFormRow("Credit Account", spCreditAccount));
        card.addView(createFormRow("Amount", etAmount));

        Button btnAdd = new Button(this);
        btnAdd.setText("Post Entry");
        btnAdd.setTextColor(0xFFFFFFFF);
        btnAdd.setBackgroundColor(0xFF3B82F6);
        btnAdd.setOnClickListener(v -> addTransaction());

        card.addView(btnAdd);
        return card;
    }

    private LinearLayout createFormRow(String labelText, View input) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(0, 0, 0, 16);

        TextView label = new TextView(this);
        label.setText(labelText);
        label.setTextColor(0xFF94A3B8);
        label.setTextSize(13);
        label.setTypeface(null, android.graphics.Typeface.BOLD);

        row.addView(label);
        row.addView(input);
        return row;
    }

    private EditText createStyledEditText(String text) {
        EditText et = new EditText(this);
        et.setText(text);
        et.setTextSize(13);
        et.setTextColor(0xFFF0F4F8);
        et.setBackgroundColor(0xFF1A2234);
        et.setPadding(16, 16, 16, 16);
        return et;
    }

    private Spinner createStyledSpinner(String[] items) {
        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setBackgroundColor(0xFF1A2234);
        return spinner;
    }

    private TableLayout createStyledTable() {
        TableLayout table = new TableLayout(this);
        table.setBackgroundColor(0xFF334155);
        return table;
    }

    private LinearLayout createLedgerView() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        String[] accountNames = accountTypes.keySet().toArray(new String[0]);
        Arrays.sort(accountNames);

        spLedgerAccount = createStyledSpinner(accountNames);
        spLedgerAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshLedgerTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        layout.addView(createFormRow("Filter Account:", spLedgerAccount));

        tableLedger = createStyledTable();
        layout.addView(tableLedger);

        return layout;
    }

    private void addTransaction() {
        try {
            String date = etDate.getText().toString().trim();
            String desc = etDescription.getText().toString().trim();
            String drAcc = (String) spDebitAccount.getSelectedItem();
            String crAcc = (String) spCreditAccount.getSelectedItem();
            double amount = Double.parseDouble(etAmount.getText().toString().trim());

            if (date.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, "Please complete all fields before submitting.", Toast.LENGTH_SHORT).show();
                return;
            }

            transactions.add(new Transaction(date, drAcc, amount, crAcc, amount, desc));
            refreshAllData();
            Toast.makeText(this, "Journal entry successfully posted!", Toast.LENGTH_SHORT).show();
            etDescription.setText("");
            etAmount.setText("");
        } catch (NumberFormatException ex) {
            Toast.makeText(this, "Please enter a valid numeric amount.", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshAllData() {
        refreshTransactionsTable();
        refreshAccountsTable();
        refreshJournalTable();
        refreshLedgerTable();
        refreshBalanceSheetTable();
    }

    private void populateTable(TableLayout table, String[] headers, List<String[]> data) {
        table.removeAllViews();

        TableRow headerRow = new TableRow(this);
        headerRow.setBackgroundColor(0xFF1E293B);
        for (String header : headers) {
            TextView tv = new TextView(this);
            tv.setText(header);
            tv.setTextColor(0xFF94A3B8);
            tv.setTypeface(null, android.graphics.Typeface.BOLD);
            tv.setPadding(16, 16, 16, 16);
            headerRow.addView(tv);
        }
        table.addView(headerRow);

        for (int i = 0; i < data.size(); i++) {
            TableRow row = new TableRow(this);
            row.setBackgroundColor(i % 2 == 0 ? 0xFF222C44 : 0xFF1E293B);
            for (String cell : data.get(i)) {
                TextView tv = new TextView(this);
                tv.setText(cell);
                tv.setTextColor(0xFFF0F4F8);
                tv.setPadding(16, 16, 16, 16);
                row.addView(tv);
            }
            table.addView(row);
        }
    }

    private void refreshTransactionsTable() {
        String[] columns = {"Date", "Description", "Debit Account", "Credit Account", "Amount"};
        List<String[]> rows = new ArrayList<>();
        for (Transaction t : transactions) {
            rows.add(new String[]{t.date, t.description, t.debitAccount, t.creditAccount, String.format("%,.2f", t.debitAmount)});
        }
        populateTable(tableTransactions, columns, rows);
    }

    private void refreshAccountsTable() {
        String[] columns = {"Account Name", "Category / Type", "Current Balance"};
        List<String[]> rows = new ArrayList<>();
        Map<String, Double> balances = calculateAccountBalances();
        for (Map.Entry<String, String> entry : accountTypes.entrySet()) {
            String acc = entry.getKey();
            String type = entry.getValue();
            double bal = balances.getOrDefault(acc, 0.0);
            if (bal != 0.0 || type.equals("ASSET") || type.equals("LIABILITY") || type.equals("EQUITY")) {
                rows.add(new String[]{acc, type, String.format("%,.2f", bal)});
            }
        }
        populateTable(tableAccounts, columns, rows);
    }

    private void refreshJournalTable() {
        String[] columns = {"Date", "Description / Details", "Account Title", "Debit", "Credit"};
        List<String[]> rows = new ArrayList<>();
        for (Transaction t : transactions) {
            rows.add(new String[]{t.date, t.description, t.debitAccount, String.format("%,.2f", t.debitAmount), ""});
            rows.add(new String[]{"", "", "   ↪ " + t.creditAccount, "", String.format("%,.2f", t.creditAmount)});
        }
        populateTable(tableJournal, columns, rows);
    }

    private void refreshLedgerTable() {
        if (spLedgerAccount == null) return;
        String selectedAcc = (String) spLedgerAccount.getSelectedItem();
        if (selectedAcc == null) return;

        String[] columns = {"Date", "Description", "Debit Account", "Credit Account", "Amount", "Running Balance"};
        List<String[]> rows = new ArrayList<>();

        double balance = 0;
        String type = accountTypes.getOrDefault(selectedAcc, "ASSET");

        for (Transaction t : transactions) {
            if (t.debitAccount.equals(selectedAcc) || t.creditAccount.equals(selectedAcc)) {
                if (type.equals("ASSET") || type.equals("EXPENSE")) {
                    if (t.debitAccount.equals(selectedAcc)) balance += t.debitAmount;
                    if (t.creditAccount.equals(selectedAcc)) balance -= t.creditAmount;
                } else {
                    if (t.creditAccount.equals(selectedAcc)) balance += t.creditAmount;
                    if (t.debitAccount.equals(selectedAcc)) balance -= t.debitAmount;
                }
                rows.add(new String[]{
                        t.date, t.description, t.debitAccount, t.creditAccount,
                        String.format("%,.2f", t.debitAccount.equals(selectedAcc) ? t.debitAmount : t.creditAmount),
                        String.format("%,.2f", balance)
                });
            }
        }
        populateTable(tableLedger, columns, rows);
    }

    private void refreshBalanceSheetTable() {
        String[] colsAssets = {"Asset Accounts", "Amount"};
        String[] colsLiab = {"Liabilities & Equity", "Amount"};
        List<String[]> rowsAssets = new ArrayList<>();
        List<String[]> rowsLiab = new ArrayList<>();

        Map<String, Double> balances = calculateAccountBalances();
        double totalAssets = 0;
        double totalLiabilities = 0;
        double totalEquity = 0;

        for (Map.Entry<String, String> entry : accountTypes.entrySet()) {
            String acc = entry.getKey();
            String type = entry.getValue();
            double bal = balances.getOrDefault(acc, 0.0);

            if (type.equals("ASSET")) {
                rowsAssets.add(new String[]{acc, String.format("%,.2f", bal)});
                totalAssets += bal;
            } else if (type.equals("LIABILITY")) {
                rowsLiab.add(new String[]{acc, String.format("%,.2f", bal)});
                totalLiabilities += bal;
            } else if (type.equals("EQUITY")) {
                rowsLiab.add(new String[]{acc, String.format("%,.2f", bal)});
                totalEquity += bal;
            }
        }

        rowsAssets.add(new String[]{"TOTAL ASSETS", String.format("%,.2f", totalAssets)});
        rowsLiab.add(new String[]{"TOTAL LIABILITIES & EQUITY", String.format("%,.2f", totalLiabilities + totalEquity)});

        populateTable(tableAssets, colsAssets, rowsAssets);
        populateTable(tableLiabilities, colsLiab, rowsLiab);
    }

    private Map<String, Double> calculateAccountBalances() {
        Map<String, Double> balances = new HashMap<>();
        for (Transaction t : transactions) {
            balances.put(t.debitAccount, balances.getOrDefault(t.debitAccount, 0.0) + t.debitAmount);
            balances.put(t.creditAccount, balances.getOrDefault(t.creditAccount, 0.0) - t.creditAmount);
        }

        Map<String, Double> normalBalances = new HashMap<>();
        for (String acc : accountTypes.keySet()) {
            double raw = balances.getOrDefault(acc, 0.0);
            String type = accountTypes.get(acc);
            if (type.equals("LIABILITY") || type.equals("INCOME") || type.equals("EQUITY")) {
                normalBalances.put(acc, -raw);
            } else {
                normalBalances.put(acc, raw);
            }
        }
        return normalBalances;
    }

    private void initializeAccounts() {
        accountTypes.put("Cash", "ASSET");
        accountTypes.put("Accounts Receivable", "ASSET");
        accountTypes.put("Prepaid Insurance", "ASSET");
        accountTypes.put("Prepaid Rent", "ASSET");
        accountTypes.put("Supplies", "ASSET");
        accountTypes.put("Equipment", "ASSET");
        accountTypes.put("Vehicles", "ASSET");
        accountTypes.put("Furniture", "ASSET");
        accountTypes.put("Accumulated Depreciation - Equipment", "ASSET");

        accountTypes.put("Accounts Payable", "LIABILITY");
        accountTypes.put("Notes Payable", "LIABILITY");
        accountTypes.put("Salaries Payable", "LIABILITY");
        accountTypes.put("Unearned Revenue", "LIABILITY");

        accountTypes.put("Owner's Capital", "EQUITY");
        accountTypes.put("Owner's Drawings", "EQUITY");

        accountTypes.put("Service Revenue", "INCOME");

        accountTypes.put("Advertising Expense", "EXPENSE");
        accountTypes.put("Utilities Expense", "EXPENSE");
        accountTypes.put("Salaries Expense", "EXPENSE");
        accountTypes.put("Maintenance Expense", "EXPENSE");
        accountTypes.put("Repairs Expense", "EXPENSE");
        accountTypes.put("Rent Expense", "EXPENSE");
        accountTypes.put("Interest Expense", "EXPENSE");
        accountTypes.put("Insurance Expense", "EXPENSE");
        accountTypes.put("Supplies Expense", "EXPENSE");
        accountTypes.put("Depreciation Expense", "EXPENSE");
        accountTypes.put("Professional Fees Expense", "EXPENSE");
    }

    private void initializeDefaultTransactions() {
        transactions.add(new Transaction("Jan 01", "Cash", 50000, "Owner's Capital", 50000, "Initial cash investment"));
        transactions.add(new Transaction("Jan 02", "Prepaid Insurance", 2400, "Cash", 2400, "Paid 1-year prepaid insurance"));
        transactions.add(new Transaction("Jan 03", "Prepaid Rent", 3000, "Cash", 3000, "Paid 3 months advance rent"));
        transactions.add(new Transaction("Jan 04", "Equipment", 15000, "Accounts Payable", 15000, "Purchased equipment on account"));
        transactions.add(new Transaction("Jan 05", "Supplies", 1200, "Cash", 1200, "Purchased office supplies"));
        transactions.add(new Transaction("Jan 06", "Cash", 4500, "Service Revenue", 4500, "Rendered services for cash"));
        transactions.add(new Transaction("Jan 07", "Accounts Receivable", 6200, "Service Revenue", 6200, "Billed client for services"));
        transactions.add(new Transaction("Jan 08", "Advertising Expense", 1500, "Cash", 1500, "Paid online advertising"));
        transactions.add(new Transaction("Jan 09", "Cash", 2000, "Unearned Revenue", 2000, "Received client advance"));
        transactions.add(new Transaction("Jan 10", "Accounts Payable", 5000, "Cash", 5000, "Paid accounts payable"));
        transactions.add(new Transaction("Jan 11", "Cash", 3000, "Accounts Receivable", 3000, "Collected client receivables"));
        transactions.add(new Transaction("Jan 12", "Supplies", 800, "Accounts Payable", 800, "Purchased supplies on credit"));
        transactions.add(new Transaction("Jan 13", "Utilities Expense", 650, "Cash", 650, "Paid electricity and water"));
        transactions.add(new Transaction("Jan 14", "Salaries Expense", 3200, "Cash", 3200, "Paid bi-weekly salaries"));
        transactions.add(new Transaction("Jan 15", "Owner's Drawings", 2000, "Cash", 2000, "Owner withdrawal"));
        transactions.add(new Transaction("Jan 16", "Accounts Receivable", 5800, "Service Revenue", 5800, "Billed customer for services"));
        transactions.add(new Transaction("Jan 17", "Cash", 20000, "Notes Payable", 20000, "Bank loan note payable"));
        transactions.add(new Transaction("Jan 18", "Vehicles", 18000, "Cash", 18000, "Purchased delivery vehicle"));
        transactions.add(new Transaction("Jan 19", "Maintenance Expense", 400, "Cash", 400, "Vehicle maintenance and fuel"));
        transactions.add(new Transaction("Jan 20", "Cash", 4000, "Accounts Receivable", 4000, "Collected receivables"));
        transactions.add(new Transaction("Jan 21", "Cash", 3500, "Service Revenue", 3500, "Instant repair services"));
        transactions.add(new Transaction("Jan 22", "Unearned Revenue", 1000, "Service Revenue", 1000, "Recognized revenue from advance"));
        transactions.add(new Transaction("Jan 23", "Utilities Expense", 600, "Cash", 600, "Paid internet and phone"));
        transactions.add(new Transaction("Jan 24", "Accounts Payable", 4000, "Cash", 4000, "Partial payment for equipment"));
        transactions.add(new Transaction("Jan 25", "Professional Fees Expense", 1200, "Accounts Payable", 1200, "Legal services invoice"));
        transactions.add(new Transaction("Jan 26", "Accounts Receivable", 7500, "Service Revenue", 7500, "Billed corporate client"));
        transactions.add(new Transaction("Jan 27", "Cash", 5000, "Accounts Receivable", 5000, "Collected partial payment"));
        transactions.add(new Transaction("Jan 28", "Salaries Expense", 3200, "Cash", 3200, "Paid bi-weekly salaries"));
        transactions.add(new Transaction("Jan 29", "Repairs Expense", 500, "Cash", 500, "Paid office repairs"));
        transactions.add(new Transaction("Jan 30", "Owner's Drawings", 1500, "Cash", 1500, "Owner withdrawal"));
        transactions.add(new Transaction("Jan 31", "Furniture", 3500, "Accounts Payable", 3500, "Purchased furniture on credit"));

        transactions.add(new Transaction("Feb 01", "Rent Expense", 1000, "Cash", 1000, "Paid warehouse rent"));
        transactions.add(new Transaction("Feb 02", "Accounts Receivable", 4200, "Service Revenue", 4200, "Billed client for project"));
        transactions.add(new Transaction("Feb 03", "Accounts Payable", 1200, "Cash", 1200, "Paid legal fees invoice"));
        transactions.add(new Transaction("Feb 05", "Cash", 3500, "Accounts Receivable", 3500, "Collected receivables"));
        transactions.add(new Transaction("Feb 07", "Advertising Expense", 800, "Cash", 800, "Paid radio marketing"));
        transactions.add(new Transaction("Feb 09", "Cash", 3000, "Unearned Revenue", 3000, "Client retainer advance"));
        transactions.add(new Transaction("Feb 12", "Supplies", 600, "Cash", 600, "Purchased stationery"));
        transactions.add(new Transaction("Feb 14", "Salaries Expense", 3200, "Cash", 3200, "Paid bi-weekly salaries"));
        transactions.add(new Transaction("Feb 16", "Cash", 5100, "Service Revenue", 5100, "Design services for cash"));
        transactions.add(new Transaction("Feb 18", "Interest Expense", 200, "Cash", 200, "Paid interest expense"));
        transactions.add(new Transaction("Feb 20", "Cash", 2500, "Accounts Receivable", 2500, "Collected receivables"));
        transactions.add(new Transaction("Feb 22", "Cash", 10000, "Owner's Capital", 10000, "Additional capital investment"));
        transactions.add(new Transaction("Feb 25", "Accounts Payable", 2000, "Cash", 2000, "Paid suppliers"));
        transactions.add(new Transaction("Feb 28", "Salaries Expense", 3200, "Cash", 3200, "Paid bi-weekly salaries"));

        transactions.add(new Transaction("Feb 28 (adj)", "Insurance Expense", 400, "Prepaid Insurance", 400, "Expired insurance"));
        transactions.add(new Transaction("Feb 28 (adj)", "Rent Expense", 2000, "Prepaid Rent", 2000, "Expired rent"));
        transactions.add(new Transaction("Feb 28 (adj)", "Supplies Expense", 2100, "Supplies", 2100, "Supplies consumed"));
        transactions.add(new Transaction("Feb 28 (adj)", "Depreciation Expense", 300, "Accumulated Depreciation - Equipment", 300, "Equipment depreciation"));
        transactions.add(new Transaction("Feb 28 (adj)", "Salaries Expense", 1600, "Salaries Payable", 1600, "Accrued unpaid salaries"));
    }
}

