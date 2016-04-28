function RestartButton(canvas, x, y, width, height, text, backColor, textColor) {
	Interactable.call(this, canvas, x, y, width, height);

	this.backColor = backColor;
	this.textColor = textColor;
	this.text = text;
}
RestartButton.prototype = new Interactable;

RestartButton.prototype.draw = function() {
	var ctx = this.canvas.ctx;
	var time = this.canvas.animationTime;

	ctx.save() // initial state

	ctx.translate(this.x, this.y);

	ctx.fillStyle = this.backColor || "white";
	roundRect(ctx, 0, 0, this.width, this.height, 5, true)

	ctx.fillStyle = this.textColor || "black";
	ctx.fillText(this.text, 0.2 * this.width, 0.7 * this.height);

	ctx.restore();
}

RestartButton.prototype.onMouseDown = function(event) {
	this.canvas.stopAnimation();
	this.canvas.restartAnimation();
}
