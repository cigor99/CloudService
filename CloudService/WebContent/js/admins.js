var currentUser;

$(document).ready(function(e){

	// Checks if there is a user that is logged in
	// If not returns to the login page
	checkIfLogged()
	
	//Calls function for viewing profile
	$('a[href="#viewProfile"]').click(function(){
		alert("View profile")
	});
	
	//Calls function for viewing VMs
	$('a[href="#viewVMs"]').click(function(){
		alert("View VMS")
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
	

	// Gets map of organisations from server
	// Calls function for pritning those organisations
	$('a[href="#viewOrganisations"]').click(function(){
		if(currentUser.role=="ADMIN")
			getMyOrganisation()
		else{
			$.ajax({
				type : 'GET',
				url : "rest/orgServ/listOrganisations",
				dataType : "json",
				success : function(response){
					printOrganisations(response);
				},
				error : function() {
					alert("Error")
				}
			});
		}
	}); 
	
	//Calls function for viewing VMs
	$('a[href="#viewDiscs"]').click(function(){
		alert("View Discs")
	});
	
	// Gets current admins organisation
	// Calls edit menu for that organisation
	$('a[href="#viewMyOrganisation"]').click(function(e){
		$.ajax({
			type : 'GET',
			url : "rest/orgServ/getMyOrg",
			dataType : "json",
			success : function(response){
				if(response != undefined){
					editOrganisation(response);
				}
			},
			error : function() {
				alert("Error")
			}
		});
	})
	
});

// Receives a map of users
// Prints the table of users
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
					row.append("<td id=\""+key+"\">" + value.organisation.name+ "</td>");
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

// Receives user to edit
// Opens menu for editing
function editUser(user){
	var forma = $("#fillEditForm")
	forma.empty();
	
	var table = $("<table id=\editUserT\" class=\"editUserT\"></table>")
	
	var row1 = $("<tr></tr>")
	row1.append("<td>Email</td>")
	row1.append("<td><input name=\"email\" id=\"email\" type=\"text\" value=\""+user.email + "\" readonly></td>")
	
	var row2 = $("<tr></tr>")
	row2.append("<td>Password</td>")
	row2.append("<td class=\"wrap-input validate-input \" data-validate=\"Password is required\"><input class=\"input-data\" name=\"password\" id=\"password\" type=\"text\" value=\""+user.password + "\"> </td>")
	
	var row3 = $("<tr></tr>")
	row3.append("<td>Name</td>")
	row3.append("<td class=\"wrap-input validate-input \" data-validate=\"Name is required\"><input class=\"input-data\" name=\"name\" id=\"name\" type=\"text\" value=\""+user.name + "\"> </td>")
	
	var row4 = $("<tr></tr>")
	row4.append("<td>Surname</td>")
	row4.append("<td class=\"wrap-input validate-input \" data-validate=\"Surname is required\"><input class=\"input-data\" name=\"surname\" id=\"surname\" type=\"text\" value=\""+user.surname + "\"> </td>")
	
	var row5 = $("<tr></tr>")
	row5.append("<td>Organisation</td>")
	row5.append("<td><input name=\"organisation\" id=\"organisation\" type=\"text\" value=\""+user.organisation.name + "\" readonly></td>")
	
	var row6 = $("<tr></tr>")
	row6.append("<td>Role</td>")
	var select = $("<select name=\"role\" id=\"role\"></select>")
	var option1 = $("<option value=\"User\">User</option>")
	select.append(option1)
	var option2 = $("<option value=\"Admin\">Admin</option>")
	select.append(option2)
	row6.append(select);
	
	var row7 = $("<tr></tr>")
	row7.append("<td><input id =\"editUser\" type=\"button\" value=\"Save Changes\"></td>");
	row7.append("<td><input id =\"discardUser\" type=\"button\" value=\"Discard Changes\"></td>");
	
	var row8 = $("<tr></tr>")
	row8.append("<td align=\"center\" colspan=\"2\"><input id =\"deleteUser\" type=\"button\" value=\"Delete user\"></td>");
	
	table.append(row1)
	table.append(row2)
	table.append(row3)
	table.append(row4)
	table.append(row5)
	if(currentUser.role!="ADMIN")
		table.append(row6)
	table.append(row7)
	table.append(row8)
	
	forma.append(table)
	
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

// Receives map of organisations
// Prints the table of  oforganisations
function printOrganisations(organisations){
	var edit = $("#fillEditForm")
	edit.empty()
	
	var header = $("#fillHeader");
	header.empty();
	
	header.append("<th>Name</th>")
	header.append("<th>Description</th>")
	header.append("<th>Logo</th>")
	
	var table = $("#fillTable")
	table.append(header)
	
	var body = $("#fillBody")
	body.empty()
	
	$.each(organisations, function(key, value){
		var row = $("<tr id=\""+key+"\" class=\"edit\"></tr>")
		row.append("<td id=\""+key+"\">" + value.name + "</td>");
		row.append("<td id=\""+key+"\">" + value.description + "</td>");
		var img = $('<img src="' +value.logo +'">')
		var rowLogo= $("<td id=\""+key+"\"></td>");
		rowLogo.append(img);
		row.append(rowLogo)
		//row.append("<td id=\""+key+"\">" + value.logo + "</td>");
		body.append(row)
	});
	
	body.append('<tr><td><input type="submit" id ="dodajOrg" value="Add new organisation"></td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>')

	table.append(body)
	
	// When clicking on a table row
	// Open edit menu for that row
	$("tr.edit").click(function(e){
		e.preventDefault();
		$.each(organisations, function (key, value) {
			if(key == e.target.id){
				editOrganisation(value)
			}
		})
	});
	
	//Opens menu for adding new organisations
	$("#dodajOrg").click(function(e){
		e.preventDefault();
		addNewOrganisation()
	});
		
	
}

// Receives map of organisations
// Prints manu for adding new users
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
	
	row7.append("<td><input id=\"addUser\" type=\"submit\" value=\"Add new User\"></td>");
	
	table.append(row1);
	table.append(row2);
	table.append(row3);
	table.append(row4);
	if(currentUser.role=="SUPER_ADMIN")
		table.append(row5);
	table.append(row6);
	table.append(row7);
	
	forma.append(table);
	
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
			organisation = currentUser.organisation.name
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
				} else {
					printUsers(response);
					
				}
			}
		});
	});
	
}

