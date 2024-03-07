package searchengine.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lemma")
@Getter
@Setter
@NoArgsConstructor
public class Lemma {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Column(name = "lemma", nullable = false)
    private String lemma;

    @Column(name = "frequency", nullable = false)
    private int frequency;

}
