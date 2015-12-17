/**
 * Created by Yogesh JANKIN on 21/07/2015.
 */

/*
$("#codeline_r").click(function(e){
    //alert("clicked codeline");
	if(document.getElementById('codeline_r').checked){
		$("#codeline").show();
		$("#amount").hide();
	}
});
$("#amount_r").click(function(e){
    //alert("clicked amount");
	if(document.getElementById('amount_r').checked){
		$("#codeline").hide();
		$("#amount").show();
	}
});
$("#both_r").click(function(e){
   // alert("clicked both");
	if(document.getElementById('both_r').checked){
		$("#codeline").show();
		$("#amount").show();
	}
});*/

$("#cdl_pass").click(function(e){
	   // alert("clicked both");
		if(document.getElementById('cdl_pass').checked){
			$("#cdl_res_header").css("background-color","#449d44");
			$("#codelineResult").val("1");
		}
	});


$("#cdl_fail").click(function(e){
	   // alert("clicked both");
		if(document.getElementById('cdl_fail').checked){
			$("#cdl_res_header").css("background-color","#E42C31");
			$("#codelineResult").val("0");
		}
	});


$("#amt_pass").click(function(e){
	   // alert("clicked both");
		if(document.getElementById('amt_pass').checked){
			$("#amt_res_header").css("background-color","#449d44");
			$("#amountResult").val("1");
		}
	});


$("#amt_fail").click(function(e){
	   // alert("clicked both");
		if(document.getElementById('amt_fail').checked){
			$("#amt_res_header").css("background-color","#E42C31");
			$("#amountResult").val("0");
		}
	});
