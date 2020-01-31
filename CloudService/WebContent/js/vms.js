var currentUser;


var selectOrg = $("<select name=\"orgName\" id=\"orgName\" </select>");

var selectCategory = $("<select name=\"category\" id=\"category\" </select>")

var core = $("<td><input type=\"text\" name=\"core\" id=\"core\" readonly ></td>")
	
var ram = $("<td><input type=\"text\" name=\"ram\" id=\"ram\" readonly ></td>")
	
var gpu = $("<td><input type=\"text\" name=\"gpu\" id=\"gpu\" readonly ></td>")

var currentCat;

checkIfLogged();

$(document).ready(function(e){
	
	$.ajax({
		type : 'GET',
		url : "rest/vmServ/getVMs",
		contentType : "application/json",
		success : function(response){
			printVMs(response)
		},
		error : function(){
			alert("Error")
		}
	});
	
	$('a[href="#viewVMs"]').click(function(){
		$.ajax({
			type : 'GET',
			url : "rest/vmServ/getVMs",
			contentType : "application/json",
			success : function(response){
				printVMs(response)
			},
			error : function(){
				alert("Error")
			}
		});
	});
	
});

function printVMs(vms){
	var edit = $("#fillEditForm")
	edit.empty()
	
	var header = $("#fillHeader")
	header.empty()
	
	var body = $("#fillBody")
	body.empty()
	
	header.append("<th>Name</th>")
	header.append("<th>Num CPU Cores</th>")
	header.append("<th>Ram</th>")
	header.append("<th>Num GPU Cores</th>")
	if(currentUser.role=="SUPER_ADMIN")
		header.append("<th>Organisation</th>")
	
	$.each(vms, function(key,value){
		var row = $("<tr id=\""+key+"\" class=\"editVM\"></tr>")
		
		row.append("<td id=\"" + key + "\">" + value.name + "</td>")
		row.append("<td id=\"" + key + "\">" + value.numCPUCores + "</td>")
		row.append("<td id=\"" + key + "\">" + value.ramCapacity + "</td>")
		row.append("<td id=\"" + key + "\">" + value.numGPUCores + "</td>")
		if(currentUser.role=="SUPER_ADMIN")
			row.append("<td id=\"" + key + "\">" + value.organisation + "</td>")
		
		body.append(row)
	});
	
	if(currentUser.role=="SUPER_ADMIN")
		body.append("<tr><td><input type=\"submit\" id=\"addVM\" name=\"addVM\" value=\"Add new VM\"></td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>");
	if(currentUser.role=="ADMIN")
		body.append("<tr><td><input type=\"submit\" id=\"addVM\" name=\"addVM\" value=\"Add new VM\"></td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>");
	
	
	$("#addVM").click(function(e){
		e.preventDefault();
		$.ajax({
			type : 'GET',
			url : "rest/vmServ/getVMs",
			dataType : "json",
			success : function(response){
				addNewVM(response);
			},
			error : function() {
				alert("Error")
			}
		});
	});
	
	$("tr.editVM").click(function(e){
		e.preventDefault();
		$.each(vms, function(key, value){
			if(key==e.target.id){
				editVM(value)
			}
		});
	});
}

