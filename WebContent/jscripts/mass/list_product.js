
function searchFunction(){
    $("#method").val($("#methodBak").val());
}

function exportToExcel(){
	$("#method").val("exportExcel");
}

function changePage(action){
    var currentPage = $("#currentPage").val();
    
    switch (action) {
        case 'PREV':
            var currentPage = $("#currentPage").val();
            if (!isNaN(currentPage)){
                currentPage = parseInt(currentPage);
                currentPage = currentPage - 1;
                if (currentPage <= 0){
                    currentPage = 1;
                }
            }
            break;
        case 'NEXT':
            var currentPage = $("#currentPage").val();
            var totalPage = document.getElementById("totalPage").value;
            if (!isNaN(currentPage) && !isNaN(totalPage)){
                currentPage = parseInt(currentPage);
                currentPage = currentPage + 1;
                if (currentPage > totalPage){
                    currentPage = totalPage;
                }
            }
            break;
        case 'FIRST':
            currentPage = 0;
            break;
        case 'LAST':
            currentPage = $("#totalPage").val();
            break;
    }
    $("#currentPage").val(currentPage);
}


function fullsizeTableMass(){
	var widthParentWrap = $(".wrap-line-mass-table").width();
	$(".wrap-mass-table").width(widthParentWrap);
	$(".wrap-mass-table").attr('style', 'max-width: none;');
	$(".wrap-mass-table").width(widthParentWrap);
}

$(document).ready(function(){
	// fullsizeTableMass();
	$("#recordPerPage").val($("#item-record-js").text());
	
	$("#recordPerPage").on('change', function(e){
		searchFunction();
		$("form[name=listProductForm]").submit();
	});
	
	
	$(".paging-button").on('click', function(e){
		var action = $(this).data('action-page');
		searchFunction();
		changePage(action);		
	});
	
	
	
});