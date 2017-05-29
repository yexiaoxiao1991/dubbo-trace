package me.hao0.trace.user.service;

import com.google.common.collect.Lists;
import me.hao0.trace.user.model.Addr;
import me.hao0.trace.user.model.User;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    public User findById(Long id) {

        User user = new User();
        user.setId(id);
        user.setPasswd("");
        user.setUsername("xiaoming");

        return user;
    }

    public List<Addr> myAddrs(Long userId) {
        return Lists.newArrayList(
            new Addr(1L, userId, "Addr" + userId, "Mobile"+userId),
            new Addr(2L, userId, "Addr" + userId, "Mobile"+userId)
        );
    }

    public void delById(Long id){
        try {
            Thread.sleep(10000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
