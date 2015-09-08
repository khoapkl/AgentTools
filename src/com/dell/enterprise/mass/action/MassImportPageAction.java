package com.dell.enterprise.mass.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.upload.FormFile;

import com.dell.enterprise.agenttool.model.Agent;
import com.dell.enterprise.agenttool.util.Constants;
import com.dell.enterprise.agenttool.util.DAOUtils;
import com.dell.enterprise.mass.util.MassContansts;
import com.dell.enterprise.mass.util.MassContansts.IMPORT_PAGE_TYPE;

public class MassImportPageAction extends RequiredLoginAction {

//	private static final Logger LOGGER = Logger
//			.getLogger("com.dell.enterprise.agenttool.actions.ImportData");
	
	public ActionForward importExcelPage(final ActionMapping mapping,
			final ActionForm form, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {
		System.out.println("importExcelPage");
		ActionForward forward;
		request.setAttribute(MassContansts.IMPORT_PAGE_TYPE_KEY, MassContansts.IMPORT_PAGE_TYPE.EXCEL_TEMPLATE);
		System.out.println("EXCEL TEMPLATE:" + MassContansts.IMPORT_PAGE_TYPE.EXCEL_TEMPLATE);
		
		request.setAttribute(MassContansts.BREAD_CRUMB_KEY, MassContansts.BREAD_CRUMB_UPLOAD_EXCEL_INVENTORY);
		forward = mapping.findForward("mass.view.import.incoming.page");
		return forward;
	}
	
	public ActionForward importReceivingPage(final ActionMapping mapping,
            final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        System.out.println("importReceivingPage function 2");
        ActionForward forward;
        request.setAttribute(MassContansts.IMPORT_PAGE_TYPE_KEY, MassContansts.IMPORT_PAGE_TYPE.RECEIVING_TEMPLATE);
		System.out.println("RECEIVING_TEMPLATE:" + MassContansts.IMPORT_PAGE_TYPE.RECEIVING_TEMPLATE);
		request.setAttribute(MassContansts.BREAD_CRUMB_KEY, MassContansts.BREAD_CRUMB_UPLOAD_TXT_RECEIVING);
		
        forward = mapping.findForward("mass.view.import.receiving.page");
        return forward;
    }
	


}
