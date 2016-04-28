
function bowling(canvasID){
	
	var canvas = new Canvas(canvasID);
	
	var line1 = {"points":[0,0, 100,150, 200,220],"color":"#55d"};
	var line2 = {"points":[0,0, 50,150, 220,220],"color":"#d55"};
	
	var graph = new Graph(canvas, 150, 30, 220, 220);
	graph.lines.push(line1);
	graph.lines.push(line2);
	canvas.interactables.push(graph);
	
	var alley1 = new BowlingAlley(canvas, 10,30,50,260, 12, line2);
	var alley2 = new BowlingAlley(canvas, 70,30,50,260, 18, line1);
	canvas.interactables.push(alley1);
	canvas.interactables.push(alley2);
	
	var bowlButton = new RestartButton(canvas, 150,260,80,30, "Bowl!", "lime");
	canvas.interactables.push(bowlButton);
	
	canvas.startAnimation();
	
};
