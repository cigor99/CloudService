var currentUser;

checkIfLogged();

$(document).ready(function(e){
	
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
	header.append("<th>Organisation</th>")
	
	$.each(vms, function(key,value){
		var row = $("<tr id=\""+key+"\" class=\"editVM\"></tr>")
		
		row.append("<td id=\"" + key + "\">" + value.name + "</td>")
		row.append("<td id=\"" + key + "\">" + value.numCPUCores + "</td>")
		row.append("<td id=\"" + key + "\">" + value.ramCapacity + "</td>")
		row.append("<td id=\"" + key + "\">" + value.numGPUCores + "</td>")
		row.append("<td id=\"" + key + "\">" + value.organisation.name + "</td>")
		
		body.append(row)
	});
	
	if(currentUser.role!="USER")
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
	row2.append("<td>Category</td>")
	$.ajax({
		type : 'GET',
		url : "rest/catServ/getCategories",
		contentType : "application/json",
		success : function(response){
			var categories = response
			var selectCat = $("<select name=\"category\" id=\"category\"</select>")
			$.each(categories, function(key, value){
				var option = $("<option></option>");
				option.append(key);
				selectCat.append(option);
			});
			row2.append(selectCat);
		}
	});
	


	var org = currentUser.organisation.name;

	
	console.log(org);
	
	form.append(table)
	var row4 = $("<tr></tr>")
	row4.append("<td>Discs<td>")
	var selectDiscs = $("<select multiple id=\"discs\"></select>")
	$.ajax({
		type : 'POST',
		url : "rest/vmServ/as",
		contentType : "application/json",
		success : function(response){
			var discs = response;
			$.each(discs, function(key, value){
				var option = $("<option></option>");
				option.append(key);
				selectDiscs.append(option);
			});
		},
		error : function(){
			alert("Error")
		}
	});

	table.append(row1)
	table.append(row2)
	
	table.append(row4)
	table.append(row5)
	
	var row5 = $("<tr></tr>")
	row5.append("<td><input id=\"add\" type=\"submit\" value=\"Add\"></td>");

	
	/*
	// When inputs with these classes get into focus, alert disappears
	$('.data-form .input-data').each(function(){
        $(this).focus(function(){
           hideValidate(this);
        });
    });
	
	$("#add").click(function(e){
		e.preventDefault()
		var name = $("#name").val()
		var core = $("#core").val()
		var ram = $("#ram").val()
		var gpu = $("#gpu").val()
		
		if(name == ''){
            showValidate($("#name"));
        }else{
        	hideValidate($("#name"));
        }
		
		if(core == ''){
            showValidate($("#core"));
        }else{
        	hideValidate($("#core"));
        }
		
		if(ram == ''){
            showValidate($("#ram"));
        }else{
        	hideValidate($("#ram"));
        }
		
		if(gpu == '')
			gpu = "0"
		
		if(!name || !core || !ram || !gpu)
			return
			
		$.ajax({
			type : 'POST',
			url : "rest/catServ/addVMCategory",
			dataType : "json",
			data : {
				"name" : name,
				"core" : core,
				"ram" : ram,
				"gpu" : gpu
			},
			success : function(response){
				if(response==undefined)
					alert("VM Category with given name already exists!")
				else
					printCategories(response)
			}
		});
	})*/
}