package com.pro02.springbootshop.repository;

import com.pro02.springbootshop.constant.ItemSellStatus;
import com.pro02.springbootshop.dto.ItemSearchDto;
import com.pro02.springbootshop.dto.MainItemDto;
import com.pro02.springbootshop.dto.QMainItemDto;
import com.pro02.springbootshop.entity.Item;
import com.pro02.springbootshop.entity.QItem;
import com.pro02.springbootshop.entity.QItemImg;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{

    private JPAQueryFactory queryFactory;

    public ItemRepositoryCustomImpl(EntityManager em){
        this.queryFactory=new JPAQueryFactory(em);
    }
    private BooleanExpression searchSellsStatusEq(ItemSellStatus searchSellStatus){
        return searchSellStatus==null?null: QItem.item.itemSellStatus.eq(searchSellStatus);
    }
    private BooleanExpression regDtsAfter(String searchDateType){
        LocalDateTime dateTime=LocalDateTime.now();
        if(StringUtils.equals("all",searchDateType)||searchDateType==null){
            return null;
        }else if(StringUtils.equals("1d",searchDateType)){
            dateTime= dateTime.minusDays(1);
        }else if(StringUtils.equals("1w",searchDateType)){
            dateTime= dateTime.minusWeeks(1);
        }else if(StringUtils.equals("1m",searchDateType)){
            dateTime= dateTime.minusMonths(1);
        }else if(StringUtils.equals("6m",searchDateType)){
            dateTime= dateTime.minusMonths(6);
        }
        return QItem.item.regTime.after(dateTime);
    }

    private BooleanExpression searchByLike(String searchBy,String searchQuery){
        if(StringUtils.equals("itemName",searchBy)){
            return QItem.item.itemName.like("%"+searchQuery+"%");
        }else if(StringUtils.equals("createBy",searchBy)){
            return QItem.item.createdBy.like("%"+searchQuery+"%");
        }
        return null;
    }
    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        List<Item> content=queryFactory.selectFrom(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellsStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(),itemSearchDto.getSearchQuery())
                        )
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content,pageable,content.size());
    }

    private BooleanExpression itemNameLike(String searchQuery){
        return StringUtils.isEmpty(searchQuery)?null:QItem.item.itemName.like("%"+searchQuery+"%");
    }
    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QItem item=QItem.item;
        QItemImg itemImg=QItemImg.itemImg;

        List<MainItemDto> content=queryFactory.select(
                new QMainItemDto(
                        item.id,
                        item.itemName,
                        item.itemDetail,
                        itemImg.imgUrl,
                        item.price)

                ).from(itemImg)
                .join(itemImg.item,item)
                .where(itemImg.repImgYn.eq("Y"))
                .where(itemNameLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(content,pageable,content.size());
    }
}
