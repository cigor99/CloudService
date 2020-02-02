$(document).ready(function(e){
	$('a[href="#viewBill"]').click(function(){
		askForBill()
	})
})

function askForBill(){
	
	var form = $("#fillEditForm")
	form.empty()
	
	var header = $("#fillHeader");
	header.empty();
	
	var body = $("#fillBody");
	body.empty();
	
	var table = $("<table></table>")
	
	header = $("<tr></tr>")
	
	header.append("<th>Start date</th>")
	header.append("<th>End date</th>")
	
	table.append(header)
	
	var row = $("<tr></tr>")
	row.append("<td><input type=\"date\" id=\"startDate\"></td>")
	row.append("<td><input type=\"date\" id=\"endDate\"></td>")
	
	table.append(row)
	
	row = $("<tr></tr>")
	row.append("<td><input id=\"getBill\" type=\"button\" value=\"Get bill\"></td>")
	
	table.append(row)
	
	form.append(table)
	
	//OVDE SI STIGLA
	
	$("#getBill").click(function(e){
		var start = $("#startDate").val()
		var end = $("#endDate").val()
		e.preventDefault()
		$.ajax({
			type : 'POST',
			url : "rest/orgServ/getMonthlyBill",
			data : {
				"startDate" : start,
				"endDate" : end
			},
			dataType : "json",
			success : function(response){
				printBill(response)
			},
			error : function(data){
				alert(data.responseText);
			}
			
		});
	})
	
}

function printBill(resources){
	var form = $("#fillEditForm");
	form.empty();
	
	var header = $("#fillHeader");
	header.empty();
	
	var body = $("#fillBody");
	body.empty();
	
	header.append("<th>Virtual Machine</th>")
	header.append("<th>Price</th>")
	
	$.each(resources.vmBills, function (key, value) {
		var row = $("<tr></tr>")
		row.append("<td>" + key + "</td>")
		row.append("<td>" + value + "€</td>")
		body.append(row)
	})
	
	var headerDisc = $("<tr class=\"redObojen\"></tr>")
	headerDisc.append("<th>Disc</th>")
	headerDisc.append("<th>Price</th>")
	
	body.append(headerDisc)
	
	$.each(resources.discBills,function(key,value){
		var row = $("<tr></tr>")
		row.append("<td>" + key + "</td>")
		row.append("<td>" + value + "€</td>")
		body.append(row)
	})
	
	
	var headerTotal = $("<tr class=\"redObojen\"></tr>")
	headerTotal.append("<th>Total</th>")
	headerTotal.append("<th>"+ resources.totalBill + "€</th>")
	
	body.append(headerTotal)
	
}