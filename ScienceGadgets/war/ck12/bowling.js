
function bowling(canvasID){
	
	var canvas = new Canvas(canvasID);
	
	var graph = new Graph(canvas, 150, 50, 220, 200);
	var a = [0,0,100,200,500,50];
	graph.lines.push(a);
	graph.lines.push([0,100,100,0,200,50]);
	canvas.interactables.push(graph);
	canvas.startAnimation();
	
};

