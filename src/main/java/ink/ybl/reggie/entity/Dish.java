package ink.ybl.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Dish {

    private static final long servalVersionUID = 1L;

    private Long id;

    // 菜品名称
    private String name;

    // 菜品分类id
    private Long categoryId;

    // 菜品价格
    private BigDecimal price;

    // 商品码
    private String code;

    // 图片
    private String image;

    // 描述信息
    private String description;

    // 状态 0 停售 1 起售
    private Integer status;

    // 排序
    private Integer sort;

    // 创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 创建人
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    // 修改人
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

    // 是否删除
    private Integer isDeleted;
}
