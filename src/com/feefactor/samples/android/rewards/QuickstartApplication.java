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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import android.app.Application;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.feefactor.ClientConfig;
import com.feefactor.FeefactorCheckedException;
import com.feefactor.RtbeUserAuthDetail;
import com.feefactor.Server;
import com.feefactor.accounts.Account;
import com.feefactor.accounts.Accounts;
import com.feefactor.charging.Transactions;
import com.feefactor.services.BrandService;
import com.feefactor.services.BrandServices;
import com.feefactor.subscriber.Brand;
import com.feefactor.subscriber.Brands;
import com.feefactor.subscriber.Profile;
import com.feefactor.subscriber.User;
import com.feefactor.subscriber.Users;

/**
 * Serves as the context for the application 
 * 
 * @author Netmobo
 */
public class QuickstartApplication extends Application {
    
    private static String CONFIG_FILENAME = "quickstart.properties";
    
    private boolean isLoggedIn;

    private Account account = null;
    private User user = null;
    private Profile profile = null;
    
    private Brand brand = null;
    private BrandService brandService = null;

    private Accounts accountUtility;
    private Brands brandUtility;
    private BrandServices brandServiceUtility;
    private Users userUtility;
    private Transactions transactionUtility;

    private ClientConfig config;

    private String host;
    private int port;
    private String uri;
    
    private long brandId;    
    private long brandserviceId;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        Properties config = loadConfig(CONFIG_FILENAME);
        
