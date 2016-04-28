function Canvas(canvasID) {
	
	var thisCanvas = this;
	this.canvasElement = document.getElementById(canvasID);
	this.ctx = this.canvasElement.getContext('2d');
	this.interactables = [];
	
	this.width = this.canvasElement.width;
	this.height = this.canvasElement.height;

	// ///////////////////////////////////
	// Animation
	// ///////////////////////////////////
	this.startTime = 0;
	this.animationTime = 0;
	this.stamp = 0;

	this.animationLoop = function(timestamp) {
		thisCanvas.stamp = timestamp;
		thisCanvas.animationTime = timestamp - thisCanvas.startTime;
		thisCanvas.reDrawCanvas();
		thisCanvas.animationId = requestAnimationFrame(thisCanvas.animationLoop);
	};

	this.startAnimation = function() {
		thisCanvas.animationId = requestAnimationFrame(thisCanvas.animationLoop);
	};
	
	this.restartAnimation = function() {
		thisCanvas.startTime = thisCanvas.stamp;
		thisCanvas.animationId = requestAnimationFrame(thisCanvas.animationLoop);
	};

	this.stopAnimation = function() {
		if (thisCanvas.animationId) {
			cancelAnimationFrame(thisCanvas.animationId);
		}
	};
	
	// ///////////////////////////////////
	// Event Listeners
	// ///////////////////////////////////
	this.canvasElement.addEventListener('mousedown', function(event) {
		for (var i = 0; i < thisCanvas.interactables.length; i++) {
			thisCanvas.interactables[i].onMouseDown(event);
		}
	}, false);

	this.canvasElement.addEventListener('mousemove', function(event) {
		var isCursorPointing = false;
		for (var i = 0; i < thisCanvas.interactables.length; i++) {
			var interactable = thisCanvas.interactables[i];
			interactable.onMouseMove(event);
			interactable.isPointedAt = (interactable.canBePointedAt && interactable.isMouseOverCheck(event));
			isCursorPointing = isCursorPointing || interactable.isPointedAt;
		}
		if (isCursorPointing) {
			thisCanvas.canvasElement.style.cursor = 'pointer';
		} else {
			thisCanvas.canvasElement.style.cursor = 'default';
		}
	}, false);

	this.canvasElement.addEventListener('mouseup', function(event) {
		for (var i = 0; i < thisCanvas.interactables.length; i++) {
			thisCanvas.interactables[i].onMouseUp(event);
		}
	}, false);
}

// Coordinates of mouse relative to the canvas
Canvas.prototype.getMouseXY = function(event) {
	var bound = this.canvasElement.getBoundingClientRect();
	return {
		x : event.clientX - bound.left,
		y : event.clientY - bound.top
	}
}

Canvas.prototype.reDrawCanvas = function() {

	this.ctx.clearRect(0, 0, this.canvasElement.width, this.canvasElement.height);
	for (var i = 0; i < this.interactables.length; i++) {
		this.interactables[i].draw();
	}
}

/**
 * This function was found online and written by Juan Mendes
 * 
 * Draws a rounded rectangle using the current state of the canvas.
 * If you omit the last three params, it will draw a rectangle
 * outline with a 5 pixel border radius
 * @param {CanvasRenderingContext2D} ctx
 * @param {Number} x The top left x coordinate
 * @param {Number} y The top left y coordinate
 * @param {Number} width The width of the rectangle
 * @param {Number} height The height of the rectangle
 * @param {Number} [radius = 5] The corner radius; It can also be an object 
 *                 to specify different radii for corners
 * @param {Number} [radius.tl = 0] Top left
 * @param {Number} [radius.tr = 0] Top right
 * @param {Number} [radius.br = 0] Bottom right
 * @param {Number} [radius.bl = 0] Bottom left
 * @param {Boolean} [fill = false] Whether to fill the rectangle.
 * @param {Boolean} [stroke = true] Whether to stroke the rectangle.
 */
function roundRect(ctx, x, y, width, height, radius, fill, stroke) {
  if (typeof stroke == 'undefined') {
    stroke = true;
  }
  if (typeof radius === 'undefined') {
    radius = 5;
  }
  if (typeof radius === 'number') {
    radius = {tl: radius, tr: radius, br: radius, bl: radius};
  } else {
    var defaultRadius = {tl: 0, tr: 0, br: 0, bl: 0};
    for (var side in defaultRadius) {
      radius[side] = radius[side] || defaultRadius[side];
    }
  }
  ctx.beginPath();
  ctx.moveTo(x + radius.tl, y);
  ctx.lineTo(x + width - radius.tr, y);
  ctx.quadraticCurveTo(x + width, y, x + width, y + radius.tr);
  ctx.lineTo(x + width, y + height - radius.br);
  ctx.quadraticCurveTo(x + width, y + height, x + width - radius.br, y + height);
  ctx.lineTo(x + radius.bl, y + height);
  ctx.quadraticCurveTo(x, y + height, x, y + height - radius.bl);
  ctx.lineTo(x, y + radius.tl);
  ctx.quadraticCurveTo(x, y, x + radius.tl, y);
  ctx.closePath();
  if (fill) {
    ctx.fill();
  }
  if (stroke) {
    ctx.stroke();
  }

}
