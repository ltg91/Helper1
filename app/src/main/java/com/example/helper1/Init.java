package com.example.helper1;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by 태규 on 2016-04-12.
 */
public class Init extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.initial_layout, container, false);
    }


    public void setTextView(String msg){
        TextView textView1=(TextView)getView().findViewById(R.id.textView_check2);
        textView1.setText(msg);
    }

    public EditText getEditTextID(){

        EditText editText_reg_id=(EditText)getView().findViewById(R.id.editText_id);
        return editText_reg_id;
    }

    public EditText getEditTextPW(){
        EditText editText_reg_pw=(EditText)getView().findViewById(R.id.editText_pw);
        return editText_reg_pw;
    }
}
