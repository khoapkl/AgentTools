<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
	
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/styles/mass/list_product.css"/>
	<script src="<%=request.getContextPath() %>/jscripts/mass/list_product.js" language="javascript"></script>
</head>

<body>

	<logic:messagesPresent message="false">
		<div class="wrap-system-errors">
			<div class="system-errors">
				<html:errors />
			</div>
		</div>
	</logic:messagesPresent>
    
    <html:form action="/listProduct.do" method="GET">
    	
        <input id="method" type="hidden" value="<%= request.getParameter("method") %>" name="method" />
        <html:hidden styleId="inventoryPage" name="listProductForm" property="inventoryPage"/>
        <input id="methodBak" type="hidden" value="<%= request.getParameter("method") %>" />
        
        <span id="item-record-js" class="item-record-hidden item-record-js"><bean:write name="listProductForm" property="recordPerPage"/></span>
                
        <img class="feature-picture" alt="Feature" src="<%=request.getContextPath() %>/images/feature_search.gif">
        
        <div class="wrap-sku-item">
            <label>SKU</label>
            <html:text name="listProductForm" property="sku"/>
            
            <html:submit onclick="searchFunction()" value="Search"></html:submit>
            
            <html:submit onclick="exportToExcel()" value="Export to excel"></html:submit>
        </div>
        
         
         
        
        <div class="wrap-paging">
        	<logic:equal name="listProductForm" property="firstPage" value="true">
	        	<!-- DISABLE IN FIRST PAGE -->
	        	<input type="submit" class="paging-button first-paging-button" data-action-page="FIRST" value="&lt;&lt;" disabled="disabled" />
	        	<input type="submit" class="paging-button prev-paging-button" data-action-page="PREV" value="&lt;" disabled="disabled" />
	        </logic:equal>
	        
	        <logic:notEqual name="listProductForm" property="firstPage" value="true">
	        	<!-- ENABLE IN FIRST PAGE -->
	        	<input type="submit" class="paging-button first-paging-button" data-action-page="FIRST" value="&lt;&lt;" />
	        	<input type="submit" class="paging-button prev-paging-button" data-action-page="PREV" value="&lt;" />
	        </logic:notEqual>
	        
	        
	         <logic:equal name="listProductForm" property="lastPage" value="true">
	         	<input type="submit" class="paging-button next-paging-button" data-action-page="NEXT" value="&gt;" disabled="disabled" />
	         	<input type="submit" class="paging-button last-paging-button" data-action-page="LAST" onclick="changePage('LAST')" value="&gt;&gt;" disabled="disabled" />
	        </logic:equal>
	        
	        <logic:notEqual name="listProductForm" property="lastPage" value="true">
	        	<input type="submit" class="paging-button next-paging-button" data-action-page="NEXT" value="&gt;"  />
	         	<input type="submit" class="paging-button last-paging-button" data-action-page="LAST" value="&gt;&gt;" />
	        </logic:notEqual>
	        
	        
	        <span class="wrap-paging-text-item wrap-total-record">
	            <bean:write name="listProductForm" property="totalRecord"/>
	            <html:hidden styleId="totalRecord" name="listProductForm" property="totalRecord"/>
	            <label>Records</label>
	        </span>
	        
	        
	        <span class="wrap-paging-text-item wrap-current-page">
	            <label>Page: </label>
	            <bean:write name="listProductForm" property="currentPage"/>
	            <html:hidden styleId="currentPage" name="listProductForm" property="currentPage"/>
	        </span>
	        
	        <span class="wrap-paging-text-item wrap-total-page">
	            <label> of </label>
	            <bean:write name="listProductForm" property="totalPage"/>
	            <html:hidden styleId="totalPage" name="listProductForm" property="totalPage"/>
	        </span>
	        
	        <span class="wrap-paging-text-item wrap-record-per-page">
	            <label>Page size:</label>
	            <logic:present name="listRecordsPerPage">
	            	<select name="recordPerPage" id="recordPerPage">
		            	<logic:iterate id="recordPerPageItem" name="listRecordsPerPage">
	            		<option value="<bean:write name="recordPerPageItem"/>">
	            			<bean:write name="recordPerPageItem"/>
	            		</option>
		            	</logic:iterate>
	            	</select>	            
	            </logic:present>
	            
	        </span>
	        
	        
	        
        </div>
        
       
        <div class="wrap-line-mass-table">
	        <div class="wrap-mass-table">
		        <table class="mass-table">
		        	<thead>
			            <tr class="mass-row mass-row-header">
			              <logic:iterate id="headerItem" name="listHeaders">
			              		<th class="mass-header"><bean:write name="headerItem"/></td>
						  </logic:iterate>
			            </tr>
			            <tr>
			            	<logic:iterate id="headerItem" name="listHeaders">
			              		<th class="mass-header"><hr width="150%" size="1" noshade="TRUE" align="left"></td>
						  	</logic:iterate>
			            </tr>
		            </thead>
		            
		            <tbody>
		            	<logic:present name="listProducts">
				            <logic:iterate id="productItem" name="listProducts">
				            <tr class="mass-row">
				                <logic:iterate name="productItem" id="productObj">
				                        <td class='mass-col <bean:write name="productObj"  property="key"/>'>
				                            <bean:write name="productObj" property="value"/>
				                        </td>
				                </logic:iterate>
				            </tr>
				            </logic:iterate>
			            </logic:present>
		            </tbody>
		            
		        </table>
	        </div>
        </div>
        
    </html:form>
    
    
   
    
    

</body>
</html>