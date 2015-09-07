package com.dell.enterprise.agenttool.actions;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.dell.enterprise.agenttool.model.Agent;
import com.dell.enterprise.agenttool.model.Customer;
import com.dell.enterprise.agenttool.model.Note;
import com.dell.enterprise.agenttool.services.AuthenticationService;
import com.dell.enterprise.agenttool.services.CustomerServices;
import com.dell.enterprise.agenttool.services.ProductServices;
import com.dell.enterprise.agenttool.services.ShopperManagerService;
import com.dell.enterprise.agenttool.util.Constants;

/**
 * @author hungnguyen
 * 
 * @version $Id$
 **/
public class Authentication extends DispatchAction
{

    private static final Logger LOGGER = Logger.getLogger("com.dell.enterprise.agenttool.actions.Authentication");

    /**
     * {@inheritDoc}
     */
    public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response)
    {
        ActionForward forward;
        HttpSession session = request.getSession();

        try
        {
            String method = this.getMethodName(mapping, form, request, response, Constants.PARAMETER);

            if (method == null || "".equals(method))
            {
                if (session.getAttribute(Constants.AGENT_INFO) != null)
                {
                    forward = mapping.findForward(Constants.LOGIN_SUCCESS);
                }
                else
                {
                    forward = mapping.findForward(Constants.LOGIN_VIEW);
                    Cookie[] cookies = request.getCookies();
                    if(cookies!=null){
	                    for (int i = 0; i < cookies.length; i++)
	                    {
	                        if (cookies[i].getName().equals("userLevel"))
	                        {
	                            if (cookies[i].getValue().equals("login.do"))
	                                forward = mapping.findForward(Constants.CUSTOMER_SESSION_TIMEOUT);
	                        }
	                    }
                    }
                }
            }
            else
            {
                forward = this.dispatchMethod(mapping, form, request, response, method);
            }
        }
        catch (Exception e)
        {
        	e.printStackTrace();
            forward = mapping.findForward(Constants.ERROR_PAGE_VIEW);
        }

        return forward;
    }

    /**
     * @param mapping
     *            ActionMapping
     * @param form
     *            ActionForm
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     * @return ActionForward
     * @throws Exception
     *             when an exception occurs
     **/
    public final ActionForward login(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception
    {
        String forward;

        try
        {
            HttpSession session = request.getSession();

            String userName = request.getParameter("userName");
            String password = request.getParameter("password");
            if (!userName.equals("") && !password.equals(""))
            {
                AuthenticationService service = new AuthenticationService();
                Agent agent = service.authenticate(userName, password);

                if (agent != null)
                {
                    forward = Constants.LOGIN_SUCCESS;
                    session.setAttribute(Constants.AGENT_INFO, agent);
                    session.setAttribute(Constants.SHOPPER_ID, agent.getAgentId());
                    session.setAttribute(Constants.SHOPPER_NAME, agent.getUserName());
                    session.setAttribute(Constants.IS_ADMIN, Boolean.valueOf(agent.isAdmin()));
                    session.removeAttribute(Constants.IS_CUSTOMER);
                    session.setAttribute(Constants.USER_LEVEL, agent.getUserLevel());

                    session.removeAttribute(Constants.OLD_SHOPPER_ID);
                    session.removeAttribute(Constants.OLD_SHOPPER_NAME);
                    session.removeAttribute(Constants.IS_SHOPPER);
                    //                session.removeAttribute(Constants.IS_CUSTOMER_NEW);
                    Cookie[] cookies = request.getCookies();
                    if(cookies!=null){
	                    for (int i = 0; i < cookies.length; i++)
	                    {
	                        if (cookies[i].getValue().equals(session.getId()))
	                        {
	                            Cookie myCookie = new Cookie("userLevel", "authenticate.do");
	                            response.addCookie(myCookie);
	
	                        }
	                    }
                    }
                }
                else
                {
                    LOGGER.info("Invalid");
                    request.setAttribute("invalid", "");
                    forward = Constants.LOGIN_FAILURE;
                }
            }
            else
            {
                LOGGER.info("Invalid");
                request.setAttribute("invalid", "");
                forward = Constants.LOGIN_FAILURE;
            }
        }
        catch (NullPointerException e)
        {
            forward = Constants.LOGIN_VIEW;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            forward = Constants.ERROR_PAGE_VIEW;
        }

        return mapping.findForward(forward);
    }

    /**
     * @param mapping
     *            ActionMapping
     * @param form
     *            ActionForm
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     * @return ActionForward
     * @throws Exception
     *             when an exception occurs
     **/
    public final ActionForward logout(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception
    {
        String forward;

        try
        {

            HttpSession session = request.getSession();
            Object isCustomer = session.getAttribute(Constants.IS_CUSTOMER);
            /*
             * Vinhhq
             */
            if (session.getAttribute(Constants.SHOPPER_ID) != null)
            {
                String shopper_id = session.getAttribute(Constants.SHOPPER_ID).toString();
                ProductServices productServices = new ProductServices();
                if (isCustomer != null)
                {
                    productServices.clearOrder(shopper_id, false);
                }
                else
                {
                    productServices.clearOrder(shopper_id, true);
                }

            }

            session.removeAttribute(Constants.AGENT_INFO);
            session.removeAttribute(Constants.SHOPPER_ID);
            session.removeAttribute(Constants.IS_ADMIN);
            session.removeAttribute(Constants.IS_CUSTOMER);
            session.removeAttribute(Constants.USER_LEVEL);
            session.removeAttribute(Constants.OLD_SHOPPER_ID);
            session.removeAttribute(Constants.OLD_SHOPPER_NAME);
            session.removeAttribute(Constants.IS_SHOPPER);
            session.removeAttribute(Constants.CUSTOMER_MANAGE);
            forward = Constants.LOGIN_VIEW;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            forward = Constants.ERROR_PAGE_VIEW;
        }

        return mapping.findForward(forward);
    }

    /**
     * @param mapping
     *            ActionMapping
     * @param form
     *            ActionForm
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     * @return ActionForward
     * @throws Exception
     *             when an exception occurs
     **/
    public final ActionForward linkfooter1(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception
    {
        String forward;

        try
        {
            forward = Constants.LINK_FOOTER_DEFAULT;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            forward = Constants.ERROR_PAGE_VIEW;
        }

        return mapping.findForward(forward);
    }

    /**
     * @param mapping
     *            ActionMapping
     * @param form
     *            ActionForm
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     * @return ActionForward
     * @throws Exception
     *             when an exception occurs
     **/
    public final ActionForward linkfooter2(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception
    {
        String forward;

        try
        {
            forward = Constants.LINK_FOOTER_DEFAULT1;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            forward = Constants.ERROR_PAGE_VIEW;
        }

        return mapping.findForward(forward);
    }

    /**
     * @param mapping
     *            ActionMapping
     * @param form
     *            ActionForm
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     * @return ActionForward
     * @throws Exception
     *             when an exception occurs
     **/
    public final ActionForward linkfooter3(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception
    {
        String forward;

        try
        {
            forward = Constants.LINK_FOOTER_DEFAULT2;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            forward = Constants.ERROR_PAGE_VIEW;
        }

        return mapping.findForward(forward);
    }

    /**
     * @param mapping
     *            ActionMapping
     * @param form
     *            ActionForm
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     * @return ActionForward
     * @throws Exception
     *             when an exception occurs
     **/
    public ActionForward shoppingAs(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception
    {
        String forward;
        HttpSession session = request.getSession();
        try
        {
            if (session.getAttribute(Constants.AGENT_INFO) != null)
            {
                String shopperID = request.getParameter(Constants.SHOPPER_ID);
                String shopperName = request.getParameter(Constants.SHOPPER_NAME);
                session.setAttribute(Constants.OLD_SHOPPER_ID, String.valueOf(session.getAttribute(Constants.SHOPPER_ID)));
                session.setAttribute(Constants.SHOPPER_ID, shopperID);
                session.setAttribute(Constants.OLD_SHOPPER_NAME, String.valueOf(session.getAttribute(Constants.SHOPPER_NAME)));
                session.setAttribute(Constants.SHOPPER_NAME, shopperName);
                session.setAttribute(Constants.IS_SHOPPER, true);
                forward = Constants.LOGIN_SUCCESS;
            }
            else
                forward = Constants.LOGIN_VIEW;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            forward = Constants.ERROR_PAGE_VIEW;
        }

        return mapping.findForward(forward);
    }

    /**
     * @param mapping
     *            ActionMapping
     * @param form
     *            ActionForm
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     * @return ActionForward
     * @throws Exception
     *             when an exception occurs
     **/
    public ActionForward shopAsAgent(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception
    {
        String forward;
        HttpSession session = request.getSession();
        Object isCustomer = session.getAttribute(Constants.IS_CUSTOMER);
        try
        {
            if (session.getAttribute(Constants.AGENT_INFO) != null)
            {
                String shopperName = String.valueOf(session.getAttribute(Constants.OLD_SHOPPER_NAME));
                String shopperId = String.valueOf(session.getAttribute(Constants.SHOPPER_ID));
                String shopperID = "";

                if (session.getAttribute(Constants.OLD_SHOPPER_ID) != null)
                {
                    //shopperID = String.valueOf(session.getAttribute(Constants.OLD_SHOPPER_ID));
                    shopperID = session.getAttribute(Constants.SHOPPER_ID).toString();
                }
                else
                {
                    Agent agent = (Agent) session.getAttribute(Constants.AGENT_INFO);
                    shopperID = Integer.toString(agent.getAgentId());
                }

                session.setAttribute(Constants.SHOPPER_ID, shopperID);
                session.setAttribute(Constants.SHOPPER_NAME, shopperName);
                //            session.setAttribute(Constants.IS_CUSTOMER, session.getAttribute(Constants.IS_CUSTOMER_NEW));

                session.removeAttribute(Constants.OLD_SHOPPER_ID);
                session.removeAttribute(Constants.OLD_SHOPPER_NAME);
                session.removeAttribute(Constants.IS_SHOPPER);
                session.removeAttribute(Constants.CUSTOMER_MANAGE);

                ShopperManagerService service = new ShopperManagerService();

                com.dell.enterprise.agenttool.model.Shopper shopper = (com.dell.enterprise.agenttool.model.Shopper) service.getShopperDetails(shopperId);
               
                List<Note> notes = service.searchNotes(shopperId, 1);
                int totalRecord=service.getTotalRecord();
                int receipts;
                if (isCustomer!=null && ((Boolean) isCustomer).booleanValue()){
                	receipts = service.getShopperReceipts_AgentStore(shopperId);
                }
                else{
                	receipts = service.getShopperReceipts(shopperId);
                }
                
                request.setAttribute(Constants.SHOPPER_INFO, shopper);
                request.setAttribute(Constants.SHOPPER_NOTES, notes);
                request.setAttribute(Constants.SHOPPER_RECEIPTS, receipts);
                request.setAttribute(Constants.ORDER_TOTAL, totalRecord);
                request.setAttribute(Constants.ORDER_NUMBER_PAGE, 1);
                forward = Constants.SHOPPER_VIEW;
            }
            else
                forward = Constants.LOGIN_VIEW;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            forward = Constants.ERROR_PAGE_VIEW;
        }

        return mapping.findForward(forward);
    }

    /**
     * @param mapping
     *            ActionMapping
     * @param form
     *            ActionForm
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     * @return ActionForward
     * @throws Exception
     *             when an exception occurs
     **/
    public ActionForward checkoutAsShopper(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception
    {
        HttpSession session = request.getSession();
        if (session.getAttribute(Constants.AGENT_INFO) != null)
        {
            try
            {
                String shopperID = request.getParameter(Constants.SHOPPER_ID);
                String shopperName = request.getParameter(Constants.SHOPPER_NAME);
                session.setAttribute(Constants.OLD_SHOPPER_ID, String.valueOf(session.getAttribute(Constants.SHOPPER_ID)));
                session.setAttribute(Constants.SHOPPER_ID, shopperID);
                session.setAttribute(Constants.OLD_SHOPPER_NAME, String.valueOf(session.getAttribute(Constants.SHOPPER_NAME)));
                session.setAttribute(Constants.SHOPPER_NAME, shopperName);
                session.setAttribute(Constants.IS_SHOPPER, true);
                //session.removeAttribute("CUSTOMER_CHECKOUT_SESSION")	;
                
                CustomerServices customerServices = new CustomerServices();
                Customer customer = customerServices.getCustomerByShopperID(shopperID);
                
              //set is session because method no pass page Customer information
                session.removeAttribute("CUSTOMER_CHECKOUT_SESSION");
                session.setAttribute("CUSTOMER_CHECKOUT_SESSION", customer);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            Checkout checkout = new Checkout();
            return checkout.showCheckout(mapping, form, request, response);
        }
        else
            return mapping.findForward(Constants.LOGIN_VIEW);
    }

    /**
     * @param mapping
     *            ActionMapping
     * @param form
     *            ActionForm
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     * @return ActionForward
     * @throws Exception
     *             when an exception occurs
     **/
    public ActionForward editAsShopper(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception
    {
        HttpSession session = request.getSession();
        if (session.getAttribute(Constants.AGENT_INFO) != null)
        {
            try
            {
                String shopperID = request.getParameter(Constants.SHOPPER_ID);
                String shopperName = request.getParameter(Constants.SHOPPER_NAME);
                session.setAttribute(Constants.OLD_SHOPPER_ID, String.valueOf(session.getAttribute(Constants.SHOPPER_ID)));
                session.setAttribute(Constants.SHOPPER_ID, shopperID);
                session.setAttribute(Constants.OLD_SHOPPER_NAME, String.valueOf(session.getAttribute(Constants.SHOPPER_NAME)));
                session.setAttribute(Constants.SHOPPER_NAME, shopperName);
                session.setAttribute(Constants.IS_SHOPPER, true);
                session.setAttribute(Constants.CUSTOMER_MANAGE, true);
                //session.setAttribute("editShopper", "editShopper");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            Shopper shopper = new Shopper();
            return shopper.prepareCheckout(mapping, form, request, response);
        }
        else
            return mapping.findForward(Constants.LOGIN_VIEW);
    }
    
    public ActionForward redirectAfterUpdateCustomer(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception
    {
        String forward;
        HttpSession session = request.getSession();
        Object isCustomer = session.getAttribute(Constants.IS_CUSTOMER);
        try
        {
            if (session.getAttribute(Constants.AGENT_INFO) != null)
            {
                String shopperId = String.valueOf(session.getAttribute(Constants.SHOPPER_ID));
               /* String shopperID = "";

                if (session.getAttribute(Constants.OLD_SHOPPER_ID) != null)
                {
                    //shopperID = String.valueOf(session.getAttribute(Constants.OLD_SHOPPER_ID));
                    shopperID = session.getAttribute(Constants.SHOPPER_ID).toString();
                }*/

                ShopperManagerService service = new ShopperManagerService();

                com.dell.enterprise.agenttool.model.Shopper shopper = (com.dell.enterprise.agenttool.model.Shopper) service.getShopperDetails(shopperId);
               
                List<Note> notes = service.searchNotes(shopperId, 1);
                int totalRecord=service.getTotalRecord();
                int receipts;
                if (isCustomer!=null && ((Boolean) isCustomer).booleanValue()){
                	receipts = service.getShopperReceipts_AgentStore(shopperId);
                }
                else{
                	receipts = service.getShopperReceipts(shopperId);
                }
                //int receipts = service.getShopperReceipts(shopperId);
                request.setAttribute(Constants.SHOPPER_INFO, shopper);
                request.setAttribute(Constants.SHOPPER_NOTES, notes);
                request.setAttribute(Constants.SHOPPER_RECEIPTS, receipts);
                request.setAttribute(Constants.ORDER_TOTAL, totalRecord);
                request.setAttribute(Constants.ORDER_NUMBER_PAGE, 1);
                forward = Constants.SHOPPER_VIEW;
            }
            else
                forward = Constants.LOGIN_VIEW;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            forward = Constants.ERROR_PAGE_VIEW;
        }

        return mapping.findForward(forward);
    }
}
