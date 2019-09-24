package com.acxiom.ams.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import com.acxiom.ams.mapper.util.MD5Util;


@Configuration
public class MyInterceptor extends WebMvcConfigurerAdapter {

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    
    HandlerInterceptor inter = new HandlerInterceptor() {

      @Override
      public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
          Object handler) throws Exception {
        String Md5password=MD5Util.MD5Encode(request.getParameter("sftpPassword"), "UTF-8");
        return true;
      }

      @Override
      public void postHandle(HttpServletRequest request, HttpServletResponse response,
          Object handler, ModelAndView modelAndView) throws Exception {
        
      }

      @Override
      public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
          Object handler, Exception ex) throws Exception {
        
      }
      
      
      
      
      
      
      
    };
    registry.addInterceptor(inter).addPathPatterns("/**");
    
  }

}
