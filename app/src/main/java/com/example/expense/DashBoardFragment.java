package com.example.expense;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expense.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
 * Use the {@link DashBoardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashBoardFragment extends Fragment {

    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    private TextView fab_income_txt;
    private TextView fab_expense_txt;

    private boolean isOpen = false;

    private Animation fadeOpen,fadeClose;

    private TextView toalIncomeResult;
    private TextView totalExpenseResult;



    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;

    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;




    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashBoardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashBoardFragment newInstance(String param1, String param2) {
        DashBoardFragment fragment = new DashBoardFragment();
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
        View myView = inflater.inflate(R.layout.fragment_dash_board, container, false);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        String uId = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uId);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uId);

        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);

        fab_main_btn = myView.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn = myView.findViewById(R.id.income_ft_btn);
        fab_expense_btn = myView.findViewById(R.id.expense_ft_btn);

        fab_income_txt = myView.findViewById(R.id.income_ft_text);
        fab_expense_txt = myView.findViewById(R.id.expense_ft_text);

        toalIncomeResult = myView.findViewById(R.id.income_set_result);
        totalExpenseResult = myView.findViewById(R.id.expense_set_result);

        mRecyclerIncome = myView.findViewById(R.id.recycler_income);
        mRecyclerExpense = myView.findViewById(R.id.recycler_expense);


        fadeOpen = AnimationUtils.loadAnimation(getActivity(),R.anim.fade_open);
        fadeClose = AnimationUtils.loadAnimation(getActivity(),R.anim.fade_close);

        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData();
                if(isOpen)
                {
                    fab_income_btn.startAnimation(fadeClose);
                    fab_expense_btn.startAnimation(fadeClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_txt.startAnimation(fadeClose);
                    fab_expense_txt.startAnimation(fadeClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);

                    isOpen = false;
                }
                else
                {
                    fab_income_btn.startAnimation(fadeOpen);
                    fab_expense_btn.startAnimation(fadeOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_txt.startAnimation(fadeOpen);
                    fab_expense_txt.startAnimation(fadeOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);

                    isOpen = true;
                }
            }
        });

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                int totalSum = 0;
                for(DataSnapshot mySnap: snapshot.getChildren())
                {
                    Data data = mySnap.getValue(Data.class);
                    totalSum += data.getAmount();

                    String stResult = String.valueOf(totalSum);
                    toalIncomeResult.setText(stResult);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                int totalSum = 0;
                for(DataSnapshot mySnap: snapshot.getChildren())
                {
                    Data data = mySnap.getValue(Data.class);
                    totalSum += data.getAmount();

                    String stResult = String.valueOf(totalSum);
                    totalExpenseResult.setText(stResult);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        LinearLayoutManager layoutManagerIncome = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);

        LinearLayoutManager layoutManagerExpense = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManagerExpense.setStackFromEnd(true);
        layoutManagerExpense.setReverseLayout(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);

        return myView;
    }

    private void ftAnimation() {
        if(isOpen)
        {
            fab_income_btn.startAnimation(fadeClose);
            fab_expense_btn.startAnimation(fadeClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_txt.startAnimation(fadeClose);
            fab_expense_txt.startAnimation(fadeClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);

            isOpen = false;
        }
        else
        {
            fab_income_btn.startAnimation(fadeOpen);
            fab_expense_btn.startAnimation(fadeOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(fadeOpen);
            fab_expense_txt.startAnimation(fadeOpen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);

            isOpen = true;
        }
    }

    private void addData(){
        fab_income_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                incomeDataInsert();
            }
        });
        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDataInsert();
            }
        });
    }

    public void incomeDataInsert(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myView = inflater.inflate(R.layout.custom_layout_for_insert_data,null);
        myDialog.setView(myView);
        final AlertDialog dialog = myDialog.create();

        dialog.setCancelable(false);

        EditText editAmount = myView.findViewById(R.id.amount_edt);
        EditText editType = myView.findViewById(R.id.type_edt);
        EditText editNote = myView.findViewById(R.id.note_edt);

        Button btnSave = myView.findViewById(R.id.btnSave);
        Button btnCancel = myView.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = editType.getText().toString().trim();
                String amount = editAmount.getText().toString().trim();
                String note = editNote.getText().toString().trim();

                if(TextUtils.isEmpty(type))
                {
                    editType.setError("Required Field..");
                    return;
                }
                if(TextUtils.isEmpty(amount))
                {
                    editAmount.setError("Required Field..");
                    return;
                }

                int ourAmount = Integer.parseInt(amount);
                if(TextUtils.isEmpty(note))
                {
                    editNote.setError("Required Field..");
                    return;
                }
                String id = mIncomeDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(ourAmount,type,note,id,mDate);
                mIncomeDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(),"Data Added!",Toast.LENGTH_SHORT).show();

                ftAnimation();
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void expenseDataInsert(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myView = inflater.inflate(R.layout.custom_layout_for_insert_data,null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        EditText amount = myView.findViewById(R.id.amount_edt);
        EditText type = myView.findViewById(R.id.type_edt);
        EditText note = myView.findViewById(R.id.note_edt);

        Button btnSave = myView.findViewById(R.id.btnSave);
        Button btnCancel = myView.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmAmount = amount.getText().toString().trim();
                String tmType = type.getText().toString().trim();
                String tmNote = note.getText().toString().trim();

                if(TextUtils.isEmpty(tmAmount))
                {
                    amount.setError("Required Field..");
                    return;
                }
                int inAmount = Integer.parseInt(tmAmount);
                if(TextUtils.isEmpty(tmType))
                {
                    type.setError("Required Field..");
                    return;
                }
                if(TextUtils.isEmpty(tmNote))
                {
                    note.setError("Required Field..");
                    return;
                }

                String id = mExpenseDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(inAmount,tmType,tmNote,id,mDate);
                mExpenseDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(), "Data Added!", Toast.LENGTH_SHORT).show();
                ftAnimation();
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onStart(){
        super.onStart();

        FirebaseRecyclerAdapter<Data, IncomeViewHolder> incomeAdapter = new FirebaseRecyclerAdapter<Data, IncomeViewHolder>
                (
                        Data.class,
                        R.layout.dashboard_income,
                        DashBoardFragment.IncomeViewHolder.class,
                        mIncomeDatabase
                ) {
            @Override
            protected void populateViewHolder(IncomeViewHolder incomeViewHolder, Data data, int i) {

                incomeViewHolder.setIncomeType(data.getType());
                incomeViewHolder.setIncomeAmount(data.getAmount());
                incomeViewHolder.setIncomeDate(data.getDate());
            }
        };
        mRecyclerIncome.setAdapter(incomeAdapter);

        FirebaseRecyclerAdapter<Data, ExpenseViewHolder> expenseAdapter = new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>
                (
                        Data.class,
                        R.layout.dashboard_expense,
                        DashBoardFragment.ExpenseViewHolder.class,
                        mExpenseDatabase
                ) {
            @Override
            protected void populateViewHolder(ExpenseViewHolder expenseViewHolder, Data data, int i) {

                expenseViewHolder.setExpenseType(data.getType());
                expenseViewHolder.setExpenseAmount(data.getAmount());
                expenseViewHolder.setExpenseDate(data.getDate());

            }
        };
        mRecyclerExpense.setAdapter(expenseAdapter);

    }

    public static class IncomeViewHolder extends RecyclerView.ViewHolder{

        View mIncomeView;

        public IncomeViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mIncomeView = itemView;
        }
        public void setIncomeType(String type){

            TextView mType = mIncomeView.findViewById(R.id.type_Income_ds);
            mType.setText(type);
        }
        public void setIncomeAmount(int amount){

            TextView mAmount = mIncomeView.findViewById(R.id.amount_income_ds);
            String strAmount = String.valueOf(amount);
            mAmount.setText(strAmount);
        }
        public void setIncomeDate(String date){

            TextView mDate = mIncomeView.findViewById(R.id.date_income_ds);
            mDate.setText(date);

        }
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder{

        View mExpenseView;

        public ExpenseViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mExpenseView = itemView;
        }
        public void setExpenseType(String type){
            TextView mType = mExpenseView.findViewById(R.id.type_Expense_ds);
            mType.setText(type);
        }
        public void setExpenseAmount(int amount){
            TextView mAmount = mExpenseView.findViewById(R.id.amount_expense_ds);

            String strAmount = String.valueOf(amount);
            mAmount.setText(strAmount);
        }
        public void setExpenseDate(String date){
            TextView mDate = mExpenseView.findViewById(R.id.date_expense_ds);
            mDate.setText(date);
        }

    }
}