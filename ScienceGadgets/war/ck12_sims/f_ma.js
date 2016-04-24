
function f_ma(canvasID){
	
	var canvas = new Canvas(canvasID);
	
	canvas.interactables.push(new Balloon(canvas, 200, 210, 40, 45, 5));
	canvas.interactables.push(new Balloon(canvas, 100, 210, 40, 45, 10));
	canvas.interactables.push(new Fan(canvas, 10, 50, 40, 40, 0.4, 30));
	canvas.interactables.push(new Fan(canvas, 10, 130, 40, 40, 0.9, 30));
	canvas.startAnimation();
};

