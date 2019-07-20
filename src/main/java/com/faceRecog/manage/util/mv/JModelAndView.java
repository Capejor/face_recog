package com.faceRecog.manage.util.mv;

import com.faceRecog.manage.util.CommUtil;
import com.faceRecog.manage.util.StringUtilsCus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JModelAndView extends ModelAndView
{
  public JModelAndView(String viewName)
  {
    if (("login".equals(viewName)) || ("index".equals(viewName)))
      super.setViewName(viewName);
    else
      super.setViewName("/templates/" + viewName);
  }

  public JModelAndView(String viewName, HttpServletRequest request, HttpServletResponse response)
  {
    String contextPath = "/".equals(request.getContextPath()) ? "" : request.getContextPath();
    String webPath = CommUtil.getURL(request);
    if ((!CommUtil.generic_domain(request).equals("localhost")) && 
      (!StringUtilsCus.ipCheck(CommUtil.generic_domain(request)))) {
      webPath = contextPath;
    }

    if (("login".equals(viewName)) || ("index".equals(viewName)) || ("home".equals(viewName)))
      super.setViewName(viewName);
    else {
      super.setViewName(viewName);
    }
    super.addObject("webPath", webPath + "/");

    String queryUrl = "";
    if ((request.getQueryString() != null) && (!request.getQueryString().equals(""))) {
      queryUrl = "?" + request.getQueryString();
    }
    super.addObject("current_url", request.getRequestURI() + queryUrl);
  }
}