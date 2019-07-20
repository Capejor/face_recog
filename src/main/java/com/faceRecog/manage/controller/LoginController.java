package com.faceRecog.manage.controller;

import com.faceRecog.manage.model.User;
import com.faceRecog.manage.util.CommUtil;
import com.faceRecog.manage.util.ImageUtil;
import com.faceRecog.manage.util.Result;
import net.sf.json.JSONObject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author Capejor
 * @className: LoginController
 * @Description: TODO
 * @date 2019-06-06 10:52
 */
@RestController
public class LoginController {

	private Logger logger = LoggerFactory.getLogger(LoginController.class);


    /**
     * @Description 用户登录
     * @Author Capejor
     * @Date 2019-06-06 16:56
     **/
    @RequestMapping("/login")
    public String login (HttpSession session, @RequestParam String userName,
                         @RequestParam(value = "password", required = false) String password,
                         HttpServletRequest request, RedirectAttributes attr) throws Exception{
        JSONObject jsonObject = new JSONObject();
        // 如果登录失败从request中获取认证异常信息，shiroLoginFailure就是shiro异常类的全限定名
        // 根据shiro返回的异常类路径判断，抛出指定异常信息
        String exceptionClassName = (String) request.getAttribute("shiroLoginFailure");
        if (exceptionClassName != null) {
            //model.addAttribute("shiroLoginFailure", exceptionClassName);
            if("org.apache.shiro.authc.UnknownAccountException".equals(exceptionClassName)){
                jsonObject.put("code", 1);
                jsonObject.put("msg", "账号不存在");
            }else if("org.apache.shiro.authc.IncorrectCredentialsException".equals(exceptionClassName)){
                jsonObject.put("code",1);
                jsonObject.put("msg", "密码错误");
            }else if("com.etv.manage.exception.UserLoggedOutException".equals(exceptionClassName)){
                jsonObject.put("code", 1);
                jsonObject.put("msg", "账号已冻结");
            }else{
                jsonObject.put("code", 1);
                jsonObject.put("msg", "登入错误");
            }
            return jsonObject.toString();//重定向
        }

        // 创建一个Subject实例，该实例认证要使用上边创建的securityManager进行
        Subject subject = SecurityUtils.getSubject();

        // 创建token令牌，记录用户认证的身份和凭证即账号和密码
        UsernamePasswordToken token = new UsernamePasswordToken(userName,
        		password);

        // 用户登陆
        try {
            subject.login(token);
        } catch (org.apache.shiro.authc.UnknownAccountException e) {
            //result=Result.responseError("登入错误UnknownAccountException");
            logger.error("用户登录!",e);
            jsonObject.put("code", 1);
            jsonObject.put("msg", "账号不存在！");
            logger.error("登入错误",e);
            return jsonObject.toString();//重定向
        }catch (org.apache.shiro.authc.IncorrectCredentialsException o) {
            jsonObject.put("code", 1);
            jsonObject.put("msg", "密码错误");
            return jsonObject.toString();//重定向
        } catch (Exception a) {
            a.printStackTrace();
            logger.error("用户登录!",a);
            jsonObject.put("code", 1);
            jsonObject.put("msg", "登入错误");
            logger.error("登入错误",a);
            return jsonObject.toString();//重定向

        }
        User user=(User) SecurityUtils.getSubject().getPrincipal();
        // 用户认证状态
        //Boolean isAuthenticated = subject.isAuthenticated();
        //System.out.println("用户认证状态----------：" + isAuthenticated);

        // 用户退出
        //subject.logout();
        // isAuthenticated = subject.isAuthenticated();
        // System.out.println("用户认证状态：" + isAuthenticated);
        //System.out.println("用户认证状态：");
        jsonObject.put("code", 0);
        //jsonObject.put("role", user.getPsRoId());
        jsonObject.put("msg", "登入成功");//30ebb962-0048-4ca2-b7b6-696d778feaa3
        jsonObject.put("token", (String) subject.getSession().getId());
        jsonObject.put("data",JSONObject.fromObject("{'username':'"+userName+"'}"));
        return jsonObject.toString();//重定向
        // return "forward:/index.jsp";

    }
    //生成验证码图片
    @RequestMapping("/valicode.htm") //对应/user/valicode.do请求
    public void valicode(HttpServletResponse response, HttpSession session) {
        //利用图片工具生成图片
        //第一个参数是生成的验证码，第二个参数是生成的图片
        response.setHeader("Cache-Control", "no-cache");
        Object[] objs = ImageUtil.createImage();
        //将验证码存入Session
        session.setAttribute("validateCode",objs[0]);
        //将图片输出给浏览器
        BufferedImage image = (BufferedImage) objs[1];
        response.setContentType("image/png");
        try{
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        }catch(Exception e){
            e.printStackTrace();
            logger.error("生成验证码图片  !",e);
        }

    }


    /**
     * @Description 用户退出
     * @Author Capejor
     * @Date 2019-06-14 17:15
     **/
    @RequestMapping("/logout")
    public Result logout(HttpSession session ) {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return Result.responseSuccess("退出成功！！！");
    }



    public static void main(String[] args) {//678fe4ae1b442af07fb83050751b7ea5  97f473de5cabe794642a501b83a72268
        String salt = "yskj";
        String password_md5_sale_1 = new Md5Hash("1", ByteSource.Util.bytes(salt), 1).toString();
        System.out.println(password_md5_sale_1);
        System.out.println(CommUtil.randomString(16));

        System.out.println(new SimpleDateFormat("yyyyMMdd").format(new Date()));
    }
}
