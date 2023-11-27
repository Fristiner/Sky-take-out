package com.sky.controller.user;

/* *
 * @packing com.sky.controller.admin
 * @author mtc
 * @date 21:14 11 27 21:14
 *
 */

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController("userShopController")
@Slf4j
@RequestMapping("/user/shop")
@Api(tags = "店铺相关接口")
public class ShopController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/status")
    @ApiOperation("获取店铺营业状态")
    public Result<Integer> getStatus() {
        Integer status = Integer.valueOf(Objects.requireNonNull(redisTemplate.opsForValue().get("SHOP_STATUS")));

        log.info("获取当前店铺的营业状态为：{}", status == 1 ? "营业中" : "打烊中");

        return Result.success(status);
    }

}
