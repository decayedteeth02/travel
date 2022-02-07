package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.CategoryDao;
import cn.itcast.travel.dao.impl.CategoryDaoImpl;
import cn.itcast.travel.domain.Category;
import cn.itcast.travel.service.CategoryService;
import cn.itcast.travel.util.JedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CategoryServiceImpl implements CategoryService {
    private CategoryDao categoryDao = new CategoryDaoImpl();

    @Override
    public List<Category> findAll() {
        //1.从redis查询
        //1.1获取jedis客户端
        Jedis jedis=JedisUtil.getJedis();
        //1.2使用sortedset排序查询
//        Set<String> categorys = jedis.zrange("category", 0, -1);
        //1.3查询sortedset的分数(cid)和值(cname)
        Set<Tuple> categorys = jedis.zrangeWithScores("category", 0, -1);
        List<Category> cs=null;
        //2.判断查询的集合是否为空
        if (categorys==null || categorys.size()==0){
//            System.out.println("数据查询库");
            //3.如果为空，需要从数据库查询，再将数据存入redis
            //3.1从数据库查询
            cs=categoryDao.findAll();
            //3.2将集合的数据的存储到category的key
            for (int i = 0; i < cs.size(); i++) {
                jedis.zadd("category",cs.get(i).getCid(),cs.get(i).getCname());
            }
        }else {
            //4.如果不为空，将set数据存入list
//            System.out.println("从redis中查询");
            cs=new ArrayList<Category>();
            for (Tuple tuple : categorys) {
                Category category = new Category();
                category.setCname(tuple.getElement());
                category.setCid((int) tuple.getScore());
                cs.add(category);
            }
        }
        return cs;
/**        Jedis jedis = JedisUtil.getJedis();
        //1.2sortedset排序查询
        //Set<String> categorys = jedis.zrange("category", 0, -1);
        //1.3查询sortedset中的分数(cid)和值(cname)
        Set<Tuple> categorys = jedis.zrangeWithScores("category", 0, -1);

        List<Category> cs = null;
        if (categorys==null || categorys.size()==0){
            System.out.println("数据库中查询");
            //从数据库中查询
            cs = categoryDao.findAll();
            System.out.println(cs);
            //3.2 将集合数据存储到redis中的 category的key
            for (int i = 0; i < cs.size(); i++) {
                jedis.zadd("category", cs.get(i).getCid(), cs.get(i).getCname());
            }

        }else {
            System.out.println("缓存中查询");
            cs = new ArrayList<Category>();
            for (Tuple tuple :categorys) {
                //遍历封装
                Category category = new Category();
                category.setCname(tuple.getElement());
                category.setCid((int)tuple.getScore());
                cs.add(category);
            }
        }

        //redis开不了，暂时用数据库获取数据
        List<Category> cs = categoryDao.findAll();

        //最后要获得List
        return cs;
        **/
    }
}
