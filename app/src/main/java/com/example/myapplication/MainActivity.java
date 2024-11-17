package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

        private EditText loanAmountEditText, interestRateEditText, loanTermEditText;
        private Button calculateButton;
        private TextView resultTextView;
        private LineChart loanPaymentChart;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);

                // Initialize the UI elements
                loanAmountEditText = findViewById(R.id.loanAmountEditText);
                interestRateEditText = findViewById(R.id.interestRateEditText);
                loanTermEditText = findViewById(R.id.loanTermEditText);
                calculateButton = findViewById(R.id.calculateButton);
                resultTextView = findViewById(R.id.resultTextView);
                loanPaymentChart = findViewById(R.id.loanPaymentChart);

                // Set an OnClickListener for the calculate button
                calculateButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                String loanAmountStr = loanAmountEditText.getText().toString();
                                String interestRateStr = interestRateEditText.getText().toString();
                                String loanTermStr = loanTermEditText.getText().toString();

                                if (!loanAmountStr.isEmpty() && !interestRateStr.isEmpty() && !loanTermStr.isEmpty()) {
                                        double loanAmount = Double.parseDouble(loanAmountStr);
                                        double interestRate = Double.parseDouble(interestRateStr);
                                        int loanTerm = Integer.parseInt(loanTermStr);

                                        // Calculate the monthly payment
                                        double monthlyPayment = calculateMonthlyPayment(loanAmount, interestRate, loanTerm);

                                        // Display the result
                                        resultTextView.setText("Monthly Payment: $" + String.format("%.2f", monthlyPayment));

                                        // Plot the loan payments over time on the graph
                                        plotLoanPayments(loanAmount, interestRate, loanTerm);
                                } else {
                                        resultTextView.setText("Please fill in all fields.");
                                }
                        }
                });
        }

        private double calculateMonthlyPayment(double loanAmount, double interestRate, int loanTerm) {
                double monthlyRate = interestRate / 100 / 12;
                int numberOfPayments = loanTerm * 12;
                double denominator = Math.pow(1 + monthlyRate, numberOfPayments) - 1;
                return (loanAmount * monthlyRate * Math.pow(1 + monthlyRate, numberOfPayments)) / denominator;
        }

        private void plotLoanPayments(double loanAmount, double interestRate, int loanTerm) {
                // Prepare the data for the graph
                ArrayList<Entry> entries = new ArrayList<>();

                double monthlyRate = interestRate / 100 / 12;
                int numberOfPayments = loanTerm * 12;
                double remainingBalance = loanAmount;
                for (int i = 0; i <= numberOfPayments; i++) {
                        double interestPayment = remainingBalance * monthlyRate;
                        double principalPayment = calculateMonthlyPayment(loanAmount, interestRate, loanTerm) - interestPayment;
                        remainingBalance -= principalPayment;

                        // Add the monthly payment to the graph
                        entries.add(new Entry(i, (float) remainingBalance));
                }

                // Create a dataset and customize the graph
                LineDataSet dataSet = new LineDataSet(entries, "Remaining Balance");
                dataSet.setLineWidth(2);
                dataSet.setColor(getResources().getColor(android.R.color.holo_blue_dark));
                dataSet.setValueTextSize(12f);

                // Create the line data object and set it on the chart
                LineData lineData = new LineData(dataSet);
                loanPaymentChart.setData(lineData);
                loanPaymentChart.invalidate(); // Refresh the chart
        }
}
