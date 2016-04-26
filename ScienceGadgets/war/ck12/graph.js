/**
 * 
 */
function Graph(canvas, x, y, width, height){
	Draggable.call(this, canvas, x, y, width, height);

	this.thickness = 3;
	
	this.lines = [];
}
Graph.prototype = new Draggable;

Graph.prototype.draw = function(){
	var ctx = this.canvas.ctx;
	var time = this.canvas.animationTime;
	
	ctx.save(); // initial state
	ctx.beginPath();
	
	//
	// Coordinates nor start from origin and go up/right
	//
	ctx.translate(this.x, this.y + this.height);
	ctx.scale(1,-1);

	ctx.save(); // axis

	ctx.rect(0, -this.thickness, this.width, this.thickness);
	ctx.moveTo(this.width, -2*this.thickness);
	ctx.lineTo(this.width, this.thickness);
	ctx.lineTo(this.width + 2*this.thickness, -0.5*this.thickness);
	ctx.closePath();
	
	ctx.rotate(Math.PI/2);

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
		var line = this.lines[i]
		ctx.beginPath();
		ctx.moveTo(line[0], line[1]);
		for( j=2 ; j<line.length ; j+=2){
			ctx.lineTo(line[j],line[j+1]);
			if(line[j] > curTime && line[j-2] <= curTime){
				curYs[i] = Math.round(line[j-1] + curTime*( ((line[j+1] - line[j-1])) / (line[j] - line[j-2]) ));
				console.log(i+" "+curYs[i]);
			}
		}
		ctx.stroke();
	}

	ctx.restore(); // lines
	
	ctx.fillStyle = "white";
	
	for( i=0 ; i<curYs.length ; i++){
		ctx.fillRect(curTime,curYs[i],5,5);
//		console.log(curTime+" "+curYs[i]);
	}
	
	ctx.restore(); // initial state
}

