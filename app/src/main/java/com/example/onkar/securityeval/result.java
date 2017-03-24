package com.example.onkar.securityeval;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.KeyguardManager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
//import android.support.v7.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.android.market.api.MarketSession;
import com.gc.android.market.api.model.Market;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.currentTimeMillis;

public class result extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        final double m1 = getPermission(); //get the value of different
        final double m2 = checkRoot();     // metrics calculated
        final double m3 = isLocked();
        final double m4 = versioninfo();
        final double m5 = humanTest();
        final double m6=  metUpdate();
        double m7=0;
        try {
            m7 = errorPro();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final double m8 = memory();
        Button b2=(Button)findViewById(R.id.but2);
        final double finalM = m7;
        b2.setOnClickListener(new View.OnClickListener(){
            //            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),finalresult.class);
                i.putExtra("m1",m1);
                i.putExtra("m2",m2); //pass the variable valuess to finalresult activity
                i.putExtra("m3",m3);
                i.putExtra("m4",m4);
                i.putExtra("m5",m5);
                i.putExtra("m6",m6);
                i.putExtra("m7", finalM);
                i.putExtra("m8",m8);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public double metUpdate()
    {
        // function to calculate the metric based on last update time of
        // all the applications in device
        PackageManager packman = getPackageManager();
        ArrayList mlist = new ArrayList();
        TextView textView = (TextView) findViewById(R.id.upd);
        List<PackageInfo>packList = packman.getInstalledPackages(PackageManager.GET_META_DATA);
        double upt=0;// integer value of the metric calculated
        double count = 0;
        long ctime = currentTimeMillis();
        for(int i=0;i<packList.size();i++) {
            PackageInfo pinfo = (PackageInfo)packList.get(i);
            String n = pinfo.applicationInfo.loadLabel(getPackageManager()).toString();
            long time = pinfo.lastUpdateTime;
            time = ctime - time;
            time = time/(1000*60*60*24*30); // average update time calculated in months
            upt = upt + (double)time;
            count++;
        }
        // on a scale of 0 o 1, 1 indicates highest level of danger to the device security
        upt = upt/count;
        if(upt<=1){
            textView.setTextColor(Color.GREEN);//Rule 1: if applications were updated less than a month
            textView.setText("Recently updated");// ago, then set upt value to zero
            upt = 0;
        }
        else if(upt>1 && upt<=2){
            textView.setTextColor(Color.YELLOW);//Rule 2: if applications were updated within 2 months
            textView.setText("device updated");// but after a month, then set upt value to 0.5
            upt = 0.5;
        }
        else{
            textView.setTextColor(Color.RED);//Rule 3: if applications were updated before more than
            textView.setText("Update required");//2 months, then set upt value to 1
            upt = 1;
        }
        return upt;
    }


   public double getPermission(){
        PackageManager packman = getPackageManager();
        List<PackageInfo>packList = packman.getInstalledPackages(PackageManager.GET_META_DATA);
        TextView textView = (TextView) findViewById(R.id.permperc);
        double normalp =0;
        double dangp = 0;
        double modp = 0;
        double plevel = 0;// integer value of the metric calculated
        for (PackageInfo pInfo : packList) {

            try {
                PackageInfo permInfo = packman.getPackageInfo(pInfo.packageName, PackageManager.GET_PERMISSIONS);
                //Get Permissions
                if (permInfo.permissions != null) {
                    // For each defined permission
                    for (PermissionInfo perm : permInfo.permissions) {
                        //calculate the number of normal,dangerous and moderately dangerous permissions
                        // Dump permission info
                        if(perm.protectionLevel==PermissionInfo.PROTECTION_NORMAL){
                            normalp++;
                        }
                        else if(perm.protectionLevel == PermissionInfo.PROTECTION_DANGEROUS ){
                            dangp++;
                        }
                        else{
                            modp++;
                        }
                    }

                }

            }
            catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
       //rules to determine the security based on permissions
       // on a scale of 0 o 1, 1 indicates highest level of danger to the device security
        plevel = dangp/(normalp + dangp + modp);
        plevel = plevel*100;
        if (plevel>70){
            textView.setTextColor(Color.RED);//Rule 4: if dangerous permissions are more than 70%,
            textView.setText((int)plevel + "%");// then security level is dangerous
        }
       else if (plevel<70 && plevel>40 ){
           textView.setTextColor(Color.YELLOW);//Rule 5: if dangerous permissions are more than 40%
           textView.setText((int)plevel + "%");// and less than 70%, then security level is moderately dangerous
       }
       else if (plevel<40){
            textView.setTextColor(Color.GREEN);//Rule 6: if dangerous permissions are less than 40%,
            textView.setText((int)plevel + "%");// then security level is high
       }
       return plevel/100;
    }

    public double checkRoot(){
        //function to check if device is rooted
        String root = android.os.Build.TAGS;
        double rt=0;// integer value of the metric calculated
        TextView textView = (TextView) findViewById(R.id.roota);
        //on a scale of 0 o 1, 1 indicates highest level of danger to the device security
        if(root != null && root.contains("test-keys")){
//
            textView.setTextColor(Color.RED);// Rule 7: if device is rooted, then set rt value to 1
            textView.setText("ROOTED");
            rt = 1;
        }
        else{
            textView.setTextColor(Color.GREEN);// Rule 8: if device is not rooted, then set rt value to 0
            textView.setText("NOT ROOTED");
        }
        return rt;
    }


    @SuppressLint("NewApi")
    public double isLocked() {
        // function to check if device has password,pin or pattern lock
        double lock = 0;// integer value of the metric calculated
        Context context = getApplicationContext();
        TextView textView = (TextView) findViewById(R.id.lock);
        KeyguardManager locked = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        //on a scale of 0 o 1, 1 indicates highest level of danger to the device security
        if (locked.isKeyguardSecure()) {
            lock = 0;         // Rule 9: if device is locked, then set lock value to 0
            textView.setTextColor(Color.GREEN);
            textView.setText("YES");

        } else {
            textView.setTextColor(Color.RED);// Rule 10: if device is not locked, then set lock value to 1
            textView.setText("NO");
            lock = 1;

        }
        return lock;

    }

    public double versioninfo() {
        //function to check the version of OS
        String name = Build.VERSION.RELEASE;
        int version = Build.VERSION.SDK_INT;
        double ver = 0;// integer value of the metric calculated
        TextView textView = (TextView) findViewById(R.id.vers);
        //on a scale of 0 o 1, 1 indicates highest level of danger to the device security
        if (version >= Build.VERSION_CODES.LOLLIPOP) {
            ver = 0;                             //Rule 11: if OS version is greater than or equal to lollipop,
            textView.setTextColor(Color.GREEN);  //then set ver value to 0;
            textView.setText("YES--"+name);
        } else if(version < Build.VERSION_CODES.KITKAT) {
            ver = 1;                             //Rule 12: if OS version is less than kitkat,
            textView.setTextColor(Color.RED);    //then set ver value to 1;
            textView.setText("NO--"+name);
        }
        else{
            ver = 0.5;                           //Rule 13: if OS version is between kitkat and lollipop,
            textView.setTextColor(Color.YELLOW); //then set ver value to 0.5;
            textView.setText("Not so old--"+name);
        }
        return ver;
    }
    public double humanTest() {
        //function to check if user is human or not
        double user = 0;// integer value of the metric calculated
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        TextView textView = (TextView) findViewById(R.id.useri);
        //on a scale of 0 o 1, 1 indicates highest level of danger to the device security
        if (activityManager.isUserAMonkey()) {
            user =1;                     //Rule 14: if user is not human,set user value to 1;
            textView.setTextColor(Color.RED);
            textView.setText("User not human");
        }

        else{
            user =0;                    // Rule 15: if user is human, set user value to 0;
            textView.setTextColor(Color.GREEN);
            textView.setText("User is human");
        }
        return user;
    }
    public double memory() {
        //function to check if sufficient ram memory available
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        double ram= 0;// integer value of the metric calculated
        TextView textView = (TextView) findViewById(R.id.ram);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (activityManager.isLowRamDevice()) {
                ram=1;         //Rule 16: if memory available is low,then set ram value to 1;
                textView.setTextColor(Color.RED);
                textView.setText("NO");
            } else {
                ram=0;         //Rule 17: if memory available is high,then set ram value to 0;
                textView.setTextColor(Color.GREEN);
                textView.setText("YES");
            }
        }
        return ram;
    }

    public double errorPro() throws Exception {
     //function to get the number of processes in error state
        List<ActivityManager.ProcessErrorStateInfo> errList;
        double pro = 0;
        TextView textView = (TextView) findViewById(R.id.pro);
        ActivityManager mActivityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        errList = mActivityManager.getProcessesInErrorState();
        if (errList == null) {
            pro = 0;       //Rule 18: if number of processes in error state is zero,
            textView.setTextColor(Color.GREEN);//then set pro value to 0;
            textView.setText("0");
        } else {
            pro = 1;
            textView.setTextColor(Color.RED);//Rule 19: if number of processes in error state is more then zero,
            int n = errList.size();//then set pro value to 1
            textView.setText(n);
        }
        return pro;
    }


}
