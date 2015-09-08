package com.dell.enterprise.mass.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.upload.FormFile;

import com.dell.enterprise.agenttool.model.Agent;
import com.dell.enterprise.agenttool.services.SecurityService;
import com.dell.enterprise.agenttool.util.Constants;
import com.dell.enterprise.agenttool.util.DAOUtils;

public class RequiredLoginAction extends DispatchAction {

	
	public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws NoSuchAlgorithmException, UnsupportedEncodingException
	 {
	        ActionForward forward;
	        HttpSession session = request.getSession();
	        Agent agent = (Agent)session.getAttribute(Constants.AGENT_INFO);
	        
	        
	        SecurityService sr = new SecurityService();
	        
	    	if ((agent != null) && (agent.isAdmin() || agent.getUserLevel().equalsIgnoreCase("A")))
	    	{
	    		 try{
	    	                String method = this.getMethodName(mapping, form, request, response, Constants.PARAMETER);
	    	                if (method == null || method.isEmpty())
	    	                {	
	    	                    request.setAttribute(Constants.IS_ADMIN, true);
	    	                    forward =  mapping.findForward("agenttool.agentSetup");
	    	                }
	    	                else
	    	                {
	    	                    forward = this.dispatchMethod(mapping, form, request, response, method);
	    	                }
	    	        }catch (Exception e)
	    	        {
	    	            forward = mapping.findForward(Constants.ERROR_PAGE_VIEW);
	    	        }    		
	    	}
	    	else
	    	{
	    		forward = mapping.findForward(Constants.LOGIN_VIEW);
	    	}
	           
	        return forward;
	    }
	
	
	
}
