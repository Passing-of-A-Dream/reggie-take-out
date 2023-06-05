package ink.ybl.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 员工实体类
 */

@Data
public class Employee implements Serializable {

    private static final long servalVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    //身份证号码
    private String idNumber;

    private Integer status;

    @TableField(fill = FieldFill.INSERT) // 插入时填充数据
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE) // 插入和更新时填充数据
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT) // 插入时填充数据
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE) // 插入和更新时填充数据
    private Long updateUser;

}
