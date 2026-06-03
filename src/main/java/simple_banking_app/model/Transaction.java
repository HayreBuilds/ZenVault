package simple_banking_app.model;

import java.io.Serializable;
import java.util.Date;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private String type;
    private double amount;
    private Date date;
    private String description;

    public Transaction(String type, double amount, Date date) {
        this(type, amount, date, "");
    }

    public Transaction(String type, double amount, Date date, String description) {
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.description = description != null ? description : ""; // Ensure description is never null
    }

    public String getType() { return type; }
    public double getAmount() { return amount; }
    public Date getDate() { return date; }
    public String getDescription() { return description; }

    public void setDescription(String description) {
        this.description = description != null ? description : ""; // Ensure description is never null
    }

    @Override
    public String toString() {
        return String.format("%tF %tT - %s: $%.2f %s",
                date, date, type, amount,
                description.isEmpty() ? "" : "(" + description + ")");
    }
}

//package simple_banking_app.model;
//
//import java.io.Serializable;
//import java.util.Date;
//
//public class Transaction implements Serializable {
//    private static final long serialVersionUID = 1L;
//
//    private String type;
//    private double amount;
//    private Date date;
//    private String description;
//
//    public Transaction(String type, double amount, Date date) {
//        this(type, amount, date, "");
//    }
//
//    public Transaction(String type, double amount, Date date, String description) {
//        this.type = type;
//        this.amount = amount;
//        this.date = date;
//        this.description = description;
//    }
//
//    public String getType() { return type; }
//    public double getAmount() { return amount; }
//    public Date getDate() { return date; }
//    public String getDescription() { return description; }
//    public void setDescription(String description) {
//        this.description = description;
//    }
//    @Override
//    public String toString() {
//        return String.format("%tF %tT - %s: $%.2f %s",
//                date, date, type, amount,
//                description.isEmpty() ? "" : "(" + description + ")");
//    }
//}