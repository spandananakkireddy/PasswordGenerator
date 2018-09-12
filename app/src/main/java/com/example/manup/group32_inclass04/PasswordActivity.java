package com.example.manup.group32_inclass04;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PasswordActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvcount;
    TextView tvlength;
    Button btnthread;
    Button btnasync;
    SeekBar sbcount;
    SeekBar sblength;
    ExecutorService threadpool;
    ProgressDialog progressDialog;
    TextView tvselectedpass;
    Handler handler;
    final static String PasswordList="password";
    String[] passwordlist1;
    int tcount=1;
    int tlength=8,progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        setTitle("InClass4");
        threadpool = Executors.newFixedThreadPool(2);
        tvcount = (TextView) findViewById(R.id.tvcount);
        tvlength = (TextView) findViewById(R.id.tvlength);
        btnthread=(Button) findViewById(R.id.btnthread);
        btnthread.setOnClickListener(this);
        btnasync = (Button) findViewById(R.id.btnasync);
        btnasync.setOnClickListener(this);
        sbcount = (SeekBar) findViewById(R.id.sbcount);
        sblength =(SeekBar) findViewById(R.id.sblength);
        tvselectedpass = (TextView) findViewById(R.id.tvselectedpass);
        progressDialog = new ProgressDialog(PasswordActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Generating Passwords....");
        //sbcount.setProgress(1);
        tvcount.setText(tcount +"");
        sbcount.setMax(9);
        sbcount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tcount=progress+1;
                tvcount.setText(tcount +"");
                /*int num = tcount+1;*/
                progressDialog.setMax(tcount);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        //sblength.setProgress(8);
        tvlength.setText(tlength +"");
        sblength.setMax(15);
        sblength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tlength=progress+8;
                tvlength.setText(tlength +"");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {

                switch (msg.what)
                {
                    case DoWork.Status_Start:

                        progressDialog.show();
                        progressDialog.setProgress(msg.getData().getInt("progress"));
                        break;

                    case DoWork.Status_Progress:
                        //progress=progressDialog.getProgress();
                        progressDialog.setProgress(msg.getData().getInt("progress"));
                        Log.d("demo","handler "+msg.getData().getInt("progress")+"");
                        break;

                    case DoWork.Status_Stop:
                        progressDialog.dismiss();
                        generateBuilder(msg.getData().getStringArray(PasswordList));
                }
                return false;
            }
        });

    }
    public void  generateBuilder(final String[] passwords){
        AlertDialog.Builder builder= new AlertDialog.Builder(PasswordActivity.this);
        int size=passwords.length;
        Log.d(PasswordList, "generateBuilder:"+size);

        builder.setTitle("Select a Password")
                .setItems(passwords, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                              tvselectedpass.setText(passwords[which]);
                    }
                });
        AlertDialog dia= builder.create();
        dia.setCancelable(false);
        dia.show();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnthread) {
            try {
                tcount = Integer.parseInt(tvcount.getText().toString());
                tlength = Integer.parseInt(tvlength.getText().toString());
                passwordlist1 = new String[tcount];
                threadpool.execute(new DoWork(tcount,tlength));

            } catch (Exception e) {
                Toast.makeText(PasswordActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
             if (v.getId() == R.id.btnasync) {
                try
                {
                tcount = Integer.parseInt(tvcount.getText().toString());
                tlength = Integer.parseInt(tvlength.getText().toString());
                passwordlist1 = new String[tcount];
                new DoWork2().execute(tcount,tlength);
            }
            catch (Exception e)
            {
                Toast.makeText(PasswordActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
    class DoWork implements Runnable
    {
        int rcount;
        int rlength;
        static final int Status_Start= 0x00;
        static final int Status_Progress=0x01;
        static final int Status_Stop=0x02;

        public DoWork(int count, int length) {

            this.rcount=count;
            this.rlength=length;
        }

        public void run() {

            Message msg = new Message();
            msg.what = Status_Start;
            Bundle bundle = new Bundle();
            bundle.putInt("progress" ,0);
            handler.sendMessage(msg);
            //int count= Integer.parseInt(tvcount.getText().toString());

            for(int i=0;i<rcount;i++)
            {
                passwordlist1[i] =Util.getPassword(rlength);
                Log.d("pass",passwordlist1+"");
                Message msg2= new Message();
                msg2.what=Status_Progress;
              int num = i+1;
                bundle.putInt("progress",num);
                msg2.setData(bundle);
                Log.d("demo",num +"");
                handler.sendMessage(msg2);
            }
            Message msg3= new Message();
            msg3.what=Status_Stop;

            handler.sendMessage(msg3);
            bundle.putStringArray(PasswordList,passwordlist1);
            msg3.setData(bundle);
        }
    }
    class DoWork2 extends AsyncTask<Integer,Integer,String[]> {

        @Override
        protected String[] doInBackground(Integer... Integer) {
            int acount= Integer[0];
            int alength=Integer[1];
            for (int i = 0; i < acount; i++) {
                passwordlist1[i] = Util.getPassword(alength);
                publishProgress(i + 1);
            }
            return passwordlist1;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(PasswordActivity.this);
            progressDialog.setMessage("Generating Passwords...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(tcount);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        @Override
        protected void onPostExecute(final String[] passwordsLister) {
            super.onPostExecute(passwordsLister);
            progressDialog.dismiss();
            AlertDialog.Builder dialog = new AlertDialog.Builder(PasswordActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Passwords List").setItems(passwordsLister, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which)
                {
                    tvselectedpass.setText(passwordsLister[which]);
                }
            });

            dialog.show();
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.d("demo", " " + values[0]);
            progressDialog.setProgress(values[0]);
        }
    }
        }


