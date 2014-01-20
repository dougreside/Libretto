function scrollToElement(id) 
{
	console.log("THIS IS THE ID: "+id);
    var elem = document.getElementById(id);
    var x = 0;
    var y = 0;
    while (elem != null) {
        x += elem.offsetLeft;
        y += elem.offsetTop;
        elem = elem.offsetParent;
    }
    window.scrollTo(x, y);
}