// Gets map of users and returns it
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

// Prints menu for adding new organisations
function addNewOrganisation(){
	
	var forma = $("#fillEditForm")
	forma.empty();
	
	var table = $("<table class=\"dodataTabela\"></table>");
	
	var row1 = $("<tr></tr>");
	var row2 = $("<tr></tr>");
	var row3 = $("<tr></tr>");
	var row4 = $("<tr></tr>");
	
	row1.append("<td>Name</td>");
	row1.append("<td class=\"wrap-input validate-input \" data-validate=\"Name is required\"><input class=\"input-data\" type=\"text\" name=\"name\" id=\"name\"></td>");
	
	row2.append("<td>Description</td>");
	row2.append("<td><input type=\"text\" name=\"description\" id=\"description\"></td>");
	
	row3.append("<td>Logo</td>");
	row3.append("<td><input type=\"file\" name=\"logo\" id=\"logo\"></td>");
	
	
	row4.append("<td><input id=\"addOrganisation\" type=\"submit\" value=\"Add\"></td>");
	
	table.append(row1);
	table.append(row2);
	table.append(row3);
	table.append(row4);
	
	forma.append(table);
	
	// Tries to add a new organisation
	// If successful gets map of organisations and calls function for printing
	// if failed makes alert
	$("#addOrganisation").click(function(e){
		e.preventDefault()
		var name = $("#name").val()
		var description = $("#description").val()
		var logo = $("#logo").val()
		console.log(logo);
		
		if(name == ''){
            showValidate($("#name"));
        }else{
        	hideValidate($("#name"));
        }
		
		if (name == ""){
			return;
		}
		
		if(logo == ""){
			logo = "default logo"
		}
		
		$.ajax({
			type : "POST",
			url : "rest/orgServ/addNewOrg",
			dataType : "json",
			data : {
				"name" : name,
				"description" : description,
				"logo" : logo
			},
			success : function(response){
				if (response == undefined) {
					alert("Organisation with given name already exists!");
				} else {
					printOrganisations(response);
					
				}
			}
		});
	});
}


