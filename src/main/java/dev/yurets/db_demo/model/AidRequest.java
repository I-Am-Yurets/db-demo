package dev.yurets.db_demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Сутність "Запит на допомогу"
 * Представляє запит від країни-отримувача на надання військової допомоги
 */
@Entity
@Table(name = "aid_requests")
public class AidRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "weapon_type", nullable = false)
    private String weaponType; // Artillery, Air Defense, Vehicles, Ammunition

    @Column(name = "weapon_name", nullable = false)
    private String weaponName; // M777 Howitzer, Leopard 2, etc.

    @Column(name = "requested_quantity", nullable = false)
    private Integer requestedQuantity;

    @Column(nullable = false)
    private String priority; // URGENT, HIGH, MEDIUM, LOW

    @Column(nullable = false)
    private String status; // PENDING, APPROVED, REJECTED, DELIVERED

    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;

    @Column(name = "request_reason", length = 500)
    private String requestReason; // Опціонально - причина запиту

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason; // Причина відмови (якщо REJECTED)

    /**
     * Країна, яка робить запит (отримувач допомоги)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requesting_country_id", nullable = false)
    @JsonIgnore
    private Country requestingCountry;

    /**
     * Країна-донор (опціонально - може бути обрана пізніше)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_country_id")
    @JsonIgnore
    private Country donorCountry;

    /**
     * Період до якого відноситься запит
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "period_id", nullable = false)
    @JsonIgnore
    private Period period;

    // Конструктори
    public AidRequest() {
    }

    public AidRequest(String weaponType, String weaponName, Integer requestedQuantity,
                      String priority, String status, LocalDate requestDate,
                      String requestReason, Country requestingCountry,
                      Country donorCountry, Period period) {
        this.weaponType = weaponType;
        this.weaponName = weaponName;
        this.requestedQuantity = requestedQuantity;
        this.priority = priority;
        this.status = status;
        this.requestDate = requestDate;
        this.requestReason = requestReason;
        this.requestingCountry = requestingCountry;
        this.donorCountry = donorCountry;
        this.period = period;
    }

    // Getters and Setters
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

    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }

    public void setRequestedQuantity(Integer requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestReason() {
        return requestReason;
    }

    public void setRequestReason(String requestReason) {
        this.requestReason = requestReason;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Country getRequestingCountry() {
        return requestingCountry;
    }

    public void setRequestingCountry(Country requestingCountry) {
        this.requestingCountry = requestingCountry;
    }

    public Country getDonorCountry() {
        return donorCountry;
    }

    public void setDonorCountry(Country donorCountry) {
        this.donorCountry = donorCountry;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    @Override
    public String toString() {
        return "AidRequest{" +
                "id=" + id +
                ", weaponType='" + weaponType + '\'' +
                ", weaponName='" + weaponName + '\'' +
                ", requestedQuantity=" + requestedQuantity +
                ", priority='" + priority + '\'' +
                ", status='" + status + '\'' +
                ", requestDate=" + requestDate +
                '}';
    }
}