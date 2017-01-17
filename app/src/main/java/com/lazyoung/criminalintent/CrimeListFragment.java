package com.lazyoung.criminalintent;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;
import java.text.*;
import java.util.*;

public class CrimeListFragment extends ListFragment
{
    private ArrayList<Crime> mCrimes;
    private static final int REQUEST_CRIME = 1;
	private boolean mSubtitleVisible;
   
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.crimes_title);
        mCrimes = CrimeLab.get(getActivity()).getCrimes();
        mSubtitleVisible = false;
		setRetainInstance(true);
        CrimeAdapter adapter = new CrimeAdapter(mCrimes);
        setListAdapter(adapter);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = super.onCreateView(inflater, container, savedInstanceState);
		if (mSubtitleVisible) {
			getActivity().getActionBar().setSubtitle(R.string.subtitle);
        }
		return v;
	}

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        Crime c = ((CrimeAdapter)getListAdapter()).getItem(position);
        Intent i = new Intent(getActivity(), CrimePagerActivity.class);
        i.putExtra(CrimeFragment.EXTRA_CRIME_ID,c.getId());
        startActivityForResult(i,REQUEST_CRIME);    
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
		MenuItem showSubtitle = menu.findItem(R.id.menu_item_show_subtitle);
		if (mSubtitleVisible && showSubtitle != null) {
			showSubtitle.setTitle(R.string.hide_subtitle);
		}
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_new_crime:
				Crime crime = new Crime();
				CrimeLab.get(getActivity()).addCrime(crime);
				Intent i = new Intent(getActivity(), CrimePagerActivity.class);
				i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
				startActivityForResult(i, 0);
				return true;
			case R.id.menu_item_show_subtitle:
				if (getActivity().getActionBar().getSubtitle() == null) {
					getActivity().getActionBar().setSubtitle(R.string.subtitle);
					mSubtitleVisible = true;
					item.setTitle(R.string.hide_subtitle);
				} else {
					getActivity().getActionBar().setSubtitle(null);
					mSubtitleVisible = false;
					item.setTitle(R.string.show_subtitle);
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
    
    private class CrimeAdapter extends ArrayAdapter<Crime> {
        
        public CrimeAdapter(ArrayList<Crime> crimes) {
            super(getActivity(),android.R.layout.simple_list_item_1,crimes); 
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if(convertView == null) {
                convertView = getActivity().
                getLayoutInflater().inflate(R.layout.list_item_crime,null);
            }

            DateFormat format = new SimpleDateFormat("EEEE, yyyy年MM月dd日, HH时mm分", Locale.CHINA);
            format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            Crime c = getItem(position);
            TextView titleTextView = (TextView) convertView.findViewById(R.id.crime_list_item_titleTextView);
            titleTextView.setText(c.getTitle());
            TextView dateTextView = (TextView) convertView.findViewById(R.id.crime_list_item_dateTextView);
            dateTextView.setText(format.format(c.getDate()));
            CheckBox solvedCheckBox = (CheckBox) convertView.findViewById(R.id.crime_list_item_solvedCheckBox);
            solvedCheckBox.setChecked(c.isSolved());
            
            return convertView;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume( );
        ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
    }   

}