// Receives organisation to edit
// Prints edit menu
function editOrganisation(organisation){
	var forma = $("#fillEditForm")
	forma.empty()
	
	var table = $("<table id=\editOrgT\" class=\"editOrgT\"></table>")
	
	var row1 = $("<tr></tr>")
	row1.append("<td>Name</td>")
	row1.append("<td><input name=\"name\" id=\"name\" type=\"text\" value=\""+organisation.name + "\" readonly></td>")
	
	var row2 = $("<tr></tr>")
	row2.append("<td>Description</td>")
	row2.append("<td><input name=\"description\" id=\"description\" type=\"text\" value=\""+organisation.description + "\"> </td>")
	
	
	var row3 = $("<tr></tr>")
	row3.append("<td>Logo</td>")
	row3.append("<td><input name=\"logo\" id=\"logo\" type=\"file\" value=\""+organisation.logo + "\"> </td>")
	
	var row4 = $("<tr></tr>")
	row4.append("<td><input id =\"editOrg\" type=\"button\" value=\"Save Changes\"></td>");
	row4.append("<td><input id =\"discardOrg\" type=\"button\" value=\"Discard Changes\"></td>");
	
	table.append(row1)
	table.append(row2)
	table.append(row3)
	table.append(row4)
	
	forma.append(table)
	
	// Discards changes
	// Gets map of organisations
	// Calls function for printing
	$("#discardOrg").click(function(e){
		e.preventDefault();
		$.ajax({
			type : "GET",
			url : "rest/orgServ/listOrganisations",
			dataType : "json",
			success : function(response){
				$("#edit").empty();
				if(currentUser.role == "SUPER_ADMIN")
					printOrganisations(response);
				else{
					getMyOrganisation()
				}
			}
		});
	});
	
	// Edits data for selected organisation
	// Gets map of organisations
	// If current user is a super_admin calls function for printing all organisations
	// if current user is an admin alerts him and calls function for printing his organisation 
	$("#editOrg").click(function(e){
		e.preventDefault();
		var name = $("#name").val()
		var description = $("#description").val()
		var logo = $("#logo").val()
		
		$.ajax({
			type : "POST",
			url : "rest/orgServ/editOrg",
			dataType : "json",
			data : {
				"name" : name,
	        	"description" : description,
	        	"logo" : logo,
	        },
			success : function(response){
				if (response != undefined) {
					$("#edit").empty();
					if(currentUser.role == "SUPER_ADMIN")
						printOrganisations(response);
					else{
						alert("Changes have been saved")
						getMyOrganisation()
					}
				}
			}
		});
		
	});
}

// Gets organisation for current user
// Calls function for editing organisation
function getMyOrganisation(){
	$.ajax({
		type : 'GET',
		url : "rest/orgServ/getMyOrg",
		dataType : "json",
		success : function(response){
			editOrganisation(response)
		},
		error : function() {
			alert("Error")
		}
	});
}

// Receives where to add class alert-validate
function showValidate(input) {
    var thisAlert = $(input).parent();

    $(thisAlert).addClass('alert-validate');
}

// Receives where to remove class alert validate
function hideValidate(input) {
    var thisAlert = $(input).parent();

    $(thisAlert).removeClass('alert-validate');
}

// Checks if there is a logged in user
// If no alerts and returns to login page
// If yes gets current user and calls function for editing profile 
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
