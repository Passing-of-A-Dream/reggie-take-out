package ink.ybl.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ink.ybl.reggie.dto.DishDto;
import ink.ybl.reggie.dto.SetmealDishDto;
import ink.ybl.reggie.dto.SetmealDto;
import ink.ybl.reggie.entity.*;
import ink.ybl.reggie.service.CategoryService;
import ink.ybl.reggie.service.DishService;
import ink.ybl.reggie.service.SetmealDishService;
import ink.ybl.reggie.service.SetmealService;
import ink.ybl.reggie.common.R;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@Log4j2
@RestController
@RequestMapping("/setmeal")
public class SetmealControll {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private DishService dishService;

    /**
     * 新增套餐，同时保存与套餐关联的菜品信息
     *
     * @param setmealDto
     */
    @PostMapping
    public R<String> saveSetmeal(@RequestBody SetmealDto setmealDto) {
        log.info(setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 分页查询套餐
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> query(int page, int pageSize, String name) {
        // 创建分页
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> pageDto = new Page<>();

        // 构建条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        // 添加过滤条件
        queryWrapper.like(name != null, Setmeal::getName, name);
        // 添加排序
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo, queryWrapper);

        // 对象拷贝
        BeanUtils.copyProperties(pageInfo, pageDto, "records");
        // 获取套餐列表
        List<Setmeal> records = pageInfo.getRecords();

        // 转换为套餐DTO列表
        List<SetmealDto> list = records.stream().map((item) -> {
            // 对象拷贝
            SetmealDto setmealDto = new SetmealDto();
            // 拷贝属性
            BeanUtils.copyProperties(item, setmealDto);
            // 查询套餐关联的菜品
            Long categoryId = item.getCategoryId();
            // 查询菜品分类名称
            Category categoty = categoryService.getById(categoryId);
            if (categoty != null) {
                // 设置菜品分类名称
                String categoryName = categoty.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        // 设置套餐DTO列表
        pageDto.setRecords(list);

        return R.success(pageDto);
    }

    /**
     * 根据ids删除套餐
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteSetmeal(@RequestParam("ids") List<Long> ids) {
        setmealService.removeWithDish(ids);
        return R.success("删除套餐成功");
    }

    /**
     * 更新套餐状态
     *
     * @param state
     * @param ids
     * @return
     */
    @PostMapping("/status/{state}")
    public R<String> updateStatus(@PathVariable Integer state, @RequestParam("ids") List<Long> ids) {
        // 查询ids对应的套餐
        List<Setmeal> setmeals = setmealService.listByIds(ids);
        // 设置套餐状态
        setmeals.forEach((item) -> {
            item.setStatus(state);
        });
        // 批量更新套餐状态
        setmealService.updateBatchById(setmeals);

        return R.success("更新套餐状态成功");
    }

    /**
     * 查询套餐内容
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getSetmeal(@PathVariable Long id) {
        log.info("id={}", id);
        SetmealDto setmealDto = setmealService.getWithDish(id);
        return R.success(setmealDto);
    }

    @PutMapping
    public R<String> updateSetmeal(@RequestBody SetmealDto setmealDto) {
        setmealService.updateWithDish(setmealDto);
        return R.success("更新套餐成功");
    }

    /**
     * 根据条件查询菜品
     *
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        // 查询菜品状态为1的菜品
        queryWrapper.eq(Setmeal::getStatus, setmeal.getStatus());
        List<Setmeal> list = setmealService.list(queryWrapper);

        /**
         * 根据套餐id查询套餐关联的菜品以及菜品的口味
         * List<SetmealDto> setmealDtoList = list.stream().map((item) -> {
         *     SetmealDto setmealDto = setmealService.getWithDish(item.getId());
         *     log.info(setmealDto.toString());
         *     List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();// 套餐关联的菜品
         *     setmealDto.setSetmealDishes(setmealDishes);
         *     return setmealDto;
         * }).collect(Collectors.toList());
         */

        return R.success(list);
    }

    @GetMapping("/dish/{id}")
    public R<List<SetmealDishDto>> getDish(@PathVariable Long id) {
        log.info("id={}", id);
        SetmealDto setmealDto = setmealService.getWithDish(id);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        List<SetmealDishDto> setmealDishDtoList = setmealDishes.stream().map((item) -> {
            SetmealDishDto setmealDishDto = new SetmealDishDto();
            BeanUtils.copyProperties(item, setmealDishDto);

            // 查询菜品图片
            Dish dish = dishService.getById(item.getDishId());
            if (dish != null) {
                setmealDishDto.setImage(dish.getImage());
            }

            return setmealDishDto;
        }).collect(Collectors.toList());

        return R.success(setmealDishDtoList);
    }

}
