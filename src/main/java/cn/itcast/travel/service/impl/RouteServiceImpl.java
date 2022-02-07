package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.FavoriteDao;
import cn.itcast.travel.dao.RouteDao;
import cn.itcast.travel.dao.RouteImgDao;
import cn.itcast.travel.dao.SellerDao;
import cn.itcast.travel.dao.impl.FavoriteDaoImpl;
import cn.itcast.travel.dao.impl.RouteDaoImpl;
import cn.itcast.travel.dao.impl.RouteImgDaoImpl;
import cn.itcast.travel.dao.impl.SellerDaoImpl;
import cn.itcast.travel.domain.PageBean;
import cn.itcast.travel.domain.Route;
import cn.itcast.travel.domain.RouteImg;
import cn.itcast.travel.domain.Seller;
import cn.itcast.travel.service.RouteService;

import java.util.List;

public class RouteServiceImpl implements RouteService {
    private RouteDao routeDao = new RouteDaoImpl();
    private RouteImgDao routeImgDao=new RouteImgDaoImpl();
    private SellerDao sellerDao=new SellerDaoImpl();
    private FavoriteDao favoriteDao=new FavoriteDaoImpl();
    @Override
    public PageBean<Route> pageQuery(int cid, int currentPage, int pageSize,String rname) {
        //封装pageBean
        PageBean<Route> pb = new PageBean<Route>();
        //设置当前页码
        pb.setCurrentPage(currentPage);
        //设置最大显示的条数
        pb.setPageSize(pageSize);

        //总的记录数
//        System.out.println("service" + cid);
//        System.out.println("service" + rname);
//        System.out.println("service" + currentPage);
//        System.out.println("service" + pageSize);
        int totalCount = routeDao.findTotalCount(cid,rname);
//        System.out.println("service:totalCount" + totalCount);
//        System.out.println("service层的totalCount" + totalCount);
        pb.setTotalCount(totalCount);
        //设置当前页显示集合
        int start=(currentPage-1)*pageSize;//开始的记录数
//        System.out.println("service层的start"+start);
        List<Route> list=routeDao.findByPage(cid,start,pageSize,rname);
//        System.out.println("service层的list"+list);
        pb.setList(list);

        //设置总页数 = 总的记录数/每页显示条数
        int totalPage=totalCount % pageSize ==0 ? totalCount / pageSize :(totalCount/pageSize)+1;
        pb.setTotalPage(totalPage);

        return pb;
    }

    /**根据id查询**/
    @Override
    public Route findOne(String rid) {
        //1.根据ID去route表中查询数据
        Route route=routeDao.findOne(Integer.parseInt(rid));
        //2.根据ID去查询图片的信息
        List<RouteImg> routeImgList = routeImgDao.findByRid(route.getRid());
        //2.2将集合设置到Route对象
        route.setRouteImgList(routeImgList);
        //3.根据route的sid查询卖家信息
        Seller seller = sellerDao.findById(route.getSid());
        route.setSeller(seller);

        //4.查询收藏次数
        int count=favoriteDao.findCountByRid(route.getRid());
        route.setCount(count);

        return route;
    }
}
