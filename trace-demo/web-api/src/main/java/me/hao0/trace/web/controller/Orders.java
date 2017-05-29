package me.hao0.trace.web.controller;

import me.hao0.trace.order.dto.OrderDto;
import me.hao0.trace.user.model.Addr;
import me.hao0.trace.order.service.OrderService;
import me.hao0.trace.user.service.UserService;
import me.hao0.trace.web.dto.OrderCreateResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@RestController
@RequestMapping("/api/orders")
public class Orders {

    private static Logger log = LoggerFactory.getLogger(Orders.class);


    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public OrderCreateResp create() {

        try {
            userService.delById(1L);

            OrderDto orderDto = orderService.create(1L, 1000);

            List<Addr> addrs = userService.myAddrs(1L);

            return new OrderCreateResp(orderDto, addrs);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw  new RuntimeException("error");
        }
    }
}
