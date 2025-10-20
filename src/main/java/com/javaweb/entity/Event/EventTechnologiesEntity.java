package com.javaweb.entity.Event;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="eventtechnologies")
@NoArgsConstructor
public class EventTechnologiesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="TechID")
    private Long techID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eventID")
    @JsonBackReference
    private EventEntity event;

    @Column(name = "Technology")
    private String technology;

    public Long getTechID() {
        return techID;
    }

    public void setTechID(Long techID) {
        this.techID = techID;
    }

    @JsonBackReference
    public EventEntity getEvent() {
        return event;
    }

    public void setEvent(EventEntity event) {
        this.event = event;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }
}
