package ink.ybl.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import ink.ybl.reggie.common.BaseContext;
import ink.ybl.reggie.common.R;
import ink.ybl.reggie.entity.ShoppingCart;
import ink.ybl.reggie.service.ShoppingCartService;
import javafx.util.converter.LocalDateTimeStringConverter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/shoppingCart")
@Log4j2
public class ShoppingCartControll {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        List<ShoppingCart> list = shoppingCartService.list();
        return R.success(list);
    }

    /**
     * 添加购物车
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> Add(@RequestBody ShoppingCart shoppingCart) {

        // 设置用户id
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        // 查询套餐是否已点
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);

        if (dishId != null) {
            // 添加菜品
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            // 添加套餐
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        // SQL: select * from shopping_cart where user_id = ? and dish_id/setmeal_id = ?
        ShoppingCart shoppingCartServiceOne = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);

        // 如果购物车里存在该订单，并且该订单口味与提交的订单相同，则数量加一
        if (shoppingCartServiceOne != null && Objects.equals(shoppingCartServiceOne.getDishFlavor(), shoppingCart.getDishFlavor())) {
            Integer number = shoppingCartServiceOne.getNumber();
            shoppingCartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(shoppingCartServiceOne);
        } else {
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            shoppingCartServiceOne = shoppingCart;
        }

        return R.success(shoppingCartServiceOne);
    }

    @PostMapping("/sub")
    public R<ShoppingCart> Sub(@RequestBody ShoppingCart shoppingCart) {
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (dishId != null) {
            lambdaQueryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId, setmealId);
        }
        ShoppingCart serviceOne = shoppingCartService.getOne(lambdaQueryWrapper);
        Integer number = serviceOne.getNumber();
        if (number != 1) {
            serviceOne.setNumber(number - 1);
            shoppingCartService.updateById(serviceOne);
            return R.success(serviceOne);
        } else {
            shoppingCartService.removeById(serviceOne);
            shoppingCart.setNumber(0);
        }
        return R.success(shoppingCart);
    }

    @DeleteMapping("/clean")
    public R<String> clean() {

//        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();

        List<ShoppingCart> shoppingCarts = shoppingCartService.list();

        shoppingCarts.stream().forEach(item -> {
            shoppingCartService.removeById(item.getId());
        });

        return R.success("清除成功");
    }
}
