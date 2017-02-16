package com.lazyoung.criminalintent;

import android.os.Environment;
import android.content.Context;
import java.util.ArrayList;
import java.io.*;
import org.json.*;

public class CriminalIntentJSONSerializer {
    private Context mContext;
    private String mFileName;

    public CriminalIntentJSONSerializer(Context c, String f) {
        mContext = c;
        mFileName = f;
    }
	
	public ArrayList<Crime> loadCrimes() throws IOException, JSONException {
        ArrayList<Crime> crimes = new ArrayList<Crime>();
        BufferedReader reader = null;
        try {
            // open and read the file into a StringBuilder
            InputStream in = null; //mContext.openFileInput(mFileName);
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                in = new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath(), mFileName));
            }
            else {
                in = mContext.openFileInput(mFileName);
            }
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                // line breaks are omitted and irrelevant
                jsonString.append(line);
            }
            // parse the JSON using JSONTokener
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            // build the array of crimes from JSONObjects
            for (int i = 0; i < array.length(); i++) {
                crimes.add(new Crime(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            // we will ignore this one, since it happens when we start fresh
        } finally {
            if (reader != null)
                reader.close();
		}
        return crimes;
    }

    public void saveCrimes(ArrayList<Crime> crimes) throws JSONException, IOException {
        JSONArray array = new JSONArray();
        for(Crime c : crimes)
            array.put(c.toJSON());

        Writer writer = null;
        try {
            OutputStream out = null; //mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                out = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath(), mFileName));
            }
            else {
                out = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
            }
                writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        } finally {
            if(writer != null)
                writer.close();
        }
    }
}
