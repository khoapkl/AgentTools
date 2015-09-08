<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic" %>

<logic:present name="breadcrumbStatus">

	<a class="breadcrumb-item" href="#">Import Files</a>
	<a class="breadcrumb-item" href="#">&gt;</a>
	
	<logic:equal name="breadcrumbStatus" value="UploadExcelInventory">
		<a class="breadcrumb-item" href="import.do?method=importExcelPage">Product Excel Templates</a>
	</logic:equal>
	
	<logic:equal name="breadcrumbStatus" value="UploadTxtReceiving">
		<a class="breadcrumb-item" href="import.do?method=importReceivingPage">Receiving Files</a>
	</logic:equal>
	
</logic:present>
