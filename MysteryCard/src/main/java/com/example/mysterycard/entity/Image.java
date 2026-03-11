package com.example.mysterycard.entity;

import com.pgvector.PGvector;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;
@Entity
@Table(name = "Image")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID imageId;
    private String imageUrl;
    @ManyToOne
    @JoinColumn(name="card_id")
    private Card card;
    @ManyToOne
    @JoinColumn(name="tracking_id")
    private Tracking tracking;
    @ManyToOne
    @JoinColumn(name="return_request_id")
    private ReturnRequest returnRequest;
    @Column(columnDefinition = "vector(512)")
    @JdbcTypeCode(SqlTypes.OTHER)
    @Transient
    private PGvector embedding;
}
