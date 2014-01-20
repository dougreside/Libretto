
var MyApp_SearchResultCount = 0;


function highlightsTextAudio()
{
var range, sel;
if (window.getSelection) 
{
sel = window.getSelection();
if (sel.getRangeAt) {
range = sel.getRangeAt(0);
}
}
var newDate = new Date;
var randomnumber= newDate.getTime();
styleRange(range, "background-color:yellow; color:black;",randomnumber);
var div1 = document.getElementById(randomnumber);
var imageTag = document.createElement("img");
imageTag.id=randomnumber;
imageTag.setAttribute("src","play.png");
imageTag.setAttribute("align","left");
imageTag.setAttribute("width","30");
imageTag.setAttribute("hight","28");
var big_coordinates=getXYpos(div1);
var bp_x = big_coordinates['x'];
var bp_y = big_coordinates['y']; 
imageTag.style.left =  0;
imageTag.style.top = bp_y;
imageTag.style.position = 'absolute';
var linkTxt = document.createElement("a");
imageTag.className="NotesImage";
linkTxt.id=randomnumber;
linkTxt.setAttribute("href","audioimage:"+randomnumber);
linkTxt.appendChild(imageTag);
div1.appendChild(linkTxt);
window.HTMLOUT.getIdAudio("audioimage:"+randomnumber.toString());
return document.body.innerHTML+"<noteseparator>"+randomnumber+"<noteseparator>"+range.toString();     
    }
    
    
    function getNodePosition(node) {
    var top = left = 0;
    while (node) {        
    if (node.tagName) {
    top = top + node.offsetTop;
    left = left + node.offsetLeft;      
    node = node.offsetParent;
    } else {
    node = node.parentNode;
    }
    } 
    return [top, left];
    }
    
    function getXYpos(elem)
    {
    if (!elem)
    {
    return {"x":0,"y":0};
    }
    var xy={"x":elem.offsetLeft,"y":elem.offsetTop}
    var par=getXYpos(elem.offsetParent);
    for (var key in par)
    {       
    xy[key]+=par[key];
    }
    return xy;
    }
    
    function styleRange(range, style,randomnumber) 
    {  
    var startNode = range.startContainer.splitText(range.startOffset);       
    var endNode = range.endContainer.splitText(range.endOffset).previousSibling;
    range.setStart(startNode,0);        
    range.setEnd(endNode,endNode.length); 
    var nodes = getNodesInRange(range);      
    for (i = 0; i < nodes.length; i++)
        {
        if(nodes[i].parentNode.tagName=="A")
        {
        var span = document.createElement('span');
        span.setAttribute("style", style);
        span.id=randomnumber;
        span.className="Applestylespan";    
        span.appendChild( document.createTextNode(nodes[i].nodeValue));
        nodes[i].parentNode.replaceChild( span, nodes[i] );
        }
        else
        {
        var span = document.createElement('a');
        span.setAttribute("style", style);
        span.id=randomnumber;
        span.className="Applestylespan"
        span.setAttribute("href","highlight:"+randomnumber);
        span.appendChild( document.createTextNode(nodes[i].nodeValue));
        nodes[i].parentNode.replaceChild( span, nodes[i] );
        }
        }
        }
        function getNodesInRange(range)
        {
        var start = range.startContainer;
        var end = range.endContainer;
        var commonAncestor = range.commonAncestorContainer;
        var nodes = [];
        var node;   
        for (node = start.parentNode; node; node = node.parentNode)
        {
        if (node.nodeType == 3)
        nodes.push(node);
        if (node == commonAncestor)
        break;
        }
        nodes.reverse();    
        for (node = start; node; node = getNextNode(node))
        {
        if (node.nodeType == 3) 
        nodes.push(node);
        if (node == end)
        break;
        }    
        return nodes;
        }    
        function getNextNode(node, end)
        {
        if (node.firstChild)
        return node.firstChild;
        while (node)
        {
        if (node.nextSibling)
        return node.nextSibling;
        node = node.parentNode;
        }
        }   
        function deletetagValueAudio(nodeId)
        {     
        var elementbtn = document.getElementById(nodeId);
        while(elementbtn)
        {
        if(elementbtn.className=="Applestylespan")
        {		   
        var text = elementbtn.textContent || elementbtn.innerText;//elementbtn.innerHTML;
        //alert(text);
        var node = document.createTextNode(text);
        
        elementbtn.parentNode.replaceChild(node, elementbtn); 
        } 
        else
        {		    
        elementbtn.parentNode.removeChild(elementbtn);
        } 
        elementbtn = document.getElementById(nodeId);
        } 
        }

        
        
        
        
        function getInnerHtml() {
        
        var innerText=document.body.innerHTML;
        return innerText;
        }
        
        
        function checkPtaginrang(rangeDiv)
        {
        var arrayC=rangeDiv.childNodes;
        for(var i=0;i<arrayC.length;i++)
        {
		//alert(arrayC[i].nodeType);
        }
        }
        
        
        
        
        function selectElementContents(el) {
        var range;
        if (window.getSelection && document.createRange) {
        range = document.createRange();
        var sel = window.getSelection();
        range.selectNodeContents(el);
        sel.removeAllRanges();
        sel.addRange(range);
        } else if (document.body && document.body.createTextRange) {
        range = document.body.createTextRange();
        range.moveToElementText(el);
        range.select();
        }
        }
        
        function highlightParagraph(){
        selectElementContents(document.body);
        };
        
        function highlightsTextFromValue(range)
        {
        
        var selectionContents = range.extractContents();
        var div = document.createElement("span");
        div.style.backgroundColor = "yellow";
        div.appendChild(selectionContents);
        range.insertNode(div);
        
        }	
        
        
        
        function setSelectionRange(input, selectionStart, selectionEnd) {
        if (input.setSelectionRange) {
        input.focus();
        input.setSelectionRange(selectionStart, selectionEnd);
        }
        else if (input.createTextRange) {
        var range = input.createTextRange();
        range.collapse(true);
        range.moveEnd('character', selectionEnd);
        range.moveStart('character', selectionStart);
        range.select();
        }
        }
        
        function selectText(currentIndex, searchString) {
        //alert(currentIndex+" : "+searchString);
        var input=document.body.nodeValue;
        input.focus();
        // make case insensitive
        searchString = searchString.toLowerCase();
        var text = input.value.toLowerCase();
        
        // If selection at end - start from start
        if(currentIndex == input.value.length)
        currentIndex = 0;
        // find string
        var startIndex = text.indexOf(searchString);
        var stringToSearch = input.value;
        if (startIndex > 0) {
        // string found
        stringToSearch = stringToSearch.substr(currentIndex, stringToSearch.length);
        // try and find one after selection
        startIndex = stringToSearch.indexOf(searchString);
        if (startIndex == 0) {
        // String not found after selection - use first found index
        startIndex = text.indexOf(searchString);
        }
        else {
        // Move start index to next found occurence
        startIndex = currentIndex + startIndex;
        }
        }
        else {
        alert("'"+searchString+"' was not found.");
        }
        
        if (document.selection) {
        // IE
        input.selection = document.selection.createRange();
        input.selection.findText(searchString, text.length);
        input.selection.select();
        }
        else if(input.setSelectionRange) {
        // FF
        if (startIndex > 0) {
        setSelectionRange(input, startIndex, startIndex + searchString.length);
        }
        }
    }


        function MyApp_HighlightAllOccurencesOfStringForElement(element,keyword) {
        if (element) {
        if (element.nodeType == 3) {        // Text node
        

//        var startIdForChange;
//        startIdForChange=MyApp_SearchResultCount+1;
        
        while (true) {
        var value = element.nodeValue;  // Search for keyword in text node
        var idx = value.toLowerCase().indexOf(keyword);
        
        
        if (idx < 0) break;             // not found, abort
            
            var span = document.createElement("span");
            var text = document.createTextNode(value.substr(idx,keyword.length));
            span.appendChild(text);
            MyApp_SearchResultCount++;
            span.setAttribute("class","MyAppHighlight");
//            var idforspan='MyAppHighlight_'+MyApp_SearchResultCount;
//            span.setAttribute("id",idforspan);
            span.style.backgroundColor="yellow";
            span.style.color="black";
            text = document.createTextNode(value.substr(idx+keyword.length));
            element.deleteData(idx, value.length - idx);
            var next = element.nextSibling;
            element.parentNode.insertBefore(span, next);
            element.parentNode.insertBefore(text, next);
            element = text;
          
            ;	// update the counter
            }
           
            } else if (element.nodeType == 1) { // Element node
            if (element.style.display != "none" && element.nodeName.toLowerCase() != 'select') {
            for (var i=element.childNodes.length-1; i>=0; i--) {
            MyApp_HighlightAllOccurencesOfStringForElement(element.childNodes[i],keyword);
            }
            }
            }
            }
            }
            
            // the main entry point to start the search
            function MyApp_HighlightAllOccurencesOfString(keyword) {
            MyApp_RemoveAllHighlights();
            MyApp_HighlightAllOccurencesOfStringForElement(document.body, keyword.toLowerCase());
             changeIdForSpan();
          
            return MyApp_SearchResultCount;
            }
            
            // helper function, recursively removes the highlights in elements and their childs
            function MyApp_RemoveAllHighlightsForElement(element) {
            if (element) {
            if (element.nodeType == 1) {
            if (element.getAttribute("class") == "MyAppHighlight") {
            var text = element.removeChild(element.firstChild);
            element.parentNode.insertBefore(text,element);
            element.parentNode.removeChild(element);
            return true;
            } else {
            var normalize = false;
            for (var i=element.childNodes.length-1; i>=0; i--) {
            if (MyApp_RemoveAllHighlightsForElement(element.childNodes[i])) {
            normalize = true;
            }
            }
            if (normalize) {
            element.normalize();
            }
            }
            }
            }
            return false;
            }
            
            // the main entry point to remove the highlights
            function MyApp_RemoveAllHighlights() {
            MyApp_SearchResultCount = 0;
            MyApp_RemoveAllHighlightsForElement(document.body);
        }
            
             
           function MyApp_Highlight(element) { 
            
            var span=document.getElementById(element);
            //span.style.backgroundColor="orange";
            
            span.scrollIntoView();

            }
            
            function MyApp_DisHighlight(element) { 
            
            var span=document.getElementById(element);
            span.style.backgroundColor="yellow";
           
            }
            
            function changeIdForSpan()
            {
            
           //var arr=new Array();
            var arr=getElementsByClassName(document.body,'span','MyAppHighlight');
           
           //var arr=getElementsByClassName("MyAppHighlight", document.body );
            
             for (var i=0 ; i<arr.length; i++) {
                   var idforspan='MyAppHighlight_'+(i+1);
                
                 var span=arr[i];
                 
                   span.setAttribute("id",idforspan);
                   }
                 
            }
                   
                  
                 
            
                   function getElementsByClassName(oElm, strTagName, strClassName){
                   var arrElements = (strTagName == "*" && document.all)? document.all : oElm.getElementsByTagName(strTagName);
                   var arrReturnElements = new Array();
                   strClassName = strClassName.replace(/\-/g, "\\-");
                   var oRegExp = new RegExp("(^|\\s)" + strClassName + "(\\s|$)");
                   var oElement;
                   for(var i=0; i<arrElements.length; i++){
                   oElement = arrElements[i];
                   if(oRegExp.test(oElement.className)){
                   arrReturnElements.push(oElement);
                   }
                   }
                 
                   return (arrReturnElements)
                   }
            
          
