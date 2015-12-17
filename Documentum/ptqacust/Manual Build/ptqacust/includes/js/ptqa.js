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
$(document).on("submit", "#getnextvoucher", function(e) {

//	alert("Submit button clicked");
	
		  var $form = $(this);
		  console.log("Calling Ajax");
		  
		  if($("#codelineResult").val() !== "-1"){
			  if (!$("input[name='cdl_res_radio']").is(':checked')) {
				   alert('Please validate Code line for this voucher.');
				   return false;
				}
				else {
				   alert('One of the radio buttons is checked!');
				}				  
		  }
		  
		  if($("#amountResult").val() !== "-1"){
			  if (!$("input[name='amt_res_radio']").is(':checked')) {
				   alert('Please validate Amount for this voucher.');
				   return false;
				}
				else {
				   alert('One of the radio buttons is checked!');
				}			  
		  }		  
		  
		  alert("chronicleId is : "+$("#chronicleId").val()+";  AmountResult:"+$("#amountResult").val()+";  CodelineResult:"+$("#codelineResult").val());
		  
		  
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
		        	}else{
		        		$("#chronicleId").val(chronicleId);
		        		alert("chronicleId is : "+$("#chronicleId").val());
		        	}
		        	
		        	if(voucher.fxa_ptqa_code_line_flag){
		        		$("#codeline").show();
		        		$("#codelineResult").val("-2");
		        		
		        		$("#cdc_op_id").text(voucher.cdc_operator_name);
		        		$("#cdc_ead").text(voucher.cdc_ead);
		        		$("#cdc_ad").text(voucher.cdc_ad);
		        		$("#cdc_bsb").text(voucher.cdc_bsb);
		        		$("#cdc_acc").text(voucher.cdc_account);
		        		$("#cdc_tc").text(voucher.cdc_tc);		        		
		        	}else{
		        		$("#codeline").hide();
		        		$("#codelineResult").val("-1");
		        		console.log("Codeline is hidden and value of codelineResult set to : "+$("#codelineResult").val());
		        	}
		        	
		        	if(voucher.fxa_ptqa_amt_flag){
		        		$("#amount").show();
		        		$("#amountResult").val("-2");
		        		$("#dips_op_id").text(voucher.dips_operator_name); 
		        		$("#dips_amt_id").text(voucher.dips_amt);
		        		$("#adj_flag").text(voucher.fxa_adjustment_flag);
		        	}else{
		        		$("#amount").hide();
		        		$("#amountResult").val("-1");
		        		console.log("Amount is hidden and value of amountResult is set to : "+$("#codelineResult").val());
		        	};		        	
		              
		        	
		        	$("input[name='cdl_res_radio']").attr('checked',false);
		        	$("input[name='amt_res_radio']").attr('checked',false);	
		        	$("#cdl_res_header").css("background-color","#F47A23");
		        	$("#amt_res_header").css("background-color","#F47A23");
		        	//$("#dips_op_id").text(voucher.fxa_ptqa_amt_flag);  
  
		        });
		  	});

	  return false;
});

function isDate(txtDate)
{
  var currVal = txtDate;
  
  if(currVal == '')
    return false;   

  //Declare Regex 
  var rxDatePattern = /^(\d{1,2})(\/|-)(\d{1,2})(\/|-)(\d{4})$/;

  var dtArray = currVal.match(rxDatePattern); // is format OK? 

  if (dtArray == null)
     return false;  

  //Checks for mm/dd/yyyy format.
  dtDay = dtArray[1];
  dtMonth= dtArray[3];
  dtYear = dtArray[5]; 

  if (dtMonth < 1 || dtMonth > 12)
      return false;
  else if (dtDay < 1 || dtDay> 31)
      return false;
  else if ((dtMonth==4 || dtMonth==6 || dtMonth==9 || dtMonth==11) && dtDay ==31)
      return false;
  else if (dtMonth == 2)
  {
     var isleap = (dtYear % 4 == 0 && (dtYear % 100 != 0 || dtYear % 400 == 0));
     if (dtDay> 29 || (dtDay ==29 && !isleap))
          return false;
  }

  return true;
}


var jsEnabled=$(navigator).javaEnabled();
if(!jsEnabled){
    alert("please enable javascript in your browser.");
}