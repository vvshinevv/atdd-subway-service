package nextstep.subway.member.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonBackReference
    private Member member;

    @ManyToOne
    private Station source;

    @ManyToOne
    private Station target;

    public Favorite() {
    }

    public Favorite(Member member, Station source, Station target) {
        this.member = member;
        this.source = source;
        this.target = target;
    }

    public Long getId() {
        return id;
    }

    public Station getSource() {
        return source;
    }

    public Station getTarget() {
        return target;
    }

    /**
     * 주어진 즐겨찾기 ID가 해당 즐겨찾기 ID와 같은지 확인합니다.
     * @param id
     * @return
     */
    public boolean equalsId(Long id) {
        return this.id.equals(id);
    }

    /**
     * 주어진 역이 즐겨찾기의 source와 같은지 확인합니다.
     * @param station 
     * @return
     */
    public boolean equalsSource(Station station) {
        return this.source.equals(station);
    }

    /**
     * 주어진 역이 즐겨찾기의 target과 같은지 확인합니다.
     * @param station 
     * @return
     */
    public boolean equalsTarget(Station station) {
        return this.target.equals(station);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Favorite favorite = (Favorite) o;
        return Objects.equals(id, favorite.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Favorite{" +
                "id=" + id +
                '}';
    }
}