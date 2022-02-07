package cn.itcast.travel.dao;

import cn.itcast.travel.domain.Seller;

public interface SellerDao {
    //根据ID查询对象
    public Seller findById(int sid);
}
