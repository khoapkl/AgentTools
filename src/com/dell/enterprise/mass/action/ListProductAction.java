package com.dell.enterprise.mass.action;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.actions.DispatchAction;

import com.dell.enterprise.agenttool.model.Agent;
import com.dell.enterprise.agenttool.util.Constants;
import com.dell.enterprise.agenttool.util.DAOUtils;
import com.dell.enterprise.mass.form.ListProductForm;
import com.dell.enterprise.mass.util.MassContansts;
import com.dell.enterprise.mass.util.MassUtils;
import com.dell.enterprise.mass.util.MassContansts.TYPE_FIELD_INVENTORY_RECEIVING_TABLE;

public class ListProductAction extends RequiredLoginAction {

//  private static final Logger LOGGER = Logger
//          .getLogger("com.dell.enterprise.agenttool.actions.ImportData");
    

	
    public ActionForward receivingList(final ActionMapping mapping,
            final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
    	List<Map<String, Object>> allProducts = null;
    	ActionForward forward = null;
    	try {
    		System.out.println("receivingList function 2");
            ListProductForm receivingForm = (ListProductForm) form;
            
            System.out.println("receivingForm.getSku: " + receivingForm.getSku());
            System.out.println("receivingForm.getCurrentPage: " + receivingForm.getCurrentPage());
            System.out.println("receivingForm.getRecordPerPage: " + receivingForm.getRecordPerPage());
            System.out.println("receivingForm.getTotalPage: " + receivingForm.getTotalPage());
            receivingForm.setInventoryPage(false);
            
            boolean isInventory = false;
            
            setValidValue(receivingForm);
            setValuesPaging(receivingForm, getTotalProductRecords(isInventory, receivingForm.getSku()));
            setValidValue(receivingForm);
            
            allProducts = queryProductsPagination(receivingForm, isInventory, true);
		} catch (Exception e) {
			addActionErrorString(request, e.getMessage());
		}
        
        
        request.setAttribute("listProducts", allProducts);
        request.setAttribute("listHeaders", MassContansts.ALL_FIELD_IN_RECEIVING_TABLE_HEADER);
        request.setAttribute("listRecordsPerPage", MassContansts.RECORD_PER_PAGE);
        
        forward = mapping.findForward("agenttool.view.receiving.list");
        return forward;
    }
    
    
    //FIXME: Change to inventory instead of receiving product
    public ActionForward inventoryList(final ActionMapping mapping,
            final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
    	System.out.println("inventoryList function 2");
        ActionForward forward;
        
        System.out.println("inventoryList function 2");
        ListProductForm receivingForm = null;
        List<Map<String, Object>> allProducts = null;
    	try {
    		receivingForm = (ListProductForm) form;
            System.out.println("inventoryList.getSku: " + receivingForm.getSku());
            System.out.println("inventoryList.getCurrentPage: " + receivingForm.getCurrentPage());
            System.out.println("inventoryList.getRecordPerPage: " + receivingForm.getRecordPerPage());
            System.out.println("inventoryList.getTotalPage: " + receivingForm.getTotalPage());
            receivingForm.setInventoryPage(true);
            
            boolean isInventory = true;
            
            setValidValue(receivingForm);
            setValuesPaging(receivingForm, getTotalProductRecords(isInventory,  receivingForm.getSku()));
            setValidValue(receivingForm);
            
            allProducts = queryProductsPagination(receivingForm, isInventory, true);
		} catch (Exception e) {
			addActionErrorString(request, e.getMessage());
		}
        
        request.setAttribute("listProducts", allProducts);
        request.setAttribute("listHeaders", MassContansts.ALL_FIELD_IN_INVENTORY_TABLE_HEADER);
        request.setAttribute("listRecordsPerPage", MassContansts.RECORD_PER_PAGE);
        
        forward = mapping.findForward("agenttool.view.inventory.list");
        return forward;
    }
    
    
    
    
    public ActionForward exportExcel(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
    	boolean isInventory = false;
    	try {
    		System.out.println("exportExcel function");
            ListProductForm receivingForm = (ListProductForm) form;
            
            System.out.println("exportExcel.getSku: " + receivingForm.getSku());
            System.out.println("exportExcel.getCurrentPage: " + receivingForm.getCurrentPage());
            System.out.println("exportExcel.getRecordPerPage: " + receivingForm.getRecordPerPage());
            System.out.println("exportExcel.getTotalPage: " + receivingForm.getTotalPage());
            System.out.println("exportExcel.inventoryPage: " + receivingForm.isInventoryPage());

            isInventory = receivingForm.isInventoryPage();
            System.out.println("IS INVENTORY EXPORT: " + isInventory);
            
            setValidValue(receivingForm);
            setValuesPaging(receivingForm, getTotalProductRecords(isInventory, receivingForm.getSku()));
            setValidValue(receivingForm);
            
            
            List<Map<String, Object>> products = queryProductsPagination(receivingForm, isInventory, false);
            String[] headers = null;        
            String fileName = null;
            String sheetName = null;
            if (isInventory){
            	headers = MassContansts.ALL_FIELD_IN_INVENTORY_TABLE_HEADER;
            	fileName = MassContansts.INVENTORY_EXPORT_FILENAME;
            	sheetName = MassContansts.INVENTORY_SHEET_NAME;
            } else{
            	headers = MassContansts.ALL_FIELD_IN_RECEIVING_TABLE_HEADER;
            	fileName = MassContansts.RECEIVING_EXPORT_FILENAME;
            	sheetName = MassContansts.RECEIVING_SHEET_NAME;
            }
            
            HSSFWorkbook workbook = MassUtils.createWorkbookFromList(products, headers, sheetName);
            response.setHeader("Content-Disposition", String.format("attachment; filename=%s", fileName));
            ServletOutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush();
            out.close();
		} catch (Exception e) {
			System.out.println("ERROR:" + e.getMessage());
			addActionErrorString(request, e.getMessage());
		}
    	request.setAttribute("listProducts", new ArrayList<Map<String, Object>>());
        request.setAttribute("listHeaders", MassContansts.ALL_FIELD_IN_INVENTORY_TABLE_HEADER);
        request.setAttribute("listRecordsPerPage", MassContansts.RECORD_PER_PAGE);
        
    	if (isInventory){
    		return mapping.findForward("agenttool.view.inventory.list");
    	}
        return mapping.findForward("agenttool.view.receiving.list");
    }
    
    
    
