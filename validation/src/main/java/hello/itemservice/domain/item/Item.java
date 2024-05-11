package hello.itemservice.domain.item;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.ScriptAssert;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
// @ScriptAssert(lang = "javascript", script = "_this.price * _this.quantity >= 10000", message = "총합은 1000원을 넘어야 합니다.")
public class Item {

    // @NotNull(groups = UpdateCheck.class) // 수정 요구 사항 추가
    private Long id;

    // @NotBlank(message = "")를 통해 에러 메시지가 설정 가능하다.
    // @NotBlank(groups = {SaveCheck.class, UpdateCheck.class}) // 빈 값 + 공백만 있는 경우를 허용하지 않는다.
    private String itemName;

    // @NotNull(groups = {SaveCheck.class, UpdateCheck.class}) // null을 허용하지 않는다.
    // @Range(min = 1000, max = 1000000, groups = {SaveCheck.class, UpdateCheck.class}) // 범위 안의 값만 허용한다.
    private Integer price;

    // @NotNull(groups = {SaveCheck.class, UpdateCheck.class})
    // @Max(value = 9999, groups = {SaveCheck.class, UpdateCheck.class}) // 최대 ~ 값만 허용한다.
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
