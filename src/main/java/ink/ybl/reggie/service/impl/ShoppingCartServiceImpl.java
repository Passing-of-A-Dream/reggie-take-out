package ink.ybl.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ink.ybl.reggie.entity.ShoppingCart;
import ink.ybl.reggie.mapper.ShoppingCartMapper;
import ink.ybl.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
