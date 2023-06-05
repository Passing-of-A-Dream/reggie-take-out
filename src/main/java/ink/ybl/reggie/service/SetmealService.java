package ink.ybl.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ink.ybl.reggie.dto.SetmealDto;
import ink.ybl.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时保存与套餐关联的菜品信息
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);

    SetmealDto getWithDish(Long id);

    void updateWithDish(SetmealDto setmealDto);
}
