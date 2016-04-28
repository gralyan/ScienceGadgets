/**
 * 
 */
function Balloon(canvas, x, y, width, height, mass){

	this.onTime = 0;
	this.isOn = false;
	
	this.mass = mass;
	this.sideLength = Math.sqrt(this.mass*130);
	
	this.wobbleOffset = Math.random()*1000;
	this.wobbleRate = Math.random()*0.4+0.8;
	
	this.autoInteract = true;
	
	this.tiedX = null;
	this.tiedY = null;
	
	Draggable.call(this, canvas, x, y, width, height+this.sideLength);
}
Balloon.prototype = new Draggable;

Balloon.shape = new Path2D('m 19.433993,39.269058 c 0,0 -15.4339992,-14.648236 -15.4339992,-23.614832 0,-8.9665941 7.2767622,-15.870996 16.2531032,-15.870996 8.97634,0 16.253101,7.1056861 16.253101,15.870996 0,8.765312 -15.767983,23.608311 -15.767983,23.608311 l 2.123088,2.478102 -5.59065,-0.007 z');


Balloon.prototype.draw = function(){
	var ctx = this.canvas.ctx;
	var time = this.canvas.animationTime;
	var angle = Math.sin((time+this.wobbleOffset)/500*this.wobbleRate)/6; 
	
	ctx.save(); // initial state
	ctx.beginPath();
	
	ctx.translate(this.x, this.y);

	var iFan = this.interactFan();
	
	if(this.isOn){
		if(iFan != null){
			if(this.x + this.width > this.canvas.width){
				this.y += iFan.speed*2;
			}else{
				var acceleration = iFan.speed/this.mass;
				this.x += acceleration*(this.x/7);
				angle += iFan.speed;
					
				ctx.save() // acceleration
				
				var aSize = acceleration*100;
				ctx.translate(this.width+10,this.height-this.sideLength/2);

				ctx.fillStyle = "lavender";
				ctx.beginPath();
			    ctx.moveTo(aSize,aSize*-2);
			    ctx.lineTo(aSize,aSize*2);
			    ctx.lineTo(aSize*4,0);
			    ctx.rect(0,aSize*-1,aSize,aSize*2);
			    ctx.fill();
				
				ctx.fillStyle = "black";
				ctx.font = aSize*2+"px sans-serif";
				ctx.textAlign = "left";
				ctx.textBaseline = 'middle';
				ctx.fillText("a",0,0);
				
				
				ctx.restore(); // acceleration
			}
		}
	}
		
	
	
	// Rotation
	ctx.save() // balloon
	
	if(this.tiedX && this.tiedY){
		angle += -Math.atan((this.tiedX - (this.x+this.width/2)) / (this.tiedY - (this.y+this.height/2)) );
	}
	
		ctx.translate(this.width/2, this.height/2+13);
		ctx.rotate(angle);
		ctx.translate(this.width/-2, this.height/-2-13);

	// balloon
	ctx.fillStyle = this.isPointedAt ? '#d85' : '#d20';
	ctx.strokeStyle = "#540";
	ctx.fill(Balloon.shape);
	ctx.stroke(Balloon.shape);
	
	ctx.restore();// balloon
	
	// Cargo
	if(this.tiedX && this.tiedY){
		ctx.beginPath();
		ctx.moveTo(this.width/2,this.height*0.7);
		ctx.lineTo(this.tiedX-this.x , this.tiedY-this.y);
		ctx.stroke();
	}else{
		ctx.fillStyle = this.isPointedAt ? '#fc5' : '#f92';
		roundRect(ctx,20-this.sideLength/2,43,this.sideLength,this.sideLength, 5, true)	
		ctx.fillStyle = "black";
		ctx.font = this.sideLength*0.9+"px sans-serif";
		ctx.textAlign = "center";
		ctx.textBaseline = 'top';
		ctx.fillText("m",20,43);
	}
	
	
	ctx.restore(); // initial state
}

Balloon.prototype.onMouseDown = function(event) {
	if(this.isMouseOverCheck(event)){
		Draggable.prototype.onMouseDown.call(this, event);
		this.isOn = false;
	}
}
Balloon.prototype.onMouseUp = function(event) {
	Draggable.prototype.onMouseUp.call(this, event);
	this.interactFan();
}
Balloon.prototype.interactFan = function(event) {
	if(!this.autoInteract){
		return null;
	}
	this.isOn = false;
	var topTresh = this.y+this.height*0.8
	var bottomThresh = this.y+this.height*0.2
	for(var iActive of this.canvas.interactables){
		if(iActive instanceof Fan){
			var iTop = iActive.y;
			var iBottom = iActive.y+iActive.height;
			var isWithin = topTresh > iTop && bottomThresh < iBottom;
			this.isOn = isWithin ? true : this.isOn;
			if(isWithin){
				this.wobbleRate = Math.abs(iActive.speed*10);
				return iActive;
			}
		}
	}
	this.wobbleRate = 1;
	
}