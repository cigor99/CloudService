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
			error : function(){
				alert("Error")
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
	row2.append("<td class=\"wrap-input validate-input \" data-validate=\"Core number is required\"><input class=\"input-data\" type=\"text\" name=\"core\" id=\"core\"></td>")
	
	var row3 = $("<tr></tr>")
	row3.append("<td>RAM capacity</td>")
	row3.append("<td class=\"wrap-input validate-input \" data-validate=\"RAM capacity is required\"><input class=\"input-data\" type=\"text\" name=\"ram\" id=\"ram\"></td>")

	var row4 = $("<tr></tr>")
	row4.append("<td>GPU</td>")
	row4.append("<td><input class=\"input-data\" type=\"text\" name=\"gpu\" id=\"gpu\"></td>")

	var row5 = $("<tr></tr>")
	row5.append("<td><input id=\"add\" type=\"submit\" value=\"Add\"></td>");

	table.append(row1)
	table.append(row2)
	table.append(row3)
	table.append(row4)
	table.append(row5)
	
	form.append(table)
	
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
					alert("VM Category with given name already exists")
				else
					printCategories(response)
			}
		});
	})
}

