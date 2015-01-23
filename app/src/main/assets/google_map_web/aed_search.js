var gridInfo;
//var aedArr = [];

var GridInfo = function(lat_start, lat_end, lat_unit, lng_start, lng_end, lng_unit) {
	this.lat_start = lat_start;
	this.lat_end = lat_end;
	this.lat_unit = lat_unit;
	this.lng_start = lng_start;
	this.lng_end = lng_end;
	this.lng_unit = lng_unit;
}

function str2JSON(str) {
	return JSON.parse(str);
}

function tempLoadData(coord) {
	var gridObj = str2JSON(aed_data);
	gridInfo = new GridInfo(gridObj.lat_grid_start, gridObj.lat_grid_end, gridObj.lat_grid_unit, gridObj.lng_grid_start, gridObj.lng_grid_end, gridObj.lng_grid_unit);
	var gridArr = findAllGrid(coord);
	var tempAEDArr;
	console.log("GridArr");
	console.log(gridArr[0]);
	console.log(gridArr[1]);
	console.log(gridArr[2]);
	console.log(gridArr[3]);
	//alert("GridArr L: " + gridArr.length);
	var idArr = fetchIDfromGrid(gridArr);
	console.log("ID Array: ");
	console.log(idArr);
	//alert("IdArr L: " + idArr.length);
	tempAEDArr = aedData(idArr, coord);
	//console.log(tempAEDArr[0]);
	tempAEDArr.sort(sortDist);
	return tempAEDArr;
}

function calcGridStart(coord) {
	var resultGridArr = [0, 0];
	var tempGridNum = -1;
	var gridStart = -1;
		
	tempGridNum = (coord.lat - gridInfo.lat_start) / gridInfo.lat_unit;
	
	gridNum = Math.floor(tempGridNum);
	
	gridStart = gridInfo.lat_start + gridInfo.lat_unit * gridNum;
	resultGridArr[0] = gridStart;

	tempGridNum = (coord.lng - gridInfo.lng_start) / gridInfo.lng_unit;
	
	gridNum = Math.floor(tempGridNum);
	
	gridStart = gridInfo.lng_start + gridInfo.lng_unit * gridNum;
	resultGridArr[1] = gridStart;

	return new Coord(resultGridArr[0], resultGridArr[1]);
}

function calcGridStartIndex(coord, ch) {
	var resultIndexArr = [0, 0];
	var tempGridNum = -1;
		
	tempGridNum = (coord.lat - gridInfo.lat_start) / gridInfo.lat_unit;
	resultIndexArr[0] = Math.floor(tempGridNum);
	tempGridNum = (coord.lng - gridInfo.lng_start) / gridInfo.lng_unit;
	resultIndexArr[1] = Math.floor(tempGridNum);
	
	return resultIndexArr[ch];
}

function findAllGrid(coord) {
	var tempAED = new AED(0, 0, coord, 0, 0, 0);
	var gridAnsArr = [];
	// [0] => west grid
	// [1] => east grid
	// [2] => south grid
	// [3] => north grid
	var distThreshold = tempAED.NEIGHBOR_DIST_THRESHOLD;
	var tempCoord = new Coord(coord.lat, coord.lng);
	//tempCoord = calcGridStart(tempCoord);
	//console.log(tempCoord);
	gridAnsArr.push(calcGridStart(dist2Coord(tempCoord, "west", distThreshold)));
	gridAnsArr.push(calcGridStart(dist2Coord(tempCoord, "east", distThreshold)));
	gridAnsArr.push(calcGridStart(dist2Coord(tempCoord, "south", distThreshold)));
	gridAnsArr.push(calcGridStart(dist2Coord(tempCoord, "north", distThreshold)));
	/*
	tempCoord.lng = tempCoord.lng - distThreshold;
	tempCoord.lat = tempCoord.lat - distThreshold;
	gridAnsArr.push(calcGridStart(tempCoord));
	tempCoord.lng = tempCoord.lng + distThreshold * 2;
	gridAnsArr.push(calcGridStart(tempCoord));
	tempCoord.lat = tempCoord.lat + distThreshold * 2;
	tempCoord.lng = tempCoord.lng - distThreshold * 2;
	gridAnsArr.push(calcGridStart(tempCoord));
	tempCoord.lng = tempCoord.lng + distThreshold * 2;
	gridAnsArr.push(calcGridStart(tempCoord));
	*/



	return gridAnsArr;
}

