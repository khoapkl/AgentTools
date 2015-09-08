package com.dell.enterprise.mass.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
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
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFCellUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
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
import com.dell.enterprise.agenttool.util.Constants;
import com.dell.enterprise.agenttool.util.DAOUtils;
import com.dell.enterprise.mass.form.ImportDataForm;
import com.dell.enterprise.mass.util.MassContansts;
import com.dell.enterprise.mass.util.MassUtils;
import com.dell.enterprise.mass.util.MassContansts.ERRORS;
import com.dell.enterprise.mass.util.MassContansts.IMPORT_PAGE_TYPE;
import com.dell.enterprise.mass.util.MassContansts.PRODUCT_FIELD_COMMON;
import com.dell.enterprise.mass.util.MassContansts.STATUS_PRODUCT_TYPE;

public class ImportDataAction extends RequiredLoginAction {
	
	
	/**
	 * Upload Incoming excel file template Action
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward uploadExcelFile(final ActionMapping mapping,
			final ActionForm form, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {
		System.out.println("Import data execute function 2");
		
		ActionForward forward;
		try {

			ImportDataForm fileUploadForm = (ImportDataForm) form;
			FormFile excelFileForm = fileUploadForm.getFile();
			
			String fileNameExt = null;
			if (excelFileForm != null && !StringUtils.isEmpty(excelFileForm.getFileName())) {
				fileNameExt = excelFileForm.getFileName();

				System.out.println("FILENAME:" + fileNameExt);

				InputStream excelFile = excelFileForm.getInputStream();

				boolean isReadCategory = true; // Read category in row 2, col 1
				boolean isExcel2003 = MassUtils.isExcel2003WithFilename(fileNameExt);

				Map<String, Object> headerDataInfo = getHeaderDataInfoExcel(
						excelFile, isExcel2003, isReadCategory);

				Map<String, Map<String, Object>> products = getIncomingDataInfoExcel(
						excelFile, headerDataInfo);

				MassUtils.printHashMapForTest(products, "Read excel");

				List<String> lstSKUFromProducts = getListSKUFromProducts(products);
				System.out.println("lstSKUFromProducts:" + lstSKUFromProducts);

				updateProductStatusFromListSKU(products, lstSKUFromProducts);

				System.out.println("AFTER UPDATE STATUS");
				MassUtils.printHashMapForTest(products, "After update status");
				
				String[] attributesOrder = null;
				Integer categoryId = (Integer)headerDataInfo.get("categoryId");
				for (Integer keyAttr : MassContansts.ATTRIBUTES_ORDER.keySet()) {
					if (keyAttr.intValue() == categoryId.intValue()){
						attributesOrder = MassContansts.ATTRIBUTES_ORDER.get(keyAttr);
					}
				}
				
				System.out.println("ATTRIBUTE ORDERS:" + Arrays.toString(attributesOrder));
				
				buildSQLQueries(products, attributesOrder);

				MassUtils.printHashMapForTestWithKey(products, "Print errors",
						MassContansts.IS_DATA_ERROR_ATTR);

				Map<String, Object> mapError = getErrorInfomation(products, headerDataInfo, fileNameExt, isExcel2003);
				
				int totalDataInsertSuccess = (Integer)mapError.get("totalDataInsertSuccess");
				int totalDataInsertError = (Integer)mapError.get("totalDataInsertError");
				int totalData = (Integer)mapError.get("totalData");
				String excelPathInServerForDownload = null;
				
				if (totalDataInsertError > 0){
					excelPathInServerForDownload = MassUtils.createExcelFile(getServlet(), fileNameExt, mapError.get("workbookError"));
				}
				
				System.out.println("TOTAL DATA ERROR:" + totalDataInsertError);
				
				setStatusImportTemplate(request, excelPathInServerForDownload, totalDataInsertSuccess, totalDataInsertError, totalData);
				
			} else {
				addActionErrorString(request, MassContansts.ERRORS.IMPORT_FILE);
			}
		} catch (Exception e) {
			addActionErrorString(request, MassUtils.getExceptionDescription(e));
		}
		
		request.setAttribute(MassContansts.IMPORT_PAGE_TYPE_KEY, MassContansts.IMPORT_PAGE_TYPE.EXCEL_TEMPLATE);
		request.setAttribute(MassContansts.BREAD_CRUMB_KEY, MassContansts.BREAD_CRUMB_UPLOAD_EXCEL_INVENTORY);
		
		forward = mapping.findForward("agenttool.view.import.data");
		return forward;
	}
	
	
	/**
	 * Upload text file template Action
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ActionForward uploadTextFile(final ActionMapping mapping,
			final ActionForm form, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {
		System.out.println("uploadTextFile function 2");
		ActionForward forward;
		HttpSession session = request.getSession();
		
		try {
			ImportDataForm fileUploadForm = (ImportDataForm) form;
			FormFile receivingWarehouseFile = fileUploadForm.getFile();
			System.out.println("GET FILE:"
					+ String.valueOf(receivingWarehouseFile != null));
			
			
			if (receivingWarehouseFile != null && !StringUtils.isEmpty(receivingWarehouseFile.getFileName())){
				Map<String, Object> txtObj = getListSKUFromTextFile(receivingWarehouseFile
						.getInputStream());
				List<String> lstSKU = (List<String>) txtObj.get("lstSKU");
				
				Map<String, Map<String, Object>> lstSKUObj = (Map<String, Map<String, Object>>) txtObj
						.get("lstSKUObj");

				MassUtils.printHashMapForTest(lstSKUObj, "Before update status");
				updateProductStatusFromListSKU(lstSKUObj, lstSKU);
				MassUtils.printHashMapForTest(lstSKUObj, "After update status");

				buildSQLQueriesReceiving(lstSKUObj);
				
				MassUtils.printHashMapForTestWithKey(lstSKUObj, "Print errors",
						MassContansts.IS_DATA_ERROR_ATTR);
				
				
				Map<String, Object> mapError = MassUtils.getProductsErrorMap(lstSKUObj, MassContansts.IS_DATA_ERROR_ATTR);
				
				MassUtils.printHashMapForTest((Map<String,Map<String,Object>>)mapError.get("productsError"), "Errors Map");
				
				HSSFWorkbook workbook = MassUtils.createWorkbookFromHash((Map<String,Map<String,Object>>)mapError.get("productsError"), MassContansts.RECEIVING_FILE_ERROR_TEMPLATE_HEADER, MassContansts.ERROR_SHEET_NAME, MassContansts.FILTER_KEY_ERROR_TEMPLATE_HEADER);
				
				
		        int totalDataInsertSuccess = (Integer)mapError.get("totalDataInsertSuccess");
				int totalDataInsertError = (Integer)mapError.get("totalDataInsertError");
				int totalData = (Integer)mapError.get("totalData");
				String excelPathInServerForDownload = null;
				
				if (totalDataInsertError > 0){
					excelPathInServerForDownload = MassUtils.createExcelFile(getServlet(), receivingWarehouseFile.getFileName(), workbook);
				}
				
				setStatusImportTemplate(request, excelPathInServerForDownload, totalDataInsertSuccess, totalDataInsertError, totalData);
				System.out.println("EXCEL ERROR IN SERVER TXT:" + excelPathInServerForDownload);
			} else{
				addActionErrorString(request, MassContansts.ERRORS.IMPORT_TXT_FILE);
			}
			
			
		} catch (Exception e) {
			//FIXME: FOR TEST
			addActionErrorString(request, MassUtils.getExceptionDescription(e));
		}
		
		request.setAttribute(MassContansts.IMPORT_PAGE_TYPE_KEY, MassContansts.IMPORT_PAGE_TYPE.RECEIVING_TEMPLATE);
		request.setAttribute(MassContansts.BREAD_CRUMB_KEY, MassContansts.BREAD_CRUMB_UPLOAD_TXT_RECEIVING);
		
		forward = mapping.findForward("agenttool.view.import.data.text");
		return forward;
	}

	
	//FIXME: Use this function for later sprint
	/**
	 * Upload excel file for update prices Action
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward updatePrices(final ActionMapping mapping,
			final ActionForm form, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {
		System.out.println("updatePrices function 2");
		ActionForward forward;
		
		try {
			ImportDataForm priceForm = (ImportDataForm) form;
			FormFile filePrice = priceForm.getFile();

			if (filePrice != null) {
				InputStream filePriceStream = filePrice.getInputStream();
				String fileName = filePrice.getFileName();
				boolean isReadCategory = false;
				boolean isExcel2003 = true;

				if (fileName != null && fileName.indexOf(".xlsx") > 0) {
					isExcel2003 = false;
				}

				Map<String, Object> headerDataInfo = getHeaderDataInfoExcel(
						filePriceStream, isExcel2003, isReadCategory);
				String[] headers = new String[] { "item_sku", "price" };
				Map<String, Map<String, Object>> itemsUpdate = getDataInfoExcelToHash(
						headerDataInfo, headers);
				MassUtils.printHashMapForTest(itemsUpdate, "Item info");
			}
		} catch (Exception e) {
			addActionErrorString(request, MassUtils.getExceptionDescription(e));
		}
		
		forward = mapping.findForward("agenttool.view.import.update.price");
		return forward;
	}
	
	
	/**
	 * Download Excel with absolute path in server
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward downloadExcelFileWithPath(final ActionMapping mapping,
			final ActionForm form, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {
		System.out.println("downloadExcelFileWithPath function 2");
		ActionForward forward;
		String path = (String) request.getParameter("path");
		System.out.println("PATH RECEIVED:" + path);
		
		File fileDownload = null;
		try {
			fileDownload = new File(path);
		} catch (Exception e) {
			System.out.println("Can't read file:" + MassUtils.getExceptionDescription(e));
		}
		
		
		if (fileDownload != null){
			try {
				if (fileDownload.exists()){
					
					String filename = FilenameUtils.getName(path);
					boolean isExcel2003 = MassUtils.isExcel2003WithFilename(filename);
					response.setHeader("Content-Disposition",
							String.format("attachment; filename=%s", filename));
					ServletOutputStream out = response.getOutputStream();
					FileInputStream fileInputStream = new FileInputStream(path);
					if (isExcel2003){
						HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
						workbook.write(out);
					} else{
						XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
						workbook.write(out);
					}
					out.flush();
					out.close();
				}
			} catch (Exception e) {
				System.out.println("HAVE PROBLEM WHEN DOWNLOAD ERROR FILE:" + MassUtils.getExceptionDescription(e));
			} finally{
				try {
					FileUtils.forceDelete(fileDownload);
				} catch (Exception e2) {
					System.out.println("DELETE FILE");
				}
				
			}
		}
		
		forward = mapping.findForward("agenttool.view.import.data");
		return forward;
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
	
	
	/**
	 * Save status when upload file (incoming, prices ...) and location of detail error in server for download
	 * @param request
	 * @param excelPathInServerForDownload
	 * @param totalDataInsertSuccess
	 * @param totalDataInsertError
	 * @param totalData
	 */
	public void setStatusImportTemplate(HttpServletRequest request, String excelPathInServerForDownload, 
			int totalDataInsertSuccess, int totalDataInsertError, int totalData){
		request.setAttribute("excelInServer", excelPathInServerForDownload); //Location of error file
		request.setAttribute("totalDataInsertSuccess", totalDataInsertSuccess);
		request.setAttribute("totalDataInsertError", totalDataInsertError);
		request.setAttribute("totalData", totalData);
	}
	
	
	

