var currentUser;

// Checks if there is a user that is logged in
// If not returns to the login page
$.ajax({
		type: 'GET',
		url: "rest/userServ/getCurrentUser",
		success : function(response){
			if(response == undefined){
				alert("You have to log in");
				$(location).attr('href', "login.html");
			}else{
				currentUser = response
			}
			
		},
		error:function(){
			alert("error")
		}
	});
$(document).ready(function(e){
	
	// Calls function for 
	$("#updateProfile").click(function(e){
		call();
	});
	
	

});



//Checks if there is a logged in user
//If no alerts and returns to login page
//If yes gets current user and calls function for editing profile 
function call(){
	$.ajax({
		type: 'GET',
		url: "rest/userServ/getCurrentUser",
		success : function(response){
			if(response == undefined){
				alert("You have to log in");
				$(location).attr('href', "login.html");
			}else{
				updateProfile(response)
			}
			
		},
		error:function(){
			alert("error")
		}
	});
}


//Receives where to add class alert-validate
function showValidate(input) {
    var thisAlert = $(input).parent();

    $(thisAlert).addClass('alert-validate');
}

// Receives where to remove class alert validate
function hideValidate(input) {
    var thisAlert = $(input).parent();

    $(thisAlert).removeClass('alert-validate');
}