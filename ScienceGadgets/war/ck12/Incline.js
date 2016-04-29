function Incline(canvas, x, y, width, height){
	Interactable.call(this, canvas, x, y, width, height);
	
	this.angle = 0.5;
	this.forceLineThickness = 3;
	
	var checkX = x + width + 50 ;
	
	this.weightCheck = new CheckBox(canvas, checkX,  y + 20, "Weight", Incline.weightColor, "Wy = W \u00B7 sin(\u03B8)");
	this.normalCheck = new CheckBox(canvas, checkX, y + 50, "Normal", Incline.normalColor, "N = Wy");
	this.frictionCheck = new CheckBox(canvas, checkX, y + 80, "Friction", Incline.frictionColor, "f = \u03BC \u00B7 N");
	this.allCheck = new CheckBox(canvas, checkX, y + 110,"All Forces", Incline.netForceColor,"Fnet = \u03A3F");
	CheckBox.prototype.selected = this.weightCheck;
	
}
Incline.prototype = new Interactable;

Incline.kineticFrictionCoef = 0.5;
Incline.inclineColor = "#99f";
Incline.boxColor = "#9e3";
Incline.normalColor = "#0277BD";
Incline.frictionColor = "#D0306A";
Incline.netForceColor = "#EF6C00";
Incline.weightColor = "#2C8C38";

Incline.prototype.arrowTo = function(angle, length, color, name, isDashed){
	var ctx = this.canvas.ctx;
	
	ctx.save();
	ctx.beginPath();
	
	if(isDashed){
		ctx.setLineDash([5, 5]);
	}

	ctx.rotate(angle);
	ctx.moveTo(0,0);
	ctx.lineTo(length,0);
	
	ctx.lineWidth = this.forceLineThickness;
	ctx.strokeStyle = color;
	ctx.stroke();
	
	ctx.translate(length, 0);
	
	ctx.moveTo(0, 2*this.forceLineThickness);
	if(length >=0){
		
		ctx.lineTo(4*this.forceLineThickness, 0);
	}else{
		
		ctx.lineTo(-4*this.forceLineThickness, 0);
	}
	ctx.lineTo(0, -2*this.forceLineThickness);

	ctx.fillStyle = color;
	ctx.fill();
	
	if(length >= 0){
		ctx.translate(10*this.forceLineThickness,0);
	}else{
		ctx.translate(-15*this.forceLineThickness,0);
	}
	ctx.rotate(-angle + this.angle);
//	ctx.translate(-10,-10);
	
	ctx.fillText(name, 0, 0);

	ctx.closePath();
	ctx.restore();
}
Incline.prototype.draw = function(){
	var ctx = this.canvas.ctx;
	var time = this.canvas.animationTime;
	
	var mode = CheckBox.prototype.selected;
	
	ctx.save(); // initial state
	
	ctx.translate(this.x, this.y + this.height);
	ctx.font = "20px sans-serif";

	ctx.save(); // Equation
	ctx.beginPath();
	ctx.textAlign = "center";
	ctx.textBaseline = "top";
	ctx.fillStyle = "silver";
	roundRect(ctx, 0, 0.4*this.width, this.width,40, 5, true)	
	ctx.fillStyle = "black";
	ctx.fillText(mode.equation, 0.5 * this.width, 0.4 * this.width + 10);
	ctx.closePath();
	ctx.restore(); // Equation

	ctx.save(); // Incline
	ctx.beginPath();
	ctx.moveTo(0,0);
	ctx.arc(0,0,this.width,0, -this.angle, true);
	ctx.lineTo(0,0);

	ctx.fillStyle = Incline.inclineColor;
	ctx.fill();
	
	ctx.restore(); // Incline
	
	
	ctx.rotate(-this.angle);
	ctx.translate(0.5*this.width,-0.25*this.width);

	ctx.save(); // Box

	ctx.fillStyle = Incline.boxColor;
	ctx.fillRect(-0.25*this.width, -0.25*this.width, 0.5*this.width, 0.5*this.width);
	
	ctx.restore();  // Box
	
	ctx.save(); // Forces
	
	ctx.textAlign = "center";
	ctx.textBaseline = "middle";
	
	var wy = 0.5*this.width*Math.cos(this.angle);
	
	
	if(mode == this.weightCheck || mode == this.allCheck){
		this.arrowTo(Math.PI/2+this.angle, 0.5*this.width, Incline.weightColor, "W");
		this.arrowTo(0, -0.5*this.width*Math.sin(this.angle), Incline.weightColor, "Wx", true);
	}
	if(mode == this.weightCheck || mode == this.normalCheck || mode == this.allCheck){
		this.arrowTo(Math.PI/2, wy, Incline.weightColor, "Wy", true);
	}
	if(mode == this.normalCheck || mode == this.frictionCheck || mode == this.allCheck){
		this.arrowTo(-Math.PI/2, wy, Incline.normalColor, "N");

	}
	if(mode == this.frictionCheck || mode == this.allCheck){
		this.arrowTo(0, Incline.kineticFrictionCoef * wy, Incline.frictionColor, "f");
		
	}
	if(mode == this.allCheck){
		this.arrowTo(0, Incline.kineticFrictionCoef * wy - 0.5*this.width*Math.sin(this.angle), Incline.netForceColor, "Fnet");
		
	}
	
	ctx.restore(); // Forces
	
	ctx.save(); // Mid dot
	
	ctx.beginPath();
	ctx.fillStyle = "#000";
	ctx.arc(0,0, this.forceLineThickness,0, 2*Math.PI);
	ctx.fill();

	ctx.restore(); // Mid dot
	
	ctx.restore(); // initial state
}
//////////////////////////////////////////////////////////////////////
// Drag Bar
/////////////////////////////////////////////////////////////////////
function InclineDrag(canvas, incline){
	this.incline = incline;
	
	Draggable.call(this, canvas, this.getX(), this.getY(), 20, 20);
}
InclineDrag.prototype = new Draggable;