	/**
	 * Get List SKU Id in list products template (Ignore SKU item error)
	 * @param products
	 * @return
	 */
	public List<String> getListSKUFromProducts(
			Map<String, Map<String, Object>> products) {
		System.out.println("getListSKUFromProducts function");
		List<String> rs = new ArrayList<String>();
		for (String key : products.keySet()) {
			Map<String, Object> item = products.get(key);
			if (item != null && item.containsKey(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD)
					&& item.get(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD) != null
					&& item.containsKey(MassContansts.IS_DATA_ERROR_ATTR)
					&& !(Boolean) item.get(MassContansts.IS_DATA_ERROR_ATTR)) {
				rs.add(item.get(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD).toString().trim());
			}
		}
		return rs;
	}
	
	
	/**
	 * Join List Id SKU of products to String for use in SQL statement
	 * Ex: From array ['SKU1', 'SKU2'] ==> to string 'SKU1','SKU2' ==> use in SQL: IN ('SKU1','SKU2')
	 * @param lstSKU
	 * @return
	 */
	public String joinStringSQL(List<String> lstSKU) {
		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		if (lstSKU != null && lstSKU.size() > 0) {
			for (int i = 0; i < lstSKU.size(); i++) {
				String itemSKU = lstSKU.get(i);
				if (itemSKU != null && !itemSKU.trim().equalsIgnoreCase("")) {
					if (isFirst) {
						sb.append("'" + itemSKU + "'");
						isFirst = false;
					} else {
						sb.append(",'" + itemSKU + "'");
					}

				}
			}
		}
		
		if (StringUtils.isEmpty(sb.toString())){
			sb.append(MassContansts.MOCK_VALUE_SKU);
		}
		
		return sb.toString();
	}

	

	/**
	 * Update status products
	 * 		MassContansts.INSERTED_INCOMING_PRODUCT: true if inserted to estore_inventory table
	 * 		MassContansts.INSERTED_HISTORY: true if inserted to estore_history table
	 * 		MassContansts.INSERT_ATTRIBUTE_PRODUCT: true if we will inserted it to receiving table
	 * @param products
	 * @param lstSKU
	 * @throws Exception
	 */
	public void updateProductStatusFromListSKU(
			Map<String, Map<String, Object>> products, List<String> lstSKU)
			throws Exception {
		DAOUtils daoUtils = DAOUtils.getInstance();
//		Connection conn =  MassUtils.getInstanceMYSQLForTest();
		Connection conn = daoUtils.getConnection();
		ResultSet rs = null;
		
		try {
			// Check INSERTED in receiving table (do nothing if exists)
			String sqlCheckInsertedInventory = String.format(daoUtils.getString("mass.sql.inventory.item"), joinStringSQL(lstSKU));
			
			PreparedStatement pstmt = conn
					.prepareStatement(sqlCheckInsertedInventory);
			System.out.println("sqlCheckInsertedInventory: "
					+ pstmt);
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String SKU = rs.getString(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD);
				String name = rs.getString("name");
				System.out.println("ITEM IGNORE (INSERTED):" + SKU + " - "
						+ name);
				Map<String, Object> item = products.get(SKU);
				item.put(MassContansts.INSERTED_INCOMING_PRODUCT, true);
				setErrorForProduct(item, String.format(MassContansts.ERRORS.DATA_INSERTED_TO_INVENTORY, SKU));
			}
			
			
			// Check INSERTED in history table (do nothing if exists)
			String sqlCheckInsertedHistory = String.format(daoUtils.getString("mass.sql.history.item"), joinStringSQL(lstSKU));
			
			pstmt = conn
					.prepareStatement(sqlCheckInsertedHistory);
			System.out.println("sqlCheckInsertedHistory: "
					+ pstmt);
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String SKU = rs.getString(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD);
				String name = rs.getString("name");
				System.out.println("ITEM IGNORE History (INSERTED):" + SKU + " - "
						+ name);
				Map<String, Object> item = products.get(SKU);
				item.put(MassContansts.INSERTED_HISTORY, true);
				setErrorForProduct(item, String.format(MassContansts.ERRORS.DATA_INSERTED_TO_HISTORY, SKU));
			}
			
			

