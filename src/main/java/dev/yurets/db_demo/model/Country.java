package dev.yurets.db_demo.model;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "total_aid_usd")
    private BigDecimal totalAidUsd;

    /**
     * Зв'язок "Один-до-Багатьох" з періодами
     * cascade = CascadeType.ALL: видалення країни → видалення всіх періодів
     * orphanRemoval = true: видалення періоду зі списку → видалення з БД
     */
    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getTotalAidUsd() {
        return totalAidUsd;
    }

    public void setTotalAidUsd(BigDecimal totalAidUsd) {
        this.totalAidUsd = totalAidUsd;
    }

    public Set<Period> getPeriods() {
        return periods;
    }

    public void setPeriods(Set<Period> periods) {
        this.periods = periods;
    }

    @Override
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", totalAidUsd=" + totalAidUsd +
                '}';
    }
}