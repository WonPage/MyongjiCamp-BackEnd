package com.won.myongjiCamp.model.board;

import com.won.myongjiCamp.model.board.role.Role;
import com.won.myongjiCamp.model.board.role.RoleAssignment;
//import com.won.myongjiCamp.model.board.role.RoleEntity;
import com.won.myongjiCamp.model.board.role.RoleState;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@DiscriminatorValue("Recruit")
public class RecruitBoard extends Board {


//    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RecruitStatus status ;  // 모집중, 모집 완료 상태 구분 , 처음 모집 중

    @Column(nullable = false)
    private String preferredLocation; //활동 지역

    @Column(nullable = false)
    private String expectedDuration; //예상 기간

/*    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true) //?
    @JoinColumn(name = "board_id")
//    @Enumerated(EnumType.STRING) //엔티티의 필드가 열거형일 때 사용
    @ElementCollection // 엔티티가 컬렉션을 포함하고 있을 대 사용
    // 이 값 타입은 기본 타입 호긍ㄴ embeddable이 붙은 클래스 일 수 o
    @CollectionTable(name = "role_collection", joinColumns = @JoinColumn(name = "board_id"))
    // elementCollection이 붙은 필드가 매핑될 테이블 지정함, name으로 테이블 이름 지정, joinColumns 속성으로 조인할 컬럼 지정
    @MapKeyColumn(name = "role_key") // role_collection이랑 차이 있는지 궁금
    @Column(name="role_value")
    private Map<Role,HashMap<RoleState,Integer>> roleMap = new HashMap<>();


    public Map<Role, HashMap<RoleState, Integer>> getRoleMap() {
        return roleMap;
    }*/

/*    public void setRoleMap(Map<Role, HashMap<RoleState, Integer>> roleMap) {
        this.roleMap = roleMap;*/
//        roleMap.put()
//    }

    //    private RoleEntity roleEntity; //collection, role과 state를 한번에



    //    @Column(nullable = false)
//    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<RoleAssignment> roleAssignments; //모집 하는 분야(태그 검색 위해 필요, 몇명 구하는지는 RoleAssignment테이블에 따로 저장됨)

}
