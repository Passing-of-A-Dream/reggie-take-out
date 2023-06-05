package ink.ybl.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ink.ybl.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
