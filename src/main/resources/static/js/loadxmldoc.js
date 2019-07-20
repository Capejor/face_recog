function loadXMLToJSON(xmlStr) 
{
	var parser=new DOMParser();  
    var xmlDoc=parser.parseFromString(xmlStr,"text/xml");
    //提取数据  
    var countrys = xmlDoc.getElementsByTagName('Error')[0].childNodes;
   
    var json = {};  
   
    for (var i = 0; i < countrys.length; i++) { 
    	if(countrys[i].nodeName=='text#'){
    		continue;
    	}
    	//console.log(countrys[i].nodeName+','+countrys[i].textContent+',');
    	json[countrys[i].nodeName]=countrys[i].textContent;  
    };
    console.log(json);
    return json;
}