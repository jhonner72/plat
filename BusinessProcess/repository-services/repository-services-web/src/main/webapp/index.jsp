<!doctype html public "-//w3c//dtd html 4.0 transitional//en">
<html>
<head>
	<title>Repository Service Test Portal</title>
</head>
<body>
<form action="processForm" method="POST">
    <table>
        <tr> 
        	<td>Select Service:</td>
            <td>
                <select name="serviceName">
				  <option value="save">Store Batch Voucher Request</option> 
				  <option value="query">Get Vouchers Request</option>
				  <option value="update">Update Vouchers Status Request</option>
				  <option value="repost">Repost Vouchers Request</option> 
				  <option value="saveListings">Store Listing Request</option>
				  <option value="triggerWorkflow">Trigger Workflow Request</option> 
				  <option value="saveReports">Store Batch Repository Reports Request</option>
				  <option value="queryVoucherInfo">Get Vouchers Information Request</option>
				  <option value="updateVoucherInfo">Update Vouchers Information Request</option>
				  <option value="saveAdjustmentLetter">Store Batch Adjustment Letters Request</option>
				</select>
            </td>
        </tr>          
        <tr>
            <td colspan=2>
                <textarea name="request" rows=20 cols=80><%=request.getAttribute("requestBody")%></textarea>
            </td>
        </tr>
        <tr>
            <td colspan=2>
                <input value="submit" type="submit"/>
            </td>
        </tr>
        <tr>
            <td colspan=2>
                <textarea name="response" rows=20 cols=80><%=request.getAttribute("responseBody")%></textarea>
            </td>
        </tr>
    </table>
</form>

</body>
</html>
