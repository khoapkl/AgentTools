package com.dell.enterprise.mass.form;

import javax.servlet.http.HttpServletRequest;
 
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;

import com.dell.enterprise.mass.util.MassContansts;
 
public class ListProductForm extends ActionForm{
 
	/**
	 * 
	 */
	
	private String sku;
	private int totalPage;
	private int currentPage;
	private int recordPerPage;
	private int totalRecord;
	private int firstPage;
	private int lastPage;
	private boolean inventoryPage;
	
	


	public boolean isInventoryPage() {
		return inventoryPage;
	}


	public void setInventoryPage(boolean inventoryPage) {
		this.inventoryPage = inventoryPage;
	}


	public int getFirstPage() {
		return firstPage;
	}


	public void setFirstPage(int firstPage) {
	}


	public int getLastPage() {
		return lastPage;
	}


	public void setLastPage(int lastPage) {
	}


	public int getTotalRecord() {
		return totalRecord;
	}


	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}


	public String getSku() {
		return sku;
	}


	public void setSku(String sku) {
		this.sku = sku;
	}


	public int getTotalPage() {
		return totalPage;
	}


	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getCurrentPage() {
		return currentPage;
	}


	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getRecordPerPage() {
		return recordPerPage;
	}



	public void setRecordPerPage(int recordPerPage) {
		this.recordPerPage = recordPerPage;
	}
	
	
	public boolean isFirstPage(){
		return currentPage == MassContansts.START_PAGE_INDEX;
	}
	
	public boolean isLastPage(){
		return currentPage == totalPage;
	}
	


	@Override
	public ActionErrors validate(ActionMapping mapping,
	   HttpServletRequest request) {
 
	    ActionErrors errors = new ActionErrors();
 
	    /*if( getFile().getFileSize()== 0){
	       errors.add("common.file.err", new ActionMessage("error.common.file.required"));
	       return errors;
	    }
 
	    //only allow textfile to upload
	    if(!"text/plain".equals(getFile().getContentType())){
	        errors.add("common.file.err.ext", new ActionMessage("error.common.file.textfile.only"));
	        return errors;
	    }
 
	    System.out.println(getFile().getFileSize());
	    if(getFile().getFileSize() > 1024 * 1024 * 50){ //50MB
	       errors.add("common.file.err.size",
		    new ActionMessage("error.common.file.size.limit", 10240));
	       return errors;
	    }*/
 
	    return errors;
	}
}
