package ink.ybl.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import ink.ybl.reggie.common.R;
import ink.ybl.reggie.entity.User;
import ink.ybl.reggie.service.UserService;
import ink.ybl.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserControll {

    @Autowired
    private UserService userService;

    /**
     * 发送手机号验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        // 获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)) {
            // 生成验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();

            // 将验证码存放到session中
            session.setAttribute(phone, code);

            return R.success(code);
        }

        return R.error("获取验证码失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        // 获取手机号
        String phone = map.get("phone").toString();
        // 获取验证码
        String code = map.get("code").toString();
        // 获取session中的验证码
        String sesCode = session.getAttribute(phone).toString();
        // 验证码校对
        if (sesCode!=null && sesCode.equals(code)) {
            // 登录成功，判断是否是新用户，否就强行注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);

            if (user == null) {
                user = new User();
                user.setPhone(phone);

                userService.save(user);
            }

            session.setAttribute("user", user.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }
}
