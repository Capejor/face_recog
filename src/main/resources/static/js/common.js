var popup;//弹窗index
var layer;
var laytpl;
var laypage;
var laydate;
var form;
var loadLayer;
//弹框
function open(titleText,width,height,openClass){
	popup=layer.open({
			  title: titleText,
			  type: 1,
			  area: [width,height], //宽高
			  content: openClass
       });
  }

$('.close').click(function(){
	layer.closeAll(); //关闭信息框 
	
})

//选项卡切换
function setTab(name,m,n){ 
	for( var i=1;i<=n;i++)
	{ 
		var menu = document.getElementById(name+i); 
		var showDiv = document.getElementById("cont_"+name+"_"+i); 
		menu.className = i==m ?"on":""; 
		showDiv.style.display = i==m?"block":"none"; 
	} 
}
// 检查选中的数量
var checkIds;
function checkCount(name,bool){
	checkIds=new Array();
	$("input[name='"+name+"']:checked").each(function(index){ 
		checkIds[index]=$(this).val();
	}); 
	if(checkIds.length <=0 ){
		layer.msg("请选择一项",{icon: 2});				
		return  false;
	}
	if(bool){
		if(checkIds.length >1 ){
			layer.msg("只能选择一项",{icon: 2});				
			return  false;
		}
	}
	return true;
}
Date.prototype.Format = function (fmt) { //new Date().Format("yyyy-MM-dd")
    var o = {
        "M+": this.getMonth() + 1, //月份 
        "d+": this.getDate(), //日 
        "h+": this.getHours(), //小时 
        "m+": this.getMinutes(), //分 
        "s+": this.getSeconds(), //秒 
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
        "S": this.getMilliseconds() //毫秒 
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}
String.prototype.trim=function() {
    return this.replace(/(^\s*)|(\s*$)/g,'');
}
function jsonStr(obj){
	return  JSON.stringify(obj) ;
}

//获取当前时间，格式YYYY-MM-DD
function getNowFormatDate() {
    var date = new Date();
    var seperator1 = "-";
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = year + seperator1 + month + seperator1 + strDate;
    return currentdate;
}

var loading;

//根据name截取url参数
function GetQueryString(name){
    var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    return r!=null?decodeURIComponent(r[2]):null;
//    if(r!=null)return  unescape(r[2]); return null;
}
//通用校验
function verification(){
	if(layer==null||layer==undefined){
		layui.use(['layer'], function(){			 
			  layer = layui.layer;
		});
	}
	var flag=true;
	$('.checkField').each(function(index,obj){		
		if(flag){
			var children=$(obj);
			if(flag&&(children.attr('isEmpty')!='true')&&(children.val()==null||children.val()=='')){						
				flag=false;	
				layer.msg(children.attr('tip'));
			}
			if(flag&&children.val()!=null&&children.val()!=''){
				///^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$/   /^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X)$/
				if(children.attr('regular')!=null&&children.attr('regular')!=''&&children.attr('regular')=='IDCard'){
					if(children.val().indexOf("*")>0){
					}else{
						if(!/^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$/.test(children.val())&&
								!/^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X)$/.test(children.val())){
							flag=false;
							layer.msg(children.attr('tipReg'));								
						}
					}
				}else if(children.attr('regular')!=null&&children.attr('regular')!=''&&!eval(children.attr('regular')).test(children.val())){							
					if(children.val().indexOf("*")>0 && children.attr('id') == 'branchNo'){
							
					}else{
						flag=false;
						layer.msg(children.attr('tipReg'));
					}
				}
			}
		}							
	});
	return flag;
}
/**
 * 带分页通用迭代数据方法
 * url请求参数
 * loadPage 是否加载分页
 * pageId 分页id
 * dataModel layer模版数据id
 * viewID 模版数据父node
 * parameter 查询参数
 *
 */
