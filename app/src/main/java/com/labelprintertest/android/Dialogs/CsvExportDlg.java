package com.labelprintertest.android.Dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.citizen.sdk.labelprint.LabelPrinter;
import com.labelprintertest.android.Common.Common;
import com.labelprintertest.android.DBManager.DbHelper;
import com.labelprintertest.android.DBManager.Queries;
import com.labelprintertest.android.PrinterManager.PrinterManager;
import com.labelprintertest.android.R;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.labelprintertest.android.Common.Common.currentActivity;
import static io.fabric.sdk.android.Fabric.TAG;

public class CsvExportDlg extends Dialog implements AdapterView.OnItemSelectedListener {
    DatePickerDialog picker;
    String selectTabel;
    EditText startdate, enddate;
    Calendar startCsvdate, endCsvdate;
    Button btnExport;
    FileWriter mFileWriter = null;
    private ArrayList<HashMap> csvlist;
    public CsvExportDlg(@NonNull Context context) {
        super(context);
        setContentView(R.layout.csv_export_dlg);
        final Spinner spinner = (Spinner) findViewById(R.id.tableSpinner);

        startdate = findViewById(R.id.csv_start_date);
        enddate = findViewById(R.id.csv_end_date);
        btnExport = findViewById(R.id.btn_csv_export);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.JAPANESE);
        String dateStr = sdf.format(new Date());
        startdate.setHint(dateStr);
        enddate.setHint(dateStr);

        startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                startCsvdate = cldr;
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                startdate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                endCsvdate = cldr;
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                enddate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        List<String> categories = new ArrayList<String>();
        categories.add("売上情報");
        categories.add("領収書情報");
        categories.add("算情報");

        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);


        if(spinner != null){
            String text = spinner.getSelectedItem().toString();
            Toast.makeText(getContext(), "Selected: " + text, Toast.LENGTH_LONG).show();
        }

        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int select = 0;
                String selectedtabel = selectTabel;
                DbHelper dbHelper = new DbHelper(currentActivity);
                Queries query = new Queries(null, dbHelper);
                ArrayList<String[]> data = new ArrayList<String[]>();
                if (startCsvdate == null){
                    Toast.makeText(getContext(), "期間を選択してください。", Toast.LENGTH_LONG).show();
                    dismiss();
                    return;
                }
                if (endCsvdate == null){
                    Toast.makeText(getContext(), "期間を選択してください。", Toast.LENGTH_LONG).show();
                    dismiss();
                    return;
                }
                if(selectedtabel == "売上情報"){
                    select = 0;
                    csvlist = query.getCsvDataByGroup(select, startCsvdate, endCsvdate);
                    data.add(new String[]{"no", "uriageno", "gyono", "tanmatsumei", "hanbaibasho", "uriagenichiji", "ticketid", "uriagesuryo", "hanbaitanka", "hanbaikingaku", "shohizeiritsu", "shohizeigaku", "tickettypecd", "meisho", "haraimodoshikb", "uriagekb", "shimeno", "sakuseiuserid", "sakuseinichiji", "koshinuserid", "koshinnichiji"});
                    for(int i = 0; i<=csvlist.size(); i++){
                        data.add(new String[]{String.valueOf(i+1)
                                , csvlist.get(i).get("uriageno").toString()
                                , csvlist.get(i).get("gyono").toString()
                                , csvlist.get(i).get("tanmatsumei").toString()
                                , csvlist.get(i).get("hanbaibasho").toString()
                                , csvlist.get(i).get("uriagenichiji").toString()
                                , csvlist.get(i).get("ticketid").toString()
                                , csvlist.get(i).get("uriagesuryo").toString()
                                , csvlist.get(i).get("hanbaitanka").toString()
                                , csvlist.get(i).get("hanbaikingaku").toString()
                                , csvlist.get(i).get("shohizeiritsu").toString()
                                , csvlist.get(i).get("shohizeigaku").toString()
                                , csvlist.get(i).get("tickettypecd").toString()
                                , csvlist.get(i).get("meisho").toString()
                                , csvlist.get(i).get("haraimodoshikb").toString()
                                , csvlist.get(i).get("uriagekb").toString()
                                , csvlist.get(i).get("shimeno").toString()
                                , csvlist.get(i).get("sakuseiuserid").toString()
                                , csvlist.get(i).get("sakuseinichiji").toString()
                                , csvlist.get(i).get("koshinuserid").toString()
                                , csvlist.get(i).get("koshinnichiji").toString()});
                    }
                }
                if(selectedtabel == "領収書情報"){
                    select =1;
                    csvlist = query.getCsvDataByGroup(select, startCsvdate, endCsvdate);
                    data.add(new String[]{"no", "ryoshuno"
                            , "tanmatsumei"
                            , "hanbaibasho"
                            , "hakkonichiji"
                            , "ryoshukingaku"
                            , "tadashigaki"
                            , "uriageno"
                            , "sakuseiuserid"
                            , "sakuseinichiji"
                            , "koshinuserid"
                            , "koshinnichiji"
                            , "tickettypecd"});
                    for(int i = 0; i<=csvlist.size(); i++){
                        data.add(new String[]{String.valueOf(i+1)
                                , csvlist.get(i).get("ryoshuno").toString()
                                , csvlist.get(i).get("tanmatsumei").toString()
                                , csvlist.get(i).get("hanbaibasho").toString()
                                , csvlist.get(i).get("hakkonichiji").toString()
                                , csvlist.get(i).get("ryoshukingaku").toString()
                                , csvlist.get(i).get("tadashigaki").toString()
                                , csvlist.get(i).get("uriageno").toString()
                                , csvlist.get(i).get("sakuseiuserid").toString()
                                , csvlist.get(i).get("sakuseinichiji").toString()
                                , csvlist.get(i).get("koshinuserid").toString()
                                , csvlist.get(i).get("koshinnichiji").toString()
                                , csvlist.get(i).get("tickettypecd").toString()});
                    }
                }
                if(selectedtabel == "清算情報"){
                    select = 2;
                    csvlist = query.getCsvDataByGroup(select, startCsvdate, endCsvdate);
                    data.add(new String[]{"no", "shimeno"
                            , "tanmatsumei"
                            , "hanbaibasho"
                            , "shimebi"
                            , "uriagekb"
                            , "haraimodoshikb"
                            , "suryo"
                            , "kingaku"
                            , "shohizei"
                            , "sakuseiuserid"
                            , "sakuseinichiji"
                            , "koshinuserid"
                            , "koshinnichiji"});
                    for(int i = 0; i<=csvlist.size(); i++){
                        data.add(new String[]{String.valueOf(i+1)
                                , csvlist.get(i).get("shimeno").toString()
                                , csvlist.get(i).get("tanmatsumei").toString()
                                , csvlist.get(i).get("hanbaibasho").toString()
                                , csvlist.get(i).get("shimebi").toString()
                                , csvlist.get(i).get("uriagekb").toString()
                                , csvlist.get(i).get("haraimodoshikb").toString()
                                , csvlist.get(i).get("suryo").toString()
                                , csvlist.get(i).get("kingaku").toString()
                                , csvlist.get(i).get("shohizei").toString()
                                , csvlist.get(i).get("sakuseiuserid").toString()
                                , csvlist.get(i).get("sakuseinichiji").toString()
                                , csvlist.get(i).get("koshinuserid").toString()
                                , csvlist.get(i).get("koshinnichiji").toString()});
                    }
                }
                if(data.size() <= 1){
                    Toast.makeText(getContext(),"資料がありません。",Toast.LENGTH_SHORT).show();
                    dismiss();
                    return;
                }

                String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "download";
                String fileName = "Data.csv";
                String filePath = baseDir + File.separator + fileName;
                File f = new File(filePath);
                CSVWriter writer = null;

                // File exist
                if(f.exists()&&!f.isDirectory())
                {
                    try {
                        mFileWriter = new FileWriter(filePath, true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    writer = new CSVWriter(mFileWriter);
                }
                else
                {
                    try {
                        writer = new CSVWriter(new FileWriter(filePath));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                writer.writeAll(data);

                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        selectTabel = item;
    }



    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
