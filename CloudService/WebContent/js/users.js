var currentUser;

$(document).ready(function(e){

	// Checks if there is a user that is logged in
	// If not returns to the login page
	checkIfLogged()
		
	//Calls function for viewing profile
	$('a[href="#viewProfile"]').click(function(){
		console.log(currentUser.name)
		updateProfile(currentUser)
		console.log(currentUser.name)
		
	});
	
	// Gets map of users form server
	// Calls function for printing
	$('a[href="#viewUsers"]').click(function(){
		$.ajax({
			type : 'GET',
			url : "rest/userServ/getUsers",
			dataType : "json",
			success : function(response){
				printUsers(response);
			},
			error : function() {
				alert("Error")
			}
		});
	}); 
});

//Receives a map of users
//Prints the table of users
function printUsers(users){
	var edit = $("#fillEditForm")
	edit.empty()
	
	var header = $("#fillHeader")
	header.empty()
	
	header.append("<th>Email</th>")
	header.append("<th>Name</th>")
	header.append("<th>Surname</th>")
	
	if(currentUser.role=="SUPER_ADMIN"){
		header.append("<th>Organisation</th>")
	}
	
	var table = $("#fillTable")
	
	table.append(header)
	
	var body = $("#fillBody")
	body.empty()
	
	$.each(users, function (key, value) {
		if(value.role!="SUPER_ADMIN"){
			var row = $("<tr id=\""+key+"\" class=\"edit\"></tr>")
			
			row.append("<td id=\""+key+"\">" + value.email + "</td>");
			row.append("<td id=\""+key+"\">" + value.name + "</td>");
			row.append("<td id=\""+key+"\">" + value.surname + "</td>");
			if(currentUser.role=="SUPER_ADMIN"){
				if(value.organisation == null){
					row.append("<td id=\""+key+"\">" + "/"+ "</td>");
				}else{
					row.append("<td id=\""+key+"\">" + value.organisation + "</td>");
				}
			}
			body.append(row)
		}
	})
	
	if(currentUser.role=="SUPER_ADMIN"){
		body.append('<tr><td><input type="submit" id="dodajUsr" name="dodajUsr" value="Add new user"></td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>');
	}else{
		body.append('<tr><td><input type="submit" id="dodajUsr" name="dodajUsr" value="Add new user"></td><td>&nbsp;</td><td>&nbsp;</td></tr>');
	}
		
	

	table.append(body)
	

	$("tr.edit").click(function(e){
		e.preventDefault();
		$.each(users, function (key, value) {
			if(key == e.target.id){
				editUser(value)
			}
		})
	});
	
	
	// Gets all organisations
	// Calls function for adding new users
	$("#dodajUsr").click(function(e){
		e.preventDefault();
		$.ajax({
			type : 'GET',
			url : "rest/orgServ/listOrganisations",
			dataType : "json",
			success : function(response){
				addNewUser(response);
			},
			error : function() {
				alert("Error")
			}
		});
		
	});
}

