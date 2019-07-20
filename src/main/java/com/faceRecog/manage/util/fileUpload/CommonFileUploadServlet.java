package com.faceRecog.manage.util.fileUpload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import com.faceRecog.manage.util.CommUtil;
import com.faceRecog.manage.util.UuidUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * 通用文件上传
 *
 * @author
 */
@Component
public class CommonFileUploadServlet {

    // 静态初使化当前类
    public static CommonFileUploadServlet commonFileUploadServlet;

    //注解@PostConstruct，这样方法就会在Bean初始化之后被Spring容器执行
    @PostConstruct
    public void init() {
        commonFileUploadServlet = this;
    }


    private Logger logger = LoggerFactory.getLogger(CommonFileUploadServlet.class);

    //@SuppressWarnings("unchecked")
    public JSONObject upload(HttpServletRequest request) throws IllegalStateException, IOException, Exception {

        //获取文件名 不带扩展名
        String srcFileName;
        //浏览版路径
        String srcFilePath = "";
        //浏览版路径
        String viewImgPath = "";
        //缩略图路径
        String minImgPath = "";
        String uploadFlg = "";
        String Suffix;//尾缀
        String realName;
        String fileSize;
        String realFileSavePath;//文件真实保存路劲
        String randomName;//随机生成文件名
        //获取配置文件里的保存路径
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream("fileSave.properties");
        Map<String, String> map = CommonUtil.readSafePro(ins);

        //封装json格式的数据
        JSONObject objectResponse = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        //将当前上下文初始化给  CommonsMutipartResolver （多部分解析器）
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        //检查form中是否有enctype="multipart/form-data"
        if (multipartResolver.isMultipart(request)) {
            //将request变成多部分request
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            List<MultipartFile> files = multiRequest.getFiles("files");
            //获取multiRequest 中所有的文件名
            if (files != null && files.size() > 0) {
                for (MultipartFile file : files) {
                    //一次遍历所有文件
                    // 得到上传的文件名称，
                    if (file.getSize() / 1024 / 1024 / 1024 > 1) {
                        //返回文件名
                        objectResponse.put("bool", false);
                        objectResponse.put("result", "请上传小于1G的文件！");
                        //上传成功返回"1" 失败返回"0"
                        objectResponse.put("fileList", jsonArray);
                        return objectResponse;
                    }
                    fileSize = String.valueOf(file.getSize());
                    String fileName = file.getOriginalFilename();
                    if (fileName == null || fileName.trim().equals("")) {
                        continue;
                    }
                    //获取文件尾缀
                    String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();

                    //服务器部署路径
                    //String realPath = CommUtil.getTomcatPath();
                    String realPath = CommUtil.getJarPath();
                    // 处理获取到的上传文件的文件名的路径部分，只保留文件名部分
                    fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
                    srcFileName = fileName.substring(0, fileName.lastIndexOf("."));

                    //文件保存路径
                    String savePath = "";

                    //判断上传文件类型 1:图片  2:音频 3:视频  4:三维 5:附件 6:临时
                    String fileType = "3";

                    /**
                     * 	1	文本
                     8	PPT
                     2	网页
                     9	文件夹
                     6	PDF
                     4	音频
                     5	视频
                     3	图片
                     7	直播
                     */
                    //判断上传的是不是附件
                    switch (fileType) {
                        case "1"://文档
                            savePath = map.get("docFilePath");
                            break;
                        case "3"://图片
                            savePath = map.get("srcImgPath");
                            break;
                        case "4"://音频
                            savePath = map.get("voiceFilePath");
                            break;
                        case "5"://视频
                            savePath = map.get("movFilePath");
                            break;
                        case "6"://软件包
                            savePath = map.get("softPath");
                            break;
                        case "7"://设备端日志文件
                            savePath = map.get("equipment_log");
                            break;
                    }

                    // 判断上传的文件路径是否存在，不存在创建上传路径
                    String saveUrl = savePath;
                    savePath = realPath + savePath;
                    //保存数据库地址

                    File srcImgPath = new File(savePath);
                    if (!srcImgPath.exists() && !srcImgPath.isDirectory()) {
                        srcImgPath.mkdirs();
                    }
                    // 创建一个文件输出流 文件名重命名 上传的时间 时分秒
                    Suffix = fileName.substring(fileName.lastIndexOf("."));
                    //日期加随机码生成真实保存的文件名
                    randomName = CommonUtil.fileNameFormat() + UuidUtils.getUuid();
                    realName = fileName = randomName + fileName.substring(fileName.lastIndexOf("."));

                    realFileSavePath =saveUrl + "/" + realName;
                    saveUrl = saveUrl + File.separator + fileName;
                    file.transferTo(new File(savePath + File.separator + fileName));

                    //附件文件路径（资源上传的时候，高清原始文件路径是不存在的）
                    srcFilePath = savePath + File.separator + fileName;
                    JSONObject json = new JSONObject();
                    //返回文件名
                    json.put("fileName", srcFileName + Suffix);
                    json.put("suffix", Suffix);
                    json.put("uploadDate", CommonUtil.dateFormat_yyyy_MM_dd());
                    json.put("srcFilePath", realFileSavePath);
                    json.put("minImgPath", minImgPath);
                    json.put("viewImgPath", viewImgPath);
                    json.put("uploadFlg", uploadFlg);
                    json.put("realName", realName);
                    json.put("fileSize", fileSize);
                    json.put("fileType", fileType);


                    jsonArray.add(json);
                }
            } else {
                objectResponse.put("bool", false);
                objectResponse.put("errMsg", "文件undefiend！");
                objectResponse.put("fileList", jsonArray);
                return objectResponse;
            }
        } else {
            objectResponse.put("bool", false);
            objectResponse.put("errMsg", "contentType错误！");
            objectResponse.put("fileList", jsonArray);
            return objectResponse;
        }
        //System.out.println("==================" + jsonArray.size());
        objectResponse.put("bool", true);
        objectResponse.put("fileList", jsonArray);
        return objectResponse;
    }
}
