package com.example.expense;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expense.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IncomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IncomeFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;

    private RecyclerView recyclerView;

    private TextView incomeTotalSum;

    private EditText editAmount;
    private EditText editType;
    private EditText editNote;

    private Button btnUpdate;
    private Button btnDelete;

    private String type;
    private String note;
    private int amount;

    private String post_key;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public IncomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IncomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IncomeFragment newInstance(String param1, String param2) {
        IncomeFragment fragment = new IncomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_income, container, false);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        String uId = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uId);

        incomeTotalSum = myView.findViewById(R.id.income_text_result);

        recyclerView = myView.findViewById(R.id.recycler_id_income);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                int totalValue = 0;
                for(DataSnapshot mySnapshot: snapshot.getChildren())
                {
                    Data data = mySnapshot.getValue(Data.class);
                    totalValue += data.getAmount();
                    String stTotalValue = String.valueOf(totalValue);

                    incomeTotalSum.setText(stTotalValue);
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        return myView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Data,MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>
                (
                        Data.class,
                        R.layout.income_recycler_data,
                        MyViewHolder.class,
                        mIncomeDatabase
                ) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, Data data, int i) {

                viewHolder.setType(data.getType());
                viewHolder.setNote(data.getNote());
                viewHolder.setDate(data.getDate());
                viewHolder.setAmount(data.getAmount());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key = getRef(i).getKey();

                        type = data.getType();
                        note = data.getNote();
                        amount = data.getAmount();
                        updateDataItem();
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public MyViewHolder(@NonNull @org.jetbrains.annotations.NotNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        private void setType(String type){
            TextView mType = mView.findViewById(R.id.type_text_income);
            mType.setText(type);
        }
        private void setNote(String note)
        {
            TextView mNote = mView.findViewById(R.id.note_text_income);
            mNote.setText(note);
        }
        private void setDate(String date)
        {
            TextView mDate = mView.findViewById(R.id.date_text_income);
            mDate.setText(date);
        }
        private void setAmount(int amount)
        {
            TextView mAmount = mView.findViewById(R.id.amount_text_income);
            String stAmount = String.valueOf(amount);
            mAmount.setText(stAmount);
        }
    }

    private void updateDataItem()
    {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myView = inflater.inflate(R.layout.update_data_item,null);
        myDialog.setView(myView);

        editAmount = myView.findViewById(R.id.amount_edt);
        editType = myView.findViewById(R.id.type_edt);
        editNote = myView.findViewById(R.id.note_edt);

        editType.setText(type);
        editType.setSelection(type.length());

        editNote.setText(note);
        editNote.setSelection(note.length());

        editAmount.setText(String.valueOf(amount));
        editAmount.setSelection(String.valueOf(amount).length());

        btnUpdate = myView.findViewById(R.id.btn_uPD_Update);
        btnDelete = myView.findViewById(R.id.btnuPD_Delete);

        AlertDialog dialog = myDialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                type = editType.getText().toString().trim();
                note = editNote.getText().toString().trim();

                String mdammount = String.valueOf(amount);
                mdammount = editAmount.getText().toString().trim();
                int myAmmount = Integer.parseInt(mdammount);

                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(myAmmount, type, note, post_key, mDate);
                mIncomeDatabase.child(post_key).setValue(data);

                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mIncomeDatabase.child(post_key).removeValue();

                dialog.dismiss();
            }
        });

        dialog.show();
    }
}