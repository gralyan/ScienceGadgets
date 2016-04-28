/**
 * 
 */
function Graph(canvas, x, y, width, height){
	Interactable.call(this, canvas, x, y, width, height);

	this.thickness = 3;
	
	this.lines = [];
}
Graph.prototype = new Interactable;

/*
 * Adds a line which may include kiks
 * 
 * points - array of coordinates
 * ex[0,0, 1,2, 3,4] makes lines through (0,0) (1,2) and (3,4)
 * 
 * color - color as string
 */
Graph.prototype.addLine = function(points, color){
	
}
Graph.prototype.draw = function(){
	var ctx = this.canvas.ctx;
	var time = this.canvas.animationTime;
	
	ctx.save(); // initial state
	ctx.beginPath();
	
	//
	// Coordinates nor start from origin and go up/right
	//
	ctx.translate(this.x, this.y + this.height);
	
	ctx.font = ctx.font="20px Ariel";
	ctx.fillText("Time",0.5*this.width,20);
	ctx.scale(1,-1);

	ctx.save(); // axis

	ctx.rect(0, -this.thickness, this.width, this.thickness);
	ctx.moveTo(this.width, -2*this.thickness);
	ctx.lineTo(this.width, this.thickness);
	ctx.lineTo(this.width + 2*this.thickness, -0.5*this.thickness);
	ctx.closePath();
	
	
	ctx.rotate(Math.PI/2);
	ctx.scale(1,-1);
	ctx.fillText("Distance",0.5*this.width,-5);
	ctx.scale(1,-1);

	ctx.rect(-this.thickness, 0, this.height+this.thickness, this.thickness);
	ctx.moveTo(this.height, -this.thickness);
	ctx.lineTo(this.height, 2*this.thickness);
	ctx.lineTo(this.height + 2*this.thickness, 0.5*this.thickness);
	ctx.closePath();
	
	ctx.fillStyle = "black";
	ctx.fill();

	ctx.restore(); // axis
	
	ctx.save(); // lines
	
	ctx.lineWidth = this.thickness;
	ctx.lineJoin = "round";
	
	var lastTimePoint = 0;
	var curYs = [];
	var curTime = Math.round(time*0.03);
	
	var i=0;
	var j=2;
	for( i=0 ; i<this.lines.length ; i++){
		var line = this.lines[i].points;
		ctx.beginPath();
		ctx.moveTo(line[0], line[1]);
		this.lines[i].curX = curTime;
		for( j=2 ; j<line.length ; j+=2){
			if(line[j-2] <= curTime){
				if(line[j] > curTime){ // Interpolated between points
					this.lines[i].curY = line[j-1] + (curTime-line[j-2])*( ((line[j+1] - line[j-1])) / (line[j] - line[j-2]) );
					ctx.lineTo(this.lines[i].curX, this.lines[i].curY);
				}else{// Before points
					ctx.lineTo(line[j],line[j+1]);
				}
			}
		}
		ctx.strokeStyle = this.lines[i].color;
		ctx.stroke();
	}

	ctx.restore(); // lines
	
	ctx.fillStyle = "white";
	for( i=0 ; i<this.lines.length ; i++){
		if(this.lines[i].curX < this.width){
			ctx.fillRect(this.lines[i].curX, this.lines[i].curY, 1 ,-this.lines[i].curY);
		}
	}
	
	ctx.restore(); // initial state
}

