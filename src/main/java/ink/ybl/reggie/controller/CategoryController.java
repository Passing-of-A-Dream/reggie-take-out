package ink.ybl.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ink.ybl.reggie.common.R;
import ink.ybl.reggie.entity.Category;
import ink.ybl.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 分页查询分类
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> query(int page, int pageSize) {
        Page pageInfo = new Page<>(page, pageSize);
        // 构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // 添加排序条件
        queryWrapper.orderByDesc(Category::getSort);

        // 执行查询
        categoryService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 菜品、套餐分类添加
     * @param category
     * @return
     */
    @PostMapping
    public R<String> add(@RequestBody Category category) {
        // 添加分类
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 修改菜品
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    /**
     * 删除分类
     * @param ids
     * @return
     */
    @DeleteMapping
    @ResponseBody
    public R<String> delete(Long ids) {
//        categoryService.removeById(ids);
        categoryService.removeCategory(ids);
        return R.success("菜品分类删除成功");
    }

    /**
     * 获取菜品分类
     * @param category
     * @return
     */
    @GetMapping("/list")
    @ResponseBody
    public R<List<Category>> getType(Category category) {
        // 构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // 查询条件
        queryWrapper.eq(category.getType() != null,Category::getType, category.getType());
        // 添加排序条件
        queryWrapper.orderByDesc(Category::getSort);
        List list = categoryService.list(queryWrapper);

        return R.success(list);
    }
}
