package com.capdocs.model;

/**
 * Modelo para Clientes.
 */
public class Client {
    private Integer id;
    private String name;
    private String phone;
    private Double balance;

    public Client() {
    }

    public Client(Integer id, String name, String phone, Double balance) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.balance = balance;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private String name;
        private String phone;
        private Double balance;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder balance(Double balance) {
            this.balance = balance;
            return this;
        }

        public Client build() {
            return new Client(id, name, phone, balance);
        }
    }
}
