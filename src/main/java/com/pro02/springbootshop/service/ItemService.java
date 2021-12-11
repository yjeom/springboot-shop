package com.pro02.springbootshop.service;

import com.pro02.springbootshop.dto.ItemFormDto;
import com.pro02.springbootshop.dto.ItemImgDto;
import com.pro02.springbootshop.dto.ItemSearchDto;
import com.pro02.springbootshop.entity.Item;
import com.pro02.springbootshop.entity.ItemImg;
import com.pro02.springbootshop.repository.ItemImgRepository;
import com.pro02.springbootshop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityExistsException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgRepository itemImgRepository;
    private final ItemImgService itemImgService;

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList)throws Exception{
        Item item=itemFormDto.createItem();
        itemRepository.save(item);

        for(int i=0;i<itemImgFileList.size();i++){
            ItemImg itemImg=new ItemImg();
            itemImg.setItem(item);
            if(i==0){
                itemImg.setRepImgYn("Y");
            }else{
                itemImg.setRepImgYn("N");
            }
            itemImgService.saveItemImg(itemImg,itemImgFileList.get(i));
        }
        return item.getId();
    }

    @Transactional(readOnly = true)
    public ItemFormDto getItemDtl(Long itemId){
        List<ItemImg> itemImgList=itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemImgDto> itemImgDtoList=new ArrayList<>();
        for(ItemImg itemImg:itemImgList){
            ItemImgDto itemImgDto=ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }
        Item item=itemRepository.findById(itemId).orElseThrow(EntityExistsException::new);
        ItemFormDto itemFormDto=ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        return itemFormDto;
    }

    public Long updateItem(ItemFormDto itemFormDto,List<MultipartFile> itemImgFileList)throws Exception{
        Item item=itemRepository.findById(itemFormDto.getId())
                .orElseThrow(EntityExistsException::new);
        item.updateItem(itemFormDto);
        List<Long> itemImgIds=itemFormDto.getItemImgIds();
        for(int i=0;i<itemImgFileList.size();i++){
            itemImgService.updateItemImg(itemImgIds.get(i),itemImgFileList.get(i));
        }
        return item.getId();
    }

    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getAdminItemPage(itemSearchDto,pageable);
    }
}
