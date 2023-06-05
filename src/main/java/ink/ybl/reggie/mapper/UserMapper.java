package ink.ybl.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ink.ybl.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User>{
}
