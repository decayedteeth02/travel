package cn.itcast.travel.service;

public interface FavoriteService {
    public boolean isFavorite(String rid,int uid);

    /**添加收藏**/
    public void add(String rid, int uid);
}
