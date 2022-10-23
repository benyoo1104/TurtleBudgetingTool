package com.example.vandyhackathon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AnalyticsActivity extends AppCompatActivity {

    private TextView totalSpentMonth;
    private Button viewChart;
    private FirebaseAuth mAuth;
    private DatabaseReference expensesRef;
    private String uid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        totalSpentMonth = findViewById(R.id.totalSpentMonth);
        viewChart = findViewById(R.id.viewChart);

        mAuth = FirebaseAuth.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(uid);

        expensesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int spendings = 0;
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Data data = snap.getValue(Data.class);
                    spendings += data.getAmount();
                }
                totalSpentMonth.setText("Total Amount Spent: $" + spendings);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        viewChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AnalyticsActivity.this, ChartActivity.class);
                startActivity(intent);
            }
        });
    }
}