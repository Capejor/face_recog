package com.faceRecog.manage.util;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public class Result implements Serializable{
	
	private static final long serialVersionUID = 5696370942787226169L;

	private int code;

    private String msg;

    private Object data;
    
    public Result() {
    	
    }
    
    public Result(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
    

    @Override
    public String toString() {
       return JSONObject.toJSONString(this);
    }
    
    
    public static Result responseSuccess(){
        return responseSuccess("","");
    }
    public static Result responseSuccess(String msg, Object data){
        Result Result=new Result();
        Result.setCode(0);
        Result.setData(data);
        Result.setMsg(msg);
        return Result;
    }

    public static Result responseSuccess(String msg){
        return responseSuccess(msg,"");
    }

    public static Result responseError(String msg, Object data){
        Result Result=new Result();
        Result.setCode(1);
        Result.setData(data);
        Result.setMsg(msg);
        return Result;
    }

    public static Result responseArgsError(){
        return responseError("参数错误","");
    }

    public static Result responseError(){
        return responseError("","");
    }

    public static Result responseError(String msg){
        return responseError(msg,"");
    }
    
    public static Result responseFlie(String msg, Object data){
        Result Result=new Result();
        Result.setCode(2);
        Result.setData(data);
        Result.setMsg(msg);
        return Result;
    }
    
    public static Result responseFile(){
        return responseFlie("","");
    }

    public static Result responseFile(String msg){
        return responseFlie(msg,"");
    }
   
    
}
