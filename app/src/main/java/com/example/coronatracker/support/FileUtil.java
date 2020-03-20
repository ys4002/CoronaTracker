package com.example.coronatracker.support;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class FileUtil {
    public static void writeToFile(String data, String fileName, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            Log.i("location", context.getFilesDir().getCanonicalPath());
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static String readFromFile(Context context, String fileName) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(fileName);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                context.deleteFile(fileName);
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public static Map<String,String> sortMap(Map<String,String> map) {
        Set<Map.Entry<String, String>> entries = map.entrySet();
        Map<String,String> sortedMap = new LinkedHashMap<>();

        TreeMap<String, String> sorted = new TreeMap<>(map);
        Set<Map.Entry<String, String>> mappings = sorted.entrySet();

        for(Map.Entry<String, String> entry : mappings)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public static TreeMap<Date,String> formatKey(Map<String,String> map) throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("M-dd-yy");
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date=null;
        TreeMap<Date,String> formattedMap = new TreeMap<>();
        for(Map.Entry<String,String> entry : map.entrySet()) {
            String newDate = null;
            String oldDate = entry.getKey().replace("/","-");
            try {
                date = dateFormat.parse(oldDate);
                newDate = formatter.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(Integer.valueOf(entry.getValue()) != 0) {
                formattedMap.put(formatter.parse(newDate), entry.getValue());
            }
        }

        return formattedMap;
    }
}
