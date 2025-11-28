package dev.yurets.db_demo.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

/**
 * Сутність "Період допомоги"
 * Рівень 1: Country → Period → Weapon
 */
@Entity
@Table(name = "periods")
public class Period {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "period_name", nullable = false)
    private String periodName;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "aid_amount_usd")
    private BigDecimal aidAmountUsd;

    /**
     * Зв'язок "Багато-до-Одного" з країною
     * FetchType.LAZY: не завантажувати країну, поки не попросять
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    /**
     * Зв'язок "Один-до-Багатьох" зі зброєю
     * cascade = CascadeType.ALL: видалення періоду → видалення всієї зброї
     */
    @OneToMany(mappedBy = "period", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Weapon> weapons;

    // Конструктор за замовчуванням (необхідний для JPA)
    public Period() {
    }

    // Конструктор для зручності
    public Period(String periodName, LocalDate startDate, LocalDate endDate,
                  BigDecimal aidAmountUsd, Country country) {
        this.periodName = periodName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.aidAmountUsd = aidAmountUsd;
        this.country = country;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPeriodName() {
        return periodName;
    }

    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getAidAmountUsd() {
        return aidAmountUsd;
    }

    public void setAidAmountUsd(BigDecimal aidAmountUsd) {
        this.aidAmountUsd = aidAmountUsd;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Set<Weapon> getWeapons() {
        return weapons;
    }

    public void setWeapons(Set<Weapon> weapons) {
        this.weapons = weapons;
    }

    @Override
    public String toString() {
        return "Period{" +
                "id=" + id +
                ", periodName='" + periodName + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", aidAmountUsd=" + aidAmountUsd +
                '}';
    }
}