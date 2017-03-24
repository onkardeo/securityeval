package com.example.onkar.securityeval;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class finalresult extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        double m1=0,m2=0,m3=0,m4=0,m5=0,m6=0,m7=0,m8=0;
        setContentView(R.layout.activity_finalresult);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            m1 = b.getDouble("m1"); //metric values recieved from result activity
            m2 = b.getDouble("m2");
            m3 = b.getDouble("m3");
            m4 = b.getDouble("m4");
            m5 = b.getDouble("m5");
            m6 = b.getDouble("m6");
            m7 = b.getDouble("m7");
            m8 = b.getDouble("m8");
        }
        TextView t1 = (TextView) findViewById(R.id.t1);
        float security = (float)(((m1+m2+m3+m4+m5+m6+m7+m8)/8)*100);//final metric calculated to evaluate
              security = 100 - security; //final security metric of the system
        if(security>=70){
            t1.setTextColor(Color.GREEN);//Rule 20: if security >= 70%,then device is secured
            t1.setText("Your device is " + security + "% secured");
            ImageView i = (ImageView)findViewById(R.id.img);
            i.setImageResource(R.drawable.saf1);
        }
        else if(security<=50){
                t1.setTextColor(Color.RED);//Rule 21: if security <=50%,then device is poorly secured
                t1.setText("Your device is " + security + "% secured");
            ImageView i = (ImageView)findViewById(R.id.img);
            i.setImageResource(R.drawable.dan1);
        }
        else {
            t1.setTextColor(Color.YELLOW);//Rule 22: if security<70% and >50,then device is moderately secured
            t1.setText("Your device is " + security + "% secured");
            ImageView i = (ImageView)findViewById(R.id.img);
            i.setImageResource(R.drawable.saf1);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_finalresult, menu);
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
}
