$(document).ready(function(e) {
	$.ajax({
		type : 'GET',
		url : "rest/userServ/loadAll",
		error : function() {
			alert("Error in loading")
		}
	});
	
	$("#logOut").click(function(e){
		e.preventDefault();
		
		$.ajax({
			type : 'GET',
			url : "rest/userServ/logOut",
			success : function(){
				$(location).attr('href', "login.html")
			},
			error : function(data) {
				alert(data.responseText)
			}
		});
	});
	
	$('.login-form .input-uspas').each(function(){
        $(this).focus(function(){
           hideValidate(this);
        });
    });
	
	$('.login-form .input-uspas').each(function(){
        $(this).focusout(function(){
           if(($("#"+this.id + "")).val()=="")
        	   showValidate(this)
        });
    });
	
	$(document).on('submit', '#login-form', function(e) {
		e.preventDefault();
		
		var email = $("#email").val();
		var password = $("#password").val();
		
		if(email == ''){
            showValidate($("#email"));
        }else{
        	hideValidate($("#email"));
        }
		
		if(password == ''){
            showValidate($("#password"));
        }else{
        	hideValidate($("#password"));
        }
        	
		if(!email || !password)
			return;
		
		$.ajax({
			type : 'POST',
			url : "rest/userServ/validate",
			dataType : "json",
			data : {
				"email" : email,
				"password" : password
			},
			success : function(response){
				whereToGo(response)
			},
			error : function(data) {
				alert(data.responseText)
			}
		});
	});
	
});

function whereToGo(user){
	if(user.role=="SUPER_ADMIN"){
		$(location).attr('href', 'super_admin.html');
	}else if(user.role=="ADMIN"){
		$(location).attr('href', 'admin.html');
	}else if(user.role=="USER"){
		$(location).attr('href', 'user.html');
	}else{
		alert("Error in redirecting")
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




