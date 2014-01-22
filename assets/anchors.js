/*function reportChapters(){
	if (appScrollManager){
	ChapterHeads = [];
	$(".NavDiv>.head").each(function(k){
		ChapterHeads.push({"text":$(this).text(),"id":$(this).attr("id")});
	});
	stringHeads = JSON.stringify(ChapterHeads);
	appScrollManager.reportHeads(stringHeads);
	}
	return;
}*/
function scrollToElement(anch){
console.log("ANCH "+anch);

if ((anch)&&((anch!="undefined")&&(anch.id))){

anchor = anch.id;

	if ($("#"+anchor.trim()).offset()){
    pos= parseFloat($("#"+anchor).offset().top)-50;
   
   $('body').first().scrollTop(pos);
   }
   else{
   console.log(anchor+" does not exist in this version");
   }
   }
   }

anchors = [];


$(document).ready(function(){

	if (appScrollManager){
scrollposition = appScrollManager.getScrollPosition();
if (scrollposition!=undefined){

scrollToElement({"id":""+scrollposition});
}
}


$(".NavDiv").each(function(k,v){

if ($("#"+v.id).offset()){

	anchors.push({"id":v.id,"pos":""+$("#"+v.id).offset().top,"sp":[]});
	
$("#"+v.id).find(".sp").each(function(key,val){
	if ($("#"+val.id).offset()){

	anchors[anchors.length-1]["sp"].push({"id":val.id,"pos":""+$("#"+val.id).offset().top});
	}
});
anchors[anchors.length-1]["sp"].push({"id":"end","pos":""+$("body").first().height()});
}

});
	anchors.push({"id":"end","pos":""+$("body").first().height(),"sp":[]});

	$(window).on('scroll',function(){
	
	 clearTimeout($.data(this, 'scrollTimer'));
    $.data(this, 'scrollTimer', setTimeout(function() {
    // Scroll timer idea comes from:
    //http://stackoverflow.com/questions/9144560/jquery-scroll-detect-when-user-stops-scrolling
	    curPos = "";
		console.log($('body').first().scrollTop()+"-----"+parseFloat($(window).height()+$("body").first().scrollTop()));
		for (n=0;n<anchors.length-1;n++){
			if(anchors[n]["pos"]){
			   if 
				(
				(parseFloat(anchors[n]["pos"])
				<
				($(window).height()+$("body").first().scrollTop())
				)
				&&
				(parseFloat(anchors[n+1]["pos"])
				>
				$("body").first().scrollTop()))
				{
				console.log("NAVDIV: "+anchors[n]["id"]);
				console.log(JSON.stringify(anchors[n]["sp"]));
				for (m=0;m<anchors[n]["sp"].length-1;m++){
				
					if 
					(
					//(parseFloat(anchors[n]["sp"][parseInt(m+1)]["pos"])>(parseFloat($("body").first().scrollTop())))&&
				(parseFloat(anchors[n]["sp"][m]["pos"])>parseFloat($("body").first().scrollTop()))
				)
				{	
				console.log("CAUGHT IT: "+anchors[n]["sp"][m]["id"]);
				curPos=anchors[n]["sp"][m]["id"]+"";
				break;}
				else{
					console.log("Between "+anchors[n]["sp"][parseInt(m)]["pos"]+" "+anchors[n]["sp"][parseInt(m+1)]["pos"]);
				}
				
				}
				
			
			
			
		}
			
			
		}
	
		}
			if (appScrollManager){
			if ((curPos!="null") && (curPos!=undefined)){
			console.log("SET SCROLL TO "+curPos);
			
						appScrollManager.setScrollPosition(curPos)
						}
						}
						
				
				        console.log("Haven't scrolled in 250ms!");
    }, 250));
				
				});
	
	$(".sp").click(function(e){

		curPos=$(this).attr("id");
		
		appScrollManager.setScrollPosition(curPos)
		
	});
	
		});
