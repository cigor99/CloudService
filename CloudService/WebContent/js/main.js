$(document).ready(function(e) {
	
	$("#logOut").click(function(e){
		e.preventDefault();
		console.log("log out");
		
		$.ajax({
			type : 'GET',
			url : "rest/userServ/logOut",
			success : function(){
				$(location).attr('href', "login.html")
			},
			error : function() {
				alert("Error")
			}
		});
	});
	
	$('.login-form .input-uspas').each(function(){
        $(this).focus(function(){
           hideValidate(this);
        });
    });
	
	$(document).on('submit', '#login-form', function(e) {
		e.preventDefault();
		console.log("try login");

		
		var username = $("#username").val();
		var password = $("#password").val();
		if(username == ''){
            showValidate($("#username"));
        }else{
        	hideValidate($("#username"));
        }
		
		if(password == ''){
            showValidate($("#password"));
        }else{
        	hideValidate($("#password"));
        }
        	
		if(username == '' || password == '')
			return;

		$.ajax({
			type : 'POST',
			url : "rest/userServ/validate",
			dataType : "json",
			data : {
				"username" : username,
				"password" : password
			},
			success : function(response) {
				if (response == undefined) {
					alert("Wrong username or password!")
				} else {
					whereToGo(response)
				}
			},
			error : function() {
				alert("Error")
			}
		});
	});
	
});

function whereToGo(user){
	if(user.role=="SUPER_ADMIN"){
		$(location).attr('href', 'superAdmin.html');
	}else if(user.role=="ADMIN"){
		$(location).attr('href', 'admin.html');
	}else if(user.role=="USER"){
		$(location).attr('href', 'user.html');
	}else{
		alert("error")
	}
	
}

function showValidate(input) {
    var thisAlert = $(input).parent();

    $(thisAlert).addClass('alert-validate');
}

function hideValidate(input) {
    var thisAlert = $(input).parent();

    $(thisAlert).removeClass('alert-validate');
}




