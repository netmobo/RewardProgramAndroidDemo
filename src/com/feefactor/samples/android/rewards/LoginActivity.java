/*
 * Copyright (c) 2010, NETMOBO LLC
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 *  * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *  * Neither the name of NETMOBO LLC nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.feefactor.samples.android.rewards;

import com.feefactor.FeefactorCheckedException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * LoginActivity class. Landing activity for the rewards application. 
 * 
 * @author Netmobo
 */
public class LoginActivity 
        extends Activity 
        implements OnClickListener{
    
    private final Handler handler = new Handler();
    private ProgressDialog dialog;
    
    public void showToastMessage(String message) {
        hideProgress();
        Toast.makeText(getApplicationContext(), 
                message, 
                Toast.LENGTH_LONG).show();
    }
    
    /**
     * Overridden method from the the Activity class. 
     * Handles call backs when this class is first created.   
     * 
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        EditText username = (EditText)findViewById(R.id.login_username);
        username.setText("reward");
        
        Button loginButton = (Button) findViewById(R.id.login_button_submit);
        loginButton.setOnClickListener(this);
    }
    
    /**
     * Overriden method implemented from the OnClickListener interface.
     * Handles call backs to registered views when a click event is
     * triggered. 
     * 
     */
    @Override
    public void onClick(View arg0) {
        showProgress();

        /*
         * Creates and runs a background thread to handle the login process. 
         */
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                login();
            }
        };
        
        Thread thread = new Thread(runnable);
        thread.start();
    }
    
    /**
     * Shows the processing progress dialog for this activity. 
     */
    private void showProgress() {
        if(dialog == null) {
            String dialogMsg = getString(R.string.login_progressdialog_message);
            dialog = ProgressDialog.show(this, "", dialogMsg, true);
        }
        
        if(dialog.isShowing()) {
            return;
        }
        
        dialog.show();
    }
    
    /**
     * Hides the progress dialog for this activity.
     */
    private void hideProgress() {
        if(dialog == null || !dialog.isShowing()) {
            return;
        }
        
        dialog.dismiss();
    }

    /**
     * Process the user login.
     */
    public void login() {
        TextView usernameField = (TextView) findViewById(R.id.login_username);
        TextView passwordField = (TextView) findViewById(R.id.login_password);

        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();

        QuickstartApplication app = (QuickstartApplication) getApplication();

        try {
            app.login(username, password);

            handler.post(new Runnable() {
                @Override
                public void run() {
                    showToastMessage("Login Successful");
                }
            });
            
            Intent intent = new Intent(this, ViewHistoryActivity.class);
            startActivity(intent);
        } catch (FeefactorCheckedException e) {
            e.printStackTrace();
            
            handler.post(new Runnable() {
                @Override
                public void run() {
                    showToastMessage("Invalid Username/Password");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            
            handler.post(new Runnable() {
                @Override
                public void run() {
                    showToastMessage("Unable to Login");
                }
            });
        }
    }
}
