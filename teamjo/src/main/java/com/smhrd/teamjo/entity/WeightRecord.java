// 파일명: WeightRecord.java
package com.smhrd.teamjo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "WEIGHT_RECORD")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeightRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RECORD_ID")
    private Long id;

    @Column(name = "USER_ID")
    private String userId; // USER_INFO의 이메일 또는 ID

    @Column(name = "WEIGHT")
    private double weight;

    @Column(name = "RECORD_DATE")
    private LocalDateTime recordedAt;

    @PrePersist
    public void onCreate() {
        this.recordedAt = LocalDateTime.now();
    }
}
