package com.won.myongjiCamp.model.board.role;

import com.won.myongjiCamp.model.board.RecruitBoard;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class RoleAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

/*    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private RecruitBoard board;*/

    //--o
/*    @Enumerated(EnumType.STRING) //엔티티의 필드가 열거형일 때 사용
    @ElementCollection // 엔티티가 컬렉션을 포함하고 있을 대 사용
    // 이 값 타입은 기본 타입 호긍ㄴ embeddable이 붙은 클래스 일 수 o
    @CollectionTable(name = "role_collection", joinColumns = @JoinColumn(name = "Id"))
    // elementCollection이 붙은 필드가 매핑될 테이블 지정함, name으로 테이블 이름 지정, joinColumns 속성으로 조인할 컬럼 지정
    @MapKeyColumn(name = "role") // 키가 매핑될 컬럼 지정
    private Role role; //collection, role과 state를 한번에




    @MapKeyEnumerated(EnumType.STRING) //엔티티가 map을 포함하고 있고 그 key가 열거형일 때 사용
    private Map<Role, Map<RoleState, Integer>> role_map_tmp;*/




//    --x코드
/*    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;*/

/*    @Column(nullable = false)
    private int requiredNumber; // 모집 하는 인원

    @Column(nullable = false)
    private int appliedNumber; // 모집된 인원*/

/*    private Map<Role, Map<RoleState, Integer>> roleMap = new HashMap<>();

    public Map<Role, Map<RoleState, Integer>> getRoleMap() {

        return roleMap;
    }

    public void setRoleMap(Map<Role, Map<RoleState, Integer>> roleMap) {
        this.roleMap = roleMap;
//        roleMap.put(roleMap);


    }*/
/*    setRole(ob){
        private Map<Role, Map<RoleState, Integer>> roleMap = new HashMap<>();
        roleMap.put(ob);
        roleMap.get(ob).put(RoleState.ROLE_WANT,ob.want);
        roleMap.get(ob).put(RoleState.ROLE_TOTAL, ob.tota);
    }*/

}
