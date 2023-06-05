package ink.ybl.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ink.ybl.reggie.common.R;
import ink.ybl.reggie.entity.Employee;
import ink.ybl.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request,@RequestBody Employee employee) {

        // 将页面提交的密码进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 根据页面提交的用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        // 如果没有查询到则返回登录失败
        if (emp == null) {
            return R.error("登录失败，没有此用户");
        }

        //密码比对，如果不一致则返回登录失败
        if(!emp.getPassword().equals(password)) {
            return R.error("登录失败，密码错误");
        }

        //查看员工状态，如果是禁用状态，返回员工已禁用
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }

        //登录成功，将员工id存入session并返回登录成功
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出登录
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        // 清理session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> add(HttpServletRequest request,@RequestBody Employee employee) {
//        log.info("新增员工，员工信息：{}", employee.toString());
        // 设置初始密码123456，需要进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        // 员工新增时间
        //employee.setCreateTime(LocalDateTime.now());
        // 员工信息更新时间
        //employee.setUpdateTime(LocalDateTime.now());

        // 获取当前登录用户的id
        //Long userId = (Long) request.getSession().getAttribute("employee");
        //employee.setCreateUser(userId);
        //employee.setUpdateUser(userId);

        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
//    @ResponseBody
    public R<Page> pageQury(int page, int pageSize, String name) {
        // log.info("page = {}, pageSize = {}, name = {}", page, pageSize ,name);

        // 构造分页构造器
        Page pageInfo = new Page<>(page,pageSize);

        // 构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        // 添加过滤条件 判空查询
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        // 添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        // 执行查询
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        if (employee.getId() == 1) {
            return R.error("禁止修改管理员账户");
        }
        // 修改更改时间
        // employee.setUpdateTime(LocalDateTime.now());
        // 修改更改用户
        // employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        // 根据id修改数据
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);

        }
        return R.error("查询不到此员工");
    }
}
