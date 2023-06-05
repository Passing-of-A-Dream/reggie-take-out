package ink.ybl.reggie.dto;

import ink.ybl.reggie.entity.DishFlavor;
import ink.ybl.reggie.entity.Setmeal;
import ink.ybl.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;

}
