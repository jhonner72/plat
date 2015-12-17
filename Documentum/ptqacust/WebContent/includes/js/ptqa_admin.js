/**
 * Created by Yogesh JANKIN on 21/07/2015.
 */
$(document).on("submit", "#getvou_amt_id", function(e) {
//	alert("Submit button clicked");
	  if(isDate($("#proc_date_amt").val())){
		  alert("Valid date");
		  var $form = $(this);
//		  console.log("Calling Ajax");
		  	$.post($form.attr("action"), $form.serialize(), function(responseText) {
		  	  alert("response received");
			  	if(responseText === "-1"){
			  		 alert("The query returned no results. Run query with different processing date. Check the log file for more info.");
			  	}else if(responseText === "-2"){
			  		 alert("Error while creating result XML file. Check the log file for more info.");
			  	}else{
			  		 alert(responseText + " vouchers created by the query. Ask operators to process PTQA");
			  	}
		  	});
	   }else{
		  alert("Invalid Date");
	  }
	  return false;
});

$(document).on("submit", "#getvou_cdc_id", function(e) {
//	alert("Submit button clicked");
	  if(isDate($("#proc_date_cdc").val())){
		  alert("Valid date");
		  var $form = $(this);
//		  console.log("Calling Ajax");
		  	$.post($form.attr("action"), $form.serialize(), function(responseText) {
		  	  alert("response received");
			  	if(responseText === "-1"){
			  		 alert("The query returned no results. Run query with different processing date. Check the log file for more info.");
			  	}else if(responseText === "-2"){
			  		 alert("Error while creating result XML file. Check the log file for more info.");
			  	}else{
			  		 alert(responseText + " vouchers created by the query. Ask operators to process PTQA");
			  	}
		  	});
	   }else{
		  alert("Invalid Date");
	  }
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