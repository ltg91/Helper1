package com.example.helper1;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {
    Init mInit;
    Register mRegister;
    EditText editText_reg_id, editText_reg_pw, editText_reg_nick, editText_reg_email;
    String in_id, in_pw, in_nick, in_email;
    TextView textView_check;
    TextView tv_reg_result;
    TextView tv_log_result;
    boolean reg_success;
    EditText editText_id, editText_pw;
    boolean log_success;
    String my_nick;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*선언부*/
        mInit = new Init();
        mRegister = new Register();
        editText_reg_id = (EditText)findViewById(R.id.editText_reg_id);
        editText_reg_pw = (EditText)findViewById(R.id.editText_reg_pw);
        editText_reg_nick = (EditText)findViewById(R.id.editText_reg_nick);
        editText_reg_email = (EditText)findViewById(R.id.editText_reg_email);
        textView_check = (TextView)findViewById(R.id.textView_check);
        tv_reg_result = (TextView) findViewById(R.id.textView_check);
        tv_log_result = (TextView) findViewById(R.id.textView_check2);
        editText_id = (EditText)findViewById(R.id.editText_id);
        editText_pw = (EditText)findViewById(R.id.editText_pw);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment, mInit);
        ft.commit();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    /*버튼 핸들링 부분*/
    public void onLoginBtnClicked(View v)
    {
        in_id = mInit.getEditTextID().getText().toString();
        in_pw = mInit.getEditTextPW().getText().toString();

        LoginTask LoginTask = new LoginTask();
        LoginTask.execute();
    }

    public void onRegBtnClicked(View v)
    {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.replace(R.id.fragment, mRegister);
        ft.commit();

    }

    public void onFindBtnClicked(View v)
    {

    }

    public void onAppBtnClicked(View v)
    {
        in_id =  mRegister.getEditTextID().getText().toString();
        in_pw = mRegister.getEditTextPW().getText().toString();
        in_nick = mRegister.getEditTextNICK().getText().toString();
        in_email = mRegister.getEditTextEMAIL().getText().toString();

        //Log.i(TAG, in_id);
        RegTask RegTask = new RegTask();
        RegTask.execute();
/*
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.replace(R.id.fragment, mInit);
        ft.commit();
*/
    }





    /* 회원가입에 필요한 부분*/
    public class RegTask extends AsyncTask<String, Void, String> {
        String sResult;

        @Override
        protected String doInBackground(String... sId) {
            sResult = "NO";
            String respond;
            try {
                String body = "user_id=" + in_id + "&user_pw=" + in_pw + "&user_email=" + in_email + "&user_nick="
                        + in_nick;
                URL u = new URL("http://blueeyes2.dothome.co.kr/register.php");
                HttpURLConnection huc = (HttpURLConnection) u.openConnection();
                huc.setReadTimeout(4000);
                huc.setConnectTimeout(4000);
                huc.setRequestMethod("POST");
                huc.setDoInput(true);
                huc.setDoOutput(true);
                huc.setRequestProperty("utf-8", "application/x-www-form-urlencoded");

                OutputStream os = huc.getOutputStream();
                os.write(body.getBytes("utf-8"));
                os.flush();
                os.close();

                /**
                 *
                 * BufferedReader br = new BufferedReader( new
                 * InputStreamReader( huc.getInputStream(), "UTF-8" ),
                 * huc.getContentLength() );
                 *
                 * while( ( respond = br.readLine() ) != null ) {
                 *
                 * System.out.println( respond );
                 *
                 * }
                 *
                 * br.close();
                 *
                 **/

                BufferedReader is = new BufferedReader(new InputStreamReader(huc.getInputStream(), "UTF-8"));
                int ch;
                StringBuffer sb = new StringBuffer();
                while ((ch = is.read()) != -1) {
                    sb.append((char) ch);
                }

                if (is != null) {
                    is.close();
                }
                sResult = sb.toString();
            } catch (Exception e) {

            }
            return sResult;
        }

        @Override
        protected void onPostExecute(String result)
        {
            // TODO: Implement this method
            super.onPostExecute(result);
            // Toast.makeText(getActivity(),respondChecker(sResult),2500).show();
            end_reg_try(respondChecker(sResult));
        }

    }

    public String respondChecker(String respond) {
        if (respond.contains("meta")) {
            if (respond.contains("ERROR")) {
                String oja = respond.substring(respond.indexOf(">") + 1, respond.length());
                return oja;
            } else if (respond.length() > 70) {
                return "ERROR : 정보값 해석불가";
            } else if (respond.contains("SUCCESS")) {
                reg_success = true;
                String nick = respond.substring(respond.indexOf(">") + 1, respond.length());
                onRegSuccess();
                return nick.trim();
            } else {
                return ErrorCatcher(respond);
            }
        } else {
            return "";
        }

    }

    public String ErrorCatcher(String err) {
        if (err.contains("user_id")) {
            return "ERROR : 이미 존재하는 아이디입니다.";
        } else if (err.contains("user_nick")) {
            return "ERROR : 이미 존재하는 닉네임입니다";
        } else if (err.contains("user_email")) {
            return "ERROR : 이미 존재하는 이메일입니다";
        }
        return "ERROR : 해석불가";

    }

    public void end_reg_try(String msg) {
        mRegister.setTextView(msg);
    }

    public void onRegSuccess() {
        // 가입 성공후 할일
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.replace(R.id.fragment, mInit);
        ft.commit();
    }


    /*  ***로그인에 필요한 부분***  */
    private class LoginTask extends AsyncTask<String, Void, String> {
        String sResult;

        @Override
        protected String doInBackground(String... sId) {
            sResult = "NO";
            String respond;

            try {
                String body = "user_id=" + in_id + "&user_pw=" + in_pw;
                URL u = new URL("http://blueeyes2.dothome.co.kr/login.php");
                HttpURLConnection huc = (HttpURLConnection) u.openConnection();
                huc.setReadTimeout(4000);
                huc.setConnectTimeout(4000);
                huc.setRequestMethod("POST");
                huc.setDoInput(true);
                huc.setDoOutput(true);
                huc.setRequestProperty("utf-8", "application/x-www-form-urlencoded");
                OutputStream os = huc.getOutputStream();
                os.write(body.getBytes("utf-8"));
                os.flush();
                os.close();

                /**
                 * BufferedReader br = new BufferedReader( new
                 * InputStreamReader( huc.getInputStream(), "UTF-8" ),
                 * huc.getContentLength() ); while( ( respond = br.readLine() )
                 * != null ) { System.out.println( respond ); } br.close();
                 **/

                BufferedReader is = new BufferedReader(new InputStreamReader(huc.getInputStream(), "UTF-8"));
                int ch;
                StringBuffer sb = new StringBuffer();
                while ((ch = is.read()) != -1) {
                    sb.append((char) ch);
                }

                if (is != null) {
                    is.close();
                }

                sResult = sb.toString();

            } catch (Exception e) {

            }
            return sResult;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO: Implement this method
            super.onPostExecute(result);
            // Toast.makeText(getActivity(),respondChecker(sResult),2500).show();
            end_login_try(respondChecker2(sResult));

        }
    }

    public String respondChecker2(String respond) {
        if (respond.contains("meta")) {
            if (respond.contains("ERROR")) {
                String oja = respond.substring(respond.indexOf(">") + 1, respond.length());
                return oja;
            } else if (respond.length() > 50) {
                return "ERROR : 정보값 해석불가";
            } else {
                log_success = true;
                String nick = respond.substring(respond.indexOf(">") + 1, respond.length());
                my_nick = nick;
                onLogSuccess();
                return nick.trim();
            }
        } else {
            return "";
        }
    }

    public void end_login_try(String msg) {
        mInit.setTextView(msg);
    }

    public void onLogSuccess() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.replace(R.id.fragment, mRegister);
        ft.commit();
    }
}
