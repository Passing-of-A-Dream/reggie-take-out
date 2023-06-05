package ink.ybl.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ink.ybl.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}
