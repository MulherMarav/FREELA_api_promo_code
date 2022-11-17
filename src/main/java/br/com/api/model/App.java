package br.com.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.UUID;


@Data
@Entity
@SuperBuilder
@Table(name = "app")
@EqualsAndHashCode(callSuper = true)
public class App extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uid;
    private String nome;
    private String token;
}
