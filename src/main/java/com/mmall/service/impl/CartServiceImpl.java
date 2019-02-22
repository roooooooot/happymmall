package com.mmall.service.impl;

import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.pojo.Cart;
import com.mmall.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    public ServerResponse add(Integer userId, Integer productId, Integer count) {
        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
    }
}
