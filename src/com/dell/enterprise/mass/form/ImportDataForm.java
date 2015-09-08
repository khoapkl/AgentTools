package com.dell.enterprise.mass.form;

import javax.servlet.http.HttpServletRequest;
 
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;
 
public class ImportDataForm extends ActionForm{
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private FormFile file;
 
	public FormFile getFile() {
		return file;
	}
 
	public void setFile(FormFile file) {
		this.file = file;
	}
 
	@Override
	public ActionErrors validate(ActionMapping mapping,
	   HttpServletRequest request) {
 
	    ActionErrors errors = new ActionErrors();
	    
	    if( getFile().getFileSize()== 0){
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
	    }
	    
 
	    return errors;
	}
}
