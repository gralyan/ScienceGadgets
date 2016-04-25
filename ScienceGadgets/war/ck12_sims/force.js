/**
 * 
 */
function Force(canvas, x, y, length, fan, color){
	Draggable.call(this, canvas, x, y, 20, 20);
	
	this.autoDragY = false;
	this.canDrag = true;
	
	this.fan = fan;
	if(fan){
		fan.color = color;
	}
	this.color = color;

	this.length = length;
	this.downLength = this.length;
	this.arrowWidth = this.width/4;

	this.affectedForces = [];
	this.netForce = null;
	this.balloon = null;
}
Force.prototype = new Draggable;

Force.prototype.draw = function(){
	if(this.length === 0){
		return;
	}
	
	var ctx = this.canvas.ctx;
	var time = this.canvas.animationTime;

	var pointingRight = this.length>0;
	var dir = pointingRight ? 1 : -1;
	
	ctx.save(); // initial state
	ctx.beginPath();
	ctx.translate(this.x, this.y);

	ctx.save(); // arrow

	// Arrow head offset
	if(!pointingRight){
		ctx.translate(this.width,0);
	}
	
	ctx.fillStyle = this.color;
	ctx.fillRect(-1*this.length,this.arrowWidth,this.length,this.arrowWidth*2);
	
	ctx.beginPath();
	ctx.strokeStyle = "black";
    ctx.moveTo(0,0);
    ctx.lineTo(0,this.arrowWidth*4);
    ctx.lineTo(this.arrowWidth*3*dir,this.arrowWidth*2);
    ctx.closePath();
    ctx.fill();

    if(this.canDrag){
    ctx.lineWidth = this.isPointedAt ? 2 : 1;
    var barSpace = this.arrowWidth*0.4 * dir;
    for(var i=2 ; i<5 ; i++){
    	ctx.moveTo(barSpace*i,this.arrowWidth*(i*.4));
    	ctx.lineTo(barSpace*i, this.arrowWidth*(4/i+1.5));
    	
    }
    ctx.stroke();
    }
    
	ctx.restore(); //arrow
	
	ctx.restore(); //initial state
}

Force.prototype.onMouseDown = function(event) {
	if(this.canDrag){
		Draggable.prototype.onMouseDown.call(this, event);
		this.downLength = this.length;
		for(var i=0 ; i<this.affectedForces.length ; i++){
			this.affectedForces[i].onMouseDown(event);
		}
	}
}
Force.prototype.onMouseMove = function(event) {
	if(this.canDrag){
		Draggable.prototype.onMouseMove.call(this, event);
		if (this.isDragging) {
			this.length = this.downLength + this.dx;
			if(this.length > 0){
				this.fan.x = 0;
				this.fan.direction = 0;
			}else{
				this.fan.x = 360;
				this.fan.direction = Math.PI;
			}
			this.fan.speed = Math.abs(this.length)/100;
			for(var i=0 ; i<this.affectedForces.length ; i++){
				this.affectedForces[i].x = this.x + this.affectedForces[i].length;
			}
			if(this.netForce != null){
				this.netForce.reNet();
			}
		}
	}
}

Force.prototype.reNet = function(){
	var sum = 0;
	for(var i=0 ; i<this.affectedForces.length ; i++){
		sum += this.affectedForces[i].length;
	}
	this.x = this.affectedForces[this.affectedForces.length-1].x;
	this.length =  this.x - this.affectedForces[0].x + this.affectedForces[0].length;
	if(this.balloon != null){
		this.balloon.x = this.x;
	}
}