package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.UserDao;
import cn.itcast.travel.dao.impl.UserDaoImpl;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.util.MailUtils;
import cn.itcast.travel.util.UuidUtil;

public class UserServiceImpl implements UserService {
    UserDao userDao =new UserDaoImpl();
    /**
     * 注册用户
     * @param user
     * @return
     */
    @Override
    public boolean regist(User user) {
        //1.根据用户名查询用户对象
        User u = userDao.findByUsername(user.getUsername());
//        System.out.println("service层"+user.getUsername());
        //判断u是否为空
        if(u!=null){
            //用户名存在，注册失败
            return false;
        }
        //2.保存用户信息
        //2.1设置激活码，唯一字符串，获得code
        user.setCode(UuidUtil.getUuid());
        //2.2设置激活状态
        user.setStatus("N");
        userDao.save(user);
        System.out.println(user.getStatus());

        //3.激活邮件发送，邮件正文？    改ip地址   为什么后面要加一串Code
//        String content="<a href='http://localhost/travel_war/activeUserServlet?code="+user.getCode()+"'>点击激活</a>";
//        String content="<a href='http://192.168.0.110/travel/user/login?code="+user.getCode()+"'>点击激活</a>";
        String content="<a href='http://localhost/travel/user/login?code="+user.getCode()+"'>点击激活</a>";

        //3个参数：发给谁，发的内容，标题
        MailUtils.sendMail(user.getEmail(),content,"邮件激活");


        
        return true;
    }


    /**
     * 激活用户
     * @param code
     * @return
     */
    @Override
    public boolean active(String code) {
        //1.根据激活码查询用户对象
        User user = userDao.findByCode(code);
        if (user!=null){
            //用户存在
            userDao.updateStatus(user);
            return true;
        }else {
            return false;
        }
    }

    /**
     * 登录方法
     * @param user
     * @return
     */
    @Override
    public User login(User user) {
        return userDao.findByUsernameAndPassword(user.getUsername(),user.getPassword());
    }

}
