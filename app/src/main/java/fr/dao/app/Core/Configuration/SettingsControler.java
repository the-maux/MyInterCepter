package fr.dao.app.Core.Configuration;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import fr.dao.app.Model.Config.Preferences;
import fr.dao.app.R;

public class                SettingsControler {
    private String          TAG = "SettingsControler";
    private File            PATH_TO_PREFERENCES_FILE;
    private Preferences     userPreferences = null;
    public String           PcapPath;
    public static String    NAME_FILE_PREFERENCE = "DaoPreferences.json";
    public String           BinaryPath = null;
    public String           FilesPath = null;
    public String           DumpsPath = null;
    public boolean          DebugMode = true, UltraDebugMode = false;
    public int[]            preferedColors;

    public SettingsControler(String FilesPath) {
        Log.d(TAG, TAG+"::initialisation");
        if (FilesPath == null) {
            Log.e(TAG, "ERROR NO FILE_PATH ON LOADING USER PREFERENCE");
        } else {
            this.FilesPath = FilesPath;
            this.BinaryPath = FilesPath;
            PATH_TO_PREFERENCES_FILE = new File(FilesPath, NAME_FILE_PREFERENCE);
            if (PATH_TO_PREFERENCES_FILE.exists())
                load();
            else {
                build();
                Log.d(TAG, "no Settings.json found");
            }
        }
        initColors();
    }

    private void            initColors() {
        preferedColors = new int[50];
        for (int i=0;i<10;i++) {
            switch (i) {
                case 0:
                    preferedColors[i] = R.color.colorTarget1;
                    break;
                case 1:
                    preferedColors[i] = R.color.colorTarget2;
                    break;
                case 2:
                    preferedColors[i] = R.color.colorTarget3;
                    break;
                case 3:
                    preferedColors[i] = R.color.colorTarget4;
                    break;
                case 4:
                    preferedColors[i] = R.color.colorTarget11;
                    break;
                case 5:
                    preferedColors[i] = R.color.colorTarget6;
                    break;
                case 6:
                    preferedColors[i] = R.color.colorTarget7;
                    break;
                case 9:
                    preferedColors[i] = R.color.colorTarget8;
                    break;
                case 8:
                    preferedColors[i] = R.color.colorTarget9;
                    break;
                case 7:
                    preferedColors[i] = R.color.colorTarget10;
                    break;
                default:
                    preferedColors[i] = R.color.accent;
                    break;
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
            Log.d(TAG, "Settings loaded");
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            Log.e(TAG, "Settings No file, so create one");
            build();
        } catch (IOException e2) {
            e2.printStackTrace();
            Log.e(TAG, "Settings loading error");
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void            build() {
        userPreferences = new Preferences();
        dump(userPreferences);
    }

    public void             dump(Preferences userPreferences) {
        if (userPreferences == null)
            userPreferences = new Preferences();
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(FilesPath + NAME_FILE_PREFERENCE);
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

    public Preferences      getUserPreferences() {
        return userPreferences;
    }

}
