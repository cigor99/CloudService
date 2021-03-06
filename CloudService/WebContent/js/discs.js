var currentUser;

var selectOrg = $("<select colspan=\"2\" name=\"orgName\" id=\"orgName\" </select>");
$(document).ready(function(e){

	$('a[href="#viewDiscs"]').click(function(){
		$.ajax({
			type : 'GET',
			url : "rest/discServ/getDiscs",
			contentType : "application/json",
			success : function(response){
				printDiscs(response)
			},
			error : function(data){
				alert(data.responseText);
			}
			
		});
		
	});
	
});

function printDiscs(discs){
	var edit = $("#fillEditForm")
	edit.empty()
	
	var header = $("#fillHeader")
	header.empty()
	
	var body = $("#fillBody")
	body.empty()
	
	header.append("<th>Name</th>")
	header.append("<th>Capacity</th>")
	header.append("<th>VM Name</th>")
	header.append("<th>Organisation</th>")
	
	$.each(discs, function(key,value){
		var row = $("<tr id=\""+key+"\" class=\"editDisc\"></tr>")
		
		row.append("<td id=\"" + key + "\">" + value.name + "</td>")
		row.append("<td id=\"" + key + "\">" + value.capacity + "</td>")
		row.append("<td id=\"" + key + "\">" + value.vmName + "</td>")
		row.append("<td id=\"" + key + "\">" + value.organisation + "</td>")
		
		body.append(row)
	});
	
	if(currentUser.role!="USER")
		body.append("<tr><td><input type=\"submit\" id=\"addDisc\" name=\"addDisc\" value=\"Add new Disc\"></td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>");

	
	$("#addDisc").click(function(e){
		e.preventDefault();
		$.ajax({
			type : 'GET',
			url : "rest/vmServ/getVMs",
			dataType : "json",
			success : function(response){
				addNewDisc(response);
			},
			error : function() {
				alert("Error")
			}
		});
	});
	
	$("tr.editDisc").click(function(e){
		e.preventDefault();
		$.each(discs, function(key, value){
			if(key==e.target.id){
				editDisc(value)
			}
		});
	});
}

