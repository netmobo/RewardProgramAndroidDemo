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

package com.feefactor.services;

import java.util.List;

import com.feefactor.services.BrandProduct;
import com.feefactor.services.BrandProductPrice;

/**
 * Decorator class for the BrandProduct 
 * adding price functionality to the BrandProduct object
 *   
 * @author Netmobo
 */
public class RatedBrandProduct extends BrandProduct{
	
	private BrandProduct brandProduct;
	
	private List<BrandProductPrice> prices;
	
	public List<BrandProductPrice> getPrices() {
		return prices;
	}
	
	public void setPrices(List<BrandProductPrice> prices) {
		this.prices = prices;
	}
	
	public double getInitialPrice() {
		if(prices == null || prices.size() == 0) {
			return 0;
		}
		
		return prices.get(0).getPrice();
	}
	
	public RatedBrandProduct(BrandProduct bp, List<BrandProductPrice> prices) {
		this.brandProduct = bp;
		this.prices = prices;
	}

	@Override
	public long getBillingBlock() {
		return brandProduct.getBillingBlock();
	}

	@Override
	public void setBillingBlock(long value) {
		brandProduct.setBillingBlock(value);
	}

	@Override
	public String getDescription() {
		return brandProduct.getDescription();
	}

	@Override
	public void setDescription(String value) {
		brandProduct.setDescription(value);
	}

	@Override
	public long getGroupProductID() {
		return brandProduct.getGroupProductID();
	}

	@Override
	public void setGroupProductID(long value) {
		brandProduct.setGroupProductID(value);
	}

	@Override
	public boolean isIsPlan() {
		return brandProduct.isIsPlan();
	}

	@Override
	public void setIsPlan(boolean value) {
		brandProduct.setIsPlan(value);
	}

	@Override
	public double getMinimumChargeable() {
		return brandProduct.getMinimumChargeable();
	}

	@Override
	public void setMinimumChargeable(double value) {
		brandProduct.setMinimumChargeable(value);
	}

	@Override
	public String getProductCode() {
		return brandProduct.getProductCode();
	}

	@Override
	public void setProductCode(String value) {
		brandProduct.setProductCode(value);
	}

	@Override
	public long getProductID() {
		return brandProduct.getProductID();
	}

	@Override
	public void setProductID(long value) {
		brandProduct.setProductID(value);
	}

	@Override
	public long getServiceID() {
		return brandProduct.getServiceID();
	}

	@Override
	public void setServiceID(long value) {
		brandProduct.setServiceID(value);
	}

	@Override
	public String getSettings() {
		return brandProduct.getSettings();
	}

	@Override
	public void setSettings(String value) {
		brandProduct.setSettings(value);
	}

	@Override
	public boolean isUseGroupPrice() {
		return brandProduct.isUseGroupPrice();
	}

	@Override
	public void setUseGroupPrice(boolean value) {
		brandProduct.setUseGroupPrice(value);
	}
}
