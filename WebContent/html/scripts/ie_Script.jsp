<script>
	  if ($.browser.msie){
	
	   	$("ul#menu.mass-menu li").live({
	      		mouseenter:
	           function()
	           {
	           	console.log("sub-menu mouseenter");

	            $(this).find("ul.sub-menu").css({visibility: "visible",display: "block"}).show();
	           },
	        mouseleave:
	           function()
	           {
	           	console.log("sub-menu mouseleave");

	           	$(this).find("ul.sub-menu").hide();
	           }
	       });
	  	}
</script>