var boolPage=true;//是否要加载分页控件
var first=false;//第一次是否要执行
var listData;
function iterData(url,parameter,loadPage,pageId,dataModel,viewID,fun){
	listData={};
	if(layer==null||layer==undefined){
		alert('layer加载失败');
		return;
	}
	if(laytpl==null||laytpl==undefined){
		alert('laytpl加载失败');
		return;
	}
	if(laypage==null||laypage==undefined){
		layui.use(['laypage'], function(){			 
			laypage = layui.laypage;
		});
	}
	if(url==null||url==''||url==undefined){
		layer.msg("url异常，请检查！");
	}
	if(parameter==null||parameter==''||parameter==undefined){
		parameter={};
	}
	if(parameter==null||parameter==undefined){
		loadPage=false;
	}
	var index;
	try{
		loadLayer=layer.load({shade: [0.3, '#393D49']});
	}catch(e){
		
	}
	$.ajax({
		url:url,
		type:"POST",
		dataType:"JSON",
		data:parameter,
		success:function(data){	
			try{
				layer.close(loadLayer);
			}catch(e){
				
			}
			//console.log(data.ResultData);
			try{
				listData=data;
				fun(data);
			}catch(e){
				//alert(e);
			}
				var temp={};
				//
				if(data.ResultText!=null&&data.ResultText!=null){
					layer.msg(data.ResultText);
					return;
				}
				temp["list"]=(data.ResultData==null?{}:data.ResultData);
				var getTpl = dataModel.innerHTML;
				//console.log(temp);
				laytpl(getTpl).render(temp, function(html){
					//console.log(html);
					$("#"+viewID).html (html.toString());
					//viewID.innerHTML=html;
				});
				if(loadPage){
					first=false;
					boolPage=true;
				}
				var RecordCount=0;
				var count=data.pageCount;
				//console.log(count);
				var pageSize=10;
				if(parameter['pageSize']>0){
					pageSize=parameter['pageSize'];
				}
				if(count%pageSize==0){
					RecordCount=count/pageSize;
				}else{
					RecordCount=count/pageSize+1;
				}
				if(boolPage&&RecordCount>0){
				  console.log("初始化分页");
				  laypage({
				    cont: pageId
				    ,pages: RecordCount //总页数
				    ,jump: function(obj){				    	
				    	if(first){
				    		parameter['page']=obj.curr;
				    		iterData(url,parameter,false,pageId,dataModel,viewID,fun);
				    	}				    	
					}
				  });
				  first=true;
				  boolPage=false;
				}
		},
		error:function(){
			try{
				layer.close(load);
			}catch(e){}
		}
	});
	
}
/**
 * 通用保存方法
 * @param url  
 * @param formId  需要保存字段的form区域
 * @param parameter 参数
 * @param prompt 提示
 */
function saveData(url,formId,parameter,prompt,callBack){
	if(!verification()){
		return;
	}
	if(layer==null||layer==undefined){
		layui.use(['layer'], function(){			 
			  layer = layui.layer;
		});
	}
	//var pwd = hex_md5($("#userPassword").val());
	//
	var myData={};
	if(formId!=null&&formId!=undefined){
		$("#"+formId).find("input[type='text'],select,input[type='password'],input[type='hidden'],input[type='checkbox']:checked").each(function(index,obj){	
			if($(obj).attr('notLoad')==undefined||!$(obj).attr('notLoad')){
				if($(obj).val()!=null&&$(obj).val()!=""){
					//console.log($(obj)[0].tagName+','+$(obj)[0].type);
					if($(obj)[0].tagName=='INPUT'&&$(obj)[0].type=='password'){
						myData[$(obj).attr('id')]=hex_md5($(obj).val());
					}else{
						myData[$(obj).attr('id')]=$(obj).val();
					}	
				}
			}								
		});
	}
	if(parameter!=null&&parameter!=undefined){
		for(var attr in parameter){
			myData[attr]=parameter[attr];
		}
	}
	try{
		loadLayer=layer.load({shade: [0.3, '#393D49']});
	}catch(e){
		
	}
	
	$.ajax({
		url:url,
		type:"POST",
		dataType:"JSON",
		data:myData,
		success:function(data){
			try{
				layer.close(loadLayer);
			}catch(e){
				
			}
			if(data.bool){
				if(callBack != null && callBack != undefined){
					callBack(data);
				}else{
					layer.msg(prompt+'成功！',function(){
						callBack(data);
						window.location.reload();						
					});
				}
			}else{
				callBack(data);
				layer.alert(prompt+'失败！'+data.ResultText);
			}
		},
		error: function (){
			try{
				layer.close(loadLayer);
			}catch(e){}
		}
	});
}
function findData(url,parameter,fun){
	try{
		loadLayer=layer.load({shade: [0.3, '#393D49']});
	}catch(e){
		
	}
	if(url==null||url==''||url==undefined){
		layer.msg("url异常，请检查！");
	}
	if(parameter==null||parameter==''||parameter==undefined){
		parameter={};
	}	
	var tempData;
	
	$.ajax({
		url:url,
		type:"POST",
		dataType:"JSON",
		async:false,
		data:parameter,
		success:function(data){	
			try{
				layer.close(loadLayer);
			}catch(e){
				
			}
			try{	
				tempData= data;
				fun(data);
			}catch(e){
				//alert(e);
			}
			
	    },
	    error: function(){
	    	try{
				layer.close(loadLayer);
			}catch(e){}
	    }
	});
	return tempData;
}
/**
 * 
 * @param data
 * @param json
 * @param boolInput 是否输入框
 */
