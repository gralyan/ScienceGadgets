/*
 Translates the clicked element with the translation stored in the title.

To make an element containing text translatable:
-add the class name "translatable"
-add a translation in the title

To make a 'pre' element translatable:
-add the class name "translatable"
-add a translated copy of it
-add an id to both (trans1...) and (trans2...) with the same suffix
*note- the trans2... will be initially hidden
*/

function makeTranslatable(){
	var translatables = document.getElementsByClassName("translatable");
    for (var i = 0; i < translatables.length; i++) {

        (function(i){
            translatables[i].onclick = function() {
				if(translatables[i].tagName == 'PRE'){
					var translatedPreID = null;
					if(translatables[i].id.indexOf('trans1') > -1){
						translatedPreID = translatables[i].id.replace('trans1', 'trans2');
					}else if(translatables[i].id.indexOf('trans2') > -1){
						translatedPreID = translatables[i].id.replace('trans2', 'trans1');
					}
					if(translatedPreID != null && document.getElementById(translatedPreID)){
						translatables[i].style.display = 'none';
						document.getElementById(translatedPreID).style.display = 'block';
					}else{
						console.error('No translation found');
					}
				}else{
					var translation = translatables[i].getAttribute("title");
					translatables[i].setAttribute("title", translatables[i].innerHTML);
					translatables[i].innerHTML = translation;
				}


            };
        })(i);
    }
}