function editVM(vm){
	var form = $("#fillEditForm")
	form.empty()
	
	var table = $("<table class=\"editCat\"></table>")
	
	var row1 = $("<tr></tr>")
	row1.append("<td>Name</td>")
	row1.append("<td colspan=\"2\" class=\"wrap-input validate-input \" data-validate=\"Name is required\"><input class=\"input-data\" type=\"text\" name=\"name\" id=\"name\" value = \"" + vm.name + "\"></td>")
	
	var row2 = $("<tr></tr>")
	row2.append("<td>Organisation</td>")
	row2.append("<td colspan=\"2\" ><input type=\"text\" name=\"org\" id=\"org\" value = \"" + vm.organisation + "\" readonly ></td>")
	
	var row3 = $("<tr></tr>")
	row3.append("<td>Discs</td>")
	
	var selectDisc = $("<select name=\"disc\" id=\"disc\" multiple=\"multiple\"</select>")
	
	$.ajax({
		type : 'POST',
		url : "rest/discServ/getAllVMDiscs",
		dataType : "json",
		data : {
			"vmName" : vm.name
		},
		success : function(response){
			selectDisc.empty()
			$.each(response, function(key, value){
				var option3 = $("<option></option>");
				option3.append(key);
				selectDisc.append(option3);
			});
		}
	})
	
	row3.append(selectDisc);
	
	selectCategory.empty()
	var row4 = $("<tr></tr>")
	row4.append("<td>Category</td>")
	$.ajax({
		type : 'GET',
		url : "rest/catServ/getCategories",
		contentType : "application/json",
		success : function(response){
			$.each(response, function(key, value){
				var option2 = $("<option></option>");
				option2.append(key);
				selectCategory.append(option2);
			});
			selectCategory.val(vm.category);
			selectCategory.trigger("change")
		}
	});
	
		
	row4.append(selectCategory);
	
	
	var row5 = $("<tr></tr>")
	row5.append("<td>Core number</td>")
	row5.append(core)
	
	var row6 = $("<tr></tr>")
	row6.append("<td>Ram capacity</td>")
	row6.append(ram)
	
	var row7 = $("<tr></tr>")
	row7.append("<td>GPU</td>")
	row7.append(gpu)
	
	var row8 = $("<tr></tr>")
	row8.append("<td><input id=\"save\" type=\"button\" value=\"Save\"></td>");
	row8.append("<td><input id=\"discard\" type=\"button\" value=\"Discard\"></td>");
	row8.append("<td><input id=\"delete\" type=\"button\" value=\"Delete\"></td>");

	selectCategory.on("change",function(){
		var catName = selectCategory.val()
		$.ajax({
			type : 'POST',
			url : "rest/catServ/getCategory",
			dataType : "json",
			data : {
				"catName" : catName
			},
			success : function(response){
				currentCat = response
				$("#core").val(response.numCPUCores)
				$("#ram").val(response.ramCapacity)
				$("#gpu").val(response.numGPUCores)
			}
		});
	})
	
	table.append(row1)
	table.append(row2)
	table.append(row3)
	table.append(row4)
	table.append(row5)
	table.append(row6)
	table.append(row7)
	table.append(row8)
	
	form.append(table)
	
	$("#save").click(function(e){
		e.preventDefault()
		var oldName = vm.name
		var vmWrap = {
				name : $("#name").val(),
				organisation : vm.organisation,
				category : selectCategory.val(),
				numCPUCores : $("#core").val(),
				ramCapacity : $("#ram").val(),
				numGPUCores : $("#gpu").val(),
				discs : $("#disc").val(),
				activityList : null,
				oldName : oldName
		}
		
		if($("#name").val() == ''){
            showValidate($("#name"));
        }else{
        	hideValidate($("#name"));
        }
		if(!$("#name"))
			return
			
		$.ajax({
			type : 'POST',
			url : "rest/vmServ/editVM",
			dataType : "json",
			data : JSON.stringify(vmWrap),
			contentType : "application/json",
			success : function(response){
				if(response==undefined)
					alert("VM  with given name already exists!")
				else
					printVMs(response)
			}
		});
	})
	
	$("#discard").click(function(e){
		e.preventDefault();
		$.ajax({
			type : 'GET',
			url : "rest/vmServ/getVMs",
			contentType : "application/json",
			success : function(response){
				printVMs(response)
			},
			error : function(){
				alert("Error")
			}
		});
	});
	
	$("#delete").click(function(e){
		e.preventDefault();
		var oldName = vm.name
		$.ajax({
			type : 'POST',
			url : "rest/vmServ/deleteVM",
			dataType : "json",
			data : {
				"oldName" : oldName,
				"organisation" : vm.organisation
			},
			success : function(response){
				printVMs(response)
			},
			error : function(){
				alert("Error")
			}
		});
	});
	
}



