package ink.ybl.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ink.ybl.reggie.common.CustomException;
import ink.ybl.reggie.dto.SetmealDto;
import ink.ybl.reggie.entity.Setmeal;
import ink.ybl.reggie.entity.SetmealDish;
import ink.ybl.reggie.mapper.SetmealMapper;
import ink.ybl.reggie.service.SetmealDishService;
import ink.ybl.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("Setmeal")
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时保存与套餐关联的菜品信息
     *
     * @param setmealDto
     */
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐信息 操作setmeal表
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        // 设置套餐id
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealDto.getId());
        });

        // 保存套餐与菜品关联信息 操作setmeal_dish表
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐，同时删除与套餐关联的菜品信息
     *
     * @param ids
     */
    @Transactional
    public void removeWithDish(List<Long> ids) {
        // select count(*) from setmeal where id in (ids) and status = 1
        // 查询套餐状态是否为在售(1)
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);

        int count = this.count(queryWrapper);

        if (count > 0) {
            // 不能删除，抛出异常
            throw new CustomException("套餐正在售卖，不能删除");
        }

        // 可以删除
        this.removeByIds(ids);

        // delete from setmeal dish where setmeal id in (ids)
        // 删除套餐与菜品关联信息 操作setmeal_dish表
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);

        setmealDishService.remove(lambdaQueryWrapper);
    }

    @Transactional
    public SetmealDto getWithDish(Long id) {
        // 查询套餐信息
        Setmeal setmeal = this.getById(id);

        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);

        // 查询套餐关联的菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> dishes = setmealDishService.list(queryWrapper);

        setmealDto.setSetmealDishes(dishes);

        return setmealDto;
    }

    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
        // 更新套餐信息 操作setmeal表
        this.updateById(setmealDto);

        // 删除套餐与菜品关联信息 操作setmeal_dish表
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(lambdaQueryWrapper);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        // 设置套餐id
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealDto.getId());
        });

        // 保存套餐与菜品关联信息 操作setmeal_dish表
        setmealDishService.saveBatch(setmealDishes);
    }
}
