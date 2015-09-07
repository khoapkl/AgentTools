/*
 * FILENAME
 *     CleanHoldOrdersListener.java
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

package com.dell.enterprise.agenttool.util;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.dell.enterprise.agenttool.model.OrdersHeld;
import com.dell.enterprise.agenttool.services.BasketService;
import com.dell.enterprise.agenttool.services.OrdersHeldService;

//
// IMPORTS
// NOTE: Import specific classes without using wildcards.
//

public class SystemsListener implements ServletContextListener
{

    Timer timer = new Timer();
    public static final int SECOND = 1000;
    public static final int MINUTE = SECOND * 60;
    public static final int HOUR = MINUTE * 60;
    public static final int DAY = HOUR * 24;

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        // TODO Auto-generated method stub
        timer.cancel();
    }

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        // TODO Auto-generated method stub
        ToDoTaskCleanOrderHeld toDoTaskCleanOrderHeld = new ToDoTaskCleanOrderHeld(sce.getServletContext());
        ToDoTaskCleanBasket toDoTaskCleanBasket = new ToDoTaskCleanBasket(sce.getServletContext());

        timer.schedule(toDoTaskCleanOrderHeld, 0, 45 * MINUTE);
        timer.schedule(toDoTaskCleanBasket, 0, 45 * MINUTE);

    }
}

class ToDoTaskCleanOrderHeld extends TimerTask
{
    private static final Logger LOGGER = Logger.getLogger("com.dell.enterprise.agenttool.util.ToDoTask");
    @SuppressWarnings("unused")
    private ServletContext context = null;

    public ToDoTaskCleanOrderHeld(ServletContext context)
    {
        this.context = context;
    }

    @SuppressWarnings("deprecation")
    public void run()
    {
        Date currentDate = new Date();
        LOGGER.info("Execute ToDoTask - run - " + currentDate.toString() + "- Hour " + currentDate.getHours());

        //Orders Hold
        OrdersHeldService ordersHeldService = new OrdersHeldService();
        List<OrdersHeld> listOrdersHeld = ordersHeldService.getOrdersHeld();

        for (OrdersHeld ordersHeld : listOrdersHeld)
        {
            if (ordersHeld.getExp_days() == 1)
            {
                //Send Mail in 1AM
               // if (currentDate.getHours() == 23)
              //  {
                    //Send email for customer expires
                    //ordersHeldService.sendMailOrderHeld(ordersHeld);
                //}
            }
            else
            {
                LOGGER.info("Delete Order Hold - OrdersHeld ID : " + ordersHeld.getHeld_order());
                //Delete Order Hold
                ordersHeldService.deleteOrderHeld(ordersHeld);
            }
        }
    }
}

class ToDoTaskCleanBasket extends TimerTask
{
    private static final Logger LOGGER = Logger.getLogger("com.dell.enterprise.agenttool.util.ToDoTaskCleanBasket");
    @SuppressWarnings("unused")
    private ServletContext context = null;

    public ToDoTaskCleanBasket(ServletContext context)
    {
        this.context = context;
    }

    @SuppressWarnings("deprecation")
    public void run()
    {
        Date currentDate = new Date();
        int minutes = currentDate.getMinutes();
        int hours = currentDate.getHours();
        LOGGER.info("Execute ToDoTaskCleanBasket - run - " + currentDate.toString() + "- Hour : " + hours + " - Minute : " + minutes);

        BasketService basketService = new BasketService();
        basketService.cleanBasketItemExp();
    }
}