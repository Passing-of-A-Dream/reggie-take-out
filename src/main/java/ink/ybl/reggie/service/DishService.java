package ink.ybl.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ink.ybl.reggie.dto.DishDto;
import ink.ybl.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    // 新增菜品，同时插入菜品口味
    public void addWithFlavor(DishDto dishDto);

    // 查询菜品，同时查询菜品口味
    public DishDto queryWithFlavor(Long id);

    // 更新菜品，同时更新菜品口味
    public void updateWithFlavor(DishDto dishDto);

    // 删除菜品，同时删除菜品口味
    public void removeById(String ids);
}
