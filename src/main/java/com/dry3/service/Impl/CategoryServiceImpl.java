package com.dry3.service.Impl;

import com.dry3.common.ServerResponse;
import com.dry3.dao.CategoryMapper;
import com.dry3.pojo.Category;
import com.dry3.service.ICategoryService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
/**
 * Created by dry3
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public ServerResponse addCategory(String categoryName, Integer parentId) {
        if (parentId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }
        if (categoryMapper.checkCategoryName(categoryName) > 0) {
            return ServerResponse.createByErrorMessage("品类名称已存在");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);
        int insertResult = categoryMapper.insert(category);
        if (insertResult > 0) {
            return ServerResponse.createBySuccessMessage("品类添加成功");
        }
        return ServerResponse.createByErrorMessage("品类添加失败");
    }

    public ServerResponse updateCategoryName(Integer categoryId, String categoryName) {
        if (categoryId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }
        if (categoryMapper.checkCategoryName(categoryName) > 0) {
            return ServerResponse.createByErrorMessage("品类名称已存在");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int updateCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (updateCount > 0) {
            return ServerResponse.createBySuccessMessage("更新品类名字成功");
        }
        return ServerResponse.createByErrorMessage("更新品类名字失败");
    }

    public ServerResponse<List<Category>> listChildrenParallelCategory(Integer parentId) {
        List<Category> categoryList = categoryMapper.listCategoryByParentId(parentId);
        if (categoryList.size() != 0) {
            return ServerResponse.createBySuccess(categoryList);
        }
        return ServerResponse.createByErrorMessage("未找到该品类的子类");
    }

    public ServerResponse<List<Integer>> listChildrenDeeplCategory(Integer categoryId) {
        Set<Category> categorySet = Sets.newHashSet();
        findDeepCategoryById(categorySet, categoryId);
        List<Integer> categoryList = Lists.newArrayList();
        //小心空指针异常
        if (categoryId != null) {
            for (Category categoryItem : categorySet) {
                categoryList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryList);

    }


    //递归算法实现
    private Set<Category> findDeepCategoryById(Set<Category> categorySet, Integer categoryId) {
        Category currentCategory = categoryMapper.selectByPrimaryKey(categoryId);
        if (currentCategory != null) {
            categorySet.add(currentCategory);
        }
        List<Category> categoryList = categoryMapper.listCategoryByParentId(categoryId);
        for (Category categoryItem : categoryList) {
             findDeepCategoryById(categorySet, categoryItem.getId());
        }
        return categorySet;
    }
}
