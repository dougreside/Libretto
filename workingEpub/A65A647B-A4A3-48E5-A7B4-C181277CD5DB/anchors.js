function scrollToElement(anch){

console.log(anch.id);
anchor = anch.id;
console.log("Gotcha "+anchor);
if ((anchor)&&((anchor!="undefined")&&(anchor.length>0))){
    pos= parseFloat($("#"+anchor).offset().top)-50;
    console.log("GO TO: "+pos);
   $('body').first().scrollTop(pos);
   }
   }

anchors = [];


$(document).ready(function(){

	if (appScrollManager){
scrollposition = appScrollManager.getScrollPosition();
if (scrollposition!=undefined){
console.log("Picked up "+scrollposition);
console.log(scrollposition);
scrollToElement({"id":scrollposition});
}
}

console.log("BUILDING ANCHORS %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
$(".sp").each(function(key,val){
	anchors.push(val.id);
});
	$(window).on('scroll',function(){
	
	 clearTimeout($.data(this, 'scrollTimer'));
    $.data(this, 'scrollTimer', setTimeout(function() {
    // Scroll timer idea comes from:
    //http://stackoverflow.com/questions/9144560/jquery-scroll-detect-when-user-stops-scrolling
	    curPos = "";
		console.log($('body').first().scrollTop()+"-----"+$(window).height()+$("body").first().scrollTop());
		for (n in anchors){
			if($("#"+anchors[n]).offset()){
			    
				if (($("#"+anchors[n]).offset().top<($(window).height()+$("body").first().scrollTop()))&&
				($("#"+anchors[n]).offset().top>$("body").first().scrollTop())
				)
				{
				console.log("CAUGHT IT: "+anchors[n]);
				curPos=anchors[n]+"";
				break;
			
			
		}
			
			
		}
	
		}
			if (appScrollManager){
			if ((curPos!=null) && (curPos!=undefined)){
			console.log("SET SCROLL TO "+curPos);
			
						appScrollManager.setScrollPosition(curPos)
						}
						}
						
				
				        console.log("Haven't scrolled in 250ms!");
    }, 250));
				
				});
		});

