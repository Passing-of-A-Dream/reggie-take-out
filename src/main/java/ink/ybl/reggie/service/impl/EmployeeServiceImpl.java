package ink.ybl.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ink.ybl.reggie.entity.Employee;
import ink.ybl.reggie.mapper.EmployeeMapper;
import ink.ybl.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;


@Service("Employee")
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
