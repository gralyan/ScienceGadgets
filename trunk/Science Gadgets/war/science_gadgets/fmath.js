function resizeFlash(name, w, h){
	//alert(name + " w:" + w + " h:" + h)
	var obj = fMath_getElement("Div" + name);
	obj.style.width = parseFloat(w);
	obj.style.height = parseFloat(h);
	var fl = fMath_getSWF(name);
	fl.width = w;
	fl.height = h;
	
	// this is for opera bug display
	var div = fMath_getElement("bugWindowId");
	if(div!=null){
		document.body.removeChild(div);
	}
	div = document.createElement('div');
	div.setAttribute("id", "bugWindowId");
	div.style.position = "absolute";
	div.style.width=1;
	div.style.height=1;
	document.body.appendChild(div);
	setTimeout('fMath_operabug()', 100);
}

function fMath_operabug(){
	var div = fMath_getElement("bugWindowId");
	div.style.display = "none";
}

function fMath_getElement(id) {
	return document.getElementById ? document.getElementById(id) : document.all[id];
}

function fMath_getSWF(movieName) {
    if (navigator.appName.indexOf("Microsoft") != -1) {
        return fMath_getElement(movieName);
    }
    else {
      if(document[movieName].length != undefined){
          return document[movieName][1];
      }
        return document[movieName];
    }
}