			// Check UPDATE or INSERT in estore_inventory table
			String sqlCheckInsertedReceiving = String.format(daoUtils.getString("mass.sql.receiving.item"), joinStringSQL(lstSKU));
			pstmt = conn.prepareStatement(sqlCheckInsertedReceiving);
			System.out.println("sqlCheckInsertedReceiving: "
					+ pstmt);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String SKU = rs.getString(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD).trim();
				String name = rs.getString("name");
				Date receivedDate = rs.getDate(MassContansts.PRODUCT_FIELD_COMMON.RECEIVED_DATE);
				MassContansts.STATUS_PRODUCT_TYPE currentStatusProductDB = null;
				if (receivedDate != null) {
					currentStatusProductDB = MassContansts.STATUS_PRODUCT_TYPE.WAREHOUSE;
				} else {
					currentStatusProductDB = MassContansts.STATUS_PRODUCT_TYPE.IMPORT;
				}
				System.out.println("ITEM:" + receivedDate + " - SKU:" + SKU
						+ " - Name:" + name);
				Map<String, Object> item = products.get(SKU);
				item.put(MassContansts.INSERT_ATTRIBUTE_PRODUCT, false);

				if (currentStatusProductDB == item
						.get(MassContansts.STATUS_ATTRIBUTE_PRODUCT)) {
					setErrorForProduct(item, String.format(MassContansts.ERRORS.INSERTED_RECEIVING_BEFORE_SAME_TYPE, SKU));
				} else {
					item.put(MassContansts.STATUS_ATTRIBUTE_PRODUCT,
							MassContansts.STATUS_PRODUCT_TYPE.FULL);
				}
			}

		} catch (Exception e) {
			throw new Exception(String.format(MassContansts.ERRORS.EXCEPTION_UPDATE_PRODUCT_STATUS, MassUtils.getExceptionDescription(e)));
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
	}

	
	
	
	/**
	 * Build SQL statement and execute when user upload excel template
	 * @param products
	 * @throws Exception
	 */
	public void buildSQLQueries(Map<String, Map<String, Object>> products, String[] attributesOrder)
			throws Exception {
		System.out.println("GO buildInsertQueries");
		if (products == null) {
			return;
		}
		
		DAOUtils daoUtils = DAOUtils.getInstance();
		
//		Connection conn = MassUtils.getInstanceMYSQLForTest();
		Connection conn = daoUtils.getConnection();

		conn.setAutoCommit(false);

		try {
			System.out.println("Connection ok:" + String.valueOf(conn != null));

			String sql = null;
			String sqlInsertWhenFullData = null;
			String sqlDelete = null;
			String sqlUpdate = null;
			
			Map<String, Object> currentProduct = null; //For add data error
			for (String key : products.keySet()) {
				
				try {
					Map<String, Object> product = products.get(key);
					currentProduct = product;
					
					boolean isDataError = (Boolean) product
							.get(MassContansts.IS_DATA_ERROR_ATTR);

					if (!isDataError) {
						boolean isInsertedInventoryProduct = (Boolean) product
								.get(MassContansts.INSERTED_INCOMING_PRODUCT);
						
						boolean isInsertedHistory = (Boolean) product
								.get(MassContansts.INSERTED_HISTORY);
						
						
						boolean isInsertedReceivingProduct = (Boolean) product
								.get(MassContansts.INSERT_ATTRIBUTE_PRODUCT);
						// boolean isReceivedFromWareHouse =
						// (Boolean)product.get(RECEIVED_WAREHOUSE_INFO_ATTRIBUTE_PRODUCT);

						MassContansts.STATUS_PRODUCT_TYPE status = (MassContansts.STATUS_PRODUCT_TYPE) product
								.get(MassContansts.STATUS_ATTRIBUTE_PRODUCT);

						StringBuilder params = new StringBuilder();
						StringBuilder paramsUpdate = new StringBuilder();
						for (int i = 0; i < MassContansts.FIELDS_IN_INVENTORY.length; i++) {
							if (i == 0) {
								params.append("?");
								if (i < MassContansts.FIELDS_IN_INVENTORY_TABLE_EXCEPT_EXT_ATTR.length) {
									paramsUpdate
											.append(MassContansts.FIELDS_IN_INVENTORY_TABLE_EXCEPT_EXT_ATTR[i]
													+ "=?");
								} else {
									paramsUpdate
											.append(MassContansts.FIELDS_ATTRIBUTES_EXT[i
													- MassContansts.FIELDS_IN_INVENTORY_TABLE_EXCEPT_EXT_ATTR.length]
													+ "=?");
								}
							} else {
								params.append(",?");
								if (i < MassContansts.FIELDS_IN_INVENTORY_TABLE_EXCEPT_EXT_ATTR.length) {
									paramsUpdate
											.append(","
													+ MassContansts.FIELDS_IN_INVENTORY_TABLE_EXCEPT_EXT_ATTR[i]
													+ "=?");
								} else {
									paramsUpdate
											.append(","
													+ MassContansts.FIELDS_ATTRIBUTES_EXT[i
															- MassContansts.FIELDS_IN_INVENTORY_TABLE_EXCEPT_EXT_ATTR.length]
													+ "=?");
								}
							}

						}

						String allFieldsInventory = StringUtils
								.join(MassContansts.FIELDS_IN_INVENTORY_TABLE_EXCEPT_EXT_ATTR,
										",")
								+ ","
								+ StringUtils.join(
										MassContansts.FIELDS_ATTRIBUTES_EXT, ",");

						if (isInsertedInventoryProduct || isInsertedHistory) {
							// FIXME: INSERTED INVENTORY - DO NOTHING
						} else {
							if (isInsertedReceivingProduct) {
								// INSERT PRODUCT
								sql = String.format(
										daoUtils.getString("mass.sql.receiving.insert.receiving.item.excel"),
										allFieldsInventory,
										params.toString());
							} else {
								// FIXME: UPDATE PRODUCT OR FULL DATA (Data error here - remove or keep)
//								sql = String.format(
//										daoUtils.getString("mass.sql.receiving.update.receiving.item.excel"),
//										paramsUpdate,
//										String.format(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD + "='%s'",
//												product.get(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD).toString()
//														.trim()));

								if (status == MassContansts.STATUS_PRODUCT_TYPE.FULL) {
									// FULL DATA, INSERT INVENTORY TABLE
									
									sqlUpdate = String.format(
											daoUtils.getString("mass.sql.receiving.update.receiving.item.excel"),
											paramsUpdate,
											String.format(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD + "='%s'",
													product.get(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD).toString()
															.trim()));
									sqlInsertWhenFullData = String.format(daoUtils.getString("mass.sql.copy.receiving.to.inventory.item"), product.get(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD).toString().trim());
									sqlDelete = daoUtils.getString("mass.sql.inventory.delete.item.excel");
								}
							}

							PreparedStatement pstmt = null;
							if (sqlDelete != null
									&& sqlInsertWhenFullData != null && sqlUpdate != null) {
								
								pstmt = conn
										.prepareStatement(sqlUpdate);
								pstmt = setStatementDefault(pstmt);
								pstmt = setStatementValue(pstmt, product, attributesOrder);
								System.out.println("SQL UPDATE INFO FULL ("
										+ product.get(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD) + "):"
										+ pstmt);
								pstmt.execute();
								
								pstmt = conn
										.prepareStatement(sqlInsertWhenFullData);
//									pstmt = setStatementDefault(pstmt);
//									pstmt = setStatementValue(pstmt, product);
								System.out.println("SQL NEW FULL ("
										+ product.get(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD) + "):"
										+ pstmt);
								pstmt.execute();

								pstmt = conn.prepareStatement(sqlDelete);
								pstmt.setString(1, product.get(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD)
										.toString().trim());
								System.out.println("SQL DELETE OLD:" + pstmt);
								pstmt.execute();

							} else {
								pstmt = conn.prepareStatement(sql);
								pstmt = setStatementDefault(pstmt);
								pstmt = setStatementValue(pstmt, product, attributesOrder);
								System.out.println("SQL NEW ("
										+ product.get(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD) + "):"
										+ pstmt);
								pstmt.execute();
								
							}

							conn.commit();
							
							currentProduct = null;
						}
					}
				} catch (Exception e) {
					System.err
					.println("Error when IMPORT (Add to errors):"
							+ MassUtils.getExceptionDescription(e));
					conn.rollback();
					setErrorForProduct(currentProduct, 
							String.format(MassContansts.ERRORS.EXCEPTION_EXECUTE_QUERY, currentProduct.get(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD), MassUtils.getExceptionDescription(e)));
					
				}

			}

		} catch (Exception e) {
			System.err.println("Exception in buildSQLQueries:" + e.getMessage());
			throw new Exception(String.format(MassContansts.ERRORS.EXCEPTION_BUILD_QUERY, MassUtils.getExceptionDescription(e)));
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException sqlE) {
				sqlE.getStackTrace();
			}
		}

	}

	/**
	 * Set correct format value for prepared statement (Throw exception if have any error)
	 * @param pstmt
	 * @param product
	 * @return
	 * @throws Exception
	 */
	public PreparedStatement setStatementValue(PreparedStatement pstmt,
			Map<String, Object> product, String[] attributesOrder) throws Exception {

		int maxInventoryField = MassContansts.FIELDS_IN_INVENTORY.length;

		// Two param for get error
		String currentField = "";
		String currentValue = "";

		try {

			for (int i = 0; i < maxInventoryField; i++) {
				Object value = null;
				String field = MassContansts.FIELDS_IN_INVENTORY[i];
				value = product.get(field);

				currentField = String.valueOf(field);
				currentValue = String.valueOf(value);

				int idxParam = i + 1;

				if (Arrays.asList(MassContansts.LIST_TYPE_FIELD_EXCEL.DATE_FIELD).contains(field)) {
					if (value != null
							&& !StringUtils.isEmpty(String.valueOf(value))) {
						pstmt.setDate(idxParam, (Date) value);
					} else {
						pstmt.setDate(idxParam, null);
					}
				} else if (Arrays.asList(MassContansts.LIST_TYPE_FIELD_EXCEL.INT_FIELD).contains(
						field)) {
					if (value != null
							&& !StringUtils.isEmpty(String.valueOf(value))) {
						pstmt.setInt(idxParam, (Integer) value);
					} else {
						pstmt.setInt(idxParam, 0);
					}
				} else if (Arrays.asList(MassContansts.LIST_TYPE_FIELD_EXCEL.FLOAT_FIELD)
						.contains(field)) {
					if (value != null
							&& !StringUtils.isEmpty(String.valueOf(value))) {
						pstmt.setDouble(idxParam,
								Double.valueOf(String.valueOf(value)));
					} else {
						pstmt.setDouble(idxParam, 0);
					}

				} else {
					String fieldFinal = new String(field);
					
					if (field.toLowerCase().trim().startsWith(MassContansts.PREFIX_CUSTOM_FIELD.toLowerCase()) && attributesOrder != null && attributesOrder.length > 0){
						int indexAtt = Integer.parseInt(field.toLowerCase().trim().replaceAll(MassContansts.PREFIX_CUSTOM_FIELD.toLowerCase(), ""));
						try {
							fieldFinal = attributesOrder[indexAtt - 1];
						} catch (Exception e) {
							System.out.println("OVER INDEX attributesOrder: " + (indexAtt - 1));
						}
					}
					
					if (value != null
							&& !StringUtils.isEmpty(String.valueOf(value)) && product.get(fieldFinal) != null) {
						pstmt.setString(idxParam,
								String.valueOf(product.get(fieldFinal)).trim());
					} else {
						pstmt.setNull(idxParam, Types.VARCHAR);
					}
				}
			}
			
			//FIXME: modified_price
			int idxModifiedPrice = MassContansts.FIELDS_IN_INVENTORY_TABLE_EXCEPT_EXT_ATTR.length + MassContansts.FIELDS_ATTRIBUTES_EXT.length + 1;
			
			Object priceObj = product.get(MassContansts.PRODUCT_FIELD_COMMON.LIST_PRICE);
			if (priceObj != null
					&& !StringUtils.isEmpty(String.valueOf(priceObj))) {
				pstmt.setDouble(idxModifiedPrice,
						Double.valueOf(String.valueOf(priceObj)));
			} else {
				pstmt.setDouble(idxModifiedPrice, 0);
			}
			
			
			
		} catch (Exception e) {
			throw new Exception(String.format(MassContansts.ERRORS.PROBLEM_SET_PARAM, String.valueOf(currentField), currentValue, MassUtils.getExceptionDescription(e)));
		}

		return pstmt;
	}

	/**
	 * Set default value for prepared statement (Because attribute is dynamic, prepared statement throw exception if leak parameter)
	 * @param pstmt
	 * @return
	 * @throws SQLException
	 */
	public PreparedStatement setStatementDefault(PreparedStatement pstmt)
			throws SQLException {
		for (int i = 0; i < MassContansts.FIELDS_IN_INVENTORY_TABLE_EXCEPT_EXT_ATTR.length; i++) {
			if (Arrays.asList(MassContansts.LIST_TYPE_FIELD_EXCEL.DATE_FIELD).contains(
					MassContansts.FIELDS_IN_INVENTORY_TABLE_EXCEPT_EXT_ATTR[i])) {
				pstmt.setNull(i + 1, Types.DATE);
			} else if (Arrays.asList(MassContansts.LIST_TYPE_FIELD_EXCEL.INT_FIELD).contains(
					MassContansts.FIELDS_IN_INVENTORY_TABLE_EXCEPT_EXT_ATTR[i])) {
				pstmt.setNull(i + 1, Types.INTEGER);
			} else {
				pstmt.setString(i + 1, null);
				pstmt.setNull(i + 1, Types.VARCHAR);
			}
		}

		for (int i = 0; i < MassContansts.FIELDS_ATTRIBUTES_EXT.length + 1; i++) {
			pstmt.setNull(MassContansts.FIELDS_IN_INVENTORY_TABLE_EXCEPT_EXT_ATTR.length + 1, Types.VARCHAR);
		}
		
		//FIXME: modified_price
		int idxModifiedPrice = MassContansts.FIELDS_IN_INVENTORY_TABLE_EXCEPT_EXT_ATTR.length + MassContansts.FIELDS_ATTRIBUTES_EXT.length + 1;
		pstmt.setNull(idxModifiedPrice, Types.DOUBLE);
		
		return pstmt;
	}

	/**
	 * Get some info incoming excel file as category id, total column, workbook, data all row
	 * @param excelFile
	 * @param isExcel2003
	 * @param isReadCategory
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getHeaderDataInfoExcel(InputStream excelFile,
			boolean isExcel2003, boolean isReadCategory) throws Exception {
		System.out.println("getDataInfoExcel function");
		Iterator<Row> rowIterator = null;

		Map<String, Object> dataInfo = null;
		int categoryId = -1;
		int totalCol = -1;

		Object workbookObj = null;

		if (isExcel2003) {
			HSSFWorkbook workbook = new HSSFWorkbook(excelFile);
			HSSFSheet sheet = null;
			
			try {
				sheet = workbook.getSheetAt(0);
			} catch (Exception e) {
				throw new Exception(MassContansts.ERRORS.SHEET_EMPTY);
			}
			
			try {
				totalCol = sheet.getRow(0).getPhysicalNumberOfCells();
			} catch (Exception e) {
				throw new Exception(MassContansts.ERRORS.HEADER_ROW_EMPTY);
			}
			
			if (isReadCategory) {
				try {
					Double categoryIdDouble = sheet.getRow(1).getCell(0)
							.getNumericCellValue();
					categoryId = categoryIdDouble.intValue();
				} catch (Exception e) {
					throw new Exception(MassContansts.ERRORS.READ_CATEGORY_ID_ERROR);
				}
			}

			rowIterator = sheet.iterator();

			workbookObj = workbook;
		} else {
			XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
			
			XSSFSheet sheet = null;
			
			try {
				sheet = workbook.getSheetAt(0);
			} catch (Exception e) {
				throw new Exception(MassContansts.ERRORS.SHEET_EMPTY);
			}
			
			
			try {
				totalCol = sheet.getRow(0).getPhysicalNumberOfCells();
			} catch (Exception e) {
				throw new Exception(MassContansts.ERRORS.HEADER_ROW_EMPTY);
			}
			
			if (isReadCategory) {
				try {
					Double categoryIdDouble = sheet.getRow(1).getCell(0)
							.getNumericCellValue();
					categoryId = categoryIdDouble.intValue();
				} catch (Exception e) {
					throw new Exception(MassContansts.ERRORS.READ_CATEGORY_ID_ERROR);
				}
				
			}

			rowIterator = sheet.iterator();
			
			workbookObj = workbook;
		}
		
		if (isReadCategory && categoryId <= 0) {
			throw new Exception(MassContansts.ERRORS.CATEGORY_EMPTY);
		}
		if (rowIterator == null) {
			throw new Exception(MassContansts.ERRORS.DATA_EMPTY);
		}
		if (totalCol <= 0) {
			throw new Exception(MassContansts.ERRORS.DATA_EMPTY);
		}
		
		dataInfo = new HashMap<String, Object>();
		dataInfo.put("categoryId", categoryId);
		dataInfo.put("dataRow", rowIterator);
		dataInfo.put("totalCol", totalCol);
		dataInfo.put("workbook", workbookObj);

		return dataInfo;
	}

	/**
	 * Get all field in excel template, include attribute field
	 * @param categoryId
	 * @return
	 */
	public String[] getFieldsOrderFullAttrTemplate(int categoryId) {
		boolean isFieldOrder = false;
		for (int i = 0; i < MassContansts.PRODUCT_IDS_FIRST_TEMPLATE_APPLY.length; i++) {
			if (categoryId == MassContansts.PRODUCT_IDS_FIRST_TEMPLATE_APPLY[i]) {
				isFieldOrder = true;
			}
		}
		if (isFieldOrder) {
			return MassContansts.FIELDS_ORDER_TEMPLATE_FULL_ATTR;
		}
		return MassContansts.FIELDS_ORDER_TEMPLATE_OTHER_FULL_ATTR;
	}
	
	/**
	 * Get all field in excel template, without attribute field
	 * @param categoryId
	 * @return
	 */
	public String[] getFieldsOrderWithoutAttrTemplate(int categoryId) {
		boolean isFieldOrder = false;
		for (int i = 0; i < MassContansts.PRODUCT_IDS_FIRST_TEMPLATE_APPLY.length; i++) {
			if (categoryId == MassContansts.PRODUCT_IDS_FIRST_TEMPLATE_APPLY[i]) {
				isFieldOrder = true;
			}
		}
		if (isFieldOrder) {
			return MassContansts.FIELDS_ORDER_TEMPLATE_FIRST_TYPE;
		}
		return MassContansts.FIELDS_ORDER_TEMPLATE_OTHER_TYPE;
	}

	/**
	 * Set error for product
	 * @param productItem
	 * @param descriptionError: error detail
	 */
	public void setErrorForProduct(Map<String, Object> productItem,
			String descriptionError) {
		productItem.put(MassContansts.IS_DATA_ERROR_ATTR, true);
		productItem.put(MassContansts.DATA_ERROR_DETAIL, descriptionError);
	}

	/**
	 * Get incoming data in excel file, add some error for data invalid (wrong data format)
	 * @param excelFile
	 * @param headerDataInfo
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Map<String, Object>> getIncomingDataInfoExcel(
			InputStream excelFile, Map<String, Object> headerDataInfo)
			throws Exception {
		Map<String, Map<String, Object>> products = null;

		Iterator<Row> rowIterator = null;
		int categoryId = -1;
		String[] fieldsOrder = null;
		int totalColInTemplate = -1;
		int totalCol = -1;

		if (headerDataInfo != null) {
			rowIterator = (Iterator<Row>) headerDataInfo.get("dataRow");
			categoryId = (Integer) headerDataInfo.get("categoryId");
			totalCol = (Integer) headerDataInfo.get("totalCol");
			fieldsOrder = getFieldsOrderFullAttrTemplate(categoryId);
			totalColInTemplate = getFieldsOrderWithoutAttrTemplate(categoryId).length;

			products = new HashMap<String, Map<String, Object>>();

			System.out.println("TOTAL COLUMN: " + totalCol
					+ " totalColInTemplate:" + totalColInTemplate);
			
			if (totalCol < totalColInTemplate) {
				throw new Exception(MassContansts.ERRORS.TOTAL_COLUMN_WRONG);
			}

			boolean isHeaderRow = true;
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				if (isHeaderRow) {
					isHeaderRow = false;
					continue;
				}

				if (row == null) {
					continue;
				}

				Map<String, Object> productItem = new HashMap<String, Object>();

				int firstCol = row.getFirstCellNum();
				int colError = -1;
				try {

					for (int colIx = firstCol; colIx < totalCol; colIx++) {
						
						productItem.put(MassContansts.IS_DATA_ERROR_ATTR,
								false);
						productItem.put(MassContansts.INDEX_IN_EXCEL,
								row.getRowNum());
						
						Cell cell = row.getCell(colIx);
						Object value = null;
						if (cell != null) {

							colError = colIx;
							value = getCellValueAllType(cell, fieldsOrder,
									colIx);
							
							productItem.put(fieldsOrder[colIx], value);
							
						}
					}

					productItem.put(MassContansts.INSERTED_INCOMING_PRODUCT,
							false);
					productItem.put(MassContansts.INSERTED_HISTORY,
							false);
					
					productItem.put(MassContansts.INSERT_ATTRIBUTE_PRODUCT,
							true);
					productItem.put(MassContansts.STATUS_ATTRIBUTE_PRODUCT,
							MassContansts.STATUS_PRODUCT_TYPE.IMPORT);
					productItem
							.put(MassContansts.RECEIVED_WAREHOUSE_INFO_ATTRIBUTE_PRODUCT,
									false);
					
					products = checkErrorReadData(products, productItem, row, categoryId);

				} catch (Exception e) {
//					System.out.println("ERROR READ LINE:" + e.getMessage());
//					e.printStackTrace();
					//ERROR when read data
					products = setErrorException(products, productItem, fieldsOrder, colError, row, e.getMessage());
				}
			}

		}
		System.out.println("PRODUCTS SIZE:" + products.size());

		if (products.size() <= 0) {
			throw new Exception(MassContansts.ERRORS.NO_DATA_VALID);
		}

		return products;
	}
	
	
	public Map<String, Map<String, Object>> setErrorException(Map<String, Map<String, Object>> products, Map<String, Object> productItem, String[] fieldsOrder, int colError, Row row, String errorMsg){
		String SKUErrorRandom = null;
		boolean isEmptySKU = false;
		
		if (!productItem.containsKey(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD)){
			setErrorForProduct(productItem, String.format(MassContansts.ERRORS.ITEM_SKU_EMPTY_LINE, row.getRowNum() + 1));
			isEmptySKU = true;
		}
		
		Object skuValue = productItem.get(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD);
		if (skuValue == null || StringUtils.isEmpty(skuValue.toString().trim())){
			setErrorForProduct(productItem, String.format(MassContansts.ERRORS.ITEM_SKU_EMPTY_LINE, row.getRowNum() + 1));
			isEmptySKU = true;
		}
		
		if (isEmptySKU){
			SKUErrorRandom = MassUtils.random();
			products.put(SKUErrorRandom, productItem);
//			System.out.println("SKU Random:" + SKUErrorRandom);
		} else{
			setErrorForProduct(productItem, String.format(MassContansts.ERRORS.ROW_DATA_INVALID, fieldsOrder[colError], colError));
			products.put(skuValue.toString().trim(), productItem);
		}
		
//		System.out.println("ADD ERROR EXCEPTION:" + errorMsg);
		return products;
	}
	
	
	public Map<String, Map<String, Object>> checkErrorReadData(Map<String, Map<String, Object>> products, Map<String, Object> productItem, Row row, int categoryId) throws Exception{
		
		Object SKUItem = productItem.get(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD);
		Object categoryItem = productItem.get(MassContansts.PRODUCT_FIELD_COMMON.CATEGORY_ID);
		
		if (categoryItem == null || StringUtils.isEmpty(categoryItem.toString())){
			setErrorForProduct(productItem, String.format(MassContansts.ERRORS.CATEGORY_EMPTY_LINE, row.getRowNum() + 1));
		} else{
			int categoryIdIt = Integer.parseInt(categoryItem.toString().trim());
			if (categoryIdIt != categoryId){
				setErrorForProduct(productItem, String.format(MassContansts.ERRORS.CATEGORY_MATCH, row.getRowNum() + 1));
			}
		} 
		if (SKUItem == null || StringUtils.isEmpty(SKUItem.toString().trim())) {
			String errMess = String.format(MassContansts.ERRORS.ITEM_SKU_EMPTY_LINE, row.getRowNum() + 1);
//			System.out.println(errMess);
			throw new Exception(errMess);
		} else{
			String SKU = SKUItem.toString().trim();
			
			if (products.containsKey(SKU)){
				//Duplicate
				String SKUErrorRandom = MassUtils.random();
				Map<String, Object> productDuplicate = products.get(SKU);
				
				productDuplicate.put(MassContansts.DUPLICATE_ORIGIN_SKU, SKU);
				productItem.put(MassContansts.DUPLICATE_ORIGIN_SKU, SKU);
				
				setErrorForProduct(productDuplicate, MassContansts.ERRORS.DUPLICATE_SKU);
				
				setErrorForProduct(productItem, MassContansts.ERRORS.DUPLICATE_SKU);
				
				//Set random SKU for duplicate SKU
				productItem.put(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD, SKUErrorRandom);
				products.put(String.valueOf(productItem.get(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD))
						.trim(), productItem);
			} else{
				products.put(
						String.valueOf(productItem.get(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD))
								.trim(), productItem);
			}
			
			
		}
		return products;
	}
	
	

	/**
	 * Get String value of cell (cell in attribute extend)
	 * @param cell
	 * @return
	 */
	public String forceGetCellStringValue(Cell cell) {
		String value = null;
		
		switch(cell.getCellType()) {
	        case Cell.CELL_TYPE_BOOLEAN:
	            value = String.valueOf(cell.getBooleanCellValue());
	            break;
	        case Cell.CELL_TYPE_NUMERIC:
	        	boolean isDateFormat = HSSFDateUtil.isCellDateFormatted(cell);
	        	if (isDateFormat){
	        		value = MassUtils.getDateStringFormat(new java.sql.Date(cell.getDateCellValue().getTime()), MassContansts.DATE_FORMAT);
	        	} else{
	        		double numValue = cell.getNumericCellValue();
	        		Double doubObj = (Double) numValue;
					int valueInt = -1;
					if (doubObj != null) {
						valueInt = doubObj.intValue();
						if (valueInt == doubObj.doubleValue()) {
							value = String.valueOf(valueInt);
						}
					}
	        	}
	            break;
	        case Cell.CELL_TYPE_STRING:
	            value = cell.getStringCellValue();
	            break;
	        case Cell.CELL_TYPE_BLANK:
	            value = "";
	            break;
	    }

		return value;
	}

	/**
	 * Get cell value with type defined
	 * @param cell
	 * @param fieldsOrder
	 * @param colIx
	 * @return
	 * @throws Exception
	 */
	public Object getCellValueAllType(Cell cell, String[] fieldsOrder, int colIx)
			throws Exception {
		Object value = null;

		if (Arrays.asList(MassContansts.LIST_TYPE_FIELD_EXCEL.DATE_FIELD).contains(
				fieldsOrder[colIx])) {
			if (cell.getDateCellValue() != null){
				value = new java.sql.Date(cell.getDateCellValue().getTime());
			}
		} else if (Arrays.asList(MassContansts.LIST_TYPE_FIELD_EXCEL.INT_FIELD).contains(
				fieldsOrder[colIx])) {
			Double temp = (Double) cell.getNumericCellValue();
			if (temp != null) {
				value = temp.intValue();
			}
		} else if (Arrays.asList(MassContansts.LIST_TYPE_FIELD_EXCEL.FLOAT_FIELD).contains(
				fieldsOrder[colIx])) {

			boolean isCurrencyField = false;
			if (Arrays.asList(MassContansts.LIST_TYPE_FIELD_EXCEL.CURRENCY_FIELD).contains(
					fieldsOrder[colIx])) {
				isCurrencyField = true;
			}
			if (isCurrencyField) {
				value = cell.getStringCellValue();
				if (!StringUtils.isEmpty(String.valueOf(value))) {
					value = Float.parseFloat(value.toString().substring(1)
							.replaceAll(",", ""));
				}
			} else {
				value = cell.getNumericCellValue();
			}

		} else {
			value = forceGetCellStringValue(cell);
			if (value != null) {
				value = value.toString().trim();
			}
		}

		return value;
	}

	/**
	 * Get list SKU Id from text file
	 * @param txtFile
	 * @return
	 * @throws Exception 
	 */
	public Map<String, Object> getListSKUFromTextFile(InputStream txtFile)
			throws Exception {

		System.out.println("getListSKU function");
		BufferedReader br = new BufferedReader(new InputStreamReader(txtFile));
		String line;

		Map<String, Object> rs = null;

		List<String> lstSKU = new ArrayList<String>();
		Map<String, Map<String, Object>> lstSKUObj = new HashMap<String, Map<String, Object>>();

		int count = 0;
		int index = 0;
		while ((line = br.readLine()) != null) {
			index++;
			String[] itemLines = line.split("\\|");
			String SKU = null;
			String receivedDateStr = null;
			Map<String, Object> productItem = new HashMap<String, Object>();
			java.util.Date date = null;
			Date dateSQL = null;
			boolean isLeakInfo = false;
			try {
				
				productItem.put(MassContansts.IS_DATA_ERROR_ATTR,
						false);
				productItem.put(MassContansts.INDEX_IN_EXCEL, index);
				
				if (itemLines == null || itemLines.length == 0){
					isLeakInfo = true;
					throw new Exception(String.format(MassContansts.ERRORS.LEAK_INFO_IN_ROW, index));
				} else if (itemLines.length  == 1){
					isLeakInfo = true;
					if (!StringUtils.isEmpty(itemLines[0])) {
						SKU = itemLines[0].trim();
					}
					throw new Exception(String.format(MassContansts.ERRORS.LEAK_INFO_IN_ROW, index));
				} else{
					boolean isEmptySKU = false;
					boolean isEmptyReceviedDate = false;
					if (!StringUtils.isEmpty(itemLines[0])) {
						SKU = itemLines[0].trim();
					} else {
						isEmptySKU = true;
					}
					if (!StringUtils.isEmpty(itemLines[1])) {
						receivedDateStr = itemLines[1].trim();
						
						try {
							SimpleDateFormat sdf = new SimpleDateFormat(
									MassContansts.RECEIVED_DATE_FORMAT);
							sdf.setLenient(false);
							date = sdf.parse(receivedDateStr);
							dateSQL = new Date(date.getTime());			
							productItem.put(MassContansts.PRODUCT_FIELD_COMMON.RECEIVED_DATE, dateSQL);
						} catch (Exception e) {
							throw new Exception(String.format(MassContansts.ERRORS.RECEIVED_DATE_FORMAT, receivedDateStr));
						}
						
					} else {
						isEmptyReceviedDate = true;
					}
					
					if (isEmptySKU){
						throw new Exception(MassContansts.ERRORS.ITEM_SKU_REQUIRED);
					} else if (isEmptyReceviedDate){
						throw new Exception(MassContansts.ERRORS.RECEIVED_DATE_REQUIRED);
					}
				}
				
			} catch (Exception e) {
				System.out.println("EXCEPTION when readline receiving file[row="+index+"]:" + MassUtils.getExceptionDescription(e));
				setErrorForProduct(productItem, String.format(MassContansts.ERRORS.READ_ITEM_RECEIVING_FILE, index, MassUtils.getExceptionDescription(e)));
			}
			
			
			
			boolean isDataError = (Boolean)productItem.get(MassContansts.IS_DATA_ERROR_ATTR);

			productItem.put(MassContansts.INSERTED_INCOMING_PRODUCT,
					false);
			productItem.put(MassContansts.INSERTED_HISTORY,
					false);
			productItem.put(MassContansts.INSERT_ATTRIBUTE_PRODUCT,
					true);
			productItem.put(MassContansts.STATUS_ATTRIBUTE_PRODUCT,
					MassContansts.STATUS_PRODUCT_TYPE.WAREHOUSE);
			productItem
					.put(MassContansts.RECEIVED_WAREHOUSE_INFO_ATTRIBUTE_PRODUCT,
							true);

			
			productItem.put(MassContansts.PRODUCT_FIELD_COMMON.RECEIVED_DATE, dateSQL);
			
			if (SKU == null){
				SKU = MassUtils.random();
				productItem.put(MassContansts.DUPLICATE_ORIGIN_SKU, MassContansts.EMPTY);
			} else if (isLeakInfo){
				productItem.put(MassContansts.DUPLICATE_ORIGIN_SKU, SKU);
			}
			
			
			productItem.put(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD, SKU);
			
			if (!StringUtils.isEmpty(SKU) && lstSKUObj.containsKey(SKU)){
				System.out.println("DUPLICATE SKU:" + SKU);
				Map<String, Object> itemDup = lstSKUObj.get(SKU);
				
				//Random for duplicate
				String SKURandom = MassUtils.random();
				productItem.put(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD, SKURandom);
				
				setErrorForProduct(productItem, MassContansts.ERRORS.DUPLICATE_SKU);
				setErrorForProduct(itemDup, MassContansts.ERRORS.DUPLICATE_SKU);
				
				productItem.put(MassContansts.DUPLICATE_ORIGIN_SKU, SKU);
				itemDup.put(MassContansts.DUPLICATE_ORIGIN_SKU, SKU);
				
			} else if (!isDataError){
				lstSKU.add(SKU);
			}
			
			lstSKUObj.put(SKU, productItem);
			count++;
		}

		System.out.println("Read completed");
		
		if (lstSKU != null && lstSKU.size() > 0 && lstSKUObj != null
				&& lstSKUObj.size() > 0) {
			rs = new HashMap<String, Object>();
			rs.put("lstSKU", lstSKU);
			rs.put("lstSKUObj", lstSKUObj);
		}
		
		if (lstSKU == null || lstSKU.size() <= 0){
			throw new Exception(MassContansts.ERRORS.NO_DATA_VALID);
		}

		System.out.println("Add to hash success");

		br.close();
		return rs;
	}


	/**
	 * Build SQL statement and execute for receiving template 
	 * @param products
	 * @throws Exception
	 */
	public void buildSQLQueriesReceiving(
			Map<String, Map<String, Object>> products) throws Exception {
		System.out.println("GO buildSQLQueriesReceiving");
		if (products == null) {
			return;
		}

		DAOUtils daoUtils = DAOUtils.getInstance();
//		Connection conn = MassUtils.getInstanceMYSQLForTest();
		Connection conn = daoUtils.getConnection();
		
		conn.setAutoCommit(false);

		try {
			System.out.println("Connection ok:" + String.valueOf(conn != null));

			String sql = null;
			String sqlInsertWhenFullData = null;
			String sqlDelete = null;
			String sqlUpdate = null;
			Map<String, Object> currentProduct = null;
			
			for (String key : products.keySet()) {
				try {
					Map<String, Object> product = products.get(key);
					currentProduct = product;
					boolean isDataError = (Boolean)product.get(MassContansts.IS_DATA_ERROR_ATTR);
					if (!isDataError){
						boolean isInsertedInventoryProduct = (Boolean) product
								.get(MassContansts.INSERTED_INCOMING_PRODUCT);
						boolean isInsertedHistory = (Boolean) product
								.get(MassContansts.INSERTED_HISTORY);
						
						boolean isInsertedReceivingProduct = (Boolean) product
								.get(MassContansts.INSERT_ATTRIBUTE_PRODUCT);
						// boolean isReceivedFromWareHouse =
						// (Boolean)product.get(RECEIVED_WAREHOUSE_INFO_ATTRIBUTE_PRODUCT);

						MassContansts.STATUS_PRODUCT_TYPE status = (MassContansts.STATUS_PRODUCT_TYPE) product
								.get(MassContansts.STATUS_ATTRIBUTE_PRODUCT);

						// Build query here when status correct
						if (isInsertedInventoryProduct || isInsertedHistory) {
							// INSERTED - DO NOTHING
						} else {
							if (isInsertedReceivingProduct) {
								// INSERT PRODUCT
								sql = daoUtils.getString("mass.sql.receiving.insert.receiving.item.txt");
							} else {
								// FIXME: UPDATE PRODUCT OR FULL DATA
//								sql = String.format(daoUtils.getString("mass.sql.receiving.update.receiving.item.txt"), product.get(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD).toString().trim());

								// INSERT and delete new table here
								if (status == MassContansts.STATUS_PRODUCT_TYPE.FULL) {
									sqlUpdate = String.format(daoUtils.getString("mass.sql.receiving.update.receiving.item.txt"), product.get(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD).toString().trim());
									sqlInsertWhenFullData = String.format(daoUtils.getString("mass.sql.copy.receiving.to.inventory.item"), product.get(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD).toString().trim());
									sqlDelete = daoUtils.getString("mass.sql.receiving.delete.receiving.item");
								}
							}

								PreparedStatement pstmt = null;
								if (sqlDelete != null && sqlInsertWhenFullData != null && sqlUpdate != null) {
									// SQL WHEN FULL DATA
									
									 pstmt = conn.prepareStatement(sqlUpdate);
									 pstmt.setString(1, product.get(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD).toString().trim());
									 pstmt.setDate(2, (Date)product.get(MassContansts.PRODUCT_FIELD_COMMON.RECEIVED_DATE));
									 System.out.println("SQL UPDATE INFO RECEIVING FULL ("
												+ product.get(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD) + "):" + pstmt);
									 pstmt.execute();
									
									 pstmt = conn.prepareStatement(sqlInsertWhenFullData);
									 System.out.println("SQL INSERT FULL ("
												+ product.get(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD) + "):" + pstmt);
									 pstmt.execute();
									 
									 pstmt = conn.prepareStatement(sqlDelete);
									 pstmt.setString(1, product.get(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD).toString().trim());
									 System.out.println("SQL DELETE FULL ("
											+ product.get(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD) + "):" + pstmt);
									 pstmt.execute();

								} else {
									pstmt = conn.prepareStatement(sql);
									pstmt.setString(1, product.get(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD).toString().trim());
									pstmt.setDate(2, (Date)product.get(MassContansts.PRODUCT_FIELD_COMMON.RECEIVED_DATE));
									System.out.println("SQL NEW ("
											+ product.get(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD) + "):" + pstmt);
									pstmt.execute();
								}

								 conn.commit();
								 currentProduct = null;
								
						}
					}
				} catch (Exception e) {
					System.err.println("Error when IMPORT (Add to errors):"
							+ MassUtils.getExceptionDescription(e));
					conn.rollback();
					setErrorForProduct(currentProduct, String.format(MassContansts.ERRORS.EXCEPTION_EXECUTE_QUERY, currentProduct.get(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD), MassUtils.getExceptionDescription(e)));
				}
			}

		} catch (Exception e) {
			throw new Exception(String.format(MassContansts.ERRORS.EXCEPTION_BUILD_QUERY_RECEIVING_FILE, MassUtils.getExceptionDescription(e)));
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException sqlE) {
				sqlE.getStackTrace();
			}
		}

	}

	/**
	 * Get data info when upload prices template
	 * @param headerDataInfo
	 * @param fieldsOrder
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Map<String, Object>> getDataInfoExcelToHash(
			Map<String, Object> headerDataInfo, String[] fieldsOrder)
			throws Exception {
		Map<String, Map<String, Object>> products = null;

		Iterator<Row> rowIterator = null;
		int totalCol = -1;
		
		if (headerDataInfo != null) {
			rowIterator = (Iterator<Row>) headerDataInfo.get("dataRow");
			totalCol = (Integer) headerDataInfo.get("totalCol");
			products = new HashMap<String, Map<String, Object>>();

			boolean isHeaderRow = true;
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				if (isHeaderRow) {
					isHeaderRow = false;
					continue;
				}

				if (row == null) {
					continue;
				}

				Map<String, Object> productItem = new HashMap<String, Object>();

				int firstCol = row.getFirstCellNum();
				int colError = -1;
				try {
					for (int colIx = firstCol; colIx < totalCol; colIx++) {
						Cell cell = row.getCell(colIx);
						Object value = null;
						if (cell != null) {
							colError = colIx;
							value = forceGetCellStringValue(cell);
							productItem.put(fieldsOrder[colIx], value);
						}
					}

					if (productItem.get(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD) != null) {
						if (productItem.get(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD).toString().trim() == "") {
							// errorsData.add("ITEM SKU in line " +
							// row.getRowNum() + " must not empty");
							setErrorForProduct(productItem, String.format(MassContansts.ERRORS.ITEM_SKU_EMPTY_LINE, row.getRowNum() + 1));
//							System.out.println(String.format(MassContansts.ERRORS.ITEM_SKU_EMPTY_LINE, row.getRowNum() + 1));
						} else {
							products.put(
									String.valueOf(productItem.get(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD))
											.trim(), productItem);
						}
					}

				} catch (Exception e) {
					System.err.println("Data WRONG in item SKU: "
							+ productItem.get(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD) + " - Field:"
							+ " - Column: " + colError + " - Row: "
							+ row.getRowNum() + 1);
					setErrorForProduct(productItem, String.format(MassContansts.ERRORS.ROW_DATA_INVALID_PRICE, row.getRowNum() + 1, colError));
				}
			}

		}
		System.out.println("PRODUCTS SIZE:" + products.size());

		if (products.size() <= 0) {
			throw new Exception(MassContansts.ERRORS.NO_DATA_VALID);
		}

		return products;
	}
	
	
	
	
	/**
	 * Get detail error when upload incoming excel template
	 * @param products
	 * @param headerDataInfo
	 * @param originFileName
	 * @param isExcel2003
	 * @return
	 * @throws Exception 
	 */
	public Map<String, Object> getErrorInfomation(Map<String, Map<String, Object>> products, Map<String, Object> headerDataInfo, String originFileName, boolean isExcel2003) throws Exception{
		
		Map<String, Object> rsInfo = new HashMap<String, Object>();
		
		
		int totalDataInsertSuccess = 0;
		int totalDataInsertError = 0;
		
		int totalRowData = products.size();
		int totalColumnExcel = (Integer)headerDataInfo.get("totalCol");
		
		System.out.println("totalRealData:" + totalRowData);
		System.out.println("totalColumnExcel:" + totalColumnExcel);
		
		int[] lstExcelSuccessIdx = new int[2*totalRowData]; //For remove in origin excel file
		
		Object workbookObj = headerDataInfo.get("workbook");
		
		HSSFWorkbook wbHs = null;
		XSSFWorkbook wbXs = null;

		HSSFSheet shHs = null;
		XSSFSheet shXs = null;

		if (isExcel2003) {
			wbHs = (HSSFWorkbook) workbookObj;
			shHs = wbHs.getSheetAt(0);
		} else {
			wbXs = (XSSFWorkbook) workbookObj;
			shXs = wbXs.getSheetAt(0);
		}
		
		//Create header error detail
		MassUtils.createCellValue(workbookObj, 0, totalColumnExcel, MassContansts.HEADER_ERROR_EXCEL);
		
		
		try {
			String keyCheck = MassContansts.IS_DATA_ERROR_ATTR;
			for (String key : products.keySet()) {
				Map<String, Object> item = products.get(key);
				
				int rowIdx = (Integer) item
						.get(MassContansts.INDEX_IN_EXCEL);
				
				if (item.containsKey(keyCheck) && (Boolean) item.get(keyCheck)) {
					//INSERT FAILED
					String detailError = item.get(
							MassContansts.DATA_ERROR_DETAIL).toString();

					System.out.println("ERROR:" + rowIdx + " - TOTAL: "
							+ totalColumnExcel + " - Detail: " + detailError
							+ "");
					
					MassUtils.createCellValue(workbookObj, rowIdx, totalColumnExcel, detailError);
					
				} else{
					//INSERT SUCCESS
					totalDataInsertSuccess++;
					lstExcelSuccessIdx[rowIdx] = rowIdx;
				}
			}
		} catch (Exception e) {
			throw new Exception(MassUtils.getExceptionDescription(e));
		}
		
		
		
		lstExcelSuccessIdx = MassUtils.removeAllZeroValueArray(lstExcelSuccessIdx);
		
		if (lstExcelSuccessIdx.length > 0){
			java.util.Arrays.sort(lstExcelSuccessIdx);
			System.out.println("----------------LST EXCEL IDX SORTED---------------------------------------");
			for (int i = lstExcelSuccessIdx.length - 1; i >= 0; i--) {
				if (isExcel2003){
					MassUtils.removeRow(shHs, lstExcelSuccessIdx[i]);
				} else{
					MassUtils.removeRow(shXs, lstExcelSuccessIdx[i]);
				}
			}
		}
		
		String fileNameError = null;
		if (isExcel2003){
			fileNameError = FilenameUtils.removeExtension(originFileName) + "ErrorDetails.xls";
		} else{
			fileNameError = FilenameUtils.removeExtension(originFileName) + "ErrorDetails.xlsx";
		}
		
		
		totalDataInsertError = totalRowData - totalDataInsertSuccess;
		
		if (isExcel2003) {
			rsInfo.put("workbookError", wbHs);
		} else {
			shXs = wbXs.getSheetAt(0);
			rsInfo.put("workbookError", wbXs);
		}
		rsInfo.put("totalDataInsertSuccess", totalDataInsertSuccess);
		rsInfo.put("totalDataInsertError", totalDataInsertError);
		rsInfo.put("totalData", totalRowData);
		rsInfo.put("fileNameError", fileNameError);
		
		
		return rsInfo;
		
	}
	
	
}
