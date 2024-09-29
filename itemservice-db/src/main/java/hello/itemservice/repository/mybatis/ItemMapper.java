package hello.itemservice.repository.mybatis;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

// MyBatis에서 자동으로 인식하여 빈으로 등록해준다.
@Mapper
public interface ItemMapper {

    void save(Item item);

    // 파라미터가 두 개가 넘어가는 경우 작성
    void update(@Param("id") Long id, @Param("updateParam")ItemUpdateDto updateParam);

    Optional<Item> findById(Long id);

    List<Item> findAll(ItemSearchCond itemSearch);

}
