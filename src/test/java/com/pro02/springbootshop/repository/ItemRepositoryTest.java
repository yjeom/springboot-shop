package com.pro02.springbootshop.repository;

import com.pro02.springbootshop.constant.ItemSellStatus;
import com.pro02.springbootshop.entity.Item;
import com.pro02.springbootshop.entity.QItem;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ItemRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    ItemRepository itemRepository;

    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest(){
        Item item =new Item();
        item.setItemName("test Item");
        item.setPrice(10000);
        item.setItemDetail("test Item Detail");
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        Item savedItem=itemRepository.save(item);
        System.out.println(savedItem.toString());
    }

    public void createItemList(){
        for(int i=1;i<=10;i++){
            Item item=new Item();
            item.setItemName("test"+i);
            item.setPrice(10000*i);
            item.setItemDetail("test item detail "+i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            Item savedItem=itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품명 조회 테스트")
    public void findByItemNameTest(){
        this.createItemList();
        List<Item> itemList=itemRepository.findByItemName("test1");
        for(Item item:itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("상품명, 상품상세설명 or 테스트")
    public void findByItemNameOrItemDetailTest(){
        this.createItemList();
        List<Item> itemList=itemRepository.findByItemNameOrItemDetail("test1","test item detail 5");
        for(Item item:itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 LessThan 테스트")
    public void findByPriceLessThanTest(){
        this.createItemList();
        List<Item> itemList=itemRepository.findByPriceLessThan(50000);
        for(Item item:itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 내림차순 조회 테스트")
    public void findByPriceLessThanOrderByPriceDesc(){
        this.createItemList();
        List<Item> itemList=itemRepository.findByPriceLessThanOrderByPriceDesc(50000);
        for(Item item:itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    public void findByItemDetailTest(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemDetail("test item detail ");
        for(Item item:itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("Querydsl 조회테스트1")
    public void queryDslTest(){
        this.createItemList();
        JPAQueryFactory queryFactory =new JPAQueryFactory(em);
        QItem qItem=QItem.item;
        JPAQuery<Item> query=queryFactory.selectFrom(qItem)
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%"+"test item detail "+"%"))
                .orderBy(qItem.price.desc());
        List<Item> itemList=query.fetch();
        for(Item item:itemList){
            System.out.println(item.toString());
        }
    }

    public void createItemList2(){
        for(int i=1;i<=10;i++){
            Item item=new Item();
            item.setItemName("test"+i);
            item.setPrice(10000*i);
            item.setItemDetail("test item detail "+i);
            if(i<6){
                item.setItemSellStatus(ItemSellStatus.SELL);
                item.setStockNumber(100);
            }
            else{
                item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
                item.setStockNumber(0);
            }
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            Item savedItem=itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품 Querydsl 조회테스트2")
    public void queryDslTest2(){
        this.createItemList2();

        BooleanBuilder booleanBuilder=new BooleanBuilder();
        QItem qitem=QItem.item;
        String itemDetail="test item detail ";
        int price= 30000;
        String itemSellStat="SELL";

        booleanBuilder.and(qitem.itemDetail.like("%"+itemDetail+"%"));
        booleanBuilder.and(qitem.price.gt(price));

        if(StringUtils.equals(itemSellStat,ItemSellStatus.SELL)){
            booleanBuilder.and(qitem.itemSellStatus.eq(ItemSellStatus.SELL));
        }
        Pageable pageable= PageRequest.of(0,5);
        Page<Item> itemPagingResult=itemRepository.findAll(booleanBuilder,pageable);
        System.out.println("total elements: "+itemPagingResult.getTotalElements());

        List<Item> resultItemList=itemPagingResult.getContent();
        for(Item item:resultItemList){
            System.out.println(item.toString());
        }
    }

}