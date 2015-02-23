var PI = 3.1415926;

var Coord = function(lat, lng){
	this.lat = lat;
	this.lng = lng;
};

var AED = function(id, location, coord, addr, imgUrlArr){
	this.id = id;
	this.location = location;
	this.coord = coord;
	this.addr = addr;
	this.imgUrlArr = imgUrlArr;
	this.NEIGHBOR_DIST_THRESHOLD = 1; //2 km
	this.dist2cur = -1;
};

AED.prototype.isNeighbor = function(coord){
	var distance = dist(this.coord, coord);
	return distance <= this.NEIGHBOR_DIST_THRESHOLD;
};

function degToRad(deg){
    return deg * PI / 180;
}

function dist(coord1, coord2){
    
	var R = 6371; //km
	var dLat = degToRad(coord2.lat - coord1.lat);
	var dLon = degToRad(coord2.lng - coord1.lng);
	var lat1 = degToRad(coord1.lat);
	var lat2 = degToRad(coord2.lat);
    
	var a = Math.sin(dLat/2) * Math.sin(dLat/2) + 
			Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2);

	var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    
	return R*c;
}

function createAedTestObject(index){
	//test object for AED
	var locationStr = "Location " + index;
	var coord = new Coord(25.0911+index*0.01, 121.5598+index*0.01);
	var addr = "Address " + index;
	var imgUrlArr = ["img_" + index + "_1.jpg", "img_" + index + "_2.jpg"];
	return new AED(index,locationStr,coord,addr,imgUrlArr);
}