package cn.itcast.travel.dao;

import cn.itcast.travel.domain.Favorite;

public interface FavoriteDao {
    public Favorite findByRidAndUid(int rid, int uid);

    /**根据线路id查询收藏次数**/
    public int findCountByRid(int rid);

    /**添加收藏**/
    public void add(int i, int uid);
}