function checkEqual(first, second) {
	var thresh = 0.000001;
	if(Math.abs(first - second) < thresh)
		return true;
	else
		return false;
}

function checkSmallerEqual(first, second) {
	if(checkEqual(first, second))
		return false;
	else if(first < second)
		return true;
	else
		return false;
}

function checkBiggerEqual(first, second) {
	if(checkEqual(first, second))
		return false;
	else if(first > second)
		return true;
	else
		return false;
}

function fetchIDfromGrid(gridArr) {
	var databaseObj = str2JSON(aed_data)
	var latGridArr = databaseObj.lat_grid;
	var latGridTemp;
	var lngGridArrTemp;
	var lngGridTemp;
	var aedLocsArrTemp;
	var aedLocTemp;
	var resultIDArr = [];
	var latIndex_start = calcGridStartIndex(gridArr[2], 0);
	var latIndex_end = calcGridStartIndex(gridArr[3], 0);
	var lngIndex_start = calcGridStartIndex(gridArr[0], 1);
	var lngIndex_end = calcGridStartIndex(gridArr[1], 1);

	//console.log("Lat start: " + latIndex_start + ", Lat end: " + latIndex_end);
	//console.log("Lng start: " + lngIndex_start + ", Lng end: " + lngIndex_end);

	for(latIndex = latIndex_start; latIndex <= latIndex_end; latIndex++) {
		latGridTemp = latGridArr[latIndex];
		//console.log("Lat: " + latGridTemp.lat_start);
		//if(checkSmallerEqual(latGridTemp.lat_start, gridArr[2].lat) || checkBiggerEqual(latGridTemp.lat_start, gridArr[3].lat))
		//	continue;
		lngGridArrTemp = latGridTemp.lng_grid;
		for(lngIndex = 0; lngIndex < lngGridArrTemp.length; lngIndex++) {
			lngGridTemp = lngGridArrTemp[lngIndex];
			if(checkSmallerEqual(lngGridTemp.lng_start, gridArr[0].lng) || checkBiggerEqual(lngGridTemp.lng_start, gridArr[1].lng))
				continue;
			//console.log("   Lng: " + lngGridTemp.lng_start);
			aedLocsArrTemp = lngGridTemp.aed_locs;
			for(locIndex = 0; locIndex < aedLocsArrTemp.length; locIndex++) {
				aedLocTemp = aedLocsArrTemp[locIndex];
				resultIDArr.push(aedLocTemp.id);
			}
		}
	}

	return resultIDArr;
}

function padZeros(num) {
	var n = num;
	//console.log("Padding: " + num);
	return String("00000" + n).slice(-5); // returns 00123 
}

function aedData(idArr, coord) {
	var tempAEDInfoStr;
	var tempAEDJSON;
	var tempAEDArr = [];

	for(index = 0; index < idArr.length; index++) {
		eval("tempAEDInfoStr = aed_" + padZeros(idArr[index]) + ";" );
		//console.log("AED_Info" + tempAEDInfoStr);
		tempAEDJSON = str2JSON(tempAEDInfoStr);
		tempAEDArr.push(new AED(tempAEDJSON.id, tempAEDJSON.loc, new Coord(tempAEDJSON.lat, tempAEDJSON.lng), tempAEDJSON.addr, tempAEDJSON.img_url));
		tempAEDArr[tempAEDArr.length - 1].dist2cur = dist(coord, new Coord(tempAEDJSON.lat, tempAEDJSON.lng));
	}
	return tempAEDArr;
}

function dist2Coord(coord, dir, distant) {
	var degree;
	var resultCoord = new Coord(coord.lat, coord.lng);
	var R = 6371; //km
	//console.log(dir);
	//console.log(coord);
	switch(dir) {
		case "east":
			degree = distant / R * 180 / PI;
			resultCoord.lng = resultCoord.lng + degree;
			break;
		case "west":
			degree = distant / R * 180 / PI;
			resultCoord.lng = resultCoord.lng - degree;
			break;
		case "north":
			degree = distant / R * 180 / PI / 2;
			resultCoord.lat = resultCoord.lat + degree;
			break;
		case "south":
			degree = distant / R * 180 / PI / 2;
			resultCoord.lat = resultCoord.lat - degree;
			break;
		default:
			break;
	}
	//console.log(resultCoord);
	return resultCoord;
}

function sortDist(a, b) {
	if(a.dist2cur < b.dist2cur)
		return -1;
	else if(a.dist2cur > b.dist2cur)
		return 1;
	else
		return 0;
}