function addNewDisc(vms){
	var form = $("#fillEditForm")
	form.empty();
	
	var table = $("<table class=\"discForm\"></table>")
	
	var row1 = $("<tr></tr>")
	row1.append("<td>Name</td>")
	row1.append("<td class=\"wrap-input validate-input \" data-validate=\"Name is required\"><input class=\"input-data\" type=\"text\" name=\"name\" id=\"name\"></td>")
	
	var row2 = $("<tr></tr>")
	row2.append("<td>Disc Type</td>")
	var select = $("<select id=\"discType\" name=\"discType\"></select>")
	var option1 = $("<option value=\"SSD\">SSD</option>")
	var option2 = $("<option value=\"HDD\">HDD</option>")
	select.append(option1)
	select.append(option2)
	row2.append(select)
	
	
	var row3 = $("<tr></tr>")
	row3.append("<td>Capacity</td>")
	row3.append("<td class=\"wrap-input validate-input \" data-validate=\"Capacity is required\"  data-error=\"Core number must be greater than 0\"><input class=\"input-data\" type=\"number\" name=\"capacity\" id=\"capacity\"></td>")

	selectOrg.empty()
	var rowOrg = $("<tr></tr>")
	rowOrg.append("<td>Organisation</td>")
	
	if(currentUser.role=="ADMIN"){
		$.ajax({
			type : 'GET',
			url : "rest/orgServ/getMyOrg",
			contentType : "application/json",
			success : function(response){
				var option1 = $("<option></option>");
				option1.append(response.name);
				selectOrg.append(option1);
				rowOrg.append(selectOrg);
				selectOrg.trigger("change")
			},
			error : function(data){
				alert(data.responseText);
			}
			
		});
	}
	
	if(currentUser.role=="SUPER_ADMIN"){
		$.ajax({
			type : 'GET',
			url : "rest/orgServ/listOrganisations",
			contentType : "application/json",
			success : function(response){
				$.each(response, function(key, value){
					var option1 = $("<option></option>");
					option1.append(key);
					selectOrg.append(option1);
				});
				rowOrg.append(selectOrg);
				selectOrg.trigger("change")
			},
			error : function(data){
				alert(data.responseText);
			}
			
		});
	}
	$("#orgName").val($("#orgName option:first").val())
	
	var row4 = $("<tr></tr>")
	row4.append("<td>VM</td>");
	
	var selectVM = $("<select name=\"vmName\" id=\"vmName\"></select>");
	/*$.each(vms, function(key, value){
		var option = $("<option></option>");
		option.append(key);
		selectVM.append(option);
	});*/
	row4.append(selectVM);

	var row5 = $("<tr></tr>")
	row5.append("<td><input id=\"add\" type=\"button\" value=\"Add\"></td>");

	table.append(row1)
	table.append(row2)
	table.append(row3)
	table.append(rowOrg)
	table.append(row4)
	table.append(row5)
	
	form.append(table)
	
	
	// When inputs with these classes get into focus, alert disappears
	$('.data-form .input-data').each(function(){
        $(this).focus(function(){
           hideValidate(this);
        });
    });
	
	selectOrg.on("change",function(){
		var orgName = selectOrg.val()
		console.log(orgName);
		$.ajax({
			type : 'POST',
			url : "rest/vmServ/getOrgVMs",
			dataType : "json",
			data : {
				"orgName" : orgName
			},
			success : function(response){
				selectVM.empty()
				var option3 = $("<option></option>");
				selectVM.append("<option></option>")
				$.each(response, function(key, value){
					
					option3.append(key);
					selectVM.append(option3);
				});
			}
		})
	})
	
	$("#add").click(function(e){
		e.preventDefault()
		var name = $("#name").val()
		var discType = $("#discType").val()
		var capacity = $("#capacity").val()
		var vmName = selectVM.val()
		var orgName = selectOrg.val();
		
		if(name == ''){
            showValidate($("#name"));
        }else{
        	hideValidate($("#name"));
        }
		
		if(capacity == ''){
            showValidate($("#capacity"));
        }else{
        	hideValidate($("#capacity"));
        }
		
		if(!name || !capacity)
			return
		
		if(capacity <= 0){
			showValidate($("#capacity"))
		}else{
			hideValidate($("#capacity"))
		}
		
		if(orgName == ''){
            showValidate($("#orgName"));
        }else{
        	hideValidate($("#orgName"));
        }
			
		$.ajax({
			type : 'POST',
			url : "rest/discServ/addDisc",
			dataType : "json",
			data : {
				"name" : name,
				"discType" : discType,
				"capacity" : capacity,
				"vmName" : vmName,
				"orgName" : orgName
			},
			success : function(response){
				if(response==undefined)
					alert("Disc with given name already exists")
				else
					printDiscs(response)
			},
			error : function(data){
				alert(data.responseText);
			}
			
		});
		
	});
}

