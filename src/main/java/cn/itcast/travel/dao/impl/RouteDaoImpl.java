package cn.itcast.travel.dao.impl;

import cn.itcast.travel.dao.RouteDao;
import cn.itcast.travel.domain.Route;
import cn.itcast.travel.util.JDBCUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

public class RouteDaoImpl implements RouteDao {
    private JdbcTemplate template=new JdbcTemplate(JDBCUtils.getDataSource());

    //根据分类的内容查询总记录数
    //cid:分类
    @Override
    public int findTotalCount(int cid,String rname) {
//        System.out.println("dao层："+cid);
//        String sql="select count(*) from tab_route where cid =?";
//        System.out.println(template.queryForObject(sql,Integer.class,cid));
        //1.定义sql模板
        String sql="select count(*) from tab_route where 1=1 ";
        StringBuilder sb = new StringBuilder(sql);
//        System.out.println("dao层："+rname);

        List params=new ArrayList();//条件们
        //判断参数是否有值
        if (cid != 0){
            sb.append(" and cid= ? ");
            params.add(cid);//添加？对应的值
        }
        if (rname != null && rname.length() > 0){
            sb.append(" and rname like ? ");
            params.add("%"+rname+"%");
        }
        sql=sb.toString();
//        System.out.println("dao层："+sql);
//        System.out.println("params.toArray():"+params);
//        System.out.println(template.queryForObject(sql,Integer.class,params.toArray()));
        return template.queryForObject(sql,Integer.class,params.toArray());
        //Integer.class 返回一个Integer（数字）
    }

    //查询当前页的数据集合
    @Override
    public List<Route> findByPage(int cid, int start, int pageSize,String rname) {
//        System.out.println("dao层"+cid);
//        String sql="select * from tab_route where cid =? and rname like ? limit ?,?";
//        System.out.println("dao层"+template.query(sql,new BeanPropertyRowMapper<Route>(Route.class),cid,start,pageSize));
        //定义sql
        String sql = "select * from tab_route where 1 =1 ";

        StringBuilder sb = new StringBuilder(sql);
//        System.out.println("dao层："+cid);
//        System.out.println("dao层："+rname);

        List params=new ArrayList();//条件们
        //判断参数是否有值
        if (cid != 0){
            sb.append(" and cid=? ");
            params.add(cid);//添加？对应的值
        }
        if (rname != null && rname.length()!=0){
            sb.append(" and rname like ?");
            params.add("%"+rname+"%");
        }
        sb.append("limit ? , ? ");//分页条件
        sql=sb.toString();

        params.add(start);
        params.add(pageSize);

//        System.out.println("dao层："+sql);
//        System.out.println("params.toArray():"+params);

        return template.query(sql,new BeanPropertyRowMapper<Route>(Route.class),params.toArray());

    }

    /**根据ID查询**/
    @Override
    public Route findOne(int rid) {
        String sql="select * from tab_route where rid =?";
        System.out.println("dao层findOne方法:"+template.queryForObject(sql,new BeanPropertyRowMapper<Route>(Route.class),rid));
        return template.queryForObject(sql,new BeanPropertyRowMapper<Route>(Route.class),rid);
    }
}
