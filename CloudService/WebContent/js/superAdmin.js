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

$(document).ready(function(e){
	
	$("#listOrgsSA").click(function(e) {
		console.log("list organisations")

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
	});
	
	
	$("#listUsersSA").click(function(e) {
		console.log("list users")

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

function printUsers(users){
	var div = $("#changeable");
	div.empty();
	$("#edit").empty();
	
	var forma = $("<form id=\"forma1\"></form>")
	
	var table = $("<table id=\dodataTabela\" class=\"dodataTabela\"></table>")
	
	table.append("<tr>" +
			"<th>Email</th>" +
			"<th>Name</th>" +
			"<th>Surname</th>" +
			"<th>Organisation</th>" +
			"<th></th>"+
			"<th></th>"+
			"</tr>");
	$.each(users, function (key, value) {
		if(value.role!="SUPER_ADMIN"){
			var row = $("<tr></tr>")
			
			row.append("<td>" + value.email + "</td>");
			row.append("<td>" + value.name + "</td>");
			row.append("<td>" + value.surname + "</td>");
			if(value.organisation == null){
				row.append("<td>" + "/"+ "</td>");
			}else{
				row.append("<td>" + value.organisation.name+ "</td>");
			}

			row.append("<td>" + "<input class=\"izmeni\" type=\"button\" id=\""+key+"\" value=\"Edit\"></td>")
			table.append(row)
		}
	})
	
	table.append('<tr><td><input type="submit" id="dodajUsr" name="dodajUsr" value="Add new user"></td></tr>');
	
	forma.append(table)
	
	div.append(forma)
	
	
	$("#dodajUsr").click(function(e){
		e.preventDefault();
		console.log("dodajUsr");
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
	
	$(".izmeni").click(function(e){
		e.preventDefault();
		$.each(users, function (key, value) {
			if(key == e.target.id){
				editUser(value)
			}
		})
	});
	
}

function editUser(user){
	var edit = $("#edit");
	edit.empty()
	var forma = $("<form id=\"editUserF\"></form>")
	var table = $("<table id=\editUserT\" class=\"editUserT\"></table>")
	var tr = $("<tr></tr>")
	var td1 =$("<td>Email</td>")
	var td2=$("<td><input type=\"text\" value=\""+user.email + "\"></td>")
	
	tr.append(td1)
	tr.append(td2)
	table.append(tr)
	forma.append(table)
	edit.append(forma)
}
function printOrganisations(organisations){
	var div = $("#changeable");
	div.empty();
	$("#edit").empty();
	
	var forma = $("<form id=\"forma1\"></form>")
	
	var table = $("<table id=\"dodataTabela\" class=\"dodataTabela\"></table>")
	
	table.append("<tr>" +
			"<th>Name</th>" +
			"<th>Description</th>" +
			"<th>Logo</th>"+
			"</tr>");
	
	$.each(organisations, function(key, value){
		var row = $("<tr></tr>")
		row.append("<td>" + value.name + "</td>");
		row.append("<td>" + value.description + "</td>");
		row.append("<td>" + value.logo + "</td>");
		table.append(row)
	});
	
	table.append('<tr><td><input type="submit" id ="dodajOrg" value="Add new organisation"></td></tr>')
	forma.append(table)
	
	div.append(forma)
	
	$("#dodajOrg").click(function(e){
		e.preventDefault();
		console.log("dodajOrg");
		addNewOrganisation()
	});
		
	
}

function addNewUser(organisations){
	var div = $("#edit")
	
	div.empty();
	
	var forma2 = $("<form id=\"forma2\"></form>");
	
	let table = $("<table class=\"dodataTabela\"></table>");
	
	var row1 = $("<tr></tr>");
	var row2 = $("<tr></tr>");
	var row3 = $("<tr></tr>");
	var row4 = $("<tr></tr>");
	var row5 = $("<tr></tr>");
	var row6 = $("<tr></tr>");
	var row7 = $("<tr></tr>");
	
	row1.append("<td>Email</td>");
	row1.append("<td class=\"wrap-input validate-input \" data-validate=\"Email is required\"><input type=\"text\" name=\"email\" id=\"email\"></td>");

	row2.append("<td>Password</td>");
	row2.append("<td class=\"wrap-input validate-input \" data-validate=\"Password is required\" ><input type=\"text\" name=\"password\" id=\"password\"></td>");
	
	row3.append("<td>Name</td>");
	row3.append("<td class=\"wrap-input validate-input \" data-validate=\"Name is required\" ><input type=\"text\" name=\"name\" id=\"name\"></td>");
	
	row4.append("<td>Surname</td>");
	row4.append("<td class=\"wrap-input validate-input \" data-validate=\"Surname is required\" ><input type=\"text\" name=\"surname\" id=\"surname\"></td>");
	
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
	
	row7.append("<td><input type=\"submit\" value=\"Add\"></td>");
	
	table.append(row1);
	table.append(row2);
	table.append(row3);
	table.append(row4);
	table.append(row5);
	table.append(row6);
	table.append(row7);
	
	forma2.append(table);
	div.append(forma2);
	
	$("#forma2").submit(function(e){
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
					$("#edit").empty();
					printUsers(response);
					
				}
			}
		});
	});
	
}

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

function addNewOrganisation(){
	var div = $("#edit")
	
	div.empty();
	
	var forma2 = $("<form id=\"forma2\"></form>");
	
	let table = $("<table class=\"dodataTabela\"></table>");
	
	var row1 = $("<tr></tr>");
	var row2 = $("<tr></tr>");
	var row3 = $("<tr></tr>");
	//var row4 = $("<tr></tr>");
	//var row5 = $("<tr></tr>");
	var row6 = $("<tr></tr>");
	
	row1.append("<td>Name</td>");
	row1.append("<td><input type=\"text\" name=\"name\" id=\"name\"></td>");
	
	row2.append("<td>Description</td>");
	row2.append("<td><input type=\"text\" name=\"description\" id=\"description\"></td>");
	
	row3.append("<td>Logo</td>");
	row3.append("<td><input type=\"text\" name=\"logo\" id=\"logo\"></td>");
	/*
	row4.append("<td>Users</td>");
	var selectUsr = $("<select name=\"users\" id=\"users\"></select>")
	var users = getAllUsers();
	$.each(users, function(key, value){
		var option = $("<otpion></option>");
		option.append(key);
		selectUsr.append(option);
	});
	var td = $("<td></td>");
	/////////============================DA LI OVO MOZE BEZ td
	td.append(selectUsr);
	row4.append(td);
	
	row5.append("<td>Resources</td>");
	var selectRes = $("<select name=\"resources\" id=\"resources\"></select>")
	for(let i = 0; i < 3; i++){
		var optionRes = $("<otpion></option>");
		optionRes.append("/");
	}
	row5.append(td);
	
*/
	
	row6.append("<td><input type=\"submit\" value=\"Add\"></td>");
	
	table.append(row1);
	table.append(row2);
	table.append(row3);
	//table.append(row4);
	//table.append(row5);
	table.append(row6);
	
	forma2.append(table);
	div.append(forma2);
	
	$("#forma2").submit(function(e){
		e.preventDefault()
		var name = $("#name").val()
		var description = $("#description").val()
		var logo = $("#logo").val()
		//var users = $("#users").val()
		//var resources = $("#resources").val()
		
		console.log(name)
		console.log(description)
		console.log(logo)
		//console.log(users)
		//console.log(resources)
		
		if (name == "" || description == "" || logo == "" ){//|| users == "" || resources == "" ) {
			alert("All fields must bi filled !");
			return;
		}
		
		$.ajax({
			type : "POST",
			url : "rest/orgServ/addNewOrganisation",
			dataType : "json",
			data : {},
			success : function(response){
				if (response == undefined) {
					alert("Organisation with given name already exists!");
				} else {
					$("#edit").empty();
					printUsers(response);
					
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
