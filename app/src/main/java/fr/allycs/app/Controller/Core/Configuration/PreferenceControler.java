package fr.allycs.app.Controller.Core.Configuration;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import fr.allycs.app.Model.Unix.Preferences;

public class                PreferenceControler {
    private String          TAG = "PreferenceControler";
    private File            PATH_TO_PREFERENCES_FILE;
    private Preferences     userPreferences = null;

    public                  PreferenceControler(String FilesPath) {
        if (FilesPath == null) {
            Log.e(TAG, "ERROR NO FILE_PATH ON LOADING USER PREFERENCE");
        } else {
            PATH_TO_PREFERENCES_FILE = new File(FilesPath, "userPreference.json");
            if (PATH_TO_PREFERENCES_FILE.exists())
                load();
            else
                Log.d(TAG, "no userPreference.json found");
        }
    }

    public void             dump() {
        if (userPreferences == null) {
            Log.e(TAG, "No user Preference file found");
            Log.e(TAG, "Generating default one");
            userPreferences = new Preferences();
        }
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(PATH_TO_PREFERENCES_FILE);
            stream.write(new JSONObject((new Gson()).toJson(userPreferences)).toString().getBytes());
        } catch (FileNotFoundException e) {
            Log.e(TAG, "user preference file not found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "File write failed: " + e.toString());
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error failed: " + e.toString());
            e.printStackTrace();
        } finally {
            if (stream != null)
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public void             load() {
        FileInputStream     in = null;
        try {
            int length = (int) PATH_TO_PREFERENCES_FILE.length();
            byte[] bytes = new byte[length];
            in = new FileInputStream(PATH_TO_PREFERENCES_FILE);
            if (in.read(bytes) > 0)
                userPreferences = new Gson().fromJson(new String(bytes), Preferences.class);
            Log.d(TAG, "userPreference loaded");
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            Log.e(TAG, "userPreference No file, so create one");
            dump();
        } catch (IOException e2) {
            e2.printStackTrace();
            Log.e(TAG, "userPreference loading error");
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Preferences      getUserPreferences() {
        return userPreferences;
    }

}
