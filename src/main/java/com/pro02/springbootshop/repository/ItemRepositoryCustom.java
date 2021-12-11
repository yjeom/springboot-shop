package com.pro02.springbootshop.repository;

import com.pro02.springbootshop.dto.ItemSearchDto;
import com.pro02.springbootshop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {

    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
}
