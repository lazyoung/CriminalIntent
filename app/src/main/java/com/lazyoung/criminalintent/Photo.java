package com.lazyoung.criminalintent;
import org.json.JSONObject;
import org.json.JSONException;
import android.view.*;
import java.io.*;

public class Photo {
     private static final String JSON_FILENAME = "filename";
     private static final String JSON_DEGREE = "degree";
     private String mFilename;
     private int mDegree;

     /** Create a Photo representing an existing file on disk */
     public Photo(String filename, int rotation) {
         mFilename = filename;
         int degrees = 0;
         switch (rotation) {
             //should count camera view rotation
             case Surface.ROTATION_0: degrees = 270; break;
             case Surface.ROTATION_90: degrees = 0; break;
             case Surface.ROTATION_180: degrees = 90; break;
             case Surface.ROTATION_270: degrees = 180; break;
         }
         mDegree = degrees;
    }

     public Photo(JSONObject json) throws JSONException {
         mFilename = json.getString(JSON_FILENAME);
         mDegree = json.getInt(JSON_DEGREE);
    }

     public JSONObject toJSON() throws JSONException {
         JSONObject json = new JSONObject();
         json.put(JSON_FILENAME, mFilename);
         json.put(JSON_DEGREE, mDegree);
         return json;
    }

    public String getFilename() {
        return mFilename;
    }
    
    public int getDegree() {
        return mDegree;
    }
    
    public void cleanPhoto() {
        File file = new File(mFilename);
        if (file.isFile() && file.exists()) {
            file.delete();
        } 
    }
}
