/*
 * F-Spot Web Sharing Gallery Scripts
 * Author: Anton Keks <anton@azib.net>
 */

var photos;
var missing_thumbs;
var thumb_size = screen.width <= 1280 ? 160 : screen.width <= 1600 ? 192 : 224;
var current_photo_id;
var editable_tag;

function $(id) {
	return document.getElementById(id);
}

function generateThumbs() {
	missing_thumbs = {};
	var html = '';
	var prev_id = null;
	for (var id in photos) {
		if (prev_id) photos[prev_id].next_id = id;
		photos[id].prev_id = prev_id;
		prev_id = id;				
		missing_thumbs[id] = true;
		html += '<a href="javascript:showPhoto(' + id + ')"><img id="thumb_' + id + '" alt="' + photos[id].name + '" src="ui/empty.png" class="horizontal" onload="changeClass(this)"/></a>';
	}
	document.getElementById('thumbs').innerHTML = html;
}

function loadThumb(img) {
	img.src = img.id.replace(/thumb_/, '/thumb/');
}

function changeClass(img) {
	if (img.height > img.width)
		img.className = 'vertical';
}

function loadVisibleThumbs() {
	var visibleTop, visibleBottom;
	if (window.innerHeight) {
		visibleTop = window.pageYOffset;
		visibleBottom = visibleTop + window.innerHeight;
	} else if (document.documentElement) {
		visibleTop = document.documentElement.scrollTop;
		visibleBottom = visibleTop + document.documentElement.clientHeight;
	}
	visibleTop -= 128; visibleBottom += 128;

	for (var id in missing_thumbs) {
		var img = $('thumb_' + id);
		if (img.offsetTop >= visibleTop && img.offsetTop <= visibleBottom) {
			loadThumb(img);
			delete missing_thumbs[id];
		}
	}
}

function largerThumbs() {
	switch (thumb_size) {
		case 128: thumb_size = 160; break;
		case 160: thumb_size = 192; break;
		case 192: thumb_size = 224; break;
		default: thumb_size = 256;
	}
	resizeThumbs();
}

function smallerThumbs() {
	switch (thumb_size) {
		case 256: thumb_size = 224; break;
		case 224: thumb_size = 192; break;
		case 192: thumb_size = 160; break;		
		default: thumb_size = 128;
	}
	resizeThumbs();
}

function resizeThumbs() {
	var cssRules = document.styleSheets[0].cssRules;
	if (!cssRules)
		cssRules = document.styleSheets[0].rules;
	// div.thumbs img.horizontal
	cssRules[3].style.width = thumb_size + 'px';
	// div.thumbs img.vertical
	cssRules[4].style.height = thumb_size + 'px';
	// div.thumbs a
	cssRules[5].style.width = cssRules[5].style.height = cssRules[5].style.lineHeight = (thumb_size + 5) + 'px';	
	loadVisibleThumbs();
}

function scalePhoto(event) {
	var vHeight, vWidth;
	if (window.innerHeight) {
		vHeight = window.innerHeight;
		vWidth = window.innerWidth;
	}
	else if (document.documentElement) {
		vHeight = document.documentElement.clientHeight;
		vWidth = document.documentElement.clientWidth;
	}
	vHeight -= 100;
	vWidth -= 50;

	var img = $('photo_img');
	if (img.width > vWidth || img.height > vHeight || (event && event.type == 'resize')) {
		if (vWidth / vHeight > img.width / img.height)
			img.height = vHeight;
		else
			img.width = vWidth;
	}
	
	img.style.display = 'inline';
}

function showThumbs() {
	$('photo').style.display = 'none';
	$('thumbs').style.display = 'block';
	$('navi_photo').style.display = 'none';
	$('navi_thumbs').style.display = 'block';
	$('photo_img').src = 'ui/empty.png';
	window.onresize = window.onscroll = loadVisibleThumbs;
	window.onkeydown = null;
	loadVisibleThumbs();
	if (current_photo_id)
		window.scroll(0, $('thumb_' + current_photo_id).offsetTop - 150);
}

function showPhoto(id) {
	current_photo_id = id;
	var photo = photos[id];
	$('thumbs').style.display = 'none';
	$('navi_photo').style.display = 'inline';
	$('navi_thumbs').style.display = 'none';
	
	$('description').innerHTML = photo.description;
	showPhotoTags(photo);
	
	var img = $('photo_img');
	img.style.display = 'none';
	img.removeAttribute("width");
	img.removeAttribute("height");
	img.src = '/photo/' + id;
	img.title = photo.name + ' (' + photo.version + ')';
	
	$('photo').style.display = 'block';
	window.onresize = scalePhoto;
	window.onscroll = null;	
	window.onkeydown = handleKeys;
	
	if (photo.next_id) {
		var preload = new Image();
		preload.src = '/photo/' + photo.next_id;
	}
}

function nextPhoto() {
	var next_id = photos[current_photo_id].next_id;
	if (next_id)
		showPhoto(next_id);
}

function prevPhoto() {
	var prev_id = photos[current_photo_id].prev_id;
	if (prev_id)
		showPhoto(prev_id);
}

function handleKeys(e) {
	switch (e.keyCode) {
		case 27 /* esc */:
			showThumbs();
			return true;
		case 37 /* left */:
			prevPhoto();
			return true;
		case 39 /* right */:
			nextPhoto();
			return true;
	}
}

function showPhotoTags(photo) {
	$('tags').innerHTML = photo.tags;
	if (editable_tag) {
		$('edit_tag').innerHTML = "<b>" + (hasTag(editable_tag) ? "-" : "+") + "</b> " + editable_tag;
	}
}

function hasTag(tag_name) {
	var tags = photos[current_photo_id].tags;
	var pos = tags.indexOf(editable_tag);
	var end = pos + editable_tag.length;
	return pos >= 0 && (pos == 0 || tags.charAt(pos-1) == ' ') && (end == tags.length || tags.charAt(end) == ',');
}

function addRemoveTag() {
	if (!editable_tag)
		return;
	
	var http = new XMLHttpRequest();
	http.open('GET', '/tag/' + (hasTag(editable_tag) ? "remove" : "add") + "/" + current_photo_id + "/" + editable_tag);
	http.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			photos[current_photo_id].tags = this.responseText.replace(/[\r\n]/g, "");
			showPhotoTags(photos[current_photo_id]);
		}
		else if (this.readyState == 4 && this.status != 200) {
			alert("Failed to update tags, Gallery is inactive?");
		}
	};
	http.send("");
}

function galleryPing() {
	var http = new XMLHttpRequest();
	http.open('GET', '/ping');
	http.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			setTimeout(galleryPing, 5000);
		}
		else if (this.readyState == 4 && this.status != 200) {
			$('title').style.display = 'none';
			$('offline').style.display = 'block';
		}
	};
	http.send("");
}

setTimeout(galleryPing, 10000);
