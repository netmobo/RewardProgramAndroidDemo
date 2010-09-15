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

import java.text.SimpleDateFormat;
import java.util.List;

import com.feefactor.FeefactorCheckedException;
import com.feefactor.accounts.Account;
import com.feefactor.accounts.AccountHistory;
import com.feefactor.accounts.Accounts;
import com.feefactor.subscriber.Profile;
import com.feefactor.subscriber.User;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author Netmobo
 */
public class ViewHistoryActivity 
		extends BaseActivity 
		implements OnItemClickListener {

    private ProgressDialog dialog;
    private ListView listView;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.viewhistory);
	    
	    QuickstartApplication app = (QuickstartApplication)getApplication();
	    
	    TextView username = (TextView)findViewById(R.id.history_textview_username);
	    TextView points = (TextView)findViewById(R.id.history_textview_points);
	    
	    User user = app.getUser();
	    Profile userProfile = app.getUserProfile(user);
	    
	    String name = userProfile.getFirstName() + " " + userProfile.getLastName();
	    username.setText(name);
	    
	    double balance = app.getAccountPoints(false);
	    points.setText(String.valueOf(balance));
	    
	    listView = (ListView) findViewById(R.id.history_listview);
		listView.setOnItemClickListener(this);
	    
		new LoadHistoryTask().execute();
		showProgress();
	}
	
	private void showProgress() {
		if(dialog == null) {
			String dialogMsg = getString(R.string.history_progressdialog_message);
			dialog = ProgressDialog.show(this, "", dialogMsg, true);
		}
		
		if(dialog.isShowing()) {
			return;
		}
		
		dialog.show();
	}
	
	private void hideProgress() {
		if(dialog == null || !dialog.isShowing()) {
			return;
		}
		
		dialog.dismiss();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, 
			int position, long id) {
		
		listView = (ListView) findViewById(R.id.history_listview);
		AccountHistory ah = (AccountHistory)listView.getItemAtPosition(position);

		String referenceId = ah.getReferenceID();
		
		String[] refParts = referenceId.split("@");
		
		if(refParts.length == 0) {
			return;
		}
	}

	private class LoadHistoryTask extends AsyncTask<Void, Void, List<AccountHistory>> {
	     protected void onPostExecute(List<AccountHistory> result) {
	    	 registerHistoryList(result);
	     }

		@Override
		protected List<AccountHistory> doInBackground(Void... params) {
			return retrieveHistory();
		}
	}
	
	private List<AccountHistory> retrieveHistory() {
		QuickstartApplication app = (QuickstartApplication)getApplication();
		Account account = app.getAccount();
		
		Accounts accountUtil = app.getAccountUtility();
        try {
            return accountUtil.getAccountHistories(account.getSerialNumber(), 
                    "", "TRANSACTIONDATE DESC", 20, 1);
        } catch (FeefactorCheckedException e) {
            e.printStackTrace();
        }
        
        return null;
	}
	
	private void registerHistoryList(List<AccountHistory> histories) {
	    Log.i("DEBUG", "History Count: " + histories.size());
	    
		HistoryAdapter prodAdapter = new HistoryAdapter(this, R.layout.historyrow, histories);
		
		listView = (ListView) findViewById(R.id.history_listview);
		listView.setAdapter(prodAdapter);
		
		hideProgress();
	}
	
    public void logout() {
        QuickstartApplication app = (QuickstartApplication) getApplication();
        app.logout();
    }
	
	private class HistoryAdapter extends ArrayAdapter<AccountHistory> {

		private List<AccountHistory> history;

		public HistoryAdapter(Context context, int textViewResourceId, List<AccountHistory> objects) {
			super(context, textViewResourceId, objects);
			this.history = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.historyrow, null);
			}
			
			if(history == null 
			        || history.size() ==0) {
			    return view;
			}
			
			AccountHistory hist = history.get(position);
			if (hist != null) {
			    String pattern = "MM/dd/yyyy hh:mm:ss a";
			    SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
				
				TextView dateView = (TextView) view.findViewById(R.id.history_date);
				TextView itemView = (TextView) view.findViewById(R.id.history_item);
				TextView costView = (TextView) view.findViewById(R.id.history_cost);
				
				dateView.setText(dateFormat.format(hist.getTransactionDate().getTime()));
				
				String description = hist.getDescription();
				double amountChange = hist.getAmountChange();
				
				if(description != null && description.length() > 0) {
					if(description.startsWith("[")) {
						description = description.substring(description.indexOf("]") + 1).trim();
					}
					
					if(amountChange < 0) {
						description = "[Reward] " + description;
					}
				}
				
				itemView.setText(description);
				costView.setText(String.valueOf(amountChange));
			}
			
			return view;
		}
	}
}
