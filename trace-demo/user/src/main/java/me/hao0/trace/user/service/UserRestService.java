package me.hao0.trace.user.service;

import javax.ws.rs.Path;

/**
 * Created by Sion on 2017/6/8 0008.
 */

@Path()
public interface UserRestService {

    String  findById(Long id);

}
