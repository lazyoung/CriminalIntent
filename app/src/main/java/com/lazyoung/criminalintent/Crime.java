package com.lazyoung.criminalintent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Crime
{
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    
    public Crime(){
        mId = UUID.randomUUID();
        mDate = new Date();
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

}
