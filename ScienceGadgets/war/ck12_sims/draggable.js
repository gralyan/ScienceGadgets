function Draggable(canvas, x, y, width, height) {
	Interactable.call(this, canvas, x, y, width, height);

	// Distance from Draggable(top/left) to Mouse
	this.draggableToMouseX = 0;
	this.draggableToMouseY = 0;
	this.canBePointedAt = true;
	this.isDragging = false;

	this.downX = 0;
	this.downY = 0;
	this.dx = 0;
	this.dy = 0;

	this.autoDragX = true;
	this.autoDragY = true;
}

Draggable.prototype = new Interactable;

Draggable.prototype.onMouseDown = function(event) {

	for (var i = 0; i < this.canvas.interactables.length; i++) {
		var inter = this.canvas.interactables[i];
		if (inter instanceof Draggable && inter.isDragging) {
			return;
		}
	}
	this.isDragging = this.isMouseOverCheck(event);

	if (this.isDragging) {
		var mouseXY = this.canvas.getMouseXY(event);
		this.downX = mouseXY.x;
		this.downY = mouseXY.y;
		this.draggableToMouseX = mouseXY.x - this.x;
		this.draggableToMouseY = mouseXY.y - this.y;
		this.canvas.canvasElement.style.cursor = 'move';
	}
}

Draggable.prototype.onMouseMove = function(event) {
	if (this.isDragging) {
		var mouseXY = this.canvas.getMouseXY(event);
		this.dx = mouseXY.x - this.downX;
		this.dy = mouseXY.y - this.downY;
		if (this.autoDragX) {
			this.x = mouseXY.x - this.draggableToMouseX;
		}
		if(this.autoDragY){
			this.y = mouseXY.y - this.draggableToMouseY;
		}
	} else {
		Interactable.prototype.onMouseMove.call(this, event);
	}
}

Draggable.prototype.onMouseUp = function(event) {
	this.isDragging = false;
}