/*
$("#submit_query").click(function(e){
	//alert("Submit button clicked");
  if(isDate($("#proc_date").val())){
	  alert("Valid date");
	  $.get("getvouchers",{procdate : }, function(responseText) {   // Execute Ajax GET request on URL of "someservlet" and execute the following function with Ajax response text...
          $("#somediv").text(responseText);           // Locate HTML DOM element with ID "somediv" and set its text content with the response text.
      });
  }else{
	  alert("Invalid date format");
  }
});
*/
$(document).on("submit", "#getnextptqavoucherforamt", function(e) {
 
//	alert("Submit button clicked");
	
		  var $form = $(this);
//		  console.log("Calling Ajax");
		  
		  if($("#amountResult").val() !== "-1"){
			  if (!$("input[name='amt_res_radio']").is(':checked')) {
				   alert('Please validate Amount for this voucher.');
				   return false;
				}
		  
		  }	
		  
//		  //DONT CHANGE THE ORDER
		  if(true){
		  		$("#dips_op_id").text(""); 
		  		$("#dips_amt_id").text("");
		  		$("#adj_flag").text("");
		  }

		  
		  //alert("chronicleId is : "+$("#chronicleId").val()+";  AmountResult:"+$("#amountResult").val()+";  CodelineResult:"+$("#codelineResult").val());
		  
		  
		  	$.post($form.attr("action"), $form.serialize(), function(responseJson) {
//		  	  alert("response received");
		        $.each(responseJson, function(index, voucher) {    // Iterate over the JSON array.
//		        	var code_line_flag=voucher.fxa_ptqa_code_line_flag;
//		        	alert(typeof code_line_flag);
		        	var chronicleId=voucher.i_chronicle_id;
//		        	alert(chronicleId);
		        	if(chronicleId === "error"){
		        		alert("Admin has not run the query to fetch PTQA Vouchers. Please contact Admin.");
		        		return;
		        	}else if(chronicleId === "zero"){
		        		alert("No results returned from previous query. Please contact Admin to run a new query. ");
//				      	$("input[name='cdl_res_radio']").attr('checked',false);
				    	$("input[name='amt_res_radio']").attr('checked',false);	
//				    	$("#cdl_res_header").css("background-color","#F47A23");
				    	$("#amt_res_header").css("background-color","#F47A23");	
//				    	$("#codelineResult").val("-1");
				    	$("#amountResult").val("-1");				    	
		        		return;
		        	}else if(chronicleId === "reset"){
		        		alert("All documents from previous results processed. Please contact Admin to run a new query. ");
//				      	$("input[name='cdl_res_radio']").attr('checked',false);
				    	$("input[name='amt_res_radio']").attr('checked',false);	
//				    	$("#cdl_res_header").css("background-color","#F47A23");
				    	$("#amt_res_header").css("background-color","#F47A23");	
//				    	$("#codelineResult").val("-1");
				    	$("#amountResult").val("-1");
		        		return;
		        	}else{
		        		$("#chronicleId").val(chronicleId);
		        		// FOR MY VM var link="http://dctmmelb:8080/D2/?docbase=NAB&login=dmadmin&password=dm@dmin01&locateId="+voucher.r_object_id+"&commandEvent=D2_ACTION_CONTENT_VIEW";
//		        		var link="http://localhost:8080/D2/servlet/Download?auth=basic&event_name=d2_view&_docbase=NAB&id="+voucher.r_object_id;
//		        		$("#d2_drl_link").attr("href",link);
		        		
		        		var iframe_link="http://localhost:8080/VoucherViewer/view.html?debug=false&format=c2pdf&docbase=NAB&locale=en&username=ptqa&password=ptqa123&contentType=tiff&r_object_id="+voucher.r_object_id;
		        		$("#amtvouhcerview_iframe").attr("src",iframe_link);		        		
		        		//alert("chronicleId is : "+$("#chronicleId").val());
		        	}
//		        	
//		        	if(voucher.fxa_ptqa_code_line_flag){
//		        		//alert("fxa_ptqa_code_line_flag is true");
//		        		$("#codeline").show();
//		        		$("#codelineResult").val("-2");
//		        		
//		        		$("#cdc_op_id").text(voucher.cdc_operator_name);
//		        		$("#cdc_ead").text(voucher.cdc_ead);
//		        		$("#cdc_ad").text(voucher.cdc_ad);
//		        		$("#cdc_bsb").text(voucher.cdc_bsb);
//		        		$("#cdc_acc").text(voucher.cdc_account);
//		        		$("#cdc_tc").text(voucher.cdc_tc);		        		
//		        	}else{
//		        		//alert("fxa_ptqa_code_line_flag is false");
//		        		$("#codeline").hide();
//		        		$("#codelineResult").val("-1");
//		        		console.log("Codeline is hidden and value of codelineResult set to : "+$("#codelineResult").val());
//		        	}
		        	
		        	if(voucher.fxa_ptqa_amt_flag){
		        		//alert("fxa_ptqa_amt_flag is false");
//		        		$("#amount").show();
		        		$("#amountResult").val("-2");
		        		$("#dips_op_id").text(voucher.dips_operator_name); 
		        		$("#dips_amt_id").text(voucher.dips_amt);
		        		$("#adj_flag").text(voucher.fxa_adjustment_flag);
		        	}else{
		        		//alert("fxa_ptqa_amt_flag is false");
//		        		$("#amount").hide();
		        		$("#amountResult").val("-1");
		        		console.log("Amount is hidden and value of amountResult is set to : "+$("#codelineResult").val());
		        	};	
		        	
				  	//reset values
//			      	$("input[name='cdl_res_radio']").attr('checked',false);
			    	$("input[name='amt_res_radio']").attr('checked',false);	
//			    	$("#cdl_res_header").css("background-color","#F47A23");
			    	$("#amt_res_header").css("background-color","#F47A23");
		        });
		  	});

	  return false;
});

