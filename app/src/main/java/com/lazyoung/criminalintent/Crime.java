package com.lazyoung.criminalintent;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

public class Crime
{
    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_SOLVED = "solved";
    private static final String JSON_DATE = "date";
	  private static final String JSON_PHOTO = "photo";
    private static final String JSON_SUSPECT = "suspect";
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
	  private Photo mPhoto;
    private String mSuspect;
    
    public Crime(){
        mId = UUID.randomUUID();
        mDate = new Date();
    }
	
    public Crime(JSONObject json) throws JSONException {
		mId = UUID.fromString(json.getString(JSON_ID));
		mTitle = json.getString(JSON_TITLE);
		mSolved = json.getBoolean(JSON_SOLVED);
		mDate = new Date(json.getLong(JSON_DATE));  
		if (json.has(JSON_PHOTO))
			mPhoto = new Photo(json.getJSONObject(JSON_PHOTO));
    if (json.has(JSON_SUSPECT))
      mSuspect = json.getString(JSON_SUSPECT);
    }

    public void setSuspect(String suspect)
    {
        mSuspect = suspect;
    }

    public String getSuspect()
    {
        return mSuspect;
    }
		
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, mId.toString());
        json.put(JSON_TITLE, mTitle);
        json.put(JSON_SOLVED, mSolved);
        json.put(JSON_DATE, mDate.getTime());
		
	    	if (mPhoto != null)
			    json.put(JSON_PHOTO, mPhoto.toJSON());
        if (mSuspect!= null)
          json.put(JSON_SUSPECT, mSuspect);

        return json;
    }

    public void setDate(Date mDate)
    {
        this.mDate = mDate;
    }

    public Date getDate()
    {
        return mDate;
    }

    public void setSolved(boolean mSolved)
    {
        this.mSolved = mSolved;
    }

    public boolean isSolved()
    {
        return mSolved;
    }

    public UUID getId()
    {
        return mId;
    }

    public void setTitle(String title)
    {
        mTitle = title;
    }

    public String getTitle()
    {
        return mTitle;
    }

    @Override
    public String toString()
    {
        return mTitle;
    }
	
	  public Photo getPhoto() {
		  return mPhoto;
    }

	  public void setPhoto(Photo p) {
		  mPhoto = p;
    }
    
    public void deletePhoto() {
      mPhoto = null;
    }

}