function editDisc(disc){
	var form = $("#fillEditForm")
	form.empty()
	
	var table = $("<table class=\"editDisc\"></table>")
	
	var row1 = $("<tr></tr>")
	row1.append("<td>Name</td>")
	if(currentUser.role!="USER")
		row1.append("<td colspan=\"2\" class=\"wrap-input validate-input \" data-validate=\"Name is required\"><input class=\"input-data\" type=\"text\" name=\"name\" id=\"name\" value = \"" + disc.name + "\"></td>")
	else
		row1.append("<td colspan=\"2\" ><input class=\"input-data\" type=\"text\" name=\"name\" id=\"name\" value = \"" + disc.name + "\" readonly ></td>")
	var row2 = $("<tr></tr>")
	row2.append("<td>Disc type</td>")
	var td = $("<td colspan=\"2\"></td>")
	if(currentUser.role!="USER"){
		var select = $("<select id=\"discType\" name=\"discType\"></select>")
		var option1 = $("<option value=\"SSD\">SSD</option>")
		var option2 = $("<option value=\"HDD\">HDD</option>")
		
		if(disc.type == "SSD"){
			select.append(option1)
			select.append(option2)
		}else{
			select.append(option2)
			select.append(option1)
		}
		td.append(select)
		row2.append(td)
	}else{
		row2.append("<td colspan=\"2\"><input class=\"input-data\" type=\"text\" name=\"discTypeU\" id=\"discTypeU\" value = \"" + disc.type + "\" readonly></td>")
	}
	
	var row3 = $("<tr></tr>")
	row3.append("<td>Capacity</td>")
	
	if(currentUser.role!="USER")
		row3.append("<td colspan=\"2\" class=\"wrap-input validate-input \" data-validate=\"Capacity must be greater or equal to 0\"><input class=\"input-data\" type=\"text\" name=\"capacity\" id=\"capacity\" value = \"" + disc.capacity + "\"></td>")
	else
		row3.append("<td colspan=\"2\"><input class=\"input-data\" type=\"text\" name=\"capacity\" id=\"capacity\" value = \"" + disc.capacity + "\" readonly></td>")
		
		
	
	var rowOrg = $("<tr></tr>")
	rowOrg.append("<td>Organisation</td>")
	
	rowOrg.append("<td colspan=\"2\"><input class=\"input-data\" type=\"text\" name=\"orgN\" id=\"orgN\" value = \"" + disc.organisation + "\" readonly></td>")
	
	var row4 = $("<tr></tr>")
	row4.append("<td>VM</td>")
	
	$.ajax({
			type : 'GET',
			url : "rest/discServ/getVMs",
			success : function(response){
				//$.each(response, function(key,value){
					var selectVM = $("<select name=\"vmName\" id=\"vmName\"></select>");
					selectVM.empty()
					var option3 = $("<option></option>");
					selectVM.append("<option></option>")
					$.each(response, function(key, value){
						var option3 = $("<option></option>");
						option3.append(key);
						selectVM.append(option3);
					});
					//row4.append(selectVM);
				//});
				row4.append(selectVM);
			}
		});
	
	var row5 = $("<tr></tr>")
	if(currentUser.role!="USER"){
		row5.append("<td><input id =\"save\" type=\"button\" value=\"Save Changes\"></td>");
		row5.append("<td><input id =\"discard\" type=\"button\" value=\"Discard Changes\"></td>");
	}
	
	if(currentUser.role=="SUPER_ADMIN")
		row5.append("<td><input id =\"delete\" type=\"button\" value=\"Delete\"></td>");
	
	table.append(row1)
	table.append(row2)
	table.append(row3)
	table.append(rowOrg)
	table.append(row4)
	table.append(row5)
	
	form.append(table)
	
	console.log(disc.vmName);
	
	$("#save").click(function(e){
		e.preventDefault()
		var name = $("#name").val()
		var discType = $("#discType").val()
		var capacity = $("#capacity").val()
		var newVMname = $("#vmName").val()
		var oldVMname = disc.vmName;
		var orgName =  $("#orgN").val()
		
		if(name == ''){
            showValidate($("#name"));
        }else{
        	hideValidate($("#name"));
        }
	
		if(capacity == ''){
            showValidate($("#capacity"));
        }else{
        	hideValidate($("#capacity"));
        }
		if(capacity <= 0){
			showValidate($("#capacity"))
		}else{
			hideValidate($("#capacity"))
		}
		
		if(!name || !capacity)
			return
			
		$.ajax({
			type : 'POST',
			url : "rest/discServ/editDisc",
			dataType : "json",
			data : {
				"oldName" : disc.name,
				"newName" : name,
				"discType" : discType,
				"capacity" : capacity,
				"oldVMname" : oldVMname,
				"newVMname" : newVMname,
				"orgName" : orgName
			},
			success : function(response){
				if(response==undefined)
					alert(" exists!")
				else
					printDiscs(response)
			},
			error : function(data){
				alert(data.responseText);
			}
			
		});
	});
	
	$("#discard").click(function(e){
		e.preventDefault();
		$.ajax({
			type : 'GET',
			url : "rest/discServ/getDiscs",
			contentType : "application/json",
			success : function(response){
				printDiscs(response)
			},
			error : function(data){
				alert(data.responseText);
			}
			
		})
	});
	
	$("#delete").click(function(e){
		e.preventDefault();
		var oldName = disc.name
		$.ajax({
			type : 'POST',
			url : "rest/discServ/deleteDisc",
			dataType : "json",
			data : {
				"oldName" : oldName
			},
			success : function(response){
				printDiscs(response)
			},
			error : function(data){
				alert(data.responseText);
			}
			
		});
	});
}





function showError(input){
	var thisError = $(input).parent();
	$(thisError).addClass('alert-error');
}


function hideError(input){
	var thisError = $(input).parent();
	
	$(thisError).removeClass('alert-error');
}

