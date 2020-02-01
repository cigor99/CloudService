function updateProfile(user){
	var body = $("#fillBody");
	body.empty();
	
	var header = $("#fillHeader");
	header.empty();
	var form = $("#fillEditForm");
	form.empty();
	
	var table = $("<table></table>");
	
	var row1 = $("<tr></tr>");
	var row2 = $("<tr></tr>");
	var row3 = $("<tr></tr>");
	var row4 = $("<tr></tr>");
	var row5 = $("<tr></tr>");
	var row6 = $("<tr></tr>");
	var row7 = $("<tr></tr>");
	
	row1.append("<td>Email</td>");
	row1.append("<td class=\"wrap-input validate-input \" data-validate=\"Email is required\"><input class=\"input-data\" type=\"text\" name=\"email\" id=\"email\"></td>")
	
	row2.append("<td>New Password</td>");
	row2.append("<td class=\"wrap-input validate-input \" data-validate=\"Password is required\"><input class=\"input-data\" type=\"text\" name=\"password\" id=\"password\"></td>");
	
	row3.append("<td>Repeat Password</td>");
	row3.append("<td class=\"wrap-input validate-input \" data-validate=\"Retype password\" ><input class=\"input-data\" type=\"text\" name=\"rep-password\" id=\"rep-password\"></td>");
	
	row4.append("<td>Name</td>");
	row4.append("<td class=\"wrap-input validate-input \" data-validate=\"Name is required\" ><input class=\"input-data\" type=\"text\" name=\"name\" id=\"name\"></td>");
	
	row5.append("<td>Surname</td>");
	row5.append("<td class=\"wrap-input validate-input \" data-validate=\"Surname is required\" ><input class=\"input-data\" type=\"text\" name=\"surname\" id=\"surname\"></td>");
	
	row6.append("<td>Organisation</td>");
	row6.append("<td><input type=\"text\" name=\"organisation\" id=\"organisation\" readonly></td>");
	
	row7.append("<td><input id=\"updateProfile\" type=\"button\" value=\"Update\"></td>");
	
	table.append(row1);
	table.append(row2);
	table.append(row3);
	table.append(row4);
	table.append(row5);
	if(user.role!="SUPER_ADMIN")
		table.append(row6);
	table.append(row7);
	
	form.append(table);
	
	$("#email").val(user.email);
	var oldEmail = $("#email").val();
	$("#password").val(user.password);
	$("#name").val(user.name);
	$("#surname").val(user.surname);
	if(user.role != "SUPER_ADMIN")
		$("#organisation").val(user.organisation);
	
	// When inputs get into focus, alert disappears
	$('.input-data').each(function(){
		$(this).focus(function(){
	           hideValidate(this);
	    });
	});
	
	//// When inputs lose focus, and are empty alert appears
	$('.input-data').each(function(){
		$(this).focusout(function(){
	           if(($("#"+this.id + "")).val()=="")
	        	   showValidate(this)
	     });
    });
	
	$("#updateProfile").click(function(e){
		e.preventDefault();
		var email = $("#email").val();
		var password = $("#password").val();
		var repPass = $("#rep-password").val();
		var name = $("#name").val();
		var surname = $("#surname").val();
		
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
		
		if(repPass == ''){
            showValidate($("#rep-password"));
        }else{
        	hideValidate($("#rep-password"));
        }
		
		if(name == ''){
            showValidate($("#name"));
        }else{
        	hideValidate($("#name"));
        }
		
		if(surname == ''){
            showValidate($("#surname"));
        }else{
        	hideValidate($("#surname"));
        }
		
		
		if (password == "" || name == "" || surname == "" || email == "" || repPass == "") {
			return;
		}
		
		if(password != repPass){
			alert("Passwords don't match")
			if(password == '')
				showValidate($("#password"))
			showValidate($("#rep-password"))
			return
		}
		
		$.ajax({
			type : "POST",
			url : "rest/userServ/updateProfile",
			dataType : "json",
			data : {
				
				"oldEmail": oldEmail,
				"email" : email,
	        	"password" : password,
	        	"name" : name,
	        	"surname" : surname
			},
			success : function(response){
				if (response == undefined) {
					alert("User with given email already exists!");
				} else {
					alert("Profile updated")
					checkIfLogged();
				}
			}
		});
	});
}



function showValidate(input) {
    var thisAlert = $(input).parent();

    $(thisAlert).addClass('alert-validate');
}

function hideValidate(input) {
    var thisAlert = $(input).parent();

    $(thisAlert).removeClass('alert-validate');
}
