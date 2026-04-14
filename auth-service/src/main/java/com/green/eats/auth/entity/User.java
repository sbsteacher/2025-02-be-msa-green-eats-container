package com.green.eats.auth.entity;

import com.green.eats.common.entity.CreatedUpdatedAt;
import com.green.eats.common.model.EnumUserRole;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User extends CreatedUpdatedAt {
    @Id @Tsid
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    private String address;

    @Column(nullable = false, length = 2)
    private EnumUserRole enumUserRole;
}
