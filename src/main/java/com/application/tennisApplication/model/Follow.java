package com.application.tennisApplication.model;

import jakarta.persistence.*;

@Entity
public class Follow {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private int followId;

    @ManyToOne
    @JoinColumn(name = "UserID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "PlayerID")
    private Player player;
}
