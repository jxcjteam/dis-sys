package cn.ds.test;

import cn.ds.service.UserService;
import cn.ds.TestUtil;
import cn.ds.model.User;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * UserServiceTest
 *
 * @author zj
 * @date 2016/9/3
 */
public class UserServiceTest extends TestUtil{

    @Resource
    private UserService userService;

    @Test
    public void testGetUser(){
        long uid = 35957l;
        User user = userService.getUser(uid);
        System.out.println(user.toJsonString());
    }

}
