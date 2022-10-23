package com.example.vandyhackathon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private CardView budgetCardView, monthCardView, analyticsCardView;
    private Toolbar toolbar;
    private TextView budgetTextView;
    private FirebaseAuth mAuth;
    private DatabaseReference budgetRef, expensesRef;
    private String uid = "";
    private int currentBudget = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        budgetCardView = findViewById(R.id.budgetCardView);
        monthCardView = findViewById(R.id.monthCardView);
        analyticsCardView = findViewById(R.id.analyticsCardView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Turtle Budgeting Tool");
        budgetTextView = findViewById(R.id.budgetTextView);

        mAuth = FirebaseAuth.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        budgetRef = FirebaseDatabase.getInstance().getReference("budget").child(uid);
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(uid);

        budgetCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BudgetActivity.class);
                startActivity(intent);
            }
        });

        monthCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SpendingsActivity.class);
                startActivity(intent);
            }
        });

        analyticsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AnalyticsActivity.class);
                startActivity(intent);
            }
        });

        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap: snapshot.getChildren())
                {
                    Data data = snap.getValue(Data.class);
                    currentBudget += data.getAmount();
                }
                budgetTextView.setText("$" + currentBudget);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        expensesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap: snapshot.getChildren())
                {
                    Data data = snap.getValue(Data.class);
                    currentBudget -= data.getAmount();
                }
                budgetTextView.setText("$" + currentBudget);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}