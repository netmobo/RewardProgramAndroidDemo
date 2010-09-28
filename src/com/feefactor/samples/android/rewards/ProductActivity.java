/*
 * Copyright (c) 2010, NETMOBO LLC
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  * Redistributions of source code must retain the above copyright notice, 
 *  this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation 
 *  and/or other materials provided with the distribution.
 *  * Neither the name of NETMOBO LLC nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without 
 *   specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
 * THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package com.feefactor.samples.android.rewards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.feefactor.FeefactorCheckedException;
import com.feefactor.accounts.Account;
import com.feefactor.charging.Transactions;
import com.feefactor.services.BrandProduct;
import com.feefactor.services.BrandProductPrice;
import com.feefactor.services.BrandService;
import com.feefactor.services.BrandServices;
import com.feefactor.services.RatedBrandProduct;
import com.feefactor.subscriber.Brand;

/**
 * Parent class for the activities that will process product display/purchase. 
 * This class contains convenience methods to retrieve and purchase products.  
 * 
 * @author Netmobo
 */
public abstract class ProductActivity 
        extends BaseActivity {
    
    /**
     * Returns a list of RatedBrandProducts for the given condition. 
     * 
     * @param condition
     * @param sortOrder
     * @return
     * @throws FeefactorCheckedException
     */
    protected List<RatedBrandProduct> getRatedBrandProducts(String condition, String sortOrder) 
            throws FeefactorCheckedException {
        
        QuickstartApplication app = (QuickstartApplication)getApplication();
        BrandService service = app.getBrandService();
        
        BrandServices bsUtil = app.getBrandServiceUtility();
        List<BrandProduct> products = bsUtil.getBrandProducts(service.getServiceID(), 
                condition, sortOrder, 0, 0);
        
        if(products == null || products.size() == 0){ 
            return null;
        }
        
        List<RatedBrandProduct> result = new ArrayList<RatedBrandProduct>();
        
        for(BrandProduct bp : products) {
            List<BrandProductPrice> prices = getProductPrices(bp);
            RatedBrandProduct rbp = new RatedBrandProduct(bp, prices);
            result.add(rbp);
        }
        
        return result;
    }
    
    private Map<Long, List<BrandProductPrice>> cachedPrices = new HashMap<Long, List<BrandProductPrice>>();
    
    /**
     * Returns the Price for the given BrandProduct 
     * 
     * @param product
     * @return
     * @throws FeefactorCheckedException
     */
    protected List<BrandProductPrice> getProductPrices(BrandProduct product) 
            throws FeefactorCheckedException {
        
        List<BrandProductPrice> prices = cachedPrices.get(product.getProductID());
        
        if(prices != null && prices.size() > 0) {
            return prices;
        }
            
        QuickstartApplication app = (QuickstartApplication)getApplication();
        BrandServices brandserviceUtil = app.getBrandServiceUtility();
        prices = brandserviceUtil.getBrandProductPrices(product.getProductID(), 
                "", "INDEXNUMBER ASC",0l,0l);
        
        if(prices != null && prices.size() > 0) {
            cachedPrices.put(product.getProductID(), prices);
        }
        
        return prices;
    }
    
    /**
     * Issues a purchase task to the Feefactor service to for the given BrandProduct    
     * 
     * @param bp - BrandProduct to purchase
     * @param description - Description that will be used in the purchase history report
     */
    protected void purchaseProduct(BrandProduct bp, String description) {
        QuickstartApplication app = (QuickstartApplication)getApplication();
        
        Transactions tu = app.getTransactionUtility();
        
        Brand brand = app.getBrand();
        BrandService service = app.getBrandService();
        Account account = app.getAccount();
        
        try {
            tu.chargeAccount(brand.getBrandID(), account.getAccountID(), 
                    service.getServiceName(), bp.getProductCode(), 1l, description);
        } catch (FeefactorCheckedException e) {
            e.printStackTrace();
        }
    }
}
