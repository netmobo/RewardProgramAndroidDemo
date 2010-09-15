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

import java.util.List;

import com.feefactor.FeefactorCheckedException;
import com.feefactor.samples.android.ListItemButton;
import com.feefactor.services.BrandProduct;
import com.feefactor.services.RatedBrandProduct;
import com.feefactor.subscriber.Profile;
import com.feefactor.subscriber.User;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;

/**
 * PurchaseProductActivity class.
 * 
 * @author Netmobo
 */
public class PurchaseProductActivity 
        extends ProductActivity 
//        implementes OnItemClickListener {
        implements OnClickListener {

    private ProgressDialog dialog;
    private int selectedItemIndex = -1;

    //Store these in member variables since they are accessed frequently in the activity.
    private ListView listView;
    private TextView points;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.purchaseproduct);

        QuickstartApplication app = (QuickstartApplication) getApplication();
        
        TextView username = (TextView)findViewById(R.id.products_textview_username);
        
        User user = app.getUser();
        Profile userProfile = app.getUserProfile(user);
        
        String name = userProfile.getFirstName() + " " + userProfile.getLastName();
        username.setText(name);
        
        double accountPoints = app.getAccountPoints(true);
        points = (TextView)findViewById(R.id.products_textview_points);
        points.setText(String.valueOf(accountPoints));
        
        listView = (ListView) findViewById(R.id.products_listview);
//        listView.setOnItemClickListener(this);
        
        new LoadProductTask().execute();
        showProgress();
    }
    
    private void showProgress() {
        if(dialog == null) {
            String dialogMsg = getString(R.string.products_progressdialog_message);
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
    
    private class LoadProductTask extends AsyncTask<Void, Void, List<RatedBrandProduct>> {
        @Override
        protected List<RatedBrandProduct> doInBackground(Void... params) {
            return retrieveProducts();
        }
        
         protected void onPostExecute(List<RatedBrandProduct> result) {
             registerListProducts(result);
         }
     }
    
    private List<RatedBrandProduct> retrieveProducts() {
        try {
            return getRatedBrandProducts("PRODUCTCODE like 'POINTS-%'", "");
        } catch (FeefactorCheckedException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    private void registerListProducts(List<RatedBrandProduct> products) {
        ProductAdapter prodAdapter = new ProductAdapter(this, R.layout.productrow, products, this);
        listView.setAdapter(prodAdapter);
        
        hideProgress();
    }
    
    public AlertDialog createPurchaseDialog(BrandProduct product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
        String msgBoxMessage = getString(R.string.products_alertdialog_message) 
                + " " + product.getDescription();
        
        builder.setMessage(msgBoxMessage)
               .setCancelable(false)
               .setPositiveButton(R.string.products_alertdialog_yes, 
                       new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int id) {
                                   purchaseProduct(selectedItemIndex);
                               }
                           })
               .setNegativeButton(R.string.products_alertdialog_no, 
                       new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int id) {
                                   dialog.cancel();
                               }
                           });
        AlertDialog alert = builder.create();
        
        return alert;
    }
    
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        selectedItemIndex = position;
//
//        BrandProduct bp = (BrandProduct)listView.getItemAtPosition(position);
//        
//        AlertDialog alert = createPurchaseDialog(bp);
//        alert.show();
//    }

    @Override
    public void onClick(View v) {
        ListItemButton button = (ListItemButton)v;
        int position = button.getIndex();
        
        selectedItemIndex = position;
        BrandProduct bp = (BrandProduct)listView.getItemAtPosition(position);
        
        listView.setSelection(selectedItemIndex);
        listView.requestFocusFromTouch();
        
        AlertDialog alert = createPurchaseDialog(bp);
        alert.show(); 
    }
    
    public void purchaseProduct(int index) {
        if(index <= -1) {
            return;
        }
        
        BrandProduct bp = (BrandProduct)listView.getItemAtPosition(index);
        
        if(bp == null) {
            //show Toast warning
            return;
        }

        String description = "Purchase Product: " + bp.getDescription()
              + " (" + bp.getProductCode() + ")";
        purchaseProduct(bp, description);
        
        QuickstartApplication app = (QuickstartApplication)getApplication();        
        points.setText(String.valueOf(app.getAccountPoints(true)));
        
        Toast.makeText(this, R.string.products_toast_message, Toast.LENGTH_LONG).show();
    }
    
    public void logout() {
        QuickstartApplication app = (QuickstartApplication) getApplication();
        app.logout();
    }
    
    private class ProductAdapter extends ArrayAdapter<RatedBrandProduct> {

        private List<RatedBrandProduct> products;
        private OnClickListener clickListener;

        public ProductAdapter(Context context, int textViewResourceId, 
                List<RatedBrandProduct> objects, OnClickListener listener) {
            super(context, textViewResourceId, objects);

            this.products = objects;
            this.clickListener = listener;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.productrow, null);
            }
            
            RatedBrandProduct bp = products.get(position);
            if (bp != null) {
                TextView productName = (TextView) view.findViewById(R.id.product_name);
                TextView productPoints = (TextView) view.findViewById(R.id.product_points);
                
                if (productName != null) {
                    productName.setText(bp.getDescription());
                }

                if (productPoints != null) {
                    double price = bp.getInitialPrice();
                    productPoints.setText(String.valueOf(price));
                }
                
                ListItemButton button = (ListItemButton)view.findViewById(R.id.product_button_buy);
                button.setText(R.string.products_button_buy);
                button.setOnClickListener(clickListener);
                button.setIndex(position);
            }
            return view;
        }
    }
}
