package com.example.playmock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Table(name = "customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Audited
public class Customer {
    @Id
    @Column(name = "customer_id")
    private Integer id;

    private String name;
}
