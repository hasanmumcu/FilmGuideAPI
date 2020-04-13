package com.filmguide.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Setter
@Getter
@MappedSuperclass
public abstract  class AbstractPersistableEntity<ID> implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2080935282245904881L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private ID id;
}
