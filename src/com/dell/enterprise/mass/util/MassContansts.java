package com.dell.enterprise.mass.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

public class MassContansts {
	
	//FIXME: Find cosmetic
	// DATABASE field --> EXCEL field	
	//	lease_number --> brand_code
	//	contract_number --> promo_code
	//	warranty_date --> factory_date
	//	manufacturer_id --> internal_sku
	//	image_url	--> cosmetic_grade 
	
	public enum STATUS_PRODUCT_TYPE {
	    IMPORT, WAREHOUSE, FULL;
	}
	
	public static final String IMPORT_PAGE_TYPE_KEY = "IMPORT_PAGE_TYPE_KEY";
	public enum IMPORT_PAGE_TYPE {
		EXCEL_TEMPLATE, RECEIVING_TEMPLATE, PRICE_TEMPLATE
	}
	
	
	public static final String HEADER_ERROR_EXCEL = "Data error detail";
	
	public static final int START_PAGE_INDEX = 1;
	
	public static final String PREFIX_CUSTOM_FIELD = "Attribute";
	public static final int FIRST_ATTRIBUTE_INDEX = 1;
	public static final String PREFIX_NAME_TEMPLATE_AUTO = "DefaultNameTemplate";

	
	public static final String INSERT_ATTRIBUTE_PRODUCT = "insert";
	public static final String STATUS_ATTRIBUTE_PRODUCT = "statusImport";
	public static final String RECEIVED_WAREHOUSE_INFO_ATTRIBUTE_PRODUCT = "receivedWarehouseInfo";
	public static final String INSERTED_INCOMING_PRODUCT = "insertedIncomingProduct";
	public static final String INSERTED_HISTORY = "insertedHistoryTable";
	
	public static final String IS_DATA_ERROR_ATTR = "isDataError";
	public static final String DATA_ERROR_DETAIL = "dataErrorDetail";
	public static final String INDEX_IN_EXCEL = "indexExcel";
	
	public static final String DUPLICATE_ORIGIN_SKU = "duplicateOriginSKU";
	public static final String MOCK_VALUE_SKU = "'MOCK_VALUE_OF_SKU'"; //Mock SKU value when don't have any valid data
	public static final String EMPTY = "";
	
	public interface LIST_TYPE_FIELD_EXCEL{
		String[] DATE_FIELD = new String[] {"warranty_date"};
		String[] INT_FIELD = new String[] {"category_id", "weight"};
		String[] FLOAT_FIELD = new String[] {"list_price", "weight"};
		String[] CURRENCY_FIELD = new String[]{"list_price"};
	}
	
	public static Integer[] PRODUCT_IDS_FIRST_TEMPLATE_APPLY = new Integer[] { 11947, 11949, 11946,
		11955, 11956, 11957, 11958, 11962, 11950, 11940, 11941 };
	
	public static final String[] FIELDS_IN_INVENTORY_TABLE_EXCEPT_EXT_ATTR = new String[]{"category_id", "item_SKU", "name", "short_description", "list_price", "mfg_part_number", "mfg_SKU", "status", "warehouse_location", "weight", "lease_number", "contract_number", "warranty_date", "manufacturer_id", "image_url"};
	public static final String[] FIELDS_ATTRIBUTES_EXT = new String[]{"attribute01", "attribute02", "attribute03", "attribute04", "attribute05", "attribute06", "attribute07", "attribute08", "attribute09", "attribute10", "attribute11", "attribute12", "attribute13", "attribute14", "attribute15", "attribute16", "attribute17", "attribute18", "attribute19", "attribute20", "attribute21", "attribute22", "attribute23", "attribute24", "attribute25", "attribute26", "attribute27", "attribute28", "attribute29", "attribute30", "attribute31", "attribute32", "attribute33", "attribute34", "attribute35", "attribute36", "attribute37", "attribute38", "attribute39", "attribute40"};
	public static String[] FIELDS_IN_INVENTORY = ArrayUtils.addAll(FIELDS_IN_INVENTORY_TABLE_EXCEPT_EXT_ATTR, FIELDS_ATTRIBUTES_EXT);

	public static final String[] FIELDS_ORDER_TEMPLATE_FIRST_TYPE = { "category_id", "item_SKU", "name",
		"short_description", "lease_number", "image_url",
		"manufacturer_id", "list_price", "mfg_part_number", "mfg_SKU",
		"contract_number", "status", "warehouse_location", "warranty_date",
		"weight"};
	
	public static final String[] FIELDS_ORDER_TEMPLATE_OTHER_TYPE = { "category_id", "name", "item_SKU",
		"weight", "short_description", "image_url", "status",
		"list_price", "mfg_part_number", "mfg_SKU" };
	
