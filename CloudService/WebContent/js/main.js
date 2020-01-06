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
				alert("Login successful !")
				$(location).attr('href', 'hello.html');
			}
		},
		error : function() {
			alert("No success :(")
		}
	})
})
$(document).ready(function(e) {
	$("#listUsers").click(function(e) {
		console.log("try listing")

		$.ajax({
			type : 'GET',
			url : "rest/userServ/getUsers",
			dataType : "json",
			success : function(response){
				printUsers(response);
			},
			error : function() {
				alert("no success")
			}
		});
	});
});


function printUsers(users){
	/*	<table name="tabela" id="tabela" border="2">
		<tr>
			<th>email</th>
		</tr>
	</table>
	<script src="js/jquery.min.js"></script>
	<script src="js/main.js"></script>*/
	console.log("uso")
	var tabela = $("#userTable");
	console.log(users);
	for(let i = 0; i < users.length; i++){
		tabela.append("<tr><td>"+ users[i].email + "</td></tr>");
		console.log(i);
		console.log(users[i]);
	}
	//tabela.append("<tr><td>hello</td></tr>")
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