package dev.yurets.db_demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

/**
 * Сутність "Країна-донор"
 * Корінь ієрархії: Country → Period → Weapon
 */
@Entity
@Table(name = "countries")
public class Country {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(nullable = false, unique = true)
    private String name;

    @Getter
    @Setter
    @Column(name = "total_aid_usd")
    private BigDecimal totalAidUsd;

    @Column(name = "is_open")
    private Boolean isOpen = true;

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
    /**
     * Зв'язок "Один-до-Багатьох" з періодами
     * @JsonIgnore - запобігає циклічній серіалізації JSON
     */
    @Getter
    @Setter
    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Period> periods;

    // Конструктор за замовчуванням (необхідний для JPA)
    public Country() {
    }

    // Конструктор для зручності
    public Country(String name, BigDecimal totalAidUsd) {
        this.name = name;
        this.totalAidUsd = totalAidUsd;
    }

    // --- Getters and Setters ---

    @Override
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", totalAidUsd=" + totalAidUsd +
                '}';
    }


}