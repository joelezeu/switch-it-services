package com.etranzact.switchit.helper;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author joel.eze
 */
@XmlRootElement(name = "Banks")
@XmlAccessorType(XmlAccessType.FIELD)
public class BankList {

    @XmlElement(name = "bank")
    private List<Bank> bank = null;

    public List<Bank> getBank() {
        return bank;
    }

    public void setBank(List<Bank> bank) {
        this.bank = bank;
    }

}
