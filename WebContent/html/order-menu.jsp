<%@page import="com.dell.enterprise.agenttool.util.Constants"%>
<%@page import="com.dell.enterprise.agenttool.model.Agent"%>
<%
Agent agent =(Agent)session.getAttribute(Constants.AGENT_INFO);
Object isCustomer = session.getAttribute(Constants.IS_CUSTOMER);
%>
<table id="MenuTable" width="100%" cellspacing="0" cellpadding="0" border="0">
	<tbody>
		<tr>
			<td width="580" valign="middle" align="right"><a
				class="nounderline"
				href="doStartPage.do"
				target="_parent"><b>Shop as : <%=agent.getUserName() %></b></a>
				<a
				class="nounderline"
				href="order_db.do"
				target="_parent"><b>Orders </b></a>
				
			<%			
			if(isCustomer==null || !((Boolean)isCustomer).booleanValue()){
			%>
				<a
				class="nounderline"
				href="shopper_manager.do"
				target="_parent"><b>Shoppers </b></a>			
			
			<a class="nounderline" href="authenticate.do?method=logout"
				target="_parent" name="Toolbar"><b><span id="Toolbar3exit.asp">Exit (<%=agent.getUserName() %>)</span></b></a>&nbsp;
			<%
			}else{
			%>
			
			<a id="ExitLink" class="nounderline" href="login.do?method=logout"
				target="_parent" name="Toolbar"><b><span id="Toolbar3exit.asp">Exit (<%=agent.getUserName() %>)</span></b></a>&nbsp;	
			<%
			}
			%>
			</td>
			<td valign="MIDDLE" align="RIGHT" nowrap="nowrap"
				width="321" valign="top" height="62"><b><span
				id="topTitle"></span></b></td>
		</tr>
	</tbody>
</table>