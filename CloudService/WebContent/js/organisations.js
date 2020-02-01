var currentUser;

$(document).ready(function(e){
	
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
				error : function(data) {
					alert(data.responseText)
				}
			});
		}
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
			error : function(data){
				alert(data.responseText);
			}
		});
	})
});

//Receives map of organisations
//Prints the table of  oforganisations
function printOrganisations(organisations){
	var edit = $("#fillEditForm")
	edit.empty()
	
	var header = $("#fillHeader");
	header.empty();
	
	header.append("<th>Name</th>")
	header.append("<th>Description</th>")
	header.append("<th>Logo</th>")
	
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
		body.append(row)
	});
	
	body.append('<tr><td><input type="submit" id ="dodajOrg" value="Add new Organisation"></td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>')
	
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

//Prints menu for adding new organisations
function addNewOrganisation(){
	
	var forma = $("#fillEditForm")
	forma.empty();
	
	var table = $("<table class=\"dodataTabela\"></table>");
	
	var row1 = $("<tr></tr>");
	var row2 = $("<tr></tr>");
	var row3 = $("<tr></tr>");
	var row4 = $("<tr></tr>");
	
	row1.append("<td>Name</td>");
	row1.append("<td class=\"wrap-input validate-input\" data-validate=\"Name is required\"><input type=\"text\" name=\"orgName\" id=\"orgName\"></td>");
	
	row2.append("<td>Description</td>");
	row2.append("<td><input type=\"text\" name=\"description\" id=\"description\"></td>");
	
	row3.append("<td>Logo</td>");
	row3.append("<td><input type=\"file\" name=\"logo\" id=\"logo\"></td>");
	
	
	row4.append("<td><input id=\"addOrganisation\" type=\"button\" value=\"Add\"></td>");
	row4.append("<td><input id=\"cancel\" type=\"button\" value=\"Cancel\"></td>");
	
	table.append(row1);
	table.append(row2);
	table.append(row3);
	table.append(row4);
	
	forma.append(table);
	
	// When inputs get into focus, alert disappears
    $("#orgName").focus(function(){
       hideValidate(this);
    });
	
	//// When inputs lose focus, and are empty alert appears
    $("#orgName").focusout(function(){
       if($("#orgName").val()=="")
    	   showValidate(this)
    });
	
	// Tries to add a new organisation
	// If successful gets map of organisations and calls function for printing
	// if failed makes alert
	$("#addOrganisation").click(function(e){
		e.preventDefault()
		var name = $("#orgName").val()
		var description = $("#description").val()
		var logo = $("#logo").val()
		console.log(logo);
		
		if(name == ''){
            showValidate($("#orgName"));
        }else{
        	hideValidate($("#orgName"));
        }
		
		if (!name){
			return;
		}
		
		if(!logo){
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
				printOrganisations(response);
			},
			error : function(data) {
				alert(data.responseText)
			}
		});
	});
	
	$("#cancel").click(function(e){
		e.preventDefault();
		$.ajax({
			type : 'GET',
			url : "rest/orgServ/listOrganisations",
			dataType : "json",
			success : function(response){
				printOrganisations(response);
			},
			error : function(data) {
				alert(data.responseText)
			}
		});
	})
}

//Receives organisation to edit
//Prints edit menu
function editOrganisation(organisation){
	if(currentUser.role == "ADMIN"){
		var body = $("#fillBody");
		body.empty();
		
		var header = $("#fillHeader");
		header.empty();
	}
	var forma = $("#fillEditForm")
	forma.empty()
	
	var table = $("<table id=\editOrgT\" class=\"editOrgT\"></table>")
	
	var row1 = $("<tr></tr>")
	row1.append("<td>Name</td>")
	row1.append("<td class=\"wrap-input validate-input \" data-validate=\"Name is required\" ><input name=\"name\" id=\"name\" type=\"text\" value=\""+organisation.name + "\"</td>")
	
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
	
	// When inputs get into focus, alert disappears
 $("#name").focus(function(){
    hideValidate(this);
 });
	
	//// When inputs lose focus, and are empty alert appears
 $("#name").focusout(function(){
    if($("#name").val()=="")
 	   showValidate(this)
 });
	
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
			},
			error : function(data){
				alert(data.responseText);
			}
		});
	});
	
	// Edits data for selected organisation
	// Gets map of organisations
	// If current user is a super_admin calls function for printing all organisations
	// if current user is an admin alerts him and calls function for printing his organisation 
	$("#editOrg").click(function(e){
		e.preventDefault();
		var oldName = organisation.name;
		var name = $("#name").val()
		var description = $("#description").val()
		var logo = $("#logo").val()
		
		$.ajax({
			type : "POST",
			url : "rest/orgServ/editOrg",
			dataType : "json",
			data : {
				"oldName" : oldName,
				"name" : name,
	        	"description" : description,
	        	"logo" : logo,
	        },
			success : function(response){
				if (response != undefined) {
					console.log("Usao")
					if(currentUser.role == "SUPER_ADMIN"){
						console.log(response)
						printOrganisations(response);
					}
						
					else{
						alert("Changes have been saved")
						getMyOrganisation()
					}
				}else
					alert("Organisation with given name already exists!")
			},
			error : function(data){
				alert(data.responseText);
			}
		});
		
	});
}

function getMyOrganisation(){
	$.ajax({
		type : 'GET',
		url : "rest/orgServ/getMyOrg",
		dataType : "json",
		success : function(response){
			editOrganisation(response)
		},
		error : function(data){
			alert(data.responseText);
		}
	});
}