//Receives map of organisations
//Prints manu for adding new users
function addNewUser(organisations){
	var forma = $("#fillEditForm")
	
	forma.empty();
	
	var table = $("<table class=\"dodataTabela\"></table>");
	
	var row1 = $("<tr></tr>");
	var row2 = $("<tr></tr>");
	var row3 = $("<tr></tr>");
	var row4 = $("<tr></tr>");
	var row5 = $("<tr></tr>");
	var row6 = $("<tr></tr>");
	var row7 = $("<tr></tr>");
	
	row1.append("<td>Email</td>");
	row1.append("<td class=\"wrap-input validate-input \" data-validate=\"Email is required\"><input class=\"input-data\" type=\"text\" name=\"email\" id=\"email\"></td>");

	row2.append("<td>Password</td>");
	row2.append("<td class=\"wrap-input validate-input \" data-validate=\"Password is required\" ><input class=\"input-data\" type=\"text\" name=\"password\" id=\"password\"></td>");
	
	row3.append("<td>Name</td>");
	row3.append("<td class=\"wrap-input validate-input \" data-validate=\"Name is required\" ><input class=\"input-data\" type=\"text\" name=\"name\" id=\"name\"></td>");
	
	row4.append("<td>Surname</td>");
	row4.append("<td class=\"wrap-input validate-input \" data-validate=\"Surname is required\" ><input class=\"input-data\" type=\"text\" name=\"surname\" id=\"surname\"></td>");

	row5.append("<td>Organisation</td>");
	var selectOrg = $("<select name=\"organisation\" id=\"organisation\"></select>");
	$.each(organisations, function(key, value){
		var option = $("<option></option>");
		option.append(key);
		selectOrg.append(option);
	});
	row5.append(selectOrg);
	
	row6.append("<td>Role</td>")
	var select = $("<select name=\"role\" id=\"role\"></select>")
	var option1 = $("<option value=\"User\">User</option>")
	select.append(option1)
	var option2 = $("<option value=\"Admin\">Admin</option>")
	select.append(option2)
	row6.append(select);
	
	row7.append("<td><input id=\"addUser\" type=\"button\" value=\"Add\"></td>");
	row7.append("<td><input id=\"cancel\" type=\"button\" value=\"Cancel\"></td>");
	
	table.append(row1);
	table.append(row2);
	table.append(row3);
	table.append(row4);
	if(currentUser.role=="SUPER_ADMIN")
		table.append(row5);
	table.append(row6);
	table.append(row7);
	
	forma.append(table);
	
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
	
	// Tries to add a new user
	// If successful gets map of users and calls function for printing
	// if failed makes alert
	$("#addUser").click(function(e){
		e.preventDefault()
		var email = $("#email").val()
		var password = $("#password").val()
		var name = $("#name").val()
		var surname = $("#surname").val()
		var organisation = $("#organisation").val()
		var role = $("#role").val()

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
		
		if (email == "" || password == "" || name == "" || surname == "" ) {
			return;
		}
		
		
		if(currentUser.role=="ADMIN"){
			organisation = currentUser.organisation
		}
		
		$.ajax({
			type : "POST",
			url : "rest/userServ/addNewUser",
			dataType : "json",
			data : {
				"email" : email,
	        	"password" : password,
	        	"name" : name,
	        	"surname" : surname,
	        	"organisation" : organisation,
	        	"role" : role
			},
			success : function(response){
				if (response == undefined) {
					alert("User with given email already exists!");	
				}else {
					printUsers(response);
				}
			},
			error : function(data) {
				alert(data.responseText)
			}
		});
	});
}
//Receives user to edit
//Opens menu for editing
function editUser(user){
	var forma = $("#fillEditForm")
	forma.empty();
	
	var table = $("<table id=\editUserT\" class=\"editUserT\"></table>")
	
	var row1 = $("<tr></tr>")
	row1.append("<td>Email</td>")
	row1.append("<td colspan=\"2\" ><input name=\"email\" id=\"email\" type=\"text\" value=\""+user.email + "\" readonly></td>")
	
	var row2 = $("<tr></tr>")
	row2.append("<td>Password</td>")
	row2.append("<td colspan=\"2\" class=\"wrap-input validate-input \" data-validate=\"Password is required\"><input class=\"input-data\" name=\"password\" id=\"password\" type=\"text\" value=\""+user.password + "\"> </td>")
	
	var row3 = $("<tr></tr>")
	row3.append("<td>Name</td>")
	row3.append("<td colspan=\"2\" class=\"wrap-input validate-input \" data-validate=\"Name is required\"><input class=\"input-data\" name=\"name\" id=\"name\" type=\"text\" value=\""+user.name + "\"> </td>")
	
	var row4 = $("<tr></tr>")
	row4.append("<td>Surname</td>")
	row4.append("<td colspan=\"2\" class=\"wrap-input validate-input \" data-validate=\"Surname is required\"><input class=\"input-data\" name=\"surname\" id=\"surname\" type=\"text\" value=\""+user.surname + "\"> </td>")
	
	var row5 = $("<tr></tr>")
	row5.append("<td>Organisation</td>")
	row5.append("<td colspan=\"2\" ><input name=\"organisation\" id=\"organisation\" type=\"text\" value=\""+user.organisation + "\" readonly></td>")
	
	var row6 = $("<tr></tr>")
	row6.append("<td>Role</td>")
	var select = $("<select colspan=\"2\"  name=\"role\" id=\"role\"></select>")
	var option1 = $("<option value=\"User\">User</option>")
	select.append(option1)
	var option2 = $("<option value=\"Admin\">Admin</option>")
	select.append(option2)
	row6.append(select);
	
	var row7 = $("<tr></tr>")
	row7.append("<td><input id =\"editUser\" type=\"button\" value=\"Save Changes\"></td>");
	row7.append("<td><input id =\"discardUser\" type=\"button\" value=\"Discard Changes\"></td>");
	row7.append("<td><input id =\"deleteUser\" type=\"button\" value=\"Delete user\"></td>");
	
	table.append(row1)
	table.append(row2)
	table.append(row3)
	table.append(row4)
	table.append(row5)
	if(currentUser.role!="ADMIN")
		table.append(row6)
	table.append(row7)
	
	forma.append(table)
	
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
	
	// Deletes selected user
	// Gets map of users
	// Calls function for printing map of users
	$("#deleteUser").click(function(e){
		e.preventDefault();
		var email = $("#email").val()
		var organisation = $("#organisation").val()
		if(currentUser.email == user.email){
			alert("ERROR 400 : You can not delete yourself !!!")
			return
		}
			
		$.ajax({
			type : "POST",
			url : "rest/userServ/deleteUser",
			dataType : "json",
			data : {
				"email" : email,
				"organisation" : organisation
			},
			success : function(response){
				$("#edit").empty();
				printUsers(response);
			}
		});
	});
	
	// Discards entered changes
	// Gets map of users
	// Calls function for printing map of users
	$("#discardUser").click(function(e){
		e.preventDefault();
		$.ajax({
			type : "GET",
			url : "rest/userServ/getUsers",
			dataType : "json",
			success : function(response){
				$("#edit").empty();
				printUsers(response);
			}
		});
	});
	
	
	// Edits data for selected user
	// Gets map of users
	// Calls function for printing map of users
	$("#editUser").click(function(e){
		e.preventDefault();
		var email = $("#email").val()
		var password = $("#password").val()
		var name = $("#name").val()
		var surname = $("#surname").val()
		var role = $("#role").val()
		if(currentUser.role == "ADMIN")
			role = "ADMIN"
		if(password == ''){
         showValidate($("#password"));
     }else{
     	hideValidate($("#password"));
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
		
		if (password == "" || name == "" || surname == "" ) {
			return;
		}
		
		$.ajax({
			type : "POST",
			url : "rest/userServ/editUser",
			dataType : "json",
			data : {
				"email" : email,
	        	"password" : password,
	        	"name" : name,
	        	"surname" : surname,
	        	"role" : role
			},
			success : function(response){
				if (response != undefined) {
					$("#edit").empty();
					printUsers(response);
				}
			}
		});
		
	});
}

//Gets map of users and returns it
function getAllUsers(){
	$.ajax({
		type : 'GET',
		url : "rest/userServ/getUsers",
		dataType : "json",
		success : function(response){
			return response;
		},
		error : function() {
			alert("Error")
		}
	});
}

//Checks if there is a logged in user
//If no alerts and returns to login page
//If yes gets current user and calls function for editing profile 
function checkIfLogged(){
	$.ajax({
		type: 'GET',
		url: "rest/userServ/getCurrentUser",
		success : function(response){
			if(response == undefined){
				alert("You have to log in");
				$(location).attr('href', "login.html");
			}else{
				currentUser=response
			}
			
		},
		error:function(){
			alert("Error")
		}
	});
}