  	<script> 	
		   	$(".input_bt").live({
	       		mouseenter:
		           function()
		           {
		          	  $(this).css('background','#0264C6');
		           },
		        mouseleave:
		           function()
		           {
		           		$(this).css('background','gray');
		           }
		       });
		       	$("ul#menu li ul.sub-menu li a.tooltip-change").live({
	       		mouseenter:
		           function()
		           {
		           		console.log("mouseenter");
		          	  $("#tooltip-changepwd").css('display','block').show();
		           },
		        mouseleave:
		           function()
		           {
		           		console.log("mouseleave");
		           		$("#tooltip-changepwd").css('display','none').hide();
		           }
		       });

		       $('.input_text').live("focus", function(){
		       		
					$(this).css('background','#f3f3f3');
		       });
		       $('.input_text').live("blur", function(){
		       		
					$(this).css('background','none');
		       });
		</script>