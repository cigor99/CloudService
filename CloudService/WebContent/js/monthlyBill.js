$(document).ready(function(e){
	$('a[href="#viewBill"]').click(function(){
		$.ajax({
			type : 'GET',
			url : "rest/vmServ/getMonthlyBill",
			contentType : "application/json",
			success : function(response){
				alert(response)
			},
			error : function(data){
				alert(data.responseText);
			}
			
		});
	})
})