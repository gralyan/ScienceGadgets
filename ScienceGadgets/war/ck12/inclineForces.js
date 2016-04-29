
function inclineForces(canvasID){
	
	var canvas = new Canvas(canvasID);
	
	var incline = new Incline(canvas, 100, 30, 150, 150);
	
	var inclineDrag = new InclineDrag(canvas, incline);
	canvas.interactables.push(incline);
	canvas.interactables.push(inclineDrag);
	
	canvas.interactables.push(incline.weightCheck);
	canvas.interactables.push(incline.normalCheck);
	canvas.interactables.push(incline.frictionCheck);
	canvas.interactables.push(incline.allCheck);
	canvas.startAnimation();
	
};
