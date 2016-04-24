function Fan(canvas, x, y, width, height, speed, distance, direction){
	Interactable.call(this,canvas, x, y, width, height);
	
	this.speed = speed;
	this.distance = distance;
	this.direction = direction;
	
	this.color = 'steelBlue';
	
	this.windLines = [this.makeWindLine(),this.makeWindLine(),this.makeWindLine(),this.makeWindLine(),this.makeWindLine()];
}

Fan.prototype = new Interactable;

Fan.prototype.makeWindLine = function(offset){
		return {
			length: Math.floor(this.windShape.length * (Math.random()*0.3+0.7)),
			segments: Math.floor((this.speed*4)+2),
			y: Math.random() * 20,
			timeOffset: Math.floor(Math.random()*1000),
			isFlipped: Math.random() < 0.5
		}
}

Fan.prototype.windShape = [[0,0,0,0,23, 118],
		  [34, 115, 43, 105, 57, 100],
		  [71, 95, 98, 85, 113, 89],
		  [127, 93, 146, 104, 157, 111],
		  [170, 119, 186, 133, 200, 140],
		  [213, 147, 238, 152, 250, 151],
		  [265, 150, 287, 135, 300, 127],
		  [313, 119, 327, 107, 338, 95],
		  [348, 84, 371, 61, 385, 53],
		  [398, 46, 430, 37, 450, 40],
		  [465, 42, 492, 53, 505, 64],
		  [516, 74, 527, 91, 532, 108],
		  [536, 122, 520, 154, 507, 165],
//		  [496, 175, 455, 179, 440, 171],
//		  [427, 164, 407, 146, 406, 131],
//		  [405, 113, 412, 100, 431, 90],
//		  [445, 83, 466, 92, 474, 102],
//		  [483, 114, 476, 127, 465, 133]
];

Fan.prototype.draw = function(){
	var time = this.canvas.animationTime*this.speed;
	var ctx = this.canvas.ctx;

	ctx.save(); //initial state
	ctx.beginPath();
	
	ctx.translate(this.x+this.width/2, this.y+this.height/2);

	ctx.save(); // label
	ctx.font = 40*this.speed+"px sans-serif";
	ctx.fillStyle = "black";
	ctx.textAlign = "center";
	ctx.textBaseline = 'middle';
	ctx.fillText("F",0, this.height/20);
	ctx.restore(); // labeCl

	ctx.rotate(this.direction);
	ctx.translate(this.width/-2, this.height/-2);
	
	ctx.save(); //blades
	ctx.translate(this.width*1.5, this.height/2);
    ctx.fillStyle = this.color;

	var blade = new Path2D("m 1,1 c -1.025184,0.8397 -0.424074,6.7469 1.741385,8.817 2.111395,2.0184 8.287084,2.0928 8.664637,1.3095 0.758298,-1.5734 0.524129,-7.4199 -2.034526,-9.2935 -1.715306,-1.2559 -4.290961,1.1039 -6.351779,0.5809 -1.248153,-0.3166 -1.556499,-1.0044 -2.019717,-1.4139 z");
	var rotation = time / 100 % (2 * Math.PI);
	var bladeAngleRelative = 2 * Math.PI / 5; //40 degrees between blades
	var bladeAngleAbsolute, scale;
	for(var i=0 ; i<5 ; i++){
		ctx.save(); //each blade
		bladeAngleAbsolute = (bladeAngleRelative*i)+rotation;
		ctx.rotate(bladeAngleAbsolute);
		scale = ((Math.sin(bladeAngleAbsolute-1)+3)/2);
		ctx.scale(scale,scale);
		ctx.fill(blade);
		ctx.restore(); //each blade
	}
	ctx.restore(); //blades
	
	ctx.save(); //wind
	ctx.lineWidth = 10;
	ctx.strokeStyle = 'white';
	for(var i=0 ; i<3 ; i++){
		var expired = this.drawWind(time, this.windLines[i]);
		if(expired){
			this.windLines[i] = this.makeWindLine();
		}
	}
	ctx.restore(); //wind
	
	ctx.restore();//initial state
}

Fan.prototype.drawWind = function(time, windLine){
	var ctx = this.canvas.ctx;
	
	var t = time + windLine.timeOffset;
	
	ctx.save(); //each wind line
	ctx.beginPath();

	var w = this.windShape;
	var segments = windLine.segments;
	var firstPoint = Math.floor(t / 35 ) % windLine.length + 1;
	var lastPoint = Math.min(firstPoint + segments, windLine.length);
	if(windLine.isFlipped){
		ctx.scale(1, -1);
		ctx.translate(40, -1*windLine.y);
	}else{
		ctx.translate(40, windLine.y);
	}
	ctx.scale((firstPoint+10)/this.distance, (firstPoint+10)/200);
	
	ctx.moveTo(w[firstPoint-1][4], w[firstPoint-1][5]);
	for(var i = firstPoint ; i<lastPoint ; i++){
		ctx.bezierCurveTo(w[i][0], w[i][1],w[i][2], w[i][3],w[i][4], w[i][5]);
	}
	ctx.strokeStyle = "rgba(240,240,255,"+firstPoint/windLine.length+")"
	ctx.stroke();
	
	ctx.restore(); //each wind line
	
	return firstPoint === lastPoint;
}
