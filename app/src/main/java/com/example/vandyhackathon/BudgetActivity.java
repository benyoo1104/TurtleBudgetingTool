package com.example.vandyhackathon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.w3c.dom.Text;

import java.sql.DatabaseMetaData;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BudgetActivity extends AppCompatActivity {

    private TextView totalBudgetTextView;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private DatabaseReference budgetRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loader;
    private String post_key = "";
    private String item = "";
    private int amount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        totalBudgetTextView = findViewById(R.id.totalBudgetTextView);
        recyclerView = findViewById(R.id.recyclerView);
        mAuth = FirebaseAuth.getInstance();
        budgetRef = FirebaseDatabase.getInstance().getReference().child("budget").child(mAuth.getCurrentUser().getUid());
        loader = new ProgressDialog(this);
        fab = findViewById(R.id.fab);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalBudget = 0;
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Data data = snap.getValue(Data.class);
                    totalBudget += data.getAmount();
                    String strTotal = String.valueOf("Monthly Budget: $" + totalBudget);
                    totalBudgetTextView.setText(strTotal);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });
    }

    private void addItem() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_layout, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);
        final Spinner itemSpinner = myView.findViewById(R.id.itemSpinner);
        final EditText amount = myView.findViewById(R.id.amount);
        final Button cancel = myView.findViewById(R.id.cancel);
        final Button save = myView.findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(amount.getText().toString())) {
                    amount.setError("Missing Amount");
                } else if (itemSpinner.getSelectedItem().toString().equals("Select a category")) {
                    Toast.makeText(BudgetActivity.this, "Select a category", Toast.LENGTH_SHORT).show();
                } else {
                    loader.setMessage("Adding a budget item");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();
                    String id = budgetRef.push().getKey();
                    DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
                    Calendar cal = Calendar.getInstance();
                    String date = dateFormat.format(cal.getTime());
                    MutableDateTime epoch = new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now = new DateTime();
                    Months months = Months.monthsBetween(epoch, now);
                    Data data = new Data(itemSpinner.getSelectedItem().toString(), date, id, null, Integer.parseInt(amount.getText().toString()), months.getMonths());
                    budgetRef.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(BudgetActivity.this, "Budget Item added Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                            loader.dismiss();
                        }
                    });
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>().setQuery(budgetRef, Data.class).build();
        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Data model) {
                holder.setItemName("Category: " + model.getItem());
                holder.setItemAmount("Amount: $" + model.getAmount());
                holder.setItemDate("On: " + model.getDate());
                holder.note.setVisibility(View.GONE);
                switch (model.getItem()) {
                    case "Charity":
                        holder.imageView.setImageResource(R.drawable.turtle);
                        break;
                    case "Education":
                        holder.imageView.setImageResource(R.drawable.turtle);
                        break;
                    case "Entertainment":
                        holder.imageView.setImageResource(R.drawable.turtle);
                        break;
                    case "Food":
                        holder.imageView.setImageResource(R.drawable.turtle);
                        break;
                    case "Health":
                        holder.imageView.setImageResource(R.drawable.turtle);
                        break;
                    case "Home":
                        holder.imageView.setImageResource(R.drawable.turtle);
                        break;
                    case "Personal":
                        holder.imageView.setImageResource(R.drawable.turtle);
                        break;
                    case "Transportation":
                        holder.imageView.setImageResource(R.drawable.turtle);
                        break;
                    case "Other":
                        holder.imageView.setImageResource(R.drawable.turtle);
                        break;
                }
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        post_key = getRef(position).getKey();
                        item = model.getItem();
                        amount = model.getAmount();
                        updateData();
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_layout, parent, false);
                return new MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
        synchronized (adapter) {
            adapter.notify();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public ImageView imageView;
        public TextView note, date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            imageView = itemView.findViewById(R.id.imageView);
            note = itemView.findViewById(R.id.note);
            date = itemView.findViewById(R.id.date);
        }

        public void setItemName(String itemName) {
            TextView item = mView.findViewById(R.id.item);
            item.setText(itemName);
        }

        public void setItemAmount(String itemAmount) {
            TextView amount = mView.findViewById(R.id.amount);
            amount.setText(itemAmount);
        }

        public void setItemDate(String itemDate) {
            TextView date = mView.findViewById(R.id.date);
            date.setText(itemDate);
        }
    }

    private void updateData() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View mView = inflater.inflate(R.layout.update_layout, null);
        myDialog.setView(mView);
        final AlertDialog dialog = myDialog.create();
        final TextView mItem = mView.findViewById(R.id.itemName);
        final EditText mAmount = mView.findViewById(R.id.amount);
        final EditText mNote = mView.findViewById(R.id.note);
        mNote.setVisibility(View.GONE);
        mItem.setText(item);
        mAmount.setText(String.valueOf(amount));
        mAmount.setSelection(String.valueOf(amount).length());
        Button deleteBtn = mView.findViewById(R.id.delete);
        Button updateBtn = mView.findViewById(R.id.update);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount = Integer.parseInt(mAmount.getText().toString());
                DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
                Calendar cal = Calendar.getInstance();
                String date = dateFormat.format(cal.getTime());
                MutableDateTime epoch = new MutableDateTime();
                epoch.setDate(0);
                DateTime now = new DateTime();
                Months months = Months.monthsBetween(epoch, now);
                Data data = new Data(item, date, post_key, null, amount, months.getMonths());
                budgetRef.child(post_key).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(BudgetActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                budgetRef.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(BudgetActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}