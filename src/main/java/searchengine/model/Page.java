package searchengine.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;



//alter table search_engine.page add index (path(100));
// alter table page
//    drop foreign key FKj2jx0gqa4h7wg8ls0k3y221h2;
//
//alter table page
//    add constraint FKj2jx0gqa4h7wg8ls0k3y221h2
//        foreign key (site_id) references site (id)
//            on delete cascade;
@Entity
@Table(name = "page")

@Getter
@Setter
@NoArgsConstructor
public class Page {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Column(name = "path", columnDefinition = "TEXT NOT NULL")
    private String path;

    @Column(name = "code", nullable = false)
    private int code;

    @Column(name = "content", columnDefinition = "MEDIUMTEXT NOT NULL")
    private String content;
}
