package com.numberscope.phone;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "phone_queries")
public class PhoneQuery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String rawInput;

    private String e164;
    private String country;
    private String region;
    private String carrier;
    private String lineType;
    private String lada;
    private boolean valid;

    @Column(nullable = false)
    private Instant createdAt;

    public PhoneQuery() {}

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getRawInput() { return rawInput; }
    public void setRawInput(String rawInput) { this.rawInput = rawInput; }
    public String getE164() { return e164; }
    public void setE164(String e164) { this.e164 = e164; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }
    public String getLineType() { return lineType; }
    public void setLineType(String lineType) { this.lineType = lineType; }
    public String getLada() { return lada; }
    public void setLada(String lada) { this.lada = lada; }
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}