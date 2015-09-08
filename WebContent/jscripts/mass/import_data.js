function validateFileExtensionText(filePath) 
{
   if(!/(\.txt)$/i.test(filePath)) {
//	   varError = varError + FieldName + "Invalid text file type.\n";
       return false;
   }
    return true;
}

function validateFileExtensionExcel(filePath) 
{
   if(!/(\.xls|\.xlsx)$/i.test(filePath)) {
//	   varError = varError + FieldName + "Invalid text file type.\n";
       return false;
   }
    return true;
}

$(document).ready(function(){
	if ($('#downloadError').length > 0){
		$('#downloadError').click(function(e) {
		    e.preventDefault();  //stop the browser from following
		    window.location.href = $(this).attr('href');
		});
		
		$('#downloadError').trigger('click');	
	} else{
		console.log("Don't have anything for download");
	}
	
	
	$(document).on('change', ".file-upload", function(e){
		console.log("FILENAME:" + $(this).val());
		
		var isValidFileName = true;
		
		var filePath = $(this).val();
		var type = $(this).attr('accept');
		var error = "";
		var isExcelType = true;
		
		if (type.indexOf("text/plain") >= 0){
			isExcelType = false;
		}
		
		if (isExcelType){
			isValidFileName = validateFileExtensionExcel(filePath);
			error = "The file format should be XLS or .XLSX";
		} else{
			isValidFileName = validateFileExtensionText(filePath);
			error = "The file format should be txt";
		}
		
		if (!isValidFileName){
	        $(this).replaceWith($(this).clone(true));
			alert(error);
		}
	});
	
	
});