
function net_forces(canvasID){
	
	var canvas = new Canvas(canvasID);
	
	var fan1 = new Fan(canvas, 0, 50, 40, 40, 0.5, 70);
	var fan2 = new Fan(canvas, 360, 100, 40, 40, 0.5, 70,Math.PI);
	var force1 = new Force(canvas, 300,200,100, fan1, "green");
	var force2 = new Force(canvas, 190,240,-100, fan2, "royalblue");
	var force3 = new Force(canvas, 190,120,0, null, "white");
	var balloon = new Balloon(canvas, force3.x+force3.width/2-20, force3.y-45, 40, 45, 1);
	balloon.autoDragX = false;
	balloon.autoDragY = false;
	balloon.canBePointedAt = false;
	balloon.autoInteract = false;
	balloon.tiedX = balloon.x+balloon.width/2;
	balloon.tiedY = balloon.y+balloon.height;
	
	force3.canDrag = false;
	force3.canBePointedAt = false;
	force3.balloon = balloon;
	
	force1.affectedForces.push(force2);
	force1.netForce = force3;
	force2.netForce = force3;
	force3.affectedForces.push(force1);
	force3.affectedForces.push(force2);
	
	canvas.interactables.push(force1);
	canvas.interactables.push(force2);
	canvas.interactables.push(force3);
	canvas.interactables.push(fan1);
	canvas.interactables.push(fan2);
	canvas.interactables.push(balloon);
	canvas.startAnimation();
};

