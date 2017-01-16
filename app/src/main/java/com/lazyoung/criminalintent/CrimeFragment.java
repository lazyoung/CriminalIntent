package com.lazyoung.criminalintent;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public class CrimeFragment extends Fragment
{
    public static final String EXTRA_CRIME_ID = "com.lazyoung.criminalintent.crime_id";
    private static final String DIALOG_DATE = "date";
    private static final int REQUEST_DATE = 0;
    private static final String DIALOG_TIME = "time";
    private static final int REQUEST_TIME = 1;
    private static final String DIALOG_CHOICE = "choice";
    private static final int REQUEST_CHOICE = 2;
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private Button mSetButton;
    private CheckBox mSolvedCheckBox;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        UUID crimeId = (UUID)getArguments().getSerializable(EXTRA_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_crime,parent,false);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                mCrime.setTitle(c.toString());
            }
            
            public void beforeTextChanged(CharSequence c, int start, int before, int count) { }    
            
            public void afterTextChanged(Editable c) { }
        });
        
        mDateButton = (Button) v.findViewById(R.id.crime_date);
        mDateButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                    dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                    dialog.show(fm, DIALOG_DATE);
                }
            });

        mTimeButton = (Button) v.findViewById(R.id.crime_time);
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(fm, DIALOG_TIME);
            }
        });

        mSetButton = (Button) v.findViewById(R.id.crime_set);
        mSetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DialogFragment dialog = ChoiceDialogFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_CHOICE);
                dialog.show(fm, DIALOG_CHOICE);
            }
        });

        updateDate();
        
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });
        
        return v;
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_DATE) {
            Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        }
        if (requestCode == REQUEST_TIME) {
            Date time = (Date)data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setDate(time);
            updateDate();
        }
        if (requestCode == REQUEST_CHOICE) {
            int choice = data.getIntExtra(ChoiceDialogFragment.EXTRA_CHOICE, 0);
            if(choice == 0){
                return;
            }
            FragmentManager fm = getActivity().getSupportFragmentManager();
            if(choice == ChoiceDialogFragment.CHOICE_DATE) {
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
            else if(choice == ChoiceDialogFragment.CHOICE_TIME) {
                TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(fm, DIALOG_TIME);
            }
        }
    }
    
    public void updateDate() {
        DateFormat formatDate = new SimpleDateFormat("EEEE, yyyy年MM月dd日", Locale.CHINA);
        DateFormat formatTime = new SimpleDateFormat("HH时mm分", Locale.CHINA);
        DateFormat formatSet = new SimpleDateFormat("EEEE, yyyy年MM月dd日, HH时mm分", Locale.CHINA);
        formatDate.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        formatTime.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        formatTime.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        mDateButton.setText(formatDate.format(mCrime.getDate()));
        mTimeButton.setText(formatTime.format(mCrime.getDate()));
        mSetButton.setText(formatSet.format(mCrime.getDate()));
    }
    
    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID,crimeId);
        
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        
        return fragment;
    }
    
    public void returnResult() {
        getActivity().setResult(Activity.RESULT_OK,null);
    }
}
