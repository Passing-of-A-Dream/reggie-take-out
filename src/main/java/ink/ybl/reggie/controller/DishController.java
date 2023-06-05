package ink.ybl.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ink.ybl.reggie.common.R;
import ink.ybl.reggie.dto.DishDto;
import ink.ybl.reggie.entity.Category;
import ink.ybl.reggie.entity.Dish;
import ink.ybl.reggie.entity.DishFlavor;
import ink.ybl.reggie.service.CategoryService;
import ink.ybl.reggie.service.DishFlavorService;
import ink.ybl.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 分页查询菜品
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> query(int page, int pageSize, String name) {
        // 创建分页
        Page pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);

        // 构建条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 添加过滤条件
        queryWrapper.like(name!=null, Dish::getName, name);
        // 添加排序
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo, queryWrapper);

        // 对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> list = pageInfo.getRecords();
        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto); // 拷贝基本属性
            Long id = item.getCategoryId(); // 获取分类id
            Category category = categoryService.getById(id); // 根据id查询分类
            String categoryName = category.getName(); // 获取分类名称
            dishDto.setCategoryName(categoryName); // 设置分类名称
            return dishDto;
        }).collect(Collectors.toList());;

        dishDtoPage.setRecords(dishDtoList);

        return R.success(dishDtoPage);
    }

    /**
     * 添加菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> add(@RequestBody DishDto dishDto) {
        dishService.addWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 获取菜品信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> update(@PathVariable Long id) {
        DishDto dishDto = dishService.queryWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 更新菜品信息
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

    /**
     * 根据条件查询菜品
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        // 构建条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        // 查询菜品状态为1（在售）的菜品
        queryWrapper.eq(Dish::getStatus, 1);
        // 添加排序
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();

            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            // 当前菜品id
            Long dishId = item.getId();

            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
            // SQL: select * from dish_flavor where dish_id = dishId
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);

            dishDto.setFlavors(dishFlavorList);

            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dishDtoList);
    }

    /**
     * 删除菜品
     *
     * @param ids
     * @return
     */
    @DeleteMapping()
    public R<String> delected(String ids) {
        dishService.removeById(ids);
        return R.success("删除菜品成功");
    }

    /**
     * 更改菜品状态
     *
     * @param state
     * @param ids
     * @return
     */
    @PostMapping("/status/{state}")
    public R<String> stateUpdate(@PathVariable Integer state, String ids) {
        String[] idsStr = ids.toString().split(",");
        int count = 0;
        for (String id : idsStr) {
            Dish dish = dishService.getById(id);
            if (dish != null) {
                dish.setStatus(state);
                dishService.updateById(dish);
                count++;
            }
        }
        if (count == idsStr.length) {
            return R.success("更改菜品状态成功");
        }
        return R.error("更改菜品状态失败");
    }
}
