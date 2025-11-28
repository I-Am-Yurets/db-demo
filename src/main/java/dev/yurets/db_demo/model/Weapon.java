package dev.yurets.db_demo.model;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Сутність "Зброя"
 * Рівень 2: Country → Period → Weapon
 */
@Entity
@Table(name = "weapons")
public class Weapon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "weapon_type", nullable = false)
    private String weaponType;

    @Column(name = "weapon_name", nullable = false)
    private String weaponName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_cost_usd")
    private BigDecimal unitCostUsd;

    @Column(name = "total_cost_usd")
    private BigDecimal totalCostUsd;

    /**
     * Зв'язок "Багато-до-Одного" з періодом
     * FetchType.LAZY: не завантажувати період, поки не попросять
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "period_id", nullable = false)
    private Period period;

    // Конструктор за замовчуванням (необхідний для JPA)
    public Weapon() {
    }

    // Конструктор для зручності
    public Weapon(String weaponType, String weaponName, Integer quantity,
                  BigDecimal unitCostUsd, BigDecimal totalCostUsd, Period period) {
        this.weaponType = weaponType;
        this.weaponName = weaponName;
        this.quantity = quantity;
        this.unitCostUsd = unitCostUsd;
        this.totalCostUsd = totalCostUsd;
        this.period = period;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWeaponType() {
        return weaponType;
    }

    public void setWeaponType(String weaponType) {
        this.weaponType = weaponType;
    }

    public String getWeaponName() {
        return weaponName;
    }

    public void setWeaponName(String weaponName) {
        this.weaponName = weaponName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitCostUsd() {
        return unitCostUsd;
    }

    public void setUnitCostUsd(BigDecimal unitCostUsd) {
        this.unitCostUsd = unitCostUsd;
    }

    public BigDecimal getTotalCostUsd() {
        return totalCostUsd;
    }

    public void setTotalCostUsd(BigDecimal totalCostUsd) {
        this.totalCostUsd = totalCostUsd;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    @Override
    public String toString() {
        return "Weapon{" +
                "id=" + id +
                ", weaponType='" + weaponType + '\'' +
                ", weaponName='" + weaponName + '\'' +
                ", quantity=" + quantity +
                ", unitCostUsd=" + unitCostUsd +
                ", totalCostUsd=" + totalCostUsd +
                '}';
    }
}