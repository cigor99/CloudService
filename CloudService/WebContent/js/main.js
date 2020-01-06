$(document).on('submit','#login-form',function(e){
	e.preventDefault();
	console.log("try login");
	
	var username = $("#username").val();
	var password = $("#password").val();
	
	$.ajax({
		type:'POST',
		url: "rest/userServ/validate",
		dataType: "json",
		data: {"username" : username, "password" : password},
		success : function(response){
			if(response == undefined){
				alert("Wrong username or password!")
			}else{
				alert("Login successful !")
			}
		},
		error : function(){
			alert("No success :(")
		}
	})
})
