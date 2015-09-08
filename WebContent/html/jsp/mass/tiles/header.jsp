<%@page import="com.dell.enterprise.agenttool.util.Constants"%>
<%@page import="com.dell.enterprise.agenttool.model.Agent"%>
<%@page import="com.dell.enterprise.agenttool.model.ShopperAs"%>
<%@page import="com.dell.enterprise.agenttool.model.Customer"%>


<%
Agent agent =(Agent)session.getAttribute(Constants.AGENT_INFO);
Object isCustomer = session.getAttribute(Constants.IS_CUSTOMER);
Boolean idShopper = (Boolean)session.getAttribute(Constants.IS_SHOPPER);
long expiryDays = 0;
if(session.getAttribute("expiryDays") !=null)
	{ 
		expiryDays = (Long)session.getAttribute("expiryDays");
	}

Object isAdmin = request.getAttribute(Constants.IS_ADMIN);
%>

<table width="100%" cellspacing="0" cellpadding="0" border="0">
	<tbody>
		<tr>
			<td width="313" valign="top"><!-- blue masthead logo goes here -->
			<a
				href="authenticate.do"><img
				width="251" border="0" alt="" src="images/delllogo.PNG"></a><br>
			</td>	
		  	
		  	
		  	<td>
		  		<table id="MenuTable" width="100%" cellspacing="0" cellpadding="0" border="0">
					<tbody>
						<tr>
							<td width="100%" valign="center" align="left">
							
							<a class="nounderline" href="listProduct.do?method=inventoryList" name="Toolbar">
									<span class="menu-item menu-item-inventory" id="Toolbar3default_tools.asp">Inventory List</span>
							</a> 
							
										
							<a class="nounderline" href="listProduct.do?method=receivingList" target="_parent" name="Toolbar">
									<span class="menu-item menu-item-receiving" id="Toolbar1basket.asp">Receiving Queue</span>
							</a>
							
							
							<ul id="menu" class="mass-menu">
								
								 <li class="import-files-menu">
							    	<a class="nounderline" href="#" target="_parent" name="Toolbar"><span class="menu-item menu-item-import-files" id="Toolbar3exit.asp">Import Files</span></a>
							    	<ul class="sub-menu">
										<li><a href="import.do?method=importExcelPage">Product Excel Templates</a></li>
										<li><a href="import.do?method=importReceivingPage">Receiving File</a></li>
									</ul>
							    </li>
							
							 	 <li class="user-menu"><a id="LastLink" class="nounderline" href="#" target="_parent" name="Toolbar"><span class="menu-item" id="Toolbar3exit.asp">Logged in as: <%=(agent!=null)?agent.getUserName():""%></span></a>
							         <ul class="sub-menu">
							            <li>
											<a class="tooltip-change" href="change_password.do?method=editPassword&amp;expire=0">Change Password
											</a>	
											<span id="tooltip-changepwd">
				    							Your password will expire in 2 days
											</span>		                
							            </li>
							            <li class="display-block">
							                <a href="authenticate.do?method=logout">Log Out</a>
							            </li>
							        </ul>
							    </li>
							    
							   
							</ul>
				
							
							</td>
							<td valign="MIDDLE" align="RIGHT" nowrap="nowrap" height="62"><b><span id="topTitle"></span></b></td>
						</tr>
					</tbody>
				</table>
		  	</td>
		  	
		</tr>
	</tbody>
</table>