        if(config != null) {
            host = config.getProperty("feefactor.ws.host");
            port = Integer.parseInt(config.getProperty("feefactor.ws.port"));
            uri = config.getProperty("feefactor.ws.path");
            
            brandId = Long.parseLong(config.getProperty("feefactor.service.brandid"));
            brandserviceId = Long.parseLong(config.getProperty("feefactor.service.brandserviceid"));
        }
    }
    
    private Properties loadConfig(String filename) {
        Resources resources = this.getResources();
        AssetManager assetManager = resources.getAssets();

        // Read from the /assets directory
        try {
            InputStream inputStream = assetManager.open(filename);
            Properties properties = new Properties();
            properties.load(inputStream);
            
            return properties;
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public Account getAccount() {
        return getAccount(false);
    }
    
    public Account getAccount(boolean refresh) {
        if(account != null && !refresh) {
            return account;
        }
        
        Accounts accountUtil = getAccountUtility();
        Account updatedAccount = null;
        try {
            updatedAccount = accountUtil.getAccount(account.getSerialNumber());
        } catch (FeefactorCheckedException e) {
            e.printStackTrace();
        }
        
        if(updatedAccount != null) {
            account = updatedAccount;
        }
        
        return account;
    }
    
    public double getAccountPoints(boolean refresh) {
        Account account = getAccount(refresh);
        double balance = (account.getBalance() + account.getCreditLimit());
        
        return balance;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }
    
    public void setBrandService(BrandService bs) {
        this.brandService = bs;
    }
    
    public BrandService getBrandService() {
        return brandService;
    }

    public Accounts getAccountUtility() {
        return accountUtility;
    }

    public void setAccountUtility(Accounts accountService) {
        this.accountUtility = accountService;
    }

    public Brands getBrandUtility() {
        return brandUtility;
    }

    public void setBrandUtility(Brands brandServices) {
        this.brandUtility = brandServices;
    }

    public Users getUserUtility() {
        return userUtility;
    }

    public void setUserUtility(Users userService) {
        this.userUtility = userService;
    }

    public ClientConfig getConfig() {
        return config;
    }

    /**
     * 
     * @return
     */
    public Server setupServer() {
        Server server = new Server();
        server.setHost(host);
        server.setPort(port);
        server.setPrefix(uri);
        
        return server;
    }
    
    private void clearUserDetails() {
        account = null;
        user = null;
        profile = null;
        
        brand = null;
        brandService = null;
    }

    public void logout() {
        clearUtilities();
        clearUserDetails();
        
        this.isLoggedIn = false;
    }
    
    /**
     * 
     * @param username
     * @param password
     * @throws FeefactorCheckedException
     */
    public void login(String username, String password) throws FeefactorCheckedException {
        clearUtilities();
        
        ClientConfig config = setupRtbeUserAuthClientConfig(brandId, username, password);
        setupUtilities(config);
        
        Brand brand = getBrand(brandId);
        setBrand(brand);
        
        User user = getUser(brand, username, password); 
        setUser(user);
        
        BrandService bs = getBrandService(brandserviceId);
        setBrandService(bs);

        Account account = getAccount(brand, user);
        setAccount(account);        
        
        this.isLoggedIn = true;
    }
    
    public Brand getBrand(long brandId) throws FeefactorCheckedException {
        Brands brandUtil = getBrandUtility();
        return brandUtil.getBrand(brandId);
    }
    
    public BrandService getBrandService(long brandserviceId) 
            throws FeefactorCheckedException {
        BrandServices brandserviceUtil = getBrandServiceUtility();
        return brandserviceUtil.getBrandService(brandserviceId);
    }
    
    public User getUser(Brand brand, String username, String password) 
            throws FeefactorCheckedException {
        
        Users userUtil = getUserUtility();

        List<User> users = userUtil.getUsers("A.BRANDID=" + brand.getBrandID() 
                + " AND USERNAME='" + username
                + "' AND PASSWORD='" + password + "'", "", 1, 1);
        
        if(users == null 
                || users.size() == 0) {
            return null;
        }
        
        return users.get(0);
    }
    
    public Account getAccount(Brand brand, User user) throws FeefactorCheckedException {
        Accounts accountUtil = getAccountUtility();
        
        String myAccountWhere = "BRANDID=" + brand.getBrandID() 
                + " AND USERID=" + user.getUserID() 
                + " AND ACCOUNTID='" + user.getUsername() + "'";
        
        List<Account> myAccounts = accountUtil.getAccounts(myAccountWhere, "", 1, 1);
        
        if(myAccounts == null 
                || myAccounts.size() == 0) {
            return null;
        }

        return myAccounts.get(0);
    }
    
    public Profile getUserProfile(User user) {
        if(profile != null) {
            return profile;
        }
        
        Users util = getUserUtility();
        
        if(util == null) {
            return null;
        }
        
        try {
            return util.getProfile(user.getUserID());
        } catch (FeefactorCheckedException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * 
     * @param config
     */
    private void setupUtilities(ClientConfig config) {
        this.config = config;
        Users userUtility = new Users(config);
        setUserUtility(userUtility);

        Brands brandUtility = new Brands(config);
        setBrandUtility(brandUtility);

        Accounts accountUtility = new Accounts(config);
        setAccountUtility(accountUtility);

        Transactions txnUtility = new Transactions(config);
        setTransactionUtility(txnUtility);

        BrandServices bsUtility = new BrandServices(config);
        setBrandServiceUtility(bsUtility);
    }

    private void clearUtilities() {
        setUserUtility(null);
        setBrandUtility(null);
        setAccountUtility(null);
    }

    /**
     * For Login
     * 
     * @param brandId
     * @param username
     * @param password
     * @return
     */
    public ClientConfig setupRtbeUserAuthClientConfig(long brandId,
            String username, String password) {
        RtbeUserAuthDetail authDetail = new RtbeUserAuthDetail();
        authDetail.setBrandID(brandId);
        authDetail.setUsername(username);
        authDetail.setPassword(password);

        return new ClientConfig(setupServer(), authDetail);
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }
    
    public void setTransactionUtility(Transactions txnUtility) {
        this.transactionUtility = txnUtility;
    }
    
    public Transactions getTransactionUtility() {
        return transactionUtility;
    }
    
    public void setBrandServiceUtility(BrandServices bsUtil) {
        this.brandServiceUtility = bsUtil;
    }
    
    public BrandServices getBrandServiceUtility() {
        return brandServiceUtility;
    }
}
