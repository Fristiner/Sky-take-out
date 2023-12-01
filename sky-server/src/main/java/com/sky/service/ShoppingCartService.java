package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

/* *
 * @packing com.sky.service
 * @author mtc
 * @date 10:09 12 01 10:09
 *
 */
public interface ShoppingCartService {
    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查看购物车
     *
     * @return
     */
    List<ShoppingCart> showShoppingCart();


    void cleanShoppingCart();


    void delete(ShoppingCartDTO shoppingCartDTO);
}
