package me.barion.capstoneprojectbarion.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@Setter
@Table(name = "Category")
@DynamicInsert
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(name = "category_name", nullable = false, unique = true)
    private String categoryName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    @ColumnDefault("0") // 기본값 설정
    @JsonIgnore
    private Store store;


    @PrePersist
    public void prePersist() {
        if (this.store == null) {
            Store defaultStore = new Store();
            defaultStore.setStoreId(0); // 기본 store_id 값 설정
            this.store = defaultStore;
        }
    }


    public Category() {}

    public Category(Integer categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }
}
