package hello.itemservice.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
// JPA는 public 또는 protected의 기본 생성자가 필수
// @Table(name = "Item") 객체 명과 같으면 생략 가능
public class Item {

    // @Id 테이블의 PK와 해당 필드를 매핑
    // @GeneratedValue PK 생성 값을 데이터베이스에서 생성하는 IDENTITY 방식을 사용
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "item_name", length = 10)
    private String itemName;
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
