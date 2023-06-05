package ink.ybl.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ink.ybl.reggie.entity.User;
import ink.ybl.reggie.service.UserService;
import org.springframework.stereotype.Service;
import ink.ybl.reggie.mapper.UserMapper;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
