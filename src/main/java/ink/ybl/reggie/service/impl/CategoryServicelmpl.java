package ink.ybl.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ink.ybl.reggie.common.CustomException;
import ink.ybl.reggie.entity.Category;
import ink.ybl.reggie.entity.Dish;
import ink.ybl.reggie.entity.Setmeal;
import ink.ybl.reggie.mapper.CategoryMapper;
import ink.ybl.reggie.service.CategoryService;
import ink.ybl.reggie.service.DishService;
import ink.ybl.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("Category")
public class CategoryServicelmpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类
     *
     * @param id
     */
    @Override
    public void removeCategory(Long id) {

        // 如果关联了菜品，不允许删除
        LambdaQueryWrapper<Dish> queryDish = new LambdaQueryWrapper<>();
        queryDish.eq(Dish::getCategoryId, id);
        int count = dishService.count(queryDish);
        // 查询当前分类是否关联了菜品
        if (count > 0) {
            throw new CustomException("当前分类关联了菜品，不能删除");
        }
        // 查询当前分类是否关联了套餐
        LambdaQueryWrapper<Setmeal> querySetmeal = new LambdaQueryWrapper<>();
        querySetmeal.eq(Setmeal::getCategoryId, id);
        int count1 = setmealService.count(querySetmeal);
        if (count1 > 0) {
            throw new CustomException("当前分类关联了套餐，不能删除");
        }
        // 如果没有关联菜品，删除分类
        super.removeById(id);
    }
}
