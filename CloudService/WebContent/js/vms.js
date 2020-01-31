var currentUser;


var selectOrg = $("<select name=\"orgName\" id=\"orgName\" </select>");

var selectCategory = $("<select name=\"category\" id=\"category\" </select>")

var core = $("<td><input type=\"text\" name=\"core\" id=\"core\" readonly ></td>")
	
var ram = $("<td><input type=\"text\" name=\"ram\" id=\"ram\" readonly ></td>")
	
var gpu = $("<td><input type=\"text\" name=\"gpu\" id=\"gpu\" readonly ></td>")

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
			row.append("<td id=\"" + key + "\">" + value.organisation.name + "</td>")
		
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
	row2.append("<td>Core number</td>")
	row2.append("<td colspan=\"2\" class=\"wrap-input validate-input \" data-validate=\"Core number is required\" data-error=\"Core number must be greater than 0\"><input class=\"input-data\" type=\"text\" name=\"core\" id=\"core\" value = \"" + vm.numCPUCores + "\"></td>")
	
	var row3 = $("<tr></tr>")
	row3.append("<td>RAM capacity</td>")
	row3.append("<td colspan=\"2\" class=\"wrap-input validate-input \" data-validate=\"RAM capacity is required\" data-error=\"RAM capacity must be greater or equal to 0\"><input class=\"input-data\" type=\"text\" name=\"ram\" id=\"ram\" value = \"" + vm.ramCapacity + "\"></td>")

	var row4 = $("<tr></tr>")
	row4.append("<td>GPU</td>")
	row4.append("<td colspan=\"2\"><input class=\"input-data\" type=\"text\" name=\"gpu\" id=\"gpu\" value = \"" + vm.numGPUCores + "\"></td>")

	var row5 = $("<tr></tr>")
	row5.append("<td>Organisation</td>")
	row5.append("<td colspan=\"2\"><input class=\"input-data\" type=\"text\" name=\"organisation\" id=\"organisation\" value=\"" + vm.organisation.name + "\"</td>");
	
	var row6 = $("<tr></tr>")
	row6.append("<td><input id =\"save\" type=\"button\" value=\"Save Changes\"></td>");
	row6.append("<td><input id =\"discard\" type=\"button\" value=\"Discard Changes\"></td>");
	row6.append("<td><input id =\"delete\" type=\"button\" value=\"Delete\"></td>");
	
	
	table.append(row1)
	table.append(row2)
	table.append(row3)
	table.append(row4)
	table.append(row5)
	table.append(row6)
	
	form.append(table)
}


/*
 * 	var row3 = $("<tr></tr>")
	if(currentUser.role == "SUPER_ADMIN"){
		$.ajax({
			type : 'GET',
			url : "rest/orgServ/listOrganisations",
			dataType : "json",
			success : function(response){
				var organisations = response
				row3.append("<td>Organisation</td>");
				var selectOrg = $("<select name=\"organisation\" id=\"organisation\"></select>");
				var def = $("<option value='' selected disabled hidden>Choose here</option>");
				//selectOrg.append(def);
				$.each(organisations, function(key, value){
					var option = $("<option></option>");
					option.append(key);
					selectOrg.append(option);
				});
				row3.append(selectOrg);
			}
		});
	}
 * */

function addNewVM(vms){
	var form = $("#fillEditForm")
	form.empty()
	
	var table = $("<table class=\"vmForm\"></table>")
	
	var row1 = $("<tr></tr>")
	row1.append("<td>Name</td>")
	row1.append("<td class=\"wrap-input validate-input \" data-validate=\"Name is required\"><input class=\"input-data\" type=\"text\" name=\"name\" id=\"name\"></td>")
	
	var row2 = $("<tr></tr>")
	row2.append("<td>Organisation</td>")
	if(currentUser.role=="ADMIN"){
		var org = currentUser.organisation.name
		row2.append("<td><input type=\"text\" name=\"org\" id=\"org\" readonly ></td>")
	}
	
	selectOrg.append("<option></option>")
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
			}
		});
	}
	
	var row3 = $("<tr></tr>")
	row3.append("<td>Discs</td>")
	
	var selectDisc = $("<select name=\"disc\" id=\"disc\" multiple=\"multiple\"</select>")
	
	row3.append(selectDisc);
	
	selectCategory.append("<option></option>")
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
				$("#core").val(response.numCPUCores)
				$("#ram").val(response.ramCapacity)
				$("#gpu").val(response.numGPUCores)
			}
		});
	})
	
	$("#add").click(function(e){
		e.preventDefault()
		var name = $("#name").val()
		var categoryName = selectCategory.val()
		var organisation = selectOrg.val()
		//var disc = $("#disc").val()
		if(name == ''){
            showValidate($("#name"));
        }else{
        	hideValidate($("#name"));
        }
		
		if(!name)
			return
			
		$.ajax({
			type : 'POST',
			url : "rest/vmServ/addVM",
			dataType : "json",
			data : {
				"name" : name,
				"categoryName" : categoryName,
				"organisation" : organisation
				//"disc" : disc
			},
			success : function(response){
				if(response==undefined)
					alert("VM  with given name already exists!")
				else
					printVMs(response)
			}
		});
	})
}

