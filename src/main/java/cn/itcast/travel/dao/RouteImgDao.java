package cn.itcast.travel.dao;

import cn.itcast.travel.domain.Route;
import cn.itcast.travel.domain.RouteImg;

import java.util.List;

public interface RouteImgDao {
    //根据ID去查询图片
    public List<RouteImg> findByRid(int rid);
}
