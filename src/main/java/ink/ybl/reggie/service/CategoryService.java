package ink.ybl.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ink.ybl.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    public void removeCategory(Long id);
}
