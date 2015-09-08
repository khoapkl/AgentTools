package com.dell.enterprise.mass.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hslf.model.Sheet;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.upload.FormFile;

public class MassUtils {

	public static HSSFWorkbook createWorkbookFromList(List<Map<String, Object>> listHash, String[] headers, String sheetName) throws Exception {
		
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet(sheetName);
        
        HSSFCell cell = null;
        HSSFRow row = null;
        
        createHeaderAndStyle(wb, sheet, headers);
        
        int i = 0;
        for (i = 0; i < listHash.size(); i++) {
            Map<String, Object> productItem = listHash.get(i);
            
            row = sheet.createRow(i + 1);
            
            int colCount = 0;
            for (String key:productItem.keySet()) {
            	cell = row.createCell(colCount);
            	
            	Object valueTemp = productItem.get(key);
            	String valueStr = "";
            	if (valueTemp != null){
            		valueStr = String.valueOf(valueTemp);
            	}
            	
            	HSSFRichTextString value = new HSSFRichTextString(valueStr);
            	
            	cell.setCellValue(value);
            	colCount++;
			}
            
        }
        return wb;
    }
	
	
	
	public static HSSFWorkbook createWorkbookFromHash(Map<String, Map<String, Object>> listHash, String[] headers, String sheetName, String[] filtersKey) throws Exception {
		
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet(sheetName);
        
        HSSFCell cell = null;
        HSSFRow row = null;
        
        createHeaderAndStyle(wb, sheet, headers);
        
        int i = 0;
        for (String keySKU : listHash.keySet()) {
        	
    		Map<String, Object> productItem = listHash.get(keySKU);
            
            row = sheet.createRow(i + 1);
            
            int colCount = 0;
//            for (String keyItem : productItem.keySet()) {
//            	if (Arrays.asList(filtersKey).contains(keyItem)){
            
            	for (int j = 0; j < filtersKey.length; j++) {
					String keyItem = filtersKey[j];
					cell = row.createCell(colCount);
                	
            		Object valueTemp = productItem.get(keyItem);
            		
            		if (productItem.containsKey(MassContansts.DUPLICATE_ORIGIN_SKU) && keyItem.equalsIgnoreCase(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD)){
            			valueTemp = productItem.get(MassContansts.DUPLICATE_ORIGIN_SKU);
            		}
                	
                	String valueStr = "";
                	if (valueTemp != null){
                		valueStr = String.valueOf(valueTemp);
                	}
                	
                	HSSFRichTextString value = new HSSFRichTextString(valueStr);
                	
                	cell.setCellValue(value);
                	colCount++;
				}
            
            		
//            	}
//			}
            i++;
		}
        
        
        return wb;
    }
	
	
    
