package com.lazyoung.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.text.*;
import java.text.*;
import java.util.*;
import android.view.*;
import android.support.v4.app.*;
import android.widget.*;
import android.content.pm.*;
import android.util.*;
import android.graphics.drawable.*;

public class CrimeFragment extends Fragment
{
    public static final String EXTRA_CRIME_ID = "com.lazyoung.criminalintent.crime_id";
    private static final String DIALOG_DATE = "date";
    private static final int REQUEST_DATE = 0;
    private static final String DIALOG_TIME = "time";
    private static final int REQUEST_TIME = 1;
    private static final String DIALOG_CHOICE = "choice";
    private static final int REQUEST_CHOICE = 2;
	  private static final int REQUEST_PHOTO = 3;
	  private static final String DIALOG_IMAGE = "image";
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private Button mSetButton;
	  private ImageButton mPhotoButton;
    private CheckBox mSolvedCheckBox;
	  private ImageView mPhotoView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        UUID crimeId = (UUID)getArguments().getSerializable(EXTRA_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
		setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.crime_list_item_context, menu);
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()) {
			case android.R.id.home:
				if (NavUtils.getParentActivityName(getActivity()) != null) {
					NavUtils.navigateUpFromSameTask(getActivity());
				}
				return true;
            case R.id.menu_item_delete_crime:
                CrimeLab.get(getActivity()).deleteCrime(mCrime);
			default:
			    return super.onOptionsItemSelected(item);
		}
	}

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_crime,parent,false);
		    if (NavUtils.getParentActivityName(getActivity()) != null) {
			    getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }
		
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
		
	    	mPhotoButton = (ImageButton)v.findViewById(R.id.crime_imageButton);
		    mPhotoButton.setOnClickListener(new View.OnClickListener() {
		  	    @Override
		  	    public void onClick(View v) {
				       Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
				       startActivityForResult(i, REQUEST_PHOTO);
			      }
	    	});
		
		    // If camera is not available, disable camera functionality
		    PackageManager pm = getActivity().getPackageManager();
		    if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) && !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
			    mPhotoButton.setEnabled(false);
		    }
		
		    mPhotoView = (ImageView)v.findViewById(R.id.crime_imageView);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Photo p = mCrime.getPhoto();
                if (p == null)
                    return;

                FragmentManager fm = getActivity().getSupportFragmentManager();
                String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
                ImageFragment.newInstance(path).show(fm, DIALOG_IMAGE);
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
        } else if (requestCode == REQUEST_PHOTO) {
			
          // Create a new Photo object and attach it to the crime
			    String filename = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
			
			    if  (filename != null) {
				    Photo p = new Photo(filename);
				    mCrime.setPhoto(p);
			    	showPhoto();
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
	
	private void showPhoto() {
		// (Re)set the image button's image based on our photo
		Photo p = mCrime.getPhoto();
		BitmapDrawable b = null;
		if (p != null) {
			String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
			b = PictureUtils.getScaledDrawable(getActivity(), path);
		}
		mPhotoView.setImageDrawable(b);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		showPhoto();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		PictureUtils.cleanImageView(mPhotoView);
	}
}
