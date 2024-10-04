package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Transactional // JPA에서 데이터를 변경할 때 항상 Transactional이 필요
public class JpaItemRepository implements ItemRepository {

    // 이것이 JPA 이를 통해 저장 및 조회
    // 스프링에서 자동으로 만들어 준다.
    private final EntityManager em;

    public JpaItemRepository(EntityManager em) {
        this.em = em;
    }
    @Override
    public Item save(Item item) {
        // persist하면 mapping 값을 가지고 데이터를 만들어주고 ID값도 생성해서 반환
        em.persist(item);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        Item findItem = em.find(Item.class, itemId);
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
        // update 저장을 해줄 필요가 없다.
        // JPA는 조회 시점에 스냅샷을 떠서 어떤 데이터가 바뀌는지 안다.
        // Transaction이 커밋되는 시점에 update 쿼리를 만들어 데이터를 변경해준다.
    }

    @Override
    public Optional<Item> findById(Long id) {
        Item item = em.find(Item.class, id);
        return Optional.ofNullable(item);
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        // 쿼리를 대상으로 하지 않고 Item entity를 대상으로 한다.
        // jpql의 단점 : 동적 쿼리에 약하다.

        String jpql = "select i from Item i";

        Integer maxPrice = cond.getMaxPrice();
        String itemName = cond.getItemName();

        if (StringUtils.hasText(itemName) || maxPrice != null) {
            jpql += " where";
        }

        boolean andFlag = false;
        List<Object> param = new ArrayList<>();
        if (StringUtils.hasText(itemName)) {
            jpql += " i.itemName like concat('%',:itemName,'%')";
            param.add(itemName);
            andFlag = true;
        }

        if (maxPrice != null) {
            if (andFlag) {
                jpql += " and";
            }
            jpql += " i.price <= :maxPrice";
            param.add(maxPrice);
        }

        log.info("jpql={}", jpql);

        TypedQuery<Item> query = em.createQuery(jpql, Item.class);
        if (StringUtils.hasText(itemName)) {
            query.setParameter("itemName", itemName);
        }
        if (maxPrice != null) {
            query.setParameter("maxPrice", maxPrice);
        }
        return query.getResultList();

/*        List<Item> result = em.createQuery(jpql, Item.class)
                .getResultList();
        return result;*/
    }
}