    private static void createHeaderAndStyle(HSSFWorkbook wb, HSSFSheet sheet, String[] headers){
        //Style
        HSSFCellStyle headerCellStyle = wb.createCellStyle();
        HSSFFont boldFont = wb.createFont();
        boldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        headerCellStyle.setFont(boldFont);
        
        HSSFRow row = sheet.createRow(0);
        
        
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(headerCellStyle);
            cell.setCellValue(new HSSFRichTextString(headers[i]));
        }
    }
	
	
	
	
	public static void printHashMapForTest(Map<String, Map<String, Object>> hash, String message) {
		System.out.println("------------------------START "+message+" NEW[" + hash.size()
				+ "]------------------------------------------");
		StringBuilder sb = new StringBuilder();
		for (String key : hash.keySet()) {
			Map<String, Object> item = hash.get(key);
			for (String keyProd : item.keySet()) {
				sb.append("[" + keyProd + "] = " + item.get(keyProd) + "\t ");
			}
			sb.append("\n\n");
		}
		System.out.println(sb.toString());
			
		System.out.println("--------------------------END----------------------------------------");
	}
	
	public static void printHashMapForTestWithKey(Map<String, Map<String, Object>> hash, String message, String keyCheck) {
		System.out.println("------------------------START "+message+" NEW[" + hash.size()
				+ "]------------------------------------------");
		StringBuilder sb = new StringBuilder();
		for (String key : hash.keySet()) {
			Map<String, Object> item = hash.get(key);
			if (item.containsKey(keyCheck) && (Boolean)item.get(keyCheck) ){
				for (String keyProd : item.keySet()) {
					sb.append("[" + keyProd + "] = " + item.get(keyProd) + "\t ");
				}
				sb.append("\n\n");
			}
		}
		System.out.println(sb.toString());
			
		System.out.println("--------------------------END----------------------------------------");
	}
	
	public static Map<String, Object> getProductsErrorMap(Map<String, Map<String, Object>> hash, String keyCheck) {
		Map<String, Object> rs = new HashMap<String, Object>();
		
		int countError = 0;
		int countSuccess = 0;
		int totalRecord = hash.size();
		Map<String, Map<String, Object>> lstError = new HashMap<String, Map<String,Object>>();
		for (String key : hash.keySet()) {
			Map<String, Object> item = hash.get(key);
			if (item.containsKey(keyCheck) && (Boolean)item.get(keyCheck) ){
				lstError.put(item.get(MassContansts.PRODUCT_FIELD_COMMON.SKU_FIELD).toString().trim(), item);
				countError++;
			}
		}
		
		countSuccess = totalRecord - countError;
		System.out.println("countSuccess : " + String.valueOf(countSuccess));
		
		rs.put("totalDataInsertSuccess", countSuccess);
		rs.put("totalDataInsertError", countError);
		rs.put("totalData", totalRecord);
		rs.put("productsError", lstError);
		
		return rs;
	}
	
	
	
	
	
	
	public static int[] removeAllZeroValueArray(int[] array){
		int j = 0;
	    for( int i=0;  i<array.length;  i++ )
	    {
	        if (array[i] != 0)
	            array[j++] = array[i];
	    }
	    int [] newArray = new int[j];
	    System.arraycopy( array, 0, newArray, 0, j );
	    return newArray;
	}
	
	
	public static void removeRow(Object sheet, int rowIndex) {
		HSSFSheet sheet1 = null;
		XSSFSheet sheet2 = null;
		
		if (sheet instanceof HSSFSheet){
			sheet1 = (HSSFSheet) sheet;
			
			int lastRowNum=sheet1.getLastRowNum();
		    if(rowIndex>=0&&rowIndex<lastRowNum){
		        sheet1.shiftRows(rowIndex+1,lastRowNum, -1);
		    }
		    if(rowIndex==lastRowNum){
		        HSSFRow removingRow=sheet1.getRow(rowIndex);
		        if(removingRow!=null){
		            sheet1.removeRow(removingRow);
		        }
		    }
			
		} else{
			sheet2 = (XSSFSheet) sheet;
			
			int lastRowNum=sheet2.getLastRowNum();
		    if(rowIndex>=0&&rowIndex<lastRowNum){
		        sheet2.shiftRows(rowIndex+1,lastRowNum, -1);
		    }
		    if(rowIndex==lastRowNum){
		        XSSFRow removingRow=sheet2.getRow(rowIndex);
		        if(removingRow!=null){
		            sheet2.removeRow(removingRow);
		        }
		    }
			
		}
		
	    
	    
	}
	
	
	public static boolean isExcel2003WithFilename(String filename){
		boolean isExcel2003 = true;
		if (FilenameUtils.getExtension(filename).equalsIgnoreCase(MassContansts.EXCEL_EXTENSION.VERSION_GREATER_2003)) {
			isExcel2003 = false;
		}
		return isExcel2003;
	}
	
	private static String createFolder(ActionServlet servlet){
		String folderPath = servlet.getServletContext().getRealPath("/") + "uploadfortest";
		File folder = new File(folderPath);
		if (!folder.exists()) {
			folder.mkdir();
		}
		return folderPath;
	}
	
	public static String removeAllSpace(String st){
		return st.replaceAll("\\s+","");
	}
	
	public static String createExcelFile(ActionServlet servlet, String fileName, Object workbook) throws Exception{
		String folderPath = createFolder(servlet);
		String fileNameWithoutExt = FilenameUtils.getBaseName(fileName);
		String fileExt = FilenameUtils.getExtension(fileName);
		File newFile = null;
		
		String randomName = String.valueOf(new java.util.Date()
		.getTime());
		
		if (StringUtils.isEmpty(fileExt) 
				|| (!fileExt.equalsIgnoreCase(MassContansts.EXCEL_EXTENSION.VERSION_2003) && !fileExt.equalsIgnoreCase(MassContansts.EXCEL_EXTENSION.VERSION_GREATER_2003)) ){
			fileExt = MassContansts.EXCEL_EXTENSION.VERSION_2003;
		}
		
		String randomFileName = removeAllSpace(fileNameWithoutExt) + randomName + "." + fileExt;
		System.out.println("RANDOM PATH:" + String.valueOf(folderPath + randomFileName));
		
		if(!StringUtils.isEmpty(randomFileName)){  
	        System.out.println("Server path:" +folderPath);
        	newFile = new File(folderPath, randomFileName);
	        if(!newFile.exists()){
	          FileOutputStream fos = new FileOutputStream(newFile);
	          
	          HSSFWorkbook hsWb = null;
	          XSSFWorkbook xsWb = null;
	          if (workbook instanceof HSSFWorkbook){
	        	  hsWb = (HSSFWorkbook) workbook;
	        	  hsWb.write(fos);
	          } else{
	        	  xsWb = (XSSFWorkbook) workbook;
	        	  xsWb.write(fos);
	          }
	          fos.flush();
	          fos.close();
	        } else{
	        	throw new Exception("File exists!");
	        }
	    }
		
		if (newFile == null){
			throw new Exception("Create file failed");
		}
		
		return newFile.getAbsolutePath();
	}
	
	
	public static String getDateStringFormat(Date date, String format){
		String dateStr = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			sdf.setLenient(false);
			dateStr = sdf.format(date);
		} catch (Exception e) {
			//FIXME: process when String wrong format
			// throw new Exception(MassContansts.ERRORS.DATE_WRONG_FORMAT);
		}
		return dateStr;
	}
	
	
	public static String getNumberStringFormat(double db, String format){
		String numberStr = "";
		try {
			NumberFormat formatter = new DecimalFormat(format);     
			numberStr = formatter.format(db);
		} catch (Exception e) {
			//FIXME: process when String wrong format
		}
		return numberStr;
	}
	
	
	
	/*public static Connection getInstanceMYSQLForTest() throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		return DriverManager.getConnection("jdbc:mysql://10.10.30.14:3306/agent_tool?user=root&password=123456");
	}*/
	
	
	public static String random(){
		Random rd = new Random();
		return String.valueOf(rd.nextInt(1000000));
	}
	
	public static long getCurrentTime(){
		return new java.util.Date().getTime();
	}
	
	public static String getExceptionDescription(Exception e){
		String error = null;
//		if (e != null && e.getStackTrace() != null && e.getStackTrace().length > 0){
//			error = e.getMessage() + ": " + e.getStackTrace()[0].toString();
//		}
		if (e != null && e.getMessage() != null){
			error = e.getMessage();
		}
		return error;
	}
	
	
	public static void createCellValue(Object workbookObj, int rowIdx, int columnIdx, String value){
		if (workbookObj instanceof HSSFWorkbook){
			HSSFWorkbook wbHs = (HSSFWorkbook) workbookObj;
			HSSFSheet shHs = wbHs.getSheetAt(0);
			HSSFRow row = shHs.getRow(rowIdx);
			Cell cell = row.createCell(columnIdx, Cell.CELL_TYPE_STRING);
			cell.setCellValue(value);
		} else{
			XSSFWorkbook wbXs = (XSSFWorkbook) workbookObj;
			XSSFSheet shXs = wbXs.getSheetAt(0);
			XSSFRow row = shXs.getRow(rowIdx);
			Cell cell = row.createCell(columnIdx, Cell.CELL_TYPE_STRING);
			cell.setCellValue(value);
		}
	}
	


}
