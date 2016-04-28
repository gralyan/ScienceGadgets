function BowlingAlley(canvas, x, y, width, height, ballRadius, lineToFollow){
	Interactable.call(this, canvas, x, y, width, height);
	
	this.lineToFollow = lineToFollow;
	this.ballRadius = ballRadius;

}
BowlingAlley.prototype = new Interactable;

BowlingAlley.pinShape = new Path2D('m 5,40 c 0,0 -2.087632,-7.50782 -1.699689,-10.60586 0.387942,-3.09803 3.754587,-5.61769 4.042643,-6.65949 0.288056,-1.0418 -4.549038,-6.64408 1.008354,-6.63769 5.557392,0.006 0.879641,5.55099 1.093982,6.63769 0.214341,1.08671 3.654701,3.56146 4.042643,6.65949 0.387943,3.09804 -1.699689,10.60586 -1.699689,10.60586 -2.30836,1.08477 -4.294374,1.39071 -6.788244,0 z');

BowlingAlley.prototype.draw = function(){
	var ctx = this.canvas.ctx;
	var time = this.canvas.animationTime;
	
	ctx.save(); // initial state
	ctx.beginPath();
	
	ctx.translate(this.x, this.y);

	// Background
	ctx.fillStyle = this.lineToFollow.color;
	ctx.fillRect(0,0,this.width,this.height);
	ctx.fillStyle = "black";
	ctx.fillRect(0,this.height-43,this.width,3);
	
	//Pins
	ctx.save(); // Pins
	ctx.translate(0, 40);

	ctx.strokeStyle = "black";
	ctx.lineWidth = 1;
	ctx.fillStyle = "white";
	for(var i = 0 ; i<4 ; i++){
		for(var j = 0 ; j<=3-i ; j++){
			if(this.lineToFollow.curX > this.lineToFollow.points[2]){
				ctx.rotate(1);
			}
			ctx.fill(BowlingAlley.pinShape);
			ctx.stroke(BowlingAlley.pinShape);
			ctx.translate(12,0);
		}
		ctx.translate(-12*j+6,5);
	}
	
	ctx.restore(); // Pins

	// Ball
	ctx.arc(this.width/2, this.height - 40 + this.ballRadius - this.lineToFollow.curY, this.ballRadius, 0, 2 * Math.PI);
	ctx.fill();
	

	ctx.restore(); // initial state
}