    public void setValuesPaging(ListProductForm form, int totalRecord){
    	form.setTotalRecord(totalRecord);
    	form.setTotalPage((totalRecord/form.getRecordPerPage()) + 1);
    }
    
    /**
	 * Add error message in struts1
	 * @param request: Current request
	 * @param error: Error message
	 */
	public void addActionErrorString(HttpServletRequest request, String error){
		ActionErrors errors = new ActionErrors();
		ActionMessage ms = new ActionMessage(error, false);
		errors.add("Error" + MassUtils.random(), ms);
		addErrors(request, errors);
	}
    
    
    public void setValidValue(ListProductForm form){
    	
    	if (form.getFirstPage() < 0){
    		form.setFirstPage(0);
    	}
    	
    	if (form.getLastPage() < 0){
    		form.setLastPage(0);
    	}
    	
    	if (form.getTotalRecord() < 0){
    		form.setTotalRecord(0);
    	}
    	
    	
    	boolean isExistsInListRecord = false;
    	for (int i = 0; i < MassContansts.RECORD_PER_PAGE.length; i++) {
    		if (MassContansts.RECORD_PER_PAGE[i] == form.getRecordPerPage()){
    			isExistsInListRecord = true;
    			break;
    		}
		}
    	
    	if (!isExistsInListRecord || form.getRecordPerPage() <= 0){
        	form.setRecordPerPage(MassContansts.DEFAULT_RECORD_PER_PAGE);
        }
        
        if (form.getTotalPage() < 0){
            form.setTotalPage(0);
        }
        
        if (form.getCurrentPage() <= 0){
            form.setCurrentPage(1);
        }
        
        if (form.getCurrentPage() > form.getTotalPage()){
        	form.setCurrentPage(form.getTotalPage());
        }
        
        
    }
    
    
    public int getTotalProductRecords(boolean isInventoryTable, String SKU) throws Exception{
    	DAOUtils daoUtil = DAOUtils.getInstance();
        Connection conn = null;
        ResultSet rs = null;
        int records = 0;
        try {
//            conn = MassUtils.getInstanceMYSQLForTest();
        	conn = daoUtil.getConnection();
            
            String tableName = null;
            if (isInventoryTable){
            	tableName = "estore_inventory";
            } else{
            	tableName = "receiving";
            }     
            
            
            String query = null;
            if (StringUtils.isEmpty(SKU)){
                query = "";
            } else{
                query = " AND item_SKU like '%"+SKU+"%' ";
            }
            
            String sql = String.format(daoUtil.getString("mass.sql.inventory_receiving.count"), tableName, query);
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()){
            	records = rs.getInt(1);
            }

        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException sqlE) {
                sqlE.getStackTrace();
            }
        }
        
        
		return records;
    }
    
    
    public List<Map<String, Object>> queryProductsPagination(ListProductForm form, boolean isInventoryTable, boolean isHavePaging) throws Exception{
        
        List<Map<String, Object>> allProducts = new ArrayList<Map<String, Object>>();
        
        DAOUtils daoUtil = DAOUtils.getInstance();
        Connection conn = null;
        ResultSet rs = null;
        try {
//            conn = MassUtils.getInstanceMYSQLForTest();
        	conn = daoUtil.getConnection();
            System.out.println("Connection ok getListObjectFromListSKU:" + String.valueOf(conn != null));
            
            boolean isAllSKU = true;
            if (form.getSku() != null && !form.getSku().equalsIgnoreCase("")){
                isAllSKU = false;
            }
            
            String query = null;
            if (isAllSKU){
                query = "";
            } else{
                query = " AND item_SKU like '%"+form.getSku()+"%' ";
            }
            
            String tableName = null;
            String allFieldJoin = null;
            String[] allField = null;
            String orderBy = null;
            if (isInventoryTable){
            	tableName = "estore_inventory";
            	allFieldJoin = StringUtils.join(MassContansts.ALL_FIELD_IN_INVENTORY_TABLE, ",");
            	allField = MassContansts.ALL_FIELD_IN_INVENTORY_TABLE;
            	orderBy = " ORDER BY updateddate DESC ";
            } else{
            	tableName = "receiving";
            	allFieldJoin = StringUtils.join(MassContansts.ALL_FIELD_IN_RECEIVING_TABLE, ",");
            	allField = MassContansts.ALL_FIELD_IN_RECEIVING_TABLE;
            	orderBy = " ORDER BY impDate DESC ";
            }     
            
            
            String sql = String.format(daoUtil.getString("mass.sql.inventory_receiving.list.without.paging"), allFieldJoin, tableName, query, orderBy);
            
            //FIXME: Break when query without paging
//            if (isHavePaging){
            	sql = String.format(daoUtil.getString("mass.sql.inventory_receiving.list.with.paging"), allFieldJoin, tableName, query, orderBy);
//            }
                        
            PreparedStatement pstmt = conn.prepareStatement(sql);
            int from = (form.getCurrentPage() - 1) * form.getRecordPerPage();
            int to = from + form.getRecordPerPage();
            
          //FIXME: Break when query without paging
//            if (isHavePaging){
            	pstmt.setInt(1, form.getRecordPerPage());
                pstmt.setInt(2, from );
//            }
            
            System.out.println("SQL AFTER SET PARAM:" + pstmt);
            rs = pstmt.executeQuery();
            while (rs.next()) {
            	Map<String, Object> item = new LinkedHashMap<String, Object>();
            	boolean haveData = false;
            	for (int i = 0; i < allField.length; i++) {
					String field = allField[i];
					
					Object value = rs.getObject(field);
					if (Arrays.asList(MassContansts.TYPE_FIELD_INVENTORY_RECEIVING_TABLE.DATE_FIELDS).contains(field)){
						value = MassUtils.getDateStringFormat(rs.getDate(field) , MassContansts.DATE_FORMAT);
					} else if (Arrays.asList(MassContansts.TYPE_FIELD_INVENTORY_RECEIVING_TABLE.DOUBLE_FIELDS).contains(field)){
						value = MassUtils.getNumberStringFormat(rs.getDouble(field), MassContansts.NUMBER_FORMAT);
					}
					
					item.put(field, value);
					haveData = true;
				}
            	
            	if (haveData){
            		allProducts.add(item);
            	}
                
            }
            
//            System.out.println("OK select database for export");
                
        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                /*
                if (cst != null)
                    cst.close();
                if (stmt != null)
                    stmt.close();*/
                if (conn != null)
                    conn.close();
            } catch (SQLException sqlE) {
                sqlE.getStackTrace();
            }
        }
        
        return allProducts;
    }
    
    
    
    
    


}
