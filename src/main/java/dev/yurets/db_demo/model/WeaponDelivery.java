package dev.yurets.db_demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Сутність "Поставка зброї"
 * Рівень 2: Weapon → WeaponDelivery
 * Конкретні поставки зброї по датах
 */
@Entity
@Table(name = "weapon_deliveries")
public class WeaponDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "delivery_date", nullable = false)
    private LocalDate deliveryDate;

    @Column(name = "quantity_delivered", nullable = false)
    private Integer quantityDelivered;

    @Column(name = "delivery_status", nullable = false)
    private String deliveryStatus; // planned, in_transit, delivered

    @Column(name = "tracking_number")
    private String trackingNumber;

    /**
     * Зв'язок "Багато-до-Одного" зі зброєю
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weapon_id", nullable = false)
    @JsonIgnore
    private Weapon weapon;

    /**
     * Зв'язок "Багато-до-Одного" з донором
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id", nullable = false)
    @JsonIgnore
    private Donor donor;

    // Конструктори
    public WeaponDelivery() {
    }

    public WeaponDelivery(LocalDate deliveryDate, Integer quantityDelivered,
                          String deliveryStatus, String trackingNumber,
                          Weapon weapon, Donor donor) {
        this.deliveryDate = deliveryDate;
        this.quantityDelivered = quantityDelivered;
        this.deliveryStatus = deliveryStatus;
        this.trackingNumber = trackingNumber;
        this.weapon = weapon;
        this.donor = donor;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Integer getQuantityDelivered() {
        return quantityDelivered;
    }

    public void setQuantityDelivered(Integer quantityDelivered) {
        this.quantityDelivered = quantityDelivered;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public Donor getDonor() {
        return donor;
    }

    public void setDonor(Donor donor) {
        this.donor = donor;
    }

    @Override
    public String toString() {
        return "WeaponDelivery{" +
                "id=" + id +
                ", deliveryDate=" + deliveryDate +
                ", quantityDelivered=" + quantityDelivered +
                ", deliveryStatus='" + deliveryStatus + '\'' +
                ", trackingNumber='" + trackingNumber + '\'' +
                '}';
    }
}