InclineDrag.prototype.getX = function(){
	return this.incline.x + this.incline.width*Math.cos(this.incline.angle);
}
InclineDrag.prototype.getY = function(){
	return this.incline.y + this.incline.height - this.incline.width*Math.sin(this.incline.angle);
}

InclineDrag.prototype.draw = function(){
	var ctx = this.canvas.ctx;
	var time = this.canvas.animationTime;

	ctx.save(); // initial state
	
	ctx.translate(this.x, this.y);

	
	ctx.rotate(-this.incline.angle);
	
	ctx.beginPath();
	
	ctx.moveTo(0,-10);
	ctx.lineTo(5,-15);
	ctx.lineTo(10,-10);
	ctx.moveTo(0,10);
	ctx.lineTo(5,15);
	ctx.lineTo(10,10);
	
	ctx.moveTo(2,3);
	ctx.lineTo(8,3);
	
	ctx.moveTo(2,0);
	ctx.lineTo(8,0);
	
	ctx.moveTo(2,-3);
	ctx.lineTo(8,-3);
	
	
	ctx.shadowColor = 'black';
	ctx.shadowBlur = 5;
	
	ctx.stroke();

	ctx.closePath()

	ctx.restore(); // initial state
}
InclineDrag.prototype.onMouseMove = function(){
	Draggable.prototype.onMouseMove.call(this, event);
	
	if(this.isDragging){
		var mouseXY = this.canvas.getMouseXY(event);
		var dx = mouseXY.x - this.incline.x;
		var dy = this.incline.y + this.incline.height - mouseXY.y;

		if(dx > 3 && dy > 3){
			this.incline.angle = Math.atan( dy/dx );
		}
		
		this.x = this.getX();
		this.y = this.getY();
	}
}

////////////////////////////////////////////////////////////////////////////////
// CheckBox
//////////////////////////////////////////////////////////////////////////////
function CheckBox(canvas, x, y, label, color, equation){
	Interactable.call(this, canvas, x, y, 20, 10);

	this.label = label;
	this.color = color;
	this.equation = equation;
}
CheckBox.prototype = new Interactable;

CheckBox.prototype.draw = function(){

	var ctx = this.canvas.ctx;
	var time = this.canvas.animationTime;

	ctx.save(); // initial state
	
	ctx.translate(this.x, this.y);
	
	ctx.beginPath();
	
	ctx.rect(0,0, this.height, this.height);
	if(CheckBox.prototype.selected == this){
		ctx.fillStyle = this.color;
		ctx.fill();
	}
	ctx.lineWidth = 2;
	ctx.stroke();

	ctx.closePath();
	
	
	ctx.fillText(this.label, 2*this.height,this.height);
	
	ctx.restore(); // initial state
}

CheckBox.prototype.onMouseDown = function(event) {
	Interactable.prototype.onMouseDown.call(this, event);
	CheckBox.prototype.selected = this;
}