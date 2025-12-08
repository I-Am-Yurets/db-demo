package dev.yurets.db_demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

/**
 * Сутність "Донор/Організація"
 * Рівень 1: Country → Donor
 * Хто конкретно надає допомогу від країни
 */
@Entity
@Table(name = "donors")
public class Donor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organization_name", nullable = false)
    private String organizationName;

    @Column(name = "organization_type", nullable = false)
    private String organizationType; // урядова, міжнародна, приватна

    @Column(name = "contact_info")
    private String contactInfo;

    /**
     * Зв'язок "Багато-до-Одного" з країною
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    @JsonIgnore
    private Country country;

    /**
     * Зв'язок "Один-до-Багатьох" з поставками
     */
    @OneToMany(mappedBy = "donor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<WeaponDelivery> deliveries;

    // Конструктори
    public Donor() {
    }

    public Donor(String organizationName, String organizationType, String contactInfo, Country country) {
        this.organizationName = organizationName;
        this.organizationType = organizationType;
        this.contactInfo = contactInfo;
        this.country = country;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(String organizationType) {
        this.organizationType = organizationType;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Set<WeaponDelivery> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(Set<WeaponDelivery> deliveries) {
        this.deliveries = deliveries;
    }

    @Override
    public String toString() {
        return "Donor{" +
                "id=" + id +
                ", organizationName='" + organizationName + '\'' +
                ", organizationType='" + organizationType + '\'' +
                '}';
    }
}