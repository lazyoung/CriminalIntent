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
import android.provider.*;
import android.net.*;
import android.database.*;

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
    private static final int REQUEST_CONTACT =  4;
	  private static final String DIALOG_IMAGE = "image";
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private Button mSetButton;
	  private ImageButton mPhotoButton;
    private CheckBox mSolvedCheckBox;
	  private ImageView mPhotoView;
    private Button mSuspectButton;

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
          return true;
          
      case R.id.menu_item_delete_photo:
          PictureUtils.cleanImageView(mPhotoView);
          UUID crimeId = (UUID)getArguments().getSerializable(EXTRA_CRIME_ID);
          CrimeLab.get(getActivity()).getCrime(crimeId).getPhoto().cleanPhoto();
          CrimeLab.get(getActivity()).getCrime(crimeId).deletePhoto();
          
          return true;
          
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
                ImageFragment.newInstance(path, p.getDegree()).show(fm, DIALOG_IMAGE);
            }
        });
        
        Button reportButton = (Button)v.findViewById(R.id.crime_reportButton);
        reportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                PackageManager pm = getActivity().getPackageManager();
                List activities = pm.queryIntentActivities(i, 0 );
                if (activities.size() > 0)
                  startActivity(i);
            }
        });
        
        Button callButton = (Button) v.findViewById(R.id.crime_dialButton);
        callButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_DIAL);
                PackageManager pm = getActivity().getPackageManager();
                List activities = pm.queryIntentActivities(i, 0 );
                if (activities.size() > 0)
                    startActivity(i);
            } 
        });
        
        mSuspectButton = (Button)v.findViewById(R.id.crime_suspectButton);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                PackageManager pm = getActivity().getPackageManager();
                List activities = pm.queryIntentActivities(i, 0 );
                if (activities.size() > 0)
                  startActivityForResult(i, REQUEST_CONTACT);
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }
            
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
          int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
			    if  (filename != null) {
				    Photo p = new Photo(filename, rotation);
				    mCrime.setPhoto(p);
			    	showPhoto();
          }
        } else if (requestCode == REQUEST_CONTACT) {
            Uri contactUri = data.getData();
            
            // Specify which fields you want your query to return values for.
            String[] queryFields = new String[] {
                ContactsContract.Contacts.DISPLAY_NAME
            };
            // Perform your query, the contactUri is like a "where" clause here
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
            
            // Double-check that you actually got results
            if (c.getCount() == 0) {
                c.close();
                return;
            }
            
            // Pull out the first column of the first row of data that is your suspect's name.
            c.moveToFirst();
            String suspect = c.getString(0);
            mCrime.setSuspect(suspect);
            mSuspectButton.setText(suspect);
            c.close();
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
			b = PictureUtils.getScaledDrawable(getActivity(), path, p.getDegree());
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
  
  private String getCrimeReport() {
    String solvedString = null;
    if (mCrime.isSolved()) {
        solvedString = getString(R.string.crime_report_solved);
    } else {
        solvedString = getString(R.string.crime_report_unsolved);
    }

    String dateString = mCrime.getDate().toString();
    String suspect = mCrime.getSuspect();
    if (suspect == null) {
        suspect = getString(R.string.crime_report_no_suspect);
    } else {
        suspect = getString(R.string.crime_report_suspect, suspect);
    }

    String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);

    return report;
  }
}
