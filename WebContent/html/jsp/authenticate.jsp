<h3>LOGIN TO AGENT TOOL</h3>
<div id="loginContainer" class="loginContainer">
<div id="loginMessage"></div>
<form
	action="<%=request.getContextPath()%>/authenticate.do?method=login"
	method="POST">
<table style="border: 0; margin: 0 auto;">
	<tr>
		<td style="text-align: right;">User Name</td>
		<td><input type="text" name="userName" id="userName" /></td>
	</tr>
	<tr>
		<td style="text-align: right;">Password</td>
		<td><input type="password" name="password" /></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td><input type="submit" value="Login" /> <input type="reset"
			value="Clear" /></td>
	</tr>
</table>
</form>
</div>

<%@include file="/html/scripts/login_script.jsp"%>