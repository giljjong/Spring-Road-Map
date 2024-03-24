package hello.itemservice.domain.item;

// @Data는 핵심 도메인 상황에 위험. 예측하지 못하게 동작할 수 있음
import lombok.Data;

@Data
public class Item {

    private Long id;
    private String itemName;
    // Integer를 쓴 이유 null이 들어가는 경우가 있어서
    private Integer price;
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