$(document).on("submit", "#getnextptqavoucherforcdc", function(e) {
	 
//	alert("Submit button clicked");
	
		  var $form = $(this);
//		  console.log("Calling Ajax");
		  
		  if($("#codelineResult").val() !== "-1"){
			  if (!$("input[name='cdl_res_radio']").is(':checked')) {
				   alert('Please validate Code line for this voucher.');
				   return false;
				}			  
		  }
		  
//		  //DONT CHANGE THE ORDER
		  if(true){
		  		$("#cdc_op_id").text("");
		  		$("#cdc_ead").text("");
		  		$("#cdc_ad").text("");
		  		$("#cdc_bsb").text("");
		  		$("#cdc_acc").text("");
		  		$("#cdc_tc").text("");	
		  		$("#cdc_fv").text("");
		  }

		  
		  //alert("chronicleId is : "+$("#chronicleId").val()+";  AmountResult:"+$("#amountResult").val()+";  CodelineResult:"+$("#codelineResult").val());
		  
		  
		  	$.post($form.attr("action"), $form.serialize(), function(responseJson) {
//		  	  alert("response received");
		        $.each(responseJson, function(index, voucher) {    // Iterate over the JSON array.
//		        	var code_line_flag=voucher.fxa_ptqa_code_line_flag;
//		        	alert(typeof code_line_flag);
		        	var chronicleId=voucher.i_chronicle_id;
//		        	alert(chronicleId);
		        	if(chronicleId === "error"){
		        		alert("Admin has not run the query to fetch PTQA Vouchers. Please contact Admin.");
		        		return;
		        	}else if(chronicleId === "zero"){
		        		alert("No results returned from previous query. Please contact Admin to run a new query. ");
				      	$("input[name='cdl_res_radio']").attr('checked',false);
//				    	$("input[name='amt_res_radio']").attr('checked',false);	
				    	$("#cdl_res_header").css("background-color","#F47A23");
//				    	$("#amt_res_header").css("background-color","#F47A23");	
				    	$("#codelineResult").val("-1");
//				    	$("#amountResult").val("-1");				    	
		        		return;
		        	}else if(chronicleId === "reset"){
		        		alert("All documents from previous results processed. Please contact Admin to run a new query. ");
				      	$("input[name='cdl_res_radio']").attr('checked',false);
//				    	$("input[name='amt_res_radio']").attr('checked',false);	
				    	$("#cdl_res_header").css("background-color","#F47A23");
//				    	$("#amt_res_header").css("background-color","#F47A23");	
				    	$("#codelineResult").val("-1");
//				    	$("#amountResult").val("-1");
		        		return;
		        	}else{
		        		$("#chronicleId").val(chronicleId);
		        		// FOR MY VM var link="http://dctmmelb:8080/D2/?docbase=NAB&login=dmadmin&password=dm@dmin01&locateId="+voucher.r_object_id+"&commandEvent=D2_ACTION_CONTENT_VIEW";
		        		//var link="http://localhost:8080/D2/servlet/Download?auth=basic&event_name=d2_view&_docbase=NAB&id="+voucher.r_object_id;
		        		//$("#d2_drl_link").attr("href",link);
		        		var iframe_link="http://localhost:8080/VoucherViewer/view.html?debug=false&format=c2pdf&docbase=NAB&locale=en&username=ptqa&password=ptqa123&contentType=tiff&r_object_id="+voucher.r_object_id;
		        		$("#cdcvouhcerview_iframe").attr("src",iframe_link);
		        		//alert("chronicleId is : "+$("#chronicleId").val());
		        	}
		        	
		        	if(voucher.fxa_ptqa_code_line_flag){
		        		//alert("fxa_ptqa_code_line_flag is true");
		        		$("#codeline").show();
		        		$("#codelineResult").val("-2");
		        		
		        		$("#cdc_op_id").text(voucher.cdc_operator_name);
		        		$("#cdc_ead").text(voucher.cdc_ead);
		        		$("#cdc_ad").text(voucher.cdc_ad);
		        		$("#cdc_bsb").text(voucher.cdc_bsb);
		        		$("#cdc_acc").text(voucher.cdc_account);
		        		$("#cdc_tc").text(voucher.cdc_tc);
		        		$("#cdc_fv").text(voucher.fxa_for_value_type);
		        	}else{
		        		//alert("fxa_ptqa_code_line_flag is false");
		        		$("#codeline").hide();
		        		$("#codelineResult").val("-1");
		        		console.log("Codeline is hidden and value of codelineResult set to : "+$("#codelineResult").val());
		        	}
		        	
				  	//reset values
			      	$("input[name='cdl_res_radio']").attr('checked',false);
//			    	$("input[name='amt_res_radio']").attr('checked',false);	
			    	$("#cdl_res_header").css("background-color","#F47A23");
//			    	$("#amt_res_header").css("background-color","#F47A23");
		        });
		  	});

	  return false;
});


var jsEnabled=$(navigator).javaEnabled();
if(!jsEnabled){
    alert("please enable javascript in your browser.");
}