package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.ResultInfo;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;

import javax.mail.Session;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@WebServlet("/user/*")
public class UserServlet extends BaseServlet {
    //UserService成员变量
    private UserService service=new UserServiceImpl();

    /**注册servlet**/
    public void regist(HttpServletRequest request,HttpServletResponse response) throws IOException {
        //验证码校验
        //前台获取
        String check = request.getParameter("check");
        //后台验证码生成
        HttpSession session = request.getSession();
        String checkcode_server = (String) session.getAttribute("CHECKCODE_SERVER");
        session.removeAttribute("CHECKCODE_SERVER");//保证验证码只使用一次
        if (checkcode_server == null || !checkcode_server.equalsIgnoreCase(check)){
            //验证码错误
            ResultInfo info = new ResultInfo();
            //注册失败
            info.setFlag(false);
            info.setErrorMsg("验证码错误！");
            //序列化为json，发回前台
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(info);
            response.setContentType("application/json;charset=utf-8");
            //write发送字符串
            response.getWriter().write(json);
            return;
        }
        //一次获取一个集合
        //1.获取数据  serualize方法把表单的所有数据提交，username=zhangsan&password=123 用map提取
        Map<String, String[]> map = request.getParameterMap();
        //2.封装对象
        User user = new User();
        try {
            BeanUtils.populate(user,map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //3.调用service完成注册
//        UserService service = new UserServiceImpl();
        boolean flag = service.regist(user);

        //info是给前台data的数据
        ResultInfo info = new ResultInfo();
        //4.响应结果
        if (flag){
            //注册成功
            info.setFlag(true);
        }else {
            //注册失败
            info.setFlag(false);
            info.setErrorMsg("注册失败！");
        }

        //将info对象序列化为json
        String json = writeValueAsString(info);
//        ObjectMapper mapper = new ObjectMapper();
//        String json = mapper.writeValueAsString(info);

        //将json数据写回客户端,         字符串用response.getWriter().write(json)
        //设置content-type,           返回json写这个response.setContentType("application/json;charset=utf-8");
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(json);
    }

    /**登录servlet**/
    public void login(HttpServletRequest request,HttpServletResponse response) throws IOException {
        //验证码校验
        //前台获取
        String check = request.getParameter("check");
        //后台获取验证码生成
        HttpSession session = request.getSession();
        //获得后台验证码
        String checkcode_server = (String) session.getAttribute("CHECKCODE_SERVER");
        //验证码只使用一次
        session.removeAttribute("CHECKCODE_SERVER");
        if (checkcode_server == null || !checkcode_server.equalsIgnoreCase(check)){
            //验证码错误
            ResultInfo info = new ResultInfo();
            info.setFlag(false);
            info.setErrorMsg("验证码错误");
            //序列化为json
            ObjectMapper mapper = new ObjectMapper();
            String json=mapper.writeValueAsString(info);
            response.setContentType("application/json;charset=utf-8");
            //发回给前台
            response.getWriter().write(json);
            return;
        }

        //1.获取用户名和密码
        Map<String, String[]> map = request.getParameterMap();
        //2.封装user对象
        User user = new User();
        try {
            BeanUtils.populate(user,map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //3.调用Userservive
//        UserService service=new UserServiceImpl();
        User u=service.login(user);
        HttpSession session1=request.getSession();
        session1.setAttribute("u",u);
//        System.out.println(u.getName());//李四

        ResultInfo info=new ResultInfo();
        //4.判断用户是否存在
        if (u == null){
            //用户名密码错误
            info.setFlag(false);
            info.setErrorMsg("用户名密码错误");
        }
        //用户是否激活
        if (!u.getStatus().equals("Y")&& u!=null){
            //用户未激活
            info.setFlag(false);
            info.setErrorMsg("用户尚未激活");
        }
        //判断登录成功
        if (u.getStatus().equals("Y")&&u!=null){
            //登录成功
            info.setFlag(true);
            System.out.println("登录成功");
        }

        //响应数据
        writeValue(info,response);
//        ObjectMapper mapper = new ObjectMapper();
//        response.setContentType("application/json;charset=utf-8");
//        mapper.writeValue(response.getOutputStream(),info);
    }
    /** 查询用户 **/
    public void findOne(HttpServletRequest request,HttpServletResponse response) throws IOException {

        //登录的时候放了session
        Object user = request.getSession().getAttribute("user");
//        System.out.println(user);//null

        //将user写回客户端  ,session传mapper.writeValue(response.getOutputStream(),user);
        // 对象转换为JSON
        writeValue(user,response);

//        ObjectMapper mapper = new ObjectMapper();
//        response.setContentType("application/json;charset=utf-8");
//        mapper.writeValue(response.getOutputStream(),user);
    }

    /**退出方法**/
    public void exit(HttpServletRequest request,HttpServletResponse response) throws IOException {
        //1.销毁session
        request.getSession().invalidate();

        //2.跳转到登录界面
        response.sendRedirect(request.getContextPath()+"/index.html");
    }

    /** 激活方法 **/
    public void active(HttpServletRequest request,HttpServletResponse response) throws IOException {
        //1.获取激活码
        String code = request.getParameter("code");
        if ( code!=null ){
            //2.调用service激活
//            UserService service=new UserServiceImpl();
            boolean flag= service.active(code);

            //3.判断标记
            String msg=null;
            if (flag){
                //激活成功
                msg="激活成功，请<a href='login.html'></a>登录";
            }else {
                //激活失败
                msg="激活失败，联系管理员";
            }
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write(msg);
        }
    }

}




