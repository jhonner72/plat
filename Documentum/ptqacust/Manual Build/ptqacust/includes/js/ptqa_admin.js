/**
 * Created by Yogesh JANKIN on 21/07/2015.
 */
$(document).on("submit", "#getvou_id", function(e) {
//	alert("Submit button clicked");
	  if(isDate($("#proc_date").val())){
		  alert("Valid date");
		  var $form = $(this);
//		  console.log("Calling Ajax");
		  	$.post($form.attr("action"), $form.serialize(), function(responseJson) {
		  	  alert("response received");
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