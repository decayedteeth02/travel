package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.PageBean;
import cn.itcast.travel.domain.Route;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.FavoriteService;
import cn.itcast.travel.service.RouteService;
import cn.itcast.travel.service.impl.FavoriteServiceImpl;
import cn.itcast.travel.service.impl.RouteServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/route/*")
public class RouteServlet extends BaseServlet {
    private RouteService service=new RouteServiceImpl();
    private FavoriteService favoriteService=new FavoriteServiceImpl();

    /**页面列表展示功能**/
    public void pageQuery(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1.接受参数
        String currentPageStr = req.getParameter("currentPage");//当前页码
        String pageSizeStr = req.getParameter("pageSize");//每页显示的条数
        String cidStr = req.getParameter("cid");//类别

        String rname=req.getParameter("rname");//接受线路名称
//        System.out.println("servlet:"+rname);
//        rname = new String(rname.getBytes("iso-8859-1"),"utf-8");
//        System.out.println("servlet:"+rname);

        //2.处理参数
        int cid=0;//类别的ID
        if (cidStr !=null && cidStr.length() > 0){
            cid=Integer.parseInt(cidStr);
        }
        int currentPage=0;//当前页码，如果不传递，赋值当前页码为1
        if (currentPageStr !=null && currentPageStr.length() > 0){
            currentPage=Integer.parseInt(currentPageStr);
        }else { //
            currentPage=1;
        }
        int pageSize=0; //每页显示条数 如果不传递，默认每页显示5条记录
        if (pageSizeStr !=null && pageSizeStr.length() > 0){
            pageSize=Integer.parseInt(pageSizeStr);
        }else { //
            pageSize=5;
        }
        //3.调用service查询PageBean对象
//        System.out.println("servlet:"+cid);
//        System.out.println("servlet:"+rname);
//        System.out.println("servlet:"+currentPage);
//        System.out.println("servlet:"+pageSize);
        PageBean<Route> pb = service.pageQuery(cid, currentPage, pageSize,rname);

        //4.将pageBean对象序列化为json
        writeValue(pb,resp);

    }
    /** 根据ID查询一个旅游线路的详细信息 **/
    public void findOne(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1.接收参数ID
        String rid = req.getParameter("rid");
        //2.调用service查询route对象
        Route route=service.findOne(rid);
        //3.转化为json
        writeValue(route,resp);
    }
    /** 判断当前用户是否收藏过该路线 **/
    public void idFavorite(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1.接收参数ID
        String rid = req.getParameter("rid");
        //2.获取登录用户user
        User user = (User) req.getSession().getAttribute("user");
        int uid;
        if (user ==null){
            //用户尚未登录
            uid=0;
        }else {
//            用户已经登录
            uid=user.getUid();
        }
        //调用FavoriteService是否收藏
        boolean flag=favoriteService.isFavorite(rid,uid);

        //3.转化为json
        writeValue(flag,resp);
    }
    /** 添加收藏 **/
    public void addFavorite(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1.获取线路rid
        String rid = req.getParameter("rid");
        //2.获取当前登录的用户
        User user = (User) req.getSession().getAttribute("user");
        System.out.println(user.getName());
        int uid;//用户id
        if (user ==null){
            //用户未登录
            return;
        }else {
            //用户已经登录
            uid=user.getUid();
        }
        //3.调用service增加
        favoriteService.add(rid,uid);

    }
}
