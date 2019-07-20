
accessid = ''
accesskey = ''
host = ''
policyBase64 = ''
signature = ''
callbackbody = ''
filename = ''
key = ''
expire = 0
g_object_name = ''
g_object_name_type = ''
now = timestamp = Date.parse(new Date()) / 1000; 
var imgUrl='';

function send_request()
{
    var xmlhttp = null;
    if (window.XMLHttpRequest)
    {
        xmlhttp=new XMLHttpRequest();
    }
    else if (window.ActiveXObject)
    {
        xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
    }
  
    if (xmlhttp!=null)
    {
        serverUrl = 'PostObjectPolicy'
        xmlhttp.open( "GET", serverUrl, false );
        xmlhttp.send( null );
        return xmlhttp.responseText
    }
    else
    {
        alert("Your browser does not support XMLHTTP.");
    }
};

function check_object_radio() {
    var tt = document.getElementsByName('myradio');
    for (var i = 0; i < tt.length ; i++ )
    {
        if(tt[i].checked)
        {
            g_object_name_type = tt[i].value;
            break;
        }
    }
}

function get_signature()
{
    //可以判断当前expire是否超过了当前时间,如果超过了当前时间,就重新取一下.3s 做为缓冲
    now = timestamp = Date.parse(new Date()) / 1000; 
    if (expire < now + 3)
    {
        body = send_request()
        var obj = eval ("(" + body + ")");
        host = obj['host']
        policyBase64 = obj['policy']
        accessid = obj['accessid']
        signature = obj['signature']
        expire = parseInt(obj['expire'])
        callbackbody = obj['callback'] 
        key = obj['dir']
        imgUrl=obj['imgUrl']
        console.log("imgUrl:"+imgUrl);
        return true;
    }    
    return false;
};

function random_string(len) {
　　len = len || 32;
　　var chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678';   
　　var maxPos = chars.length;
　　var pwd = '';
　　for (i = 0; i < len; i++) {
    　　pwd += chars.charAt(Math.floor(Math.random() * maxPos));
    }
    return pwd;
}

function get_suffix(filename) {
    pos = filename.lastIndexOf('.')
    suffix = ''
    if (pos != -1) {
        suffix = filename.substring(pos)
    }
    return suffix;
}

function calculate_object_name(filename)
{
   
        g_object_name += "${filename}"
   
    return ''
}

function get_uploaded_object_name(filename)
{
     tmp_name = g_object_name
        tmp_name = tmp_name.replace("${filename}", filename);
        return tmp_name
    
}

function set_upload_param(up, filename, ret)
{
//	debugger;
    if (ret == false)
    {
        ret = get_signature()
    }
    g_object_name = key;
    if (filename != '') {
        suffix = get_suffix(filename)
        calculate_object_name(filename)
    }
    
    new_multipart_params = {
        'key' : g_object_name,
        'policy': policyBase64,
        'OSSAccessKeyId': accessid, 
        'success_action_status' : '200', //让服务端返回200,不然，默认会返回204
        'callback' : callbackbody,
        'signature': signature,
    };

    up.setOption({
        'url': host,
        'multipart_params': new_multipart_params
    });

    up.start();
}

