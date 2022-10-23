package com.example.vandyhackathon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class SpendingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView amountSpent;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ProgressDialog loader;
    private FirebaseAuth mAuth;
    private String uid = "";
    private DatabaseReference expensesRef;
    private SpendingsAdapter spendingsAdapter;
    private List<Data> myDataList;
    private String post_key = "";
    private String item = "";
    private String note = "";
    private int amount = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spendings);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Spendings this Month");
        amountSpent = findViewById(R.id.amountSpent);
        progressBar = findViewById(R.id.progressBar);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemSpentOn();
            }
        });
        loader = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(uid);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        myDataList = new ArrayList<>();
        spendingsAdapter = new SpendingsAdapter(SpendingsActivity.this, myDataList);
        recyclerView.setAdapter(spendingsAdapter);
        readItems();
    }

    private void readItems() {
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(uid);
        Query query = reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDataList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    Data data = snapshot.getValue(Data.class);
                    myDataList.add(data);
                }
                spendingsAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                int totalAmount = 0;
                for (DataSnapshot dss: snapshot.getChildren())
                {
                    Map<String, Object> map = (Map<String, Object>)dss.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;
                    amountSpent.setText("Month's total spending: $" + totalAmount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>().setQuery(expensesRef, Data.class).build();
        FirebaseRecyclerAdapter<Data, SpendingsActivity.MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, SpendingsActivity.MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SpendingsActivity.MyViewHolder holder, int position, @NonNull Data model) {
                holder.setItemName("Category: " + model.getItem());
                holder.setItemAmount("Amount: $" + model.getAmount());
                holder.setItemDate("On: " + model.getDate());
                holder.setItemNote("Note: " + model.getNote());
                switch (model.getItem())
                {
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
                        note = model.getNote();
                        updateData();
                    }
                });
            }

            @NonNull
            @Override
            public SpendingsActivity.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_layout, parent, false);
                return new SpendingsActivity.MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
        synchronized (adapter)
        {
            adapter.notify();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        public ImageView imageView;
        public TextView note, date;
        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            mView = itemView;
            imageView = itemView.findViewById(R.id.imageView);
            note = itemView.findViewById(R.id.note);
            date = itemView.findViewById(R.id.date);
        }
        public void setItemName(String itemName)
        {
            TextView item = mView.findViewById(R.id.item);
            item.setText(itemName);
        }
        public void setItemAmount(String itemAmount)
        {
            TextView amount = mView.findViewById(R.id.amount);
            amount.setText(itemAmount);
        }
        public void setItemDate(String itemDate)
        {
            TextView date = mView.findViewById(R.id.date);
            date.setText(itemDate);
        }
        public void setItemNote(String itemNote)
        {
            TextView note = mView.findViewById(R.id.note);
            note.setText(itemNote);
        }
    }

    private void addItemSpentOn()
    {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_layout, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);
        final Spinner itemSpinner = myView.findViewById(R.id.itemSpinner);
        final EditText amount = myView.findViewById(R.id.amount);
        final EditText note = myView.findViewById(R.id.note);
        final Button cancel = myView.findViewById(R.id.cancel);
        final Button save = myView.findViewById(R.id.save);

        note.setVisibility(View.VISIBLE);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(amount.getText().toString()))
                {
                    amount.setError("Missing Amount");
                }
                else if (itemSpinner.getSelectedItem().toString().equals("Select a category"))
                {
                    Toast.makeText(SpendingsActivity.this, "Select a category", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(note.getText().toString()))
                {
                    note.setError("Missing Note");
                }
                else
                {
                    loader.setMessage("Adding a budget item");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();
                    String id = expensesRef.push().getKey();
                    DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
                    Calendar cal = Calendar.getInstance();
                    String date = dateFormat.format(cal.getTime());
                    MutableDateTime epoch = new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now = new DateTime();
                    Months months = Months.monthsBetween(epoch, now);
                    Data data = new Data(itemSpinner.getSelectedItem().toString(), date, id, note.getText().toString(), Integer.parseInt(amount.getText().toString()), months.getMonths());
                    expensesRef.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(SpendingsActivity.this, "Budget Item added Successfully", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(SpendingsActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
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

    private void updateData()
    {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View mView = inflater.inflate(R.layout.update_layout, null);
        myDialog.setView(mView);
        final AlertDialog dialog = myDialog.create();
        final TextView mItem = mView.findViewById(R.id.itemName);
        final EditText mAmount = mView.findViewById(R.id.amount);
        final EditText mNote = mView.findViewById(R.id.note);
        mItem.setText(item);
        mAmount.setText(String.valueOf(amount));
        mNote.setText(note);
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
                Data data = new Data(item, date, post_key, mNote.getText().toString(), amount, months.getMonths());
                expensesRef.child(post_key).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(SpendingsActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(SpendingsActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expensesRef.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(SpendingsActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(SpendingsActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}