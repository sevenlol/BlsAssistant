var INDEX_MAX = 3;
var AED_ICON_PATH = "icon/aed/aed-32.png";

function createInfoWindowTestArr(indexMax){
	var infoWindowTestArr = [];
	for (var i=0;i<indexMax;i++)
		infoWindowTestArr.push(createInfoWindowTestObject(i));
	return infoWindowTestArr;
}

function createInfoWindowTestObject(index){
	var contentStr = "<h1>Title " + index + "</h1>";
	var infoWindow = new google.maps.InfoWindow({
		content: contentStr
	});
	return infoWindow;
}

function createMarkerTestArr(indexMax){
	var markerTestArr = [];
	for (var i=0;i<indexMax;i++)
		markerTestArr.push(createMarkerTestObject(i));

	return markerTestArr;
}

function createMarkerTestObject(index){
	var latLng = new google.maps.LatLng(25.0911+index*0.01, 121.5598+index*0.01);
	var marker = new google.maps.Marker({
		position: latLng,
		map: null,
		title: "title " + index,
		icon: AED_ICON_PATH
	});
	return marker;
}