var uploader = new plupload.Uploader({
	runtimes : 'html5,flash,silverlight,html4',
	browse_button : 'selectfiles', 
    //multi_selection: false,
	container: document.getElementById('container'),
	flash_swf_url : 'lib/plupload-2.1.2/js/Moxie.swf',
	silverlight_xap_url : 'lib/plupload-2.1.2/js/Moxie.xap',
    url : 'http://oss.aliyuncs.com',

    filters: {
        mime_types : [ //只允许上传图片和zip,rar文件
        { title : "Image files", extensions : "jpg,png,bmp" }
//        , 
//        { title : "Zip files", extensions : "zip,rar" }
        ],
        max_file_size : '10mb', //最大只能上传10mb的文件
        prevent_duplicates : true //不允许选取重复文件
    },

	init: {
		PostInit: function() {
			//document.getElementById('ossfile').innerHTML = '';
			document.getElementById('postfiles').onclick = function() {
            set_upload_param(uploader, '', false);
            return false;
			};
		},

		FilesAdded: function(up, files) {
			//console.log(up.files);
			plupload.each(files, function(file) {
//				document.getElementById('ossfile').innerHTML += '<div id="' + file.id + '">' + file.name + ' (' + plupload.formatSize(file.size) + ')<b></b>'
//				+'<div class="progress"><div class="progress-bar" style="width: 0%"></div></div>'
//				+'</div>';
					
				if(uploadType=='singleUp'){
					var html=
						 '<div class="upload-img-items" id="' + file.id + '">'+
				         '<img src="'+webPath+'img/login-bg.png">'+
				         '<div class="block-bg"></div>'+
				         '<div class="layui-progress img-progress" lay-showpercent="true" lay-filter="' + file.id + '">'+
				         '<div class="layui-progress-bar layui-bg-red"  lay-percent="0%"></div>'+
				         '</div>'+
				         '<div class="upload-info">'+plupload.formatSize(file.size)+'</div>'+
				         '</div>';
					$("#ossfile").html(html);
				}else{
					var html=
						 '<div class="upload-img-items" id="' + file.id + '">'+
				         '<img src="'+webPath+'img/login-bg.png">'+
				         '<div class="block-bg"></div>'+
				         '<div class="upload-close"><i class="layui-icon">&#x1007;</i></div>'+
				         '<div class="layui-progress img-progress" lay-showpercent="true" lay-filter="' + file.id + '">'+
				         '<div class="layui-progress-bar layui-bg-red"  lay-percent="0%"></div>'+
				         '</div>'+
				         '<div class="upload-info">'+plupload.formatSize(file.size)+'</div>'+
				         '</div>';
					$("#ossfile").append(html);
				}
			});
		},

		BeforeUpload: function(up, file) {
//            check_object_radio();
            set_upload_param(up, file.name, true);
            
        },

		UploadProgress: function(up, file) {
			try{
				element.progress(file.id, file.percent+'%');
			}catch(e){
				console.log(e);
			}
//			var d = document.getElementById(file.id);
//			d.getElementsByTagName('b')[0].innerHTML = '<span>' + file.percent + "%</span>";
//            var prog = d.getElementsByTagName('div')[0];
//			var progBar = prog.getElementsByTagName('div')[0]
//			progBar.style.width= 2*file.percent+'px';
//			progBar.setAttribute('aria-valuenow', file.percent);
		},

		FileUploaded: function(up, file, info) {
            if (info.status == 200)
            {
            	jsonFiles.push(file);
            	$("#"+file.id+" img").attr('src',imgUrl+file.name);
                //document.getElementById(file.id).getElementsByTagName('b')[0].innerHTML = 'upload to oss success, object name:' + get_uploaded_object_name(file.name);
            	if(uploadType=='singleUp'){
            		$.ajax({
            			url:'updatePersonerPic.json',
            			data:{'picUr':jsonFiles[0].name},
            			dataType:'json',
            			type:'post',
            			success:function(data){
            				layer.closeAll();
            				if (data.bool) {
            					layer.msg('修改成功！', {icon : 1,time : 1000},function(){
            						$('#img', parent.document).attr("src",''+jsonFiles[0].name==''||jsonFiles[0].name==null?"http://t.cn/RCzsdCq":"http://aptmgmtsystem.oss-cn-beijing.aliyuncs.com/user-dir"+jsonFiles[0].name+'');
            					});	 
            				} else {
            					layer.msg('修改失败！', {icon : 2,time : 2000});
            				}
            			}
            		});
            	}
            }
            else
            {
                //document.getElementById(file.id).getElementsByTagName('b')[0].innerHTML = info.response;
                //document.getElementById('console').appendChild(document.createTextNode(info.response));
                var str = info.response;//"<?xml version='1.0' encoding='utf-8'?> <movies> <movie> <name>辩护人</name> <country>韩国</country> </movie> <movie> <name>V字仇杀队</name> <country>美国</country> </movie> <movie> <name>盗梦空间</name> <country>美国</country> </movie> </movies>";   
                //创建文档对象  
                var parser=new DOMParser();  
                var xmlDoc=parser.parseFromString(str,"text/xml");  
               
                //提取数据  
                var countrys = xmlDoc.getElementsByTagName('country');  
               
                var arr = [];  
               
                for (var i = 0; i < countrys.length; i++) {  
                    arr.push(countrys[i].textContent);  
                };  
                console.log(arr);  
            } 
		},

		Error: function(up, err) {
            if (err.code == -600) {
                document.getElementById('console').appendChild(document.createTextNode("\n选择的文件太大了,可以根据应用情况，在upload.js 设置一下上传的最大大小"));
            }
            else if (err.code == -601) {
                document.getElementById('console').appendChild(document.createTextNode("\n选择的文件后缀不对,可以根据应用情况，在upload.js进行设置可允许的上传文件类型"));
            }
            else if (err.code == -602) {
                document.getElementById('console').appendChild(document.createTextNode("\n该文件已选择"));
            }
            else 
            {           
            	var parser=new DOMParser();  
                var xmlDoc=parser.parseFromString(err.response,"text/xml");
                console.log(xmlDoc);
                //提取数据  
                var countrys = xmlDoc.getElementsByTagName('Error')[0].childNodes;
               
                var json = {};  
               
                for (var i = 0; i < countrys.length; i++) { 
                	if(countrys[i].nodeName=='text#'){
                		continue;
                	}
                	//console.log(countrys[i].nodeName+','+countrys[i].textContent+',');
                	json[countrys[i].nodeName]=countrys[i].textContent;  
                	document.getElementById('console').appendChild(document.createTextNode("\n" +countrys[i].nodeName+':'+ countrys[i].textContent));
                };
            }
		}
	}
});

uploader.init();

