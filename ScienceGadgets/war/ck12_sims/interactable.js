function Interactable(canvas, x, y, width, height) {

	this.canvas = canvas;
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
	this.canBePointedAt = false;
	this.isPointedAt = false;
	this.wasMouseOver = false;

}

//MUST be overridden by subclass!!!
Interactable.prototype.draw = function() {
	console.warn('Interactable objects should have a draw function '
			+ this.toString() + 'does not');
}

// Can be overridden
Interactable.prototype.onMouseMove = function(event) {
	if (!this.wasMouseOver && this.isMouseOverCheck(event)) {
		this.onMouseOver();
		this.wasMouseOver = true;
	} else if (this.wasMouseOver && !this.isMouseOverCheck(event)) {
		this.onMouseOut();
		this.wasMouseOver = false;
	}
}

// Can be overridden
Interactable.prototype.onMouseOver = function(event) {
}
// Can be overridden
Interactable.prototype.onMouseOut = function(event) {
}
// Can be overridden
Interactable.prototype.onMouseDown = function(event) {	
}
// Can be overridden
Interactable.prototype.onMouseUp = function(event) {
}

Interactable.prototype.isMouseOverCheck = function(event) {
	var mouseXY = this.canvas.getMouseXY(event);
	var dX = mouseXY.x - this.x;
	var dY = mouseXY.y - this.y;
	var isWithinX = dX > 0 && dX < this.width;
	var isWithinY = dY > 0 && dY < this.height;
	return isWithinX && isWithinY;
}