function addNewVM(vms){
	var form = $("#fillEditForm")
	form.empty()
	
	var table = $("<table class=\"vmForm\"></table>")
	
	var row1 = $("<tr></tr>")
	row1.append("<td>Name</td>")
	row1.append("<td class=\"wrap-input validate-input \" data-validate=\"Name is required\"><input class=\"input-data\" type=\"text\" name=\"name\" id=\"name\"></td>")
	
	
	
	selectOrg.empty()
	var row2 = $("<tr></tr>")
	row2.append("<td>Organisation</td>")
	if(currentUser.role=="ADMIN"){
		$.ajax({
			type : 'GET',
			url : "rest/orgServ/getMyOrg",
			contentType : "application/json",
			success : function(response){
				var option1 = $("<option></option>");
				option1.append(response.name);
				selectOrg.append(option1);
				row2.append(selectOrg);
				selectOrg.trigger("change")
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
				row2.append(selectOrg);
				selectOrg.trigger("change")
			}
		});
	}
	

	$("#orgName").val($("#orgName option:first").val())
	
	var row3 = $("<tr></tr>")
	row3.append("<td>Discs</td>")
	
	var selectDisc = $("<select name=\"disc\" id=\"disc\" multiple=\"multiple\"</select>")
	
	row3.append(selectDisc);
	
	selectCategory.empty()
	var row4 = $("<tr></tr>")
	row4.append("<td>Category</td>")
	$.ajax({
		type : 'GET',
		url : "rest/catServ/getCategories",
		contentType : "application/json",
		success : function(response){
			$.each(response, function(key, value){
				var option2 = $("<option></option>");
				option2.append(key);
				selectCategory.append(option2);
			});
			selectCategory.trigger("change")
		}
	});
	

	$("#category").val($("#category option:first").val())
	
	row4.append(selectCategory);
	
	var row5 = $("<tr></tr>")
	row5.append("<td>Core number</td>")
	row5.append(core)
	
	var row6 = $("<tr></tr>")
	row6.append("<td>Ram capacity</td>")
	row6.append(ram)
	
	var row7 = $("<tr></tr>")
	row7.append("<td>GPU</td>")
	row7.append(gpu)
	
	var row8 = $("<tr></tr>")
	row8.append("<td><input id=\"add\" type=\"submit\" value=\"Add\"></td>");

	table.append(row1)
	table.append(row2)
	table.append(row3)
	table.append(row4)
	table.append(row5)
	table.append(row6)
	table.append(row7)
	table.append(row8)
	
	form.append(table)
	
	
	
	// When inputs with these classes get into focus, alert disappears
	$('.input-data').each(function(){
        $(this).focus(function(){
           hideValidate(this);
        });
    });
	

	selectOrg.on("change",function(){
		var orgName = selectOrg.val()
		$.ajax({
			type : 'POST',
			url : "rest/discServ/getOrgFreeDiscs",
			dataType : "json",
			data : {
				"orgName" : orgName
			},
			success : function(response){
				selectDisc.empty()
				$.each(response, function(key, value){
					var option3 = $("<option></option>");
					option3.append(key);
					selectDisc.append(option3);
				});
			}
		})
	})
	
	selectCategory.on("change",function(){
		var catName = selectCategory.val()
		$.ajax({
			type : 'POST',
			url : "rest/catServ/getCategory",
			dataType : "json",
			data : {
				"catName" : catName
			},
			success : function(response){
				currentCat = response
				$("#core").val(response.numCPUCores)
				$("#ram").val(response.ramCapacity)
				$("#gpu").val(response.numGPUCores)
			}
		});
	})
	
	$("#add").click(function(e){
		e.preventDefault()
		var vm = {
				name : $("#name").val(),
				organisation : selectOrg.val(),
				category : selectCategory.val(),
				numCPUCores : $("#core").val(),
				ramCapacity : $("#ram").val(),
				numGPUCores : $("#gpu").val(),
				discs : $("#disc").val(),
				activityList : null
		}
		
		if($("#name").val() == ''){
            showValidate($("#name"));
        }else{
        	hideValidate($("#name"));
        }
		
		if(!$("#name"))
			return
			
		$.ajax({
			type : 'POST',
			url : "rest/vmServ/addVM",
			dataType : "json",
			data : JSON.stringify(vm),
			contentType : "application/json",
			success : function(response){
				if(response==undefined)
					alert("VM  with given name already exists!")
				else
					printVMs(response)
			}
		});
	})
}

