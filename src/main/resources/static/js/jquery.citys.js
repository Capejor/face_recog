/**
 * jquery.citys.js 1.0
 * http://jquerywidget.com
 */
var options,updateData,format,_api,$province,$city,$area;
var province,city,area,hasCity,getApi;



;(function (factory) {
    if (typeof define === "function" && (define.amd || define.cmd) && !jQuery) {
        // AMD或CMD
        define([ "jquery" ],factory);
    } else if (typeof module === 'object' && module.exports) {
        // Node/CommonJS
        module.exports = function( root, jQuery ) {
            if ( jQuery === undefined ) {
                if ( typeof window !== 'undefined' ) {
                    jQuery = require('jquery');
                } else {
                    jQuery = require('jquery')(root);
                }
            }
            factory(jQuery);
            return jQuery;
        };
    } else {
        //Browser globals
        factory(jQuery);
    }
}(function ($) {
    $.support.cors = true;
    $.fn.citys = function(parameter,getApi1) {
        if(typeof parameter == 'function'){ //重载
            getApi = parameter;
            parameter = {};
        }else{
            parameter = parameter || {};
            getApi = getApi1||function(){};
        }
        var defaults = {
            dataUrl:'http://passer-by.com/data_location/list.json',     //数据库地址
            dataType:'json',          //数据库类型:'json'或'jsonp'
            provinceField:'province', //省份字段名
            cityField:'city',         //城市字段名
            areaField:'area',         //地区字段名
            townField:'town',         //地区字段名
            valueType:'code',         //下拉框值的类型,code行政区划代码,name地名
            code:0,                   //地区编码
            province:0,               //省份,可以为地区编码或者名称
            city:0,                   //城市,可以为地区编码或者名称
            area:0,                   //地区,可以为地区编码或者名称
            town:0,                   //街道,可以为街道编码或者名称
            required: false,           //是否必须选一个
            nodata: 'hidden',         //当无数据时的表现形式:'hidden'隐藏,'disabled'禁用,为空不做任何处理
            onChange:function(){}     //地区切换时触发,回调函数传入地区数据
        };
        options = $.extend({}, defaults, parameter);
        
        //对象定义
        var $this = $(this);
        $province = $this.find('select[name="'+options.provinceField+'"]');
        $city = $this.find('select[name="'+options.cityField+'"]');
        $area = $this.find('select[name="'+options.areaField+'"]');
        $town = $this.find('select[name="'+options.townField+'"]');
        
        set();
//        $.ajax({
//            url:options.dataUrl,
//            type:'GET',
//            crossDomain: true,
//            dataType:options.dataType,
//            jsonpCallback:'jsonp_location',
//            success:function(getData){
//            	data=getData;                    
//            	
//            }
//        });
    };
}));
function set(){
	if(options.code){   //如果设置地区编码，则忽略单独设置的信息
	    var c = options.code - options.code%1e4;
	    if(data[c]){
	        options.province = c;
	    }
	    c = options.code - (options.code%1e4 ? options.code%1e2 : options.code);
	    if(data[c]){
	        options.city = c;
	    }
	    c = options.code%1e2 ? options.code : 0;
	    if(data[c]){
	        options.area = c;
	    }
	}
	//初始化
	updateData();
	format.province();
	if(options.code){
	    options.onChange(getInfo());
	}
	getApi(_api);
	
}
updateData = function(){                    	
    province = {},city={},area={};
    hasCity = false;       //判断是非有地级城市
    for(var code in data){
        if(!(code%1e4)){     //获取所有的省级行政单位
            province[code]=data[code];
            if(options.required&&!options.province){
                if(options.city&&!(options.city%1e4)){  //省未填，并判断为直辖市
                    options.province = options.city;
                }else{
                    options.province = code;
                }
            }else if(data[code].indexOf(options.province)>-1){
                options.province = isNaN(options.province)?code:options.province;
            }
        }else{
            var p = code - options.province;
            if(options.province&&p>0&&p<1e4){    //同省的城市或地区
                if(!(code%100)){
                    hasCity = true;
                    city[code]=data[code];
                    if(data[code].indexOf(options.city)>-1){
                        options.city = isNaN(options.city)?code:options.city;
                    }
                }else if(p>9000){                   //省直辖县级行政单位
                    city[code] = data[code];
                    if(data[code].indexOf(options.city)>-1){
                        options.city = isNaN(options.city)?code:options.city;
                    }
                }else if(hasCity){                  //非直辖市
                	//console.log('非直辖市:'+code+','+options.city);               	
                    var c = code-options.city;                    
                    if(options.city&&c>0&&c<100){     //同个城市的地区
                        area[code]=data[code];
                        console.log(area);
                        if(data[code].indexOf(options.area)>-1){
                            options.area = isNaN(options.area)?code:options.area;
                        }
                    }
                }else{
                    area[code]=data[code];            //直辖市
                    if(data[code].indexOf(options.area)>-1){
                        options.area = isNaN(options.area)?code:options.area;
                    }
                }
            }
        }
    }
//    console.log('hasCity:'+hasCity);
//    console.log(province);
//    console.log(city);
//    console.log(area);
};
format = {
    province:function(){
        $province.empty();
        if(!options.required){
            $province.append('<option value=""> - 请选择 - </option>');
        }
        for(var i in province){
            $province.append('<option value="'+(options.valueType=='code'?i:province[i])+'" data-code="'+i+'">'+province[i]+'</option>');
        }
        if(options.province){
            var value = options.valueType=='code'?options.province:province[options.province];
            $province.val(value);
        }
        this.city();
    },
    city:function(){
        $city.empty();
        if(!hasCity){
            $city.css('display','none');
        }else{
            $city.css('display','');
            if(!options.required){
                $city.append('<option value=""> - 请选择 - </option>');
            }
            if(options.nodata=='disabled'){
                $city.prop('disabled',$.isEmptyObject(city));
            }else if(options.nodata=='hidden'){
                $city.css('display',$.isEmptyObject(city)?'none':'');
            }
            console.log("city1:"+city);
            for(var i in city){
                $city.append('<option value="'+(options.valueType=='code'?i:city[i])+'" data-code="'+i+'">'+city[i]+'</option>');
            }
            if(options.city){
                var value = options.valueType=='code'?options.city:city[options.city];
                $city.val(value);
            }else if(options.area){
                var value = options.valueType=='code'?options.area:city[options.area];
                $city.val(value);
            }
        }
        this.area();
    },
    area:function(){
        $area.empty();
        if(!options.required){
            $area.append('<option value=""> - 请选择 - </option>');
        }
        if(options.nodata=='disabled'){
            $area.prop('disabled',$.isEmptyObject(area));
        }else if(options.nodata=='hidden'){
            $area.css('display',$.isEmptyObject(area)?'none':'');
        }
        for(var i in area){
            $area.append('<option value="'+(options.valueType=='code'?i:area[i])+'" data-code="'+i+'">'+area[i]+'</option>');
        }
        if(options.area){
            var value = options.valueType=='code'?options.area:area[options.area];
            $area.val(value);
        }
    }
};
//获取当前地理信息
getInfo = function(){
    var status = {
        direct:!hasCity,
        province:data[options.province]||'',
        city:data[options.city]||'',
        area:data[options.area]||'',
        code:options.area||options.city||options.province
    };
    return status;
};
//事件绑定
//$province.on('change',function(){
//    options.province = $(this).find('option:selected').data('code')||0; //选中节点的区划代码                        
//    options.city = 0;
//    options.area = 0;
//    updateData();
//    format.city();
//    options.onChange(_api.getInfo());
//});
//$city.on('change',function(){
//    options.city = $(this).find('option:selected').data('code')||0; //选中节点的区划代码
//    options.area = 0;
//    updateData();
//    format.area();
//    options.onChange(_api.getInfo());
//});
//$area.on('change',function(){
//    options.area = $(this).find('option:selected').data('code')||0; //选中节点的区划代码
//    options.onChange(_api.getInfo());
//});

var $town = $('#province select[name="town"]');
var townFormat = function(info) {
	$town.hide().empty();
	if (info['code'] % 1e4 && info['code'] < 7e6) { //是否为“区”且不是港澳台地区
		console.log('http://passer-by.com/data_location/town/'
					+ info['code'] + '.json');
		$.ajax({
			url : 'http://passer-by.com/data_location/town/'
					+ info['code'] + '.json',
			dataType : 'json',
			success : function(town) {
				//$town.show();
				//console.log(town);
				for (i in town) {
					$town.append('<option value="'+town[i]+'"   data-code="'+i+'">' + town[i]
							+ '</option>');
				}
				if(form!=null&&form!=undefined){
					form.render('select');
				}
				$town.find("option[value='"+options.town+"']").attr("selected","selected");
			}
		});
	}
};




