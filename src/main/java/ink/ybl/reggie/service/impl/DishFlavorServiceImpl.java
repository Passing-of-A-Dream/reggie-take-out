package ink.ybl.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ink.ybl.reggie.entity.DishFlavor;
import ink.ybl.reggie.mapper.DishFlavorMapper;
import ink.ybl.reggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
