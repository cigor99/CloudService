$.ajax({
		type: 'GET',
		url: "rest/userServ/redirect",
		success : function(response){
			if(response == undefined){
				alert("You have to log in");
				$(location).attr('href', "login.html");
			}
			
		},
		error:function(){
			alert("error")
		}
	});
