package ru.otus.main.base.dataSets;

import javax.persistence.*;

@Entity
@Table(name = "address")
public class Address extends DataSet {

    @Column(name = "street")
    private String street;

    @Column(name = "postal_index")
    private int index;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Address() {
    }

    public Address(String street, int index) {
        this.street = street;
        this.index = index;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Address{" +
                "index=" + index +
                ", street='" + street + '\'' +
                '}';
    }
}
