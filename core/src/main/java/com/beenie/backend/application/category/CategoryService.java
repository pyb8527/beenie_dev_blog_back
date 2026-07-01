package com.beenie.backend.application.category;

import com.beenie.backend.domain.category.Category;
import com.beenie.backend.domain.category.CategoryCount;
import com.beenie.backend.domain.category.CategoryRepository;
import com.beenie.backend.domain.post.PostRepository;
import com.beenie.backend.support.exception.BusinessException;
import com.beenie.backend.support.exception.ErrorCode;
import com.beenie.backend.support.util.SlugGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;

    public List<CategoryCount> listAllWithPostCount() {
        return categoryRepository.findAllWithPostCount();
    }

    @Transactional
    public Category create(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new BusinessException(ErrorCode.CATEGORY_DUPLICATE);
        }
        Category category = Category.builder().name(name).slug(uniqueSlug(name)).build();
        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(Long id, String name) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        if (!category.getName().equals(name) && categoryRepository.existsByName(name)) {
            throw new BusinessException(ErrorCode.CATEGORY_DUPLICATE);
        }
        category.setName(name);
        category.setSlug(uniqueSlug(name));
        return categoryRepository.save(category);
    }

    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        if (category.isDefault()) {
            throw new BusinessException(ErrorCode.CATEGORY_DELETE_NOT_ALLOWED);
        }
        postRepository.reassignCategoryToDefault(id);
        categoryRepository.deleteById(id);
    }

    private String uniqueSlug(String name) {
        return SlugGenerator.generate(name);
    }
}
