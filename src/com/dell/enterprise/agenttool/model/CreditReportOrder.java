/*
 * FILENAME
 *     Desktop.java
 *
 * FILE LOCATION
 *     $Source$
 *
 * VERSION
 *     $Id$
 *         @version       $Revision$
 *         Check-Out Tag: $Name$
 *         Locked By:     $Lockers$
 *
 * FORMATTING NOTES
 *     * Lines should be limited to 120 characters.
 *     * Files should contain no tabs.
 *     * Indent code using four-character increments.
 *
 * COPYRIGHT
 *     Copyright (C) 2010 HEB. All rights reserved.
 *     This software is the confidential and proprietary information of
 *     HEB ("Confidential Information"). You shall not
 *     disclose such Confidential Information and shall use it only in
 *     accordance with the terms of the licence agreement you entered into
 *     with HEB.
 */

package com.dell.enterprise.agenttool.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;
import java.util.Map;

//
// IMPORTS
// NOTE: Import specific classes without using wildcards.
//

/**
 * <p>
 * TODO Add one-sentence summarising the class functionality here; this sentence
 * should only contain one full-stop.
 * </p>
 * <p>
 * TODO Add detailed HTML description of class here, including the following,
 * where relevant:
 * <ul>
 * <li>TODO Description of what the class does and how it is done.</li>
 * <li>TODO Explanatory notes on usage, including code examples.</li>
 * <li>TODO Notes on class dynamic behaviour e.g. is it thread-safe?</li>
 * </ul>
 * </p>
 * <p>
 * <h2>Responsibilities</h2>
 * </p>
 * <p>
 * <ul>
 * <li>TODO Reponsibility #1.</li>
 * <li>TODO Reponsibility #2.</li>
 * <li>TODO Reponsibility #3.</li>
 * <li>TODO Reponsibility #4.</li>
 * </ul>
 * </p>
 * <p>
 * <h2>Protected Interface</h2>
 * </p>
 * <p>
 * TODO Document the protected interface here, including the following:
 * <ul>
 * <li>TODO Which aspects of the class's functionality can be extended.</li>
 * <li>TODO How this extension is done, including usage examples.</li>
 * </ul>
 * </p>
 * 
 * @see TODO Related Information
 * 
 * @author thuynguyen
 * 
 * @version $Id$
 **/
public class CreditReportOrder implements Serializable
{
    /**
     * <p>
     * TODO Describe what this data member models and how it's used.</p>
     **/
    
    private static final long serialVersionUID = 1L;
    /**
     * 
     */
    private Long id;
    private String contact_name;
    private String ordernumber;
    private String item_sku;
    private String account;
    private String reason;
    private String credit_date;
    private String amount;
    private String cat2;
    private String cat1;
    private String sum_amount;
    private String sales_price;
    private String ship_to_state;
   
    /**
     * @return the sum_amount
     */
    public String getSum_amount()
    {
        return sum_amount;
    }
    /**
     * @param sumAmount the sum_amount to set
     */
    public void setSum_amount(String sumAmount)
    {
        sum_amount = sumAmount;
    }
    /**
     * @return the contact_name
     */
    public String getContact_name()
    {
        return contact_name;
    }
    /**
     * @param contactName the contact_name to set
     */
    public void setContact_name(String contactName)
    {
        contact_name = contactName;
    }
    /**
     * @return the ordernumber
     */
    public String getOrdernumber()
    {
        return ordernumber;
    }
    /**
     * @param ordernumber the ordernumber to set
     */
    public void setOrdernumber(String ordernumber)
    {
        this.ordernumber = ordernumber;
    }
    /**
     * @return the item_sku
     */
    public String getItem_sku()
    {
        return item_sku;
    }
    /**
     * @param itemSku the item_sku to set
     */
    public void setItem_sku(String itemSku)
    {
        item_sku = itemSku;
    }
    /**
     * @return the account
     */
    public String getAccount()
    {
        return account;
    }
    /**
     * @param account the account to set
     */
    public void setAccount(String account)
    {
        this.account = account;
    }
    /**
     * @return the reason
     */
    public String getReason()
    {
        return reason;
    }
    /**
     * @param reason the reason to set
     */
    public void setReason(String reason)
    {
        this.reason = reason;
    }
    /**
     * @return the credit_date
     */
    public String getCredit_date()
    {
        return credit_date;
    }
    /**
     * @param creditDate the credit_date to set
     */
    public void setCredit_date(String creditDate)
    {
        credit_date = creditDate;
    }
    /**
     * @return the amount
     */
    public String getAmount()
    {
        return amount;
    }
    /**
     * @param amount the amount to set
     */
    public void setAmount(String amount)
    {
        this.amount = amount;
    }
    /**
     * @return the cat2
     */
    public String getCat2()
    {
        return cat2;
    }
    /**
     * @param cat2 the cat2 to set
     */
    public void setCat2(String cat2)
    {
        this.cat2 = cat2;
    }
    /**
     * @return the cat1
     */
    public String getCat1()
    {
        return cat1;
    }
    /**
     * @param cat1 the cat1 to set
     */
    public void setCat1(String cat1)
    {
        this.cat1 = cat1;
    }
    /**
     * @return the serialversionuid
     */
    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }
    /**
     * @param id the id to set
     */
    public void setId(Long id)
    {
        this.id = id;
    }
    /**
     * @return the id
     */
    public Long getId()
    {
        return id;
    }
	public String getSales_price() {
		return sales_price;
	}
	public void setSales_price(String sales_price) {
		this.sales_price = sales_price;
	}
	public String getShip_to_state() {
		return ship_to_state;
	}
	public void setShip_to_state(String ship_to_state) {
		this.ship_to_state = ship_to_state;
	}
    
    
    
    
    


    
}
