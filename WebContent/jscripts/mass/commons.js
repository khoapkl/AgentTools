$(document).ready(function(){
	
	if ($("#method").length > 0){
		switch ($("#method").val()) {
		    case "receivingList":
		    	$(".menu-item-receiving").addClass('mass-current-selected-menu');
		        break;
		    case "inventoryList":
		    	$(".menu-item-inventory").addClass('mass-current-selected-menu');
		        break;
		    case "importExcelPage":
		    case "importReceivingPage":
		    	$(".menu-item-import-files").addClass('mass-current-selected-menu');
		    	break;
		}
	} else{
		console.log("Item id = method is not exists");
	}

});