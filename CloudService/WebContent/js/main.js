$(document).ready(function(e) {
	$(document).on('submit', '#login-form', function(e) {
		e.preventDefault();
		console.log("try login");

		var username = $("#username").val();
		var password = $("#password").val();

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
				alert("")
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




