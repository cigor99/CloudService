var currentUser;
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

var selectOrg = $("<select colspan=\"2\" name=\"orgName\" id=\"orgName\" </select>");

var selectCategory = $("<select colspan=\"2\" name=\"category\" id=\"category\" </select>")

var core = $("<td colspan=\"2\"><input type=\"text\" name=\"core\" id=\"core\" readonly ></td>")
	
var ram = $("<td colspan=\"2\"><input type=\"text\" name=\"ram\" id=\"ram\" readonly ></td>")
	
var gpu = $("<td colspan=\"2\"><input type=\"text\" name=\"gpu\" id=\"gpu\" readonly ></td>")

var checkBox = $("<tr><td><input type=\"checkbox\"  id=\"check\" name=\"check\" > On/Off</td></tr>")

var currentCat;

$(document).ready(function(e){
	checkIfLoggedVM()
	
	
	$.ajax({
		type : 'GET',
		url : "rest/vmServ/getVMs",
		contentType : "application/json",
		success : function(response){
			printVMs(response)
		},
		error : function(data){
			alert(data.responseText);
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
			error : function(data){
				alert(data.responseText);
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
	console.log(currentUser);
	checkIfLoggedVM()
	console.log(currentUser)
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
	
	table = $("<table></table>")
	
	table.append("<tr><td colspan =\"4\" ><input id=\"search\" name=\"search\" class=\"searchBar\" type=\"text\" placeholder=\"Search..\" ></td></tr>")
	table.append("<tr><td colspan =\"4\2\" >Fillter by:</td></tr>")
	
	var rowF3 = $("<tr></tr>")
	rowF3.append("<td>Number of cores:</td>")
	rowF3.append("<input id=\"cpufrom\" name=\"cpufrom\" class=\"fillterField\" type=\"text\" >")
	rowF3.append("<td>-</td>")
	rowF3.append("<input id=\"cputo\" name=\"cputo\" class=\"fillterField\" type=\"text\" >")
	
	var rowF4 = $("<tr></tr>")
	rowF4.append("<td>RAM:</td>")
	rowF4.append("<input id=\"ramfrom\" name=\"ramfrom\" class=\"fillterField\" type=\"text\" >")
	rowF4.append("<td>-</td>")
	rowF4.append("<input id=\"ramto\" name=\"ramto\" class=\"fillterField\" type=\"text\" >")
	
	var rowF5 = $("<tr></tr>")
	rowF5.append("<td>GPU:</td>")
	rowF5.append("<input id=\"gpufrom\" name=\"gpufrom\" class=\"fillterField\" type=\"text\" >")
	rowF5.append("<td>-</td>")
	rowF5.append("<input id=\"gputo\" name=\"gputo\" class=\"fillterField\" type=\"text\" >")
	
	var rowF6 = $("<tr></tr>")
	rowF6.append("<td><input type=\"button\" id=\"filterVM\" name=\"filterVM\" value=\"Filter VMs\"</td>")
	
	table.append(rowF3)
	table.append(rowF4)
	table.append(rowF5)
	table.append(rowF6)
	
	edit.append(table)
	
	$("#filterVM").click(function(e){
		e.preventDefault();
		$.ajax({
			type : 'POST',
			url : "rest/vmServ/filterVM",
			dataType : "json",
			data : {
				"cpufrom" : $("#cpufrom").val(),
				"cputo" : $("#cputo").val(),
				"ramfrom" : $("#ramfrom").val(),
				"ramto" : $("#ramto").val(),
				"gpufrom" : $("#gpufrom").val(),
				"gputo" : $("#gputo").val(),
				"text" : $("#search").val()
			},
			success : function(vms){
				var body = $("#fillBody")
				body.empty()
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

			},
			error : function(response){
				alert(response.responseText)
			}
		})
		
	});
	
	 $("#search").on("keyup", function(){
		 var text = $("#search").val()
			$.ajax({
				type : 'POST',
				url : "rest/vmServ/getFillterVMs",
				dataType : "json",
				data :
					{
						"text" : text
					},
				success : function(vms){
					var body = $("#fillBody")
					body.empty()
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

				},
				error : function() {
					alert("Error")
				}
			});
	 });
	
	 
	$("#addVM").click(function(e){
		e.preventDefault();
		$.ajax({
			type : 'GET',
			url : "rest/vmServ/getVMs",
			dataType : "json",
			success : function(response){
				addNewVM(response);
			},
			error : function(data){
				alert(data.responseText);
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
	
	var selectDisc = $("<select colspan=\"2\" name=\"disc\" id=\"disc\" multiple=\"multiple\"</select>")
	
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
		url : "rest/catServ/getCategoriesUnsafe",
		contentType : "application/json",
		success : function(response){
			$.each(response, function(key, value){
				var option2 = $("<option></option>");
				option2.append(key);
				selectCategory.append(option2);
			});
			selectCategory.val(vm.category);
			selectCategory.trigger("change")
		},
		error : function(data){
			alert("GRESKA U GET_CATEGORIES_UNSAFE");
		}
	});
	
		
	row4.append(selectCategory);
	
	//aktivnosti
	var aktivnostiTable =$("<table></table>")
	var row5 = $("<tr></tr>")
	var aktivnostiHeader = $("<thead class=\"thead-dark\"></thead>")
	aktivnostiHeader.append("<th>Activity start</th>")
	aktivnostiHeader.append("<th>Activity end</th>")
	var aktivnostBody =$("<tbody></tbody>")
	
	for (let i = 0; i < vm.activityList.length; ++i) {
		var row = $("<tr></tr>")
		row.append("<td>" + vm.activityList[i].onTime + "</td>");
		row.append("<td>" + vm.activityList[i].offTime + "</td>");
		aktivnostBody.append(row)
	}
	
	aktivnostiTable.append(aktivnostiHeader);
	aktivnostiTable.append(aktivnostBody)
	
	var proba = $("<td colspan=\"2\"</td>")
	proba.append(aktivnostiTable)
	row5.append(proba)
			
	var row6 = $("<tr></tr>")
	row6.append("<td>Core number</td>")
	row6.append(core)
	
	var row7 = $("<tr></tr>")
	row7.append("<td>Ram capacity</td>")
	row7.append(ram)
	
	var row8 = $("<tr></tr>")
	row8.append("<td>GPU</td>")
	row8.append(gpu)
	
	
	
	var row10 = $("<tr></tr>")
	row10.append("<td><input id=\"save\" type=\"button\" value=\"Save\"></td>");
	row10.append("<td><input id=\"discard\" type=\"button\" value=\"Discard\"></td>");
	row10.append("<td><input id=\"delete\" type=\"button\" value=\"Delete\"></td>");

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
	if(currentUser.role == "ADMIN")
		table.append(checkBox)
	table.append(row10)
	
	if(vm.activityList.length != 0){
		if(vm.activityList[vm.activityList.length - 1].offTime == ""){
			$('#check').prop("checked", true)
			console.log($('#check').is(":checked"))
		}
		else
			$('#check').prop('checked', false);
	}
	form.append(table)
	
	$("#save").click(function(e){
		e.preventDefault()
		var oldName = vm.name
		var check = $('#check').is(":checked")
		var vmWrap = {
				name : $("#name").val(),
				organisation : vm.organisation,
				category : selectCategory.val(),
				numCPUCores : $("#core").val(),
				ramCapacity : $("#ram").val(),
				numGPUCores : $("#gpu").val(),
				discs : $("#disc").val(),
				activityList : vm.activityList,
				oldName : oldName,
				checked : check
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
			},
			error : function(data){
				alert(data.responseText);
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
			error : function(data){
				alert(data.responseText);
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
			error : function(data){
				alert(data.responseText);
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
				row2.append(selectOrg);
				selectOrg.trigger("change")
			},
			error : function(data){
				alert(data.responseText);
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
		url : "rest/catServ/getCategoriesUnsafe",
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
		
		var name = $("#name").val()
		
		if(!name)
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
			},
			error : function(data){
				alert(data.responseText);
			}
			
		});
	})
}

function checkIfLoggedVM(){
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

