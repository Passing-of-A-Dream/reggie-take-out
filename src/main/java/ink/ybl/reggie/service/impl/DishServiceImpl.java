package ink.ybl.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ink.ybl.reggie.dto.DishDto;
import ink.ybl.reggie.entity.Dish;
import ink.ybl.reggie.entity.DishFlavor;
import ink.ybl.reggie.mapper.DishMapper;
import ink.ybl.reggie.service.DishFlavorService;
import ink.ybl.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service("Dish")
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 新增菜品，同时插入菜品口味
     * @param dishDto
     */
    @Transactional
    public void addWithFlavor(DishDto dishDto) {
        // 保存菜品的基本信息
        this.save(dishDto);

        Long id = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.forEach(dishFlavor -> {
            dishFlavor.setDishId(id);
        });
        // 保存菜品口味
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 查询菜品，同时查询菜品口味
     * @param id
     * @return
     */

    public DishDto queryWithFlavor(Long id) {
        // 查询菜品基本信息
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        // 查询菜品口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        // 更新菜品基本信息
        this.updateById(dishDto);

        // 清理原来的菜品口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        // 添加当前提交的菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.forEach(dishFlavor -> {
            dishFlavor.setDishId(dishDto.getId());
        });
        dishFlavorService.saveBatch(flavors);
    }

    @Transactional
    public void removeById(String ids) {
        // 切割ids
        String[] idArray = ids.split(",");
        // 循环修改菜品和口味的isDeleted为1
        for (String id : idArray) {
            // 修改菜品
            Dish dish = new Dish();
            dish.setId(Long.parseLong(id));
            dish.setIsDeleted(1);
            this.updateById(dish);
            // 修改口味
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId, Long.parseLong(id));
            List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper);
            dishFlavors.forEach(dishFlavor -> {
                dishFlavor.setIsDeleted(1);
            });
            dishFlavorService.updateBatchById(dishFlavors);
//            dishFlavorService.upda
        }
    }
}
