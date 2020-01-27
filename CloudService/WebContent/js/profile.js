function updateProfile(user){
	let div = $("#changeable");
	div.empty();
	console.log(user.email)
	
	let forma2 = $("<form id=\"forma2\"></form>");
	
	let table = $("<table></table>");
	
	let row1 = $("<tr></tr>");
	let row2 = $("<tr></tr>");
	let row3 = $("<tr></tr>");
	let row4 = $("<tr></tr>");
	let row5 = $("<tr></tr>");
	let row6 = $("<tr></tr>");
	let row7 = $("<tr></tr>");
	
	row1.append("<td>Email</td>");
	row1.append("<td class=\"wrap-input validate-input \" data-validate=\"Email is required\"><input type=\"text\" name=\"email\" id=\"email\"></td>")
	
	row2.append("<td>New Password</td>");
	row2.append("<td class=\"wrap-input validate-input \" data-validate=\"Password is required\"><input type=\"text\" name=\"password\" id=\"password\"></td>");
	
	row3.append("<td>Repeat Password</td>");
	row3.append("<td class=\"wrap-input validate-input \" data-validate=\"Retype password\" ><input type=\"text\" name=\"rep-password\" id=\"rep-password\"></td>");
	
	row4.append("<td>Name</td>");
	row4.append("<td class=\"wrap-input validate-input \" data-validate=\"Name is required\" ><input type=\"text\" name=\"name\" id=\"name\"></td>");
	
	row5.append("<td>Surname</td>");
	row5.append("<td class=\"wrap-input validate-input \" data-validate=\"Surname is required\" ><input type=\"text\" name=\"surname\" id=\"surname\"></td>");
	
	row6.append("<td>Organisation</td>");
	row6.append("<td><input type=\"text\" name=\"organisation\" id=\"organisation\"></td>");
	
	row7.append("<td><input type=\"submit\" value=\"Update\"></td>");
	
	table.append(row1);
	table.append(row2);
	table.append(row3);
	table.append(row4);
	table.append(row5);
	if(user.role!="SUPER_ADMIN")
		table.append(row6);
	table.append(row7);
	
	forma2.append(table);
	div.append(forma2);
	
	console.log("trazi da popuni")
	$("#email").val(user.email);
	var oldEmail = $("#email").val();
	$("#password").val(user.password);
	$("#name").val(user.name);
	$("#surname").val(user.surname);
	if(user.role != "SUPER_ADMIN")
		$("#organisation").val(user.organisation.name);
	
	
	$("#forma2").submit(function(e){
		e.preventDefault();
		let email = $("#email").val();
		let password = $("#password").val();
		let repPass = $("#rep-password").val();
		let name = $("#name").val();
		let surname = $("#surname").val();
		
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
			showValidate($("#rep-password"))
			showValidate($("#password"))
		}
		
		console.log("pred slanje")
		console.log(oldEmail)
		console.log(email)
		console.log(password)
		console.log(name)
		console.log(surname)
		
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
					console.log("uspeo")
					$("#changeable").empty();
					alert("Profile updated")
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
