function LightBulb(canvas, x, y, width, height){
	Draggable.call(this, canvas, x, y, width, height);

	this.onTime = 0;
	this.isOn = false;
}
LightBulb.prototype = new Draggable;

LightBulb.prototype.draw = function(){
	var ctx = this.canvas.ctx;
	var time = this.canvas.animationTime;

	ctx.save(); // initial state
	ctx.beginPath();

	ctx.translate(this.x, this.y);

	 if(this.isDragging){
		ctx.translate(-5,-5);
	}

	// Leafs
	ctx.save(); // leafs
	ctx.translate(19.5, 17.5);
    ctx.fillStyle = 'rgba(240,170,0,1)';
    ctx.strokeStyle = 'rgb(225,155,0)';

	var leaf = new Path2D("m 1,1 c -1.025184,0.8397 -0.424074,6.7469 1.741385,8.817 2.111395,2.0184 8.287084,2.0928 8.664637,1.3095 0.758298,-1.5734 0.524129,-7.4199 -2.034526,-9.2935 -1.715306,-1.2559 -4.290961,1.1039 -6.351779,0.5809 -1.248153,-0.3166 -1.556499,-1.0044 -2.019717,-1.4139 z");

	if(this.isOn){
		ctx.rotate(time/300);
	}
	var leafAngle = 2 * Math.PI / 9; // 40 degrees
	for(var i=0 ; i<9 ; i++){
		ctx.rotate(leafAngle);
		ctx.fill(leaf);
		ctx.stroke(leaf);
	}
	ctx.restore(); // leafs

	// Bulb
	var brightness = this.isOn ? (this.canvas.animationTime - this.onTime)/2000 + 0.3 : 0.3;
    ctx.fillStyle = 'rgba(255,255,255,'+brightness+')';
    var bulb = new Path2D("m 19.548155,31.203114 c 1.483079,0.03592 3.468213,-0.02231 4.174765,-0.630416 0.706552,-0.608106 1.002177,-2.171845 1.187794,-3.786984 0.185617,-1.615139 3.950424,-5.161364 4.107143,-9.375 0.156719,-4.213636 -4.188673,-9.2963114 -9.464286,-9.3749998 -5.275613,-0.078688 -9.394269,4.9090598 -9.375,9.3750008 0.01927,4.465941 3.783522,7.803054 4.017858,9.482777 0.234336,1.679722 0.128115,2.642736 0.959164,3.505121 0.831048,0.862386 2.909483,0.768581 4.392562,0.804501 z");
	ctx.fill(bulb);

	
	// Base
    ctx.fillStyle = '#007d8b';
	var base = new Path2D('m 19.528184,37.812612 c 0.500217,0.02531 1.44838,-0.0801 1.443642,-0.520395 -0.0047,-0.440296 -0.159622,-0.347253 -0.168997,-0.590997 -0.0094,-0.243743 2.955504,-0.451935 2.99889,-1.215339 0.04339,-0.763404 -0.791074,-0.527403 -0.820749,-0.915451 -0.02968,-0.388048 0.702833,-0.265143 0.69448,-0.947018 -0.0084,-0.681875 -0.772455,-0.480997 -0.757614,-0.947018 0.01484,-0.466021 0.833162,-0.112462 0.820749,-0.883883 -0.01241,-0.771421 -1.518936,-0.780382 -4.230014,-0.69448 -2.711078,0.0859 -4.152401,-0.330109 -4.166879,0.631345 -0.01448,0.961454 0.808678,0.744682 6.250318,1.104854 -5.543523,0.238769 -6.175554,-0.281697 -6.187184,0.662913 -0.01163,0.94461 1.249829,0.673435 6.187185,1.136422 -5.16417,0.05261 -6.056144,-0.0699 -6.092483,0.789181 -0.03634,0.859086 2.720277,0.879893 2.730569,1.207449 0.01029,0.327556 -0.168206,0.147995 -0.17362,0.65502 -0.0054,0.507025 0.97149,0.502089 1.471707,0.527397 z');
	ctx.fill(base);

	ctx.restore();
}

LightBulb.prototype.onMouseMove = function(event) {
	Draggable.prototype.onMouseMove.call(this, event);
	if(this.isDragging){
	var mouseXY = this.canvas.getMouseXY(event);
	for(var interactable of this.canvas.interactables){
		if(interactable instanceof Fan){
			var isWithin = interactable.y < mouseXY.y && interactable.y + interactable.height > mouseXY.y;
			if(!this.isOn){
				this.onTime = this.canvas.animationTime;
			}
			this.isOn = isWithin;
		}
	}
	}
}

