/*var User = function(){*/
	
	/*this.init = function(){*/
		
		//模拟上传excel
		/* $("#uploadEventBtn").unbind("click").bind("click",function(){
			 $("#uploadEventFile").click();
		 });
		 $("#uploadEventFile").bind("change",function(){
			 $("#uploadEventPath").attr("value",$("#uploadEventFile").val());
		 });
		
	};*/
	
	$(function(){
		
		/* $("#uploadEventFile").unbind("click").bind("click",function(){
			 $("#uploadEventFile").click();
		 });*/
		 $("#uploadEventFile").bind("change",function(){
			 $("#uploadEventPath").attr("value",$(this).val());
			 // $('#button').click();
		 });
		
	 
		
		
	 
	//点击上传按钮
	uploadBtn = function(){
		var uploadEventFile = $("#uploadEventFile").val();
		if(uploadEventFile == ''){
			layer.alert("请选择vbs文件,再上传");
		}else if(uploadEventFile.lastIndexOf(".vbs")<0){//可判断以.xls和.xlsx结尾的excel
			layer.alert("只能上传vbs文件");
		}else{
			var url = webPath+ 'importAuthFile.json';
			var formData = new FormData($('#batchUpload')[0]);
			$.ajax({
				url : url,
				type : 'post',
				async:false,
				data : formData,
				success : function(data) {
					if (data.code==0) {
						layer.msg('导入成功！',{time:1000,icon:1},function(){
							location.reload();
						});
					} else {
						var msg=data.result_msg;
						layer.msg(msg,{icon:2,time:2000});
					}
			},
			error : function() {
				layer.msg("授权文件上传失败",{icon:2,time:2000});
			},
			cache : false,
			contentType : false,
			processData : false
		});
	};
	
	}
  });
 

 