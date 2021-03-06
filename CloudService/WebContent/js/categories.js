var currentUser;

$(document).ready(function(e){
	
	//Calls function for viewing VMs
	$('a[href="#viewCategories"]').click(function(){
		$.ajax({
			type : 'GET',
			url : "rest/catServ/getCategories",
			contentType : "application/json",
			success : function(response){
				printCategories(response)
			},
			error : function(response){
				alert(response.responseText)
			}
		})
	});
})

function printCategories(categories){
	var edit = $("#fillEditForm")
	edit.empty()
	
	var header = $("#fillHeader")
	header.empty()
	
	var body = $("#fillBody")
	body.empty()
	
	header.append("<th>Name</th>")
	header.append("<th>Core number</th>")
	header.append("<th>RAM capacity</th>")
	header.append("<th>GPU</th>")
	
	$.each(categories, function(key,value){
		var row = $("<tr id=\""+key+"\" class=\"editCategory\"></tr>")
	
		row.append("<td id=\"" + key + "\">" + value.name + "</td>")
		row.append("<td id=\"" + key + "\">" + value.numCPUCores + "</td>")
		row.append("<td id=\"" + key + "\">" + value.ramCapacity + "</td>")
		row.append("<td id=\"" + key + "\">" + value.numGPUCores + "</td>")
		
		body.append(row)
	})
	
	body.append("<tr><td><input type=\"submit\" id=\"addCat\" name=\"addCat\" value=\"Add new VMCategory\"></td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>")
	
	//Adds new VMCategory
	$("#addCat").click(function(e){
		addNewCategory(categories)
	})
	
	$("tr.editCategory").click(function(e){
		e.preventDefault();
		$.each(categories, function(key , value){
			if(key==e.target.id){
				editVMCategory(value)
			}
		})
	})
	
}

function addNewCategory(categories){
	
	var form = $("#fillEditForm")
	form.empty()
	
	var table = $("<table class=\"categoryForm\"></table>")
	
	var row1 = $("<tr></tr>")
	row1.append("<td>Name</td>")
	row1.append("<td class=\"wrap-input validate-input \" data-validate=\"Name is required\"><input class=\"input-data\" type=\"text\" name=\"name\" id=\"name\"></td>")
	
	var row2 = $("<tr></tr>")
	row2.append("<td>Core number</td>")
	row2.append("<td  class=\"wrap-input validate-input \" data-validate=\"Number of CPU cores must be greater than 0\"><input class=\"input-data\" type=\"number\" min=\"1\" max=\"256\" name=\"core\" id=\"core\"></td>")
	
	var row3 = $("<tr></tr>")
	row3.append("<td>RAM capacity</td>")
	row3.append("<td  class=\"wrap-input validate-input \" data-validate=\"Ram capacity must be greater than 0\"><input class=\"input-data\" type=\"number\" min=\"1\" max=\"256\" name=\"ram\" id=\"ram\"></td>")

	var row4 = $("<tr></tr>")
	row4.append("<td>GPU</td>")
	row4.append("<td  class=\"wrap-input validate-input \" data-validate=\"Number of GPU cores must be greater or equal to 0\"><input type=\"number\" min=\"0\" max=\"256\" name=\"gpu\" id=\"gpu\"></td>")

	var row5 = $("<tr></tr>")
	row5.append("<td><input id=\"add\" type=\"submit\" value=\"Add\"></td>");

	table.append(row1)
	table.append(row2)
	table.append(row3)
	table.append(row4)
	table.append(row5)
	
	form.append(table)
	
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
				

		if(core <0 || ram == "" ){
            showValidate($("#core"));
        }else{
        	hideValidate($("#core"));
        }
		
		if(ram < 0 || ram == ""){
            showValidate($("#ram"));
        }else{
        	hideValidate($("#ram"));
        }
		
		if(gpu < 0){
            showValidate($("#gpu"));
        }else{
        	hideValidate($("#gpu"));
        }

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
				printCategories(response)
			},
			error : function(response) {
				alert(response.responseText)
			}
		});
	})
}

function editVMCategory(category){
	var form = $("#fillEditForm")
	form.empty()
	
	var table = $("<table class=\"editCat\"></table>")
	
	var row1 = $("<tr></tr>")
	row1.append("<td>Name</td>")
	row1.append("<td colspan=\"2\" class=\"wrap-input validate-input \" data-validate=\"Name is required\"><input class=\"input-data\" type=\"text\" name=\"name\" id=\"name\" value = \"" + category.name + "\"></td>")
	
	var row2 = $("<tr></tr>")
	row2.append("<td>Core number</td>")
	row2.append("<td colspan=\"2\" class=\"wrap-input validate-input \" data-validate=\"Core number must be greater than 0\"><input class=\"input-data\" type=\"text\" name=\"core\" id=\"core\" value = \"" + category.numCPUCores + "\"></td>")
	
	var row3 = $("<tr></tr>")
	row3.append("<td>RAM capacity</td>")
	row3.append("<td colspan=\"2\" class=\"wrap-input validate-input \" data-validate=\"RAM capacity must be greater or equal to 0\"><input class=\"input-data\" type=\"text\" name=\"ram\" id=\"ram\" value = \"" + category.ramCapacity + "\"></td>")

	var row4 = $("<tr></tr>")
	row4.append("<td>GPU</td>")
	row4.append("<td colspan=\"2\" class=\"wrap-input validate-input \" data-validate=\"Number of GPU cores must be greater or equal to 0\"><input class=\"input-data\" type=\"text\" name=\"gpu\" id=\"gpu\" value = \"" + category.numGPUCores + "\"></td>")

	var row5 = $("<tr></tr>")
	row5.append("<td><input id =\"save\" type=\"button\" value=\"Save Changes\"></td>");
	row5.append("<td><input id =\"discard\" type=\"button\" value=\"Discard Changes\"></td>");
	row5.append("<td><input id =\"delete\" type=\"button\" value=\"Delete\"></td>");
	
	

	table.append(row1)
	table.append(row2)
	table.append(row3)
	table.append(row4)
	table.append(row5)
	
	form.append(table)
	
	$("#save").click(function(e){
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
				
		if(core <0 ){
            showValidate($("#core"));
        }else{
        	hideValidate($("#core"));
        }
		
		if(ram < 0){
            showValidate($("#ram"));
        }else{
        	hideValidate($("#ram"));
        }
		
		if(gpu < 0){
            showValidate($("#gpu"));
        }else{
        	hideValidate($("#gpu"));
        }
		
		if(!name || !core || !ram || !gpu)
			return
			
		$.ajax({
			type : 'POST',
			url : "rest/catServ/editVMCategory",
			dataType : "json",
			data : {
				"oldName" : category.name,
				"newName" : name,
				"core" : core,
				"ram" : ram,
				"gpu" : gpu
			},
			success : function(response){
				printCategories(response)
			},
			error : function(response) {
				alert(response.responseText)
			}
		});
	})
	
	$("#discard").click(function(e){
		e.preventDefault();
		$.ajax({
			type : 'GET',
			url : "rest/catServ/getCategories",
			contentType : "application/json",
			success : function(response){
				printCategories(response)
			},
			error : function(response) {
				alert(response.responseText)
			}
		})
	});
	
	$("#delete").click(function(e){
		e.preventDefault();
		var oldName = category.name
		$.ajax({
			type : 'POST',
			url : "rest/catServ/deleteCategory",
			dataType : "json",
			data : {
				"oldName" : oldName
			},
			success : function(response){
				printCategories(response)
			},
			error : function(response){
				alert(response.responseText)
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

