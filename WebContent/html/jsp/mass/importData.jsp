<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic" %>
 
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/styles/mass/import_data.css"/>
		<script src="<%=request.getContextPath() %>/jscripts/mass/import_data.js" language="javascript"></script>
	</head>
	
	<body>
		
		<logic:messagesPresent message="false">
			<div class="wrap-system-errors">
				<div class="system-errors">
					<html:errors />
				</div>
			</div>
		</logic:messagesPresent>
		
		<input id="method" type="hidden" value="<%= request.getParameter("method") %>"/>
		
		<logic:present name="totalData">
			<div id="wrap-all-error" class="wrap-error-status">
			
				<label class="item-error-status title-status">Import successful!</label>
				
				<logic:present name="totalData">
					<label class="item-error-status">
						Imported Records: 
						<span class="total-data-item"><bean:write name="totalData"/></span>
					</label>
				</logic:present>
				
				<logic:present name="totalDataInsertSuccess">
					<label class="item-error-status">
						Successful Records: 
						<span class="total-successful-item"><bean:write name="totalDataInsertSuccess"/></span>
					</label>
				</logic:present>
				
				<logic:present name="totalDataInsertError">
					<label class="item-error-status">
						Number of Errors:
						<span class="total-error-item"><bean:write name="totalDataInsertError"/></span>
					</label>
				</logic:present>
				
				
				<logic:present name="excelInServer">
					<a id="downloadError" href="upload.do?method=downloadExcelFileWithPath&path=<bean:write name="excelInServer"/>">Download</a>
				</logic:present>
			</div>
		</logic:present>
		
		
		<logic:present name="IMPORT_PAGE_TYPE_KEY">
			<logic:equal name="IMPORT_PAGE_TYPE_KEY" value="EXCEL_TEMPLATE">
				<html:form styleClass="form-upload-template" action="/upload.do?method=uploadExcelFile" method="post" enctype="multipart/form-data">
					<label class="label-upload">Excel Template: </label>
					<input type="file" class="file-upload" accept=".xls,.xlsx" name="file" />
				 	<input class="button-upload" type="submit" name="method" value="Import" />
				</html:form>
			</logic:equal>
			
			<logic:equal name="IMPORT_PAGE_TYPE_KEY" value="RECEIVING_TEMPLATE">
				<html:form styleClass="form-upload-template" action="/upload.do?method=uploadTextFile" method="post" enctype="multipart/form-data">
					<label class="label-upload">Receiving Template: </label>
					<input type="file" class="file-upload" accept="text/plain" name="file" />
					<input class="button-upload" type="submit" name="method" value="Import" />
				</html:form>
			</logic:equal>
			
			<logic:equal name="IMPORT_PAGE_TYPE_KEY" value="PRICE_TEMPLATE">
				<html:form styleClass="form-upload-template" action="/upload.do?method=updatePrices" method="post" enctype="multipart/form-data">
					<label class="label-upload">Prices Template: </label>
					<input type="file" class="file-upload" accept=".xls,.xlsx" name="file" />
					<input class="button-upload" type="submit" name="method" value="Import" />
				</html:form>
			</logic:equal>
		</logic:present>
		
		
		
		
	 
	 
		
		
		
	 
	</body>
</html>