function fillData(data,json,boolInput){
	var bool=false;
	if(json!=null&&json!=undefined){
		bool=true;
	}
	//console.log(json.length);
	//console.log(jsonStr(json));
	//console.log(bool);
	for(var key in data){
		if($("#"+key).length>0 || $('input[name="'+key+'"]').length>0){
			//console.log(bool);
			if(bool){
				var count=true;
				for(var k in json){
					//console.log(key+','+k);
					if(k==key){
						if(data[key]!=null&&data[key]!=''){
							if(json[k]=='select'){
								console.log(key+','+data[key]);
								$("#"+key+" option[value='"+data[key]+"']").prop("selected",true);
							}else if(json[k]=='date'){
								if(boolInput==undefined||boolInput){
									$("#"+key).val(data[key]);
								}else{
									$("#"+key).text(new Date(parseInt(data[key])).Format("yyyy-MM-dd hh:mm:ss"));
								}
							}else if(json[k] == 'checkbox'||json[k] == 'radio'){
								$('input[name="'+k+'"][value="'+data[key]+'"]').prop('checked',true);
							}
							
							count=false;
							continue;
						}
						
					}
				}
				if(count){
					if(boolInput==undefined||boolInput){
						$("#"+key).val(data[key]);
					}else{
						$("#"+key).text(data[key]);
					}
				}
			}else{
				if(boolInput==undefined||boolInput){
					$("#"+key).val(data[key]);
				}else{
					$("#"+key).text(data[key]);
				}
			}
		}
	}
}

function checkInputNotIsEmpty(selected){
	var val=$('#'+selected).val();
	if(val == null || val == undefined || val == ''){
		return false;
	}
	return true;
}

/**
 * 
 * @param url	请求url
 * @param data	需要保存的数据
 * @param prompt	消息提示
 * @param callBack	执行方法
 */
function saveDataFun(url,data,prompt,callBack){
	try{
		loadLayer=layer.load({shade: [0.3, '#393D49']});
	}catch(e){
		
	}
	$.ajax({
		url:url,
		type:"POST",
		dataType:"JSON",
		data:data,
		success:function(data){
			try{
				layer.close(loadLayer);
			}catch(e){
				
			}
			try{	
				if(callBack != null && callBack != undefined){
					callBack(data);
				}else{
					if(data.bool){
						layer.msg(prompt+'成功！',{icon :1,time : 3000},function(){
							window.location.reload();
						});
					}else{
						layer.alert(prompt+'失败！',{icon :2,time : 3000},+data.ResultText);
					}
				}		
			}catch(e){
				//alert(e);
			}
	    },
	    error:function(){
	    	try{
				layer.close(loadLayer);
			}catch(e){}
	    }
	});
}
$.ajaxSetup({  
	timeout: 15000,	
    cache: false,     
    contentType:"application/x-www-form-urlencoded;charset=utf-8",   
	error: function (jqXHR, textStatus, errorMsg) { 
		if(loading!=null&&loading!=undefined){
			layer.close(loading);
		}
		try{
			layer.msg("请求失败!错误信息:"+errorMsg,{icon:5});
		}catch(e){
			alert("请求失败!错误信息:"+errorMsg);
		}
		
	},
    complete:function(XHR,textStatus){     
    	// 超时,status还有success,error等值的情况
    	if(textStatus=='timeout'){
      　　　　　	
      　　　　　		try{
      　　　　　			layer.msg("请求超时",{icon:5},function(){
       　　　　　	  　　　　　layer.closeAll();
       　　　　　	  		});
       　　　　　	  　　}catch(e){
       　　　　　	  　　alert("请求超时");	
       　　　　　	  　　}
       　　　　　	
      　　　	}else{
      　　　		var resText = XHR.responseText;  
      　　      if(resText=='ajaxLoginFail'){ 
      　           	window.location.href="login.html";
      　        	}else if(resText=='ajaxSessionTimeOutJson'){ 
      　        	    window.location.href="login.html";
      　        	}else if(resText=='ajaxNoPermission'){
      　        		//window.location.href="404.html";
      　        			layer.msg("当前用户没有操作权限");
      　        		}
      　　　}
        
    }   
 
}); 


