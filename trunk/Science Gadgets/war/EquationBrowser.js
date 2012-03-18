function insertrow(row) {
//			alert("alert1");
			$("p").add(row).appendTo(document.body);
			
			M.parseMath(document.body);
//			alert("alert2");
		}

function insertrow(row, e) {
	$("p").add(row).appendTo(e);
	M.parseMath(e);
}