	public static String[] FIELDS_ORDER_TEMPLATE_FULL_ATTR = ArrayUtils.addAll(FIELDS_ORDER_TEMPLATE_FIRST_TYPE, FIELDS_ATTRIBUTES_EXT);
	public static String[] FIELDS_ORDER_TEMPLATE_OTHER_FULL_ATTR = ArrayUtils.addAll(FIELDS_ORDER_TEMPLATE_OTHER_TYPE, FIELDS_ATTRIBUTES_EXT);
	
	
	public static String[] ALL_FIELD_IN_INVENTORY_TABLE = {"item_SKU", "category_id", "manufacturer_id", "mfg_part_number", "name", "image_url", "short_description", "long_description", "weight", "download_filename", "received_by", "received_date", "warehouse_location", "status", "status_date", "list_price", "modified_price", "modified_date", "warranty_date", "flagtype", "lease_number", "contract_number", "mfg_SKU", "qty", "days_at_mri", "notes", "attribute01", "attribute02", "attribute03", "attribute04", "attribute05", "attribute06", "attribute07", "attribute08", "attribute09", "attribute10", "attribute11", "attribute12", "attribute13", "attribute14", "attribute15", "attribute16", "attribute17", "attribute18", "attribute19", "attribute20", "attribute21", "attribute22", "attribute23", "attribute24", "attribute25", "attribute26", "attribute27", "attribute28", "attribute29", "attribute30", "attribute31", "attribute32", "attribute33", "attribute34", "attribute35", "attribute36", "attribute37", "attribute38", "attribute39", "attribute40", "updatedby", "updateddate", "notrigger", "origin"};
	public static String[] ALL_FIELD_IN_RECEIVING_TABLE = {"item_SKU", "category_id", "manufacturer_id", "mfg_part_number", "name", "image_url", "short_description", "long_description", "weight", "download_filename", "received_by", "received_date", "warehouse_location", "status", "status_date", "list_price", "modified_price", "modified_date", "warranty_date", "flagType", "lease_number", "contract_number", "mfg_SKU", "notes", "attribute01", "attribute02", "attribute03", "attribute04", "attribute05", "attribute06", "attribute07", "attribute08", "attribute09", "attribute10", "attribute11", "attribute12", "attribute13", "attribute14", "attribute15", "attribute16", "attribute17", "attribute18", "attribute19", "attribute20", "attribute21", "attribute22", "attribute23", "attribute24", "attribute25", "attribute26", "attribute27", "attribute28", "attribute29", "attribute30", "attribute31", "attribute32", "attribute33", "attribute34", "attribute35", "attribute36", "attribute37", "attribute38", "attribute39", "attribute40", "defective", "impDate", "origin"};
	
	public static String[] ALL_FIELD_IN_INVENTORY_TABLE_HEADER = {"Item SKU", "Category Id", "Manufacturer Id", "Mfg Part Number", "Name", "Image Url", "Short Description", "Long Description", "Weight", "Download Filename", "Received By", "Received Date", "Warehouse Location", "Status", "Status Date", "List Price", "Modified Price", "Modified Date", "Warranty Date", "Flagtype", "Lease Number", "Contract Number", "Mfg Sku", "Qty", "Days At Mri", "Notes", "Attribute01", "Attribute02", "Attribute03", "Attribute04", "Attribute05", "Attribute06", "Attribute07", "Attribute08", "Attribute09", "Attribute10", "Attribute11", "Attribute12", "Attribute13", "Attribute14", "Attribute15", "Attribute16", "Attribute17", "Attribute18", "Attribute19", "Attribute20", "Attribute21", "Attribute22", "Attribute23", "Attribute24", "Attribute25", "Attribute26", "Attribute27", "Attribute28", "Attribute29", "Attribute30", "Attribute31", "Attribute32", "Attribute33", "Attribute34", "Attribute35", "Attribute36", "Attribute37", "Attribute38", "Attribute39", "Attribute40", "Updatedby", "Updateddate", "Notrigger", "Origin"};
	public static String[] ALL_FIELD_IN_RECEIVING_TABLE_HEADER = {"Item SKU", "Category Id", "Manufacturer Id", "Mfg Part Number", "Name", "Image Url", "Short Description", "Long Description", "Weight", "Download Filename", "Received By", "Received Date", "Warehouse Location", "Status", "Status Date", "List Price", "Modified Price", "Modified Date", "Warranty Date", "Flagtype", "Lease Number", "Contract Number", "Mfg Sku", "Notes", "Attribute01", "Attribute02", "Attribute03", "Attribute04", "Attribute05", "Attribute06", "Attribute07", "Attribute08", "Attribute09", "Attribute10", "Attribute11", "Attribute12", "Attribute13", "Attribute14", "Attribute15", "Attribute16", "Attribute17", "Attribute18", "Attribute19", "Attribute20", "Attribute21", "Attribute22", "Attribute23", "Attribute24", "Attribute25", "Attribute26", "Attribute27", "Attribute28", "Attribute29", "Attribute30", "Attribute31", "Attribute32", "Attribute33", "Attribute34", "Attribute35", "Attribute36", "Attribute37", "Attribute38", "Attribute39", "Attribute40", "Defective", "Imp Date", "Origin"};
	
	
	public static String[] RECEIVING_FILE_ERROR_TEMPLATE_HEADER = {"Item SKU", "Received Date", "Data error detail"};
	public static String[] FILTER_KEY_ERROR_TEMPLATE_HEADER = {"item_SKU", "received_date", "dataErrorDetail"};
	
	
	public static String RECEIVED_DATE_FORMAT = "M/d/yyyy";
	public static String DATE_FORMAT= "MM/dd/yyyy";
	public static final String NUMBER_FORMAT = "#0.00";
	
	
	public static String INVENTORY_EXPORT_FILENAME = "InventoryList.xls";
	public static String RECEIVING_EXPORT_FILENAME = "ReceivingQueue.xls";
	
	public static String INVENTORY_SHEET_NAME = "Inventory Export";
	public static String RECEIVING_SHEET_NAME = "Receiving Export";
	public static String ERROR_SHEET_NAME = "Errors";
	
	public interface ERRORS {
	    String CATEGORY_EMPTY = "Category in row 2 - col 1 must not empty";
	    String DATA_EMPTY = "Excel template must have data";
	    String HEADER_ROW_EMPTY = "Invalid template excel file";
	    String SHEET_EMPTY = "Sheet empty";
	    String READ_CATEGORY_ID_ERROR = "Cannot read category id";
	    String TOTAL_COLUMN_WRONG = "Invalid template excel file";
	    // String ITEM_SKU_EMPTY_LINE = "ITEM SKU in line %s must not empty"; //Line error
	    String ITEM_SKU_EMPTY_LINE = "[item_SKU] data in row %s is not exist"; //Line error
	    String CATEGORY_MATCH = "Category Id must have the same CategoryID value";
	    String CATEGORY_EMPTY_LINE = "Category Id in line %s must not empty";
	    String DUPLICATE_SKU = "Duplicate SKU";
	    String ROW_DATA_INVALID = "Error field: %s - Column: %s"; //Field - column index error
	    String NO_DATA_VALID = "Invalid template of receiving file";
	    String DATA_INSERTED_TO_INVENTORY = "SKU item %s was inserted into estore_inventory table"; //SKU
	    String DATA_INSERTED_TO_HISTORY = "SKU item %s was inserted into estore_history table"; //SKU
	    String EXCEPTION_UPDATE_PRODUCT_STATUS = "Exception updateProductStatusFromListSKU: %s"; //SKU
	    String EXCEPTION_EXECUTE_QUERY = "Error when execute query [SKU=%s]: %s"; //SKU, Error exception
	    String EXCEPTION_BUILD_QUERY = "Error when build query: %s"; //SKU, Error exception
	    
	    //Receiving error
	    String ITEM_SKU_REQUIRED = "Item SKU is required";
	    String RECEIVED_DATE_REQUIRED = "Received date is required";
	    String RECEIVED_DATE_FORMAT = "Wrong format datetime in receiving file: %s"; //Value datetime format
	    String LEAK_INFO_IN_ROW = "Leak information in row: %s"; //Row
	    String READ_ITEM_RECEIVING_FILE = "Row error: %s - Message: %s";
	    String EXCEPTION_BUILD_QUERY_RECEIVING_FILE = "Exception in buildSQLQueries: %s";
	    
	    //Prices
	    String ROW_DATA_INVALID_PRICE = "Error row: %s - Column: %s"; //Row - column index error
	    
	    String IMPORT_FILE = "Please select a file to upload";
	    String IMPORT_TXT_FILE = "Please select a file to upload";
	    String DATE_WRONG_FORMAT = "Wrong format date";
		String INSERTED_RECEIVING_BEFORE_SAME_TYPE = "SKU item %s was inserted into receiving table with the same type";
		String PROBLEM_SET_PARAM = "Have problem when set param (field='%s', value='%s'): %s";
	}
	
	
	public interface PRODUCT_FIELD_COMMON{
		String SKU_FIELD =  "item_SKU";
		String NAME_FIELD =  "name";
		String RECEIVED_DATE =  "received_date";
		String CATEGORY_ID =  "category_id";
		String LIST_PRICE = "list_price";
	}
	
	
	public interface EXCEL_EXTENSION{
		String VERSION_2003 = "xls";
		String VERSION_GREATER_2003 = "xlsx";
	}
	
	
	public static final String BREAD_CRUMB_UPLOAD_EXCEL_INVENTORY = "UploadExcelInventory";
	public static final String BREAD_CRUMB_UPLOAD_TXT_RECEIVING = "UploadTxtReceiving";
	public static final String BREAD_CRUMB_KEY = "breadcrumbStatus";
	
	
	public static final int[] RECORD_PER_PAGE = {10, 20, 30, 40, 50, 75, 100, 125, 150, 175, 200};
	public static final int DEFAULT_RECORD_PER_PAGE = 50;
	
	
	
	public interface TYPE_FIELD_INVENTORY_RECEIVING_TABLE{
		String[] DATE_FIELDS = {"status_date", "modified_date", "warranty_date", "received_date", "updateddate", "impDate"};
		String[] DOUBLE_FIELDS = {"list_price", "modified_price", "warranty_date"};
	}
	
	
	//LAPTOP, Warranty NB, Warranty DT
	public static final Map<Integer, String[]> ATTRIBUTES_ORDER;
    static {
        Map<Integer, String[]> aMap = new HashMap<Integer, String[]>();
        
        String MONITOR[] = {"attribute01", "attribute04", "attribute02", "attribute05", "attribute06", "attribute03", "attribute07"};
        String TPRINTER[] = {"attribute01", "attribute07", "attribute02"};
        String PROJECTORS[] = {"attribute01","attribute12","attribute02","attribute05","attribute14","attribute15","attribute16"};
        String DOCKING[] = {"attribute01", "attribute02", "attribute03"};
        String MEMORY[] = {"attribute01","attribute02","attribute03","attribute04","attribute35"};
        String BATTERY[] = {"attribute01","attribute02","attribute03","attribute04","attribute05","attribute06","attribute07","attribute08","attribute09","attribute35"};
        String STORAGE_NETWORKING_PARTS[] = {"attribute02","attribute03","attribute04","attribute05"};

        //EMPTY	attribute20	attribute26	attribute27	attribute31
        String SYSTEMS_WORKSTATION_DESKTOP_SERVERS_EXCEPT_11950[] = {"attribute01", "attribute02", "attribute03", "attribute04", "attribute05", "attribute06", "attribute37", "attribute07", "attribute08", "attribute09", "attribute10", "attribute11", "attribute12", "attribute13", "attribute14", "attribute15", "attribute16", "attribute17", "attribute18", "attribute19", "attribute22", "attribute23", "attribute24", "attribute25", "attribute21", "attribute33", "attribute34", "attribute35", "attribute29", "attribute30", "attribute36", "attribute32", "attribute38", "attribute28"};
        
        //EMPTY attribute01
        String SYSTEMS_WORKSTATION_DESKTOP_SERVERS_11950[] = {"attribute02", "attribute03", "attribute04", "attribute05", "attribute16", null, null, null, null, null, "attribute06", "attribute07", "attribute08", "attribute09", "attribute10", "attribute11", "attribute12", "attribute13", "attribute14", "attribute15", "attribute18", "attribute19", "attribute21", "attribute22", null, null, "attribute23", "attribute27", "attribute25", "attribute24", "attribute26", null, null, "attribute17"};
        
        aMap.put(11955, MONITOR);
        aMap.put(11957, TPRINTER);
        aMap.put(11956, PROJECTORS);
        
        aMap.put(11958, DOCKING);
        aMap.put(11959, MEMORY);
        aMap.put(11960, BATTERY);
        
        aMap.put(11961, STORAGE_NETWORKING_PARTS);//Storage
        aMap.put(11962, STORAGE_NETWORKING_PARTS);//Networking
        aMap.put(11963, STORAGE_NETWORKING_PARTS);//Parts
        
        aMap.put(11947, SYSTEMS_WORKSTATION_DESKTOP_SERVERS_EXCEPT_11950);
        aMap.put(11949, SYSTEMS_WORKSTATION_DESKTOP_SERVERS_EXCEPT_11950);
        
        aMap.put(11950, SYSTEMS_WORKSTATION_DESKTOP_SERVERS_11950);
        
        ATTRIBUTES_ORDER = Collections.unmodifiableMap(aMap);
    }
	
	
}
