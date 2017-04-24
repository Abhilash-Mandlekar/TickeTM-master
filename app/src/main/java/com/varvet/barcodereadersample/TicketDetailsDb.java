package com.varvet.barcodereadersample;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;
/**
 * Created by Abhilash on 06-03-2017.
 */

@DynamoDBTable(tableName = "TicketmDetailsDb")
public class TicketDetailsDb {
    private String trans_id;
    private int validity;
    private String service;
    private String time_stamp;
    private int penalty;
    private Long key;
    private String status;



    @DynamoDBHashKey(attributeName = "Trans_id")
    public String getTrans_id() {
        return trans_id;
    }

    public void setTrans_id(String trans_id) {
        this.trans_id = trans_id;
    }


    @DynamoDBAttribute(attributeName = "Time_stamp")
    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }

    @DynamoDBAttribute(attributeName = "Validity")
    public int getValidity() {
        return validity;
    }

    public void setValidity(int validity) {
        this.validity = validity;
    }

    @DynamoDBAttribute(attributeName = "Penalty")
    public int getPenalty() {
        return penalty;
    }

    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }


    @DynamoDBAttribute(attributeName = "Status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
