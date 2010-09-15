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

import android.app.Activity;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * BaseActivity class for the application activities. 
 * Handles menu creation, events and menu based navigation.
 * 
 * @author Netmobo
 */
public abstract class BaseActivity extends Activity{
	
	public abstract void logout();

	/**
	 * Creates the menu items
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.nav_menu, menu);
	    return true;
	}

	/**
	 *  Handles menu navigation selection
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id._menu_history:
			switchView(ViewHistoryActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP);
			return true;
		case R.id._menu_products:
			switchView(PurchaseProductActivity.class);
			return true;
		case R.id._menu_rewards:
			switchView(ClaimRewardActivity.class);
			return true;
		case R.id._menu_logout:
			logout();
			switchView(LoginActivity.class);
			return true;
		}
		
	    return false;
	}
	
	/**
	 * Handles the switching from one activity to the other 
	 * 
	 * @param destination
	 */
	private void switchView(Class destination) {
		switchView(destination, Intent.FLAG_ACTIVITY_NO_HISTORY);
	}
	
	/**
     * Handles the switching from one activity to the other 
     * 
     * @param destination
     */
	private void switchView(Class destination, int intentType) {
	    Intent intent = new Intent(this, destination);
        intent.setFlags(intentType);
        startActivity(intent);
	}
}
