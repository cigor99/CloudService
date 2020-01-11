$(document).on('submit', '#login-form', function(e) {
	e.preventDefault();
	console.log("try login");

	var username = $("#username").val();
	var password = $("#password").val();

	$.ajax({
		type : 'POST',
		url : "rest/userServ/validate",
		dataType : "json",
		data : {
			"username" : username,
			"password" : password
		},
		success : function(response) {
			if (response == undefined) {
				alert("Wrong username or password!")
			} else {
				$(location).attr('href', 'mainPage.html');
			}
		},
		error : function() {
			alert("")
		}
	})
})
$(document).ready(function(e) {
	$("#listUsers").click(function(e) {
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
	var tabela = $("#userTable");
	var tr = 
	console.log(users);
	$.each(users, function (key, value) {
		tabela.append("<tr>" + "<td>"+ value.email+ "</td>" +"<td>"+ value.password+ "</td>" + 
				"<td>"+ value.name+ "<td>"+ value.surname+ "</td>" + "</td>" +"</tr>")
	})
}


/*
$("#prikazDestinacija").click(function(e) {
	$.ajax({
		type : "GET",
		url : "rest/svc/getDestinacije",
		dataType : "json",
		success : function(response) {
			updateDestinacije(response);
			$("#edit").empty();
		